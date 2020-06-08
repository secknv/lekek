package net.sknv.game;

import net.sknv.engine.*;
import net.sknv.engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;

    private final Transformation transformation;

    private ShaderProgram shaderProgram, hudShaderProgram;

    private float specularPower;
    private boolean devMode;

    private final LinkedBlockingQueue<AlienVAO> alienVAOQueue = new LinkedBlockingQueue<>();

    //spaghet
    private ArrayList<Vector3f> track = new ArrayList<>();

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
        devMode = true;
    }

    public void init(Window window) throws Exception {
        setupSceneShader();
        setupHudShader();
    }

    private void setupSceneShader() throws Exception {
        //create shader
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        shaderProgram.link();

        //create uniforms for world and projection matrices
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");

        //material uniform
        shaderProgram.createMaterialUniform("material");

        //light uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        shaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        shaderProgram.createDirectionalLightUniform("directionalLight");
    }

    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.createVertexShader(Utils.loadResource("/shaders/hud_vertex.glsl"));
        hudShaderProgram.createFragmentShader(Utils.loadResource("/shaders/hud_fragment.glsl"));
        hudShaderProgram.link();

        // Create uniforms for Ortographic-model projection matrix and base colour
        hudShaderProgram.createUniform("projModelMatrix");
        hudShaderProgram.createUniform("colour");
        hudShaderProgram.createUniform("hasTexture");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, MouseInput mouseInput, Camera camera, ArrayList<GameItem> gameItems, Vector3f ambientLight, DirectionalLight directionalLight, IHud hud) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        //update projection matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        //update view matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        //update light uniforms
        renderLights(viewMatrix, ambientLight, directionalLight);

        shaderProgram.setUniform("texture_sampler", 0);

        //dbz mark -------------------------------------------------------------------------------
        Vector3f worldRay = mouseInput.getWorldRay(window, projectionMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();

        //ray casting
        RayCast ray = new RayCast(this, cameraPos, new Vector3f(worldRay.x, worldRay.y, worldRay.z));

        //ray casting triangle intersection test
        if(ray.intersectsTriangle(new Vector3f(-5,0,0), new Vector3f(-10,0,0),new Vector3f(-10,5,0))|| ray.intersectsTriangle(new Vector3f(-5,0,0),new Vector3f(-10,5,0), new Vector3f(-5,5,0)) ){
            GraphUtils.drawQuad(this, new Vector4f(0f,255f,0,0), new Vector3f(-5,0,0), new Vector3f(-10,0,0),new Vector3f(-10,5,0), new Vector3f(-5,5,0));
        } else{
            GraphUtils.drawQuad(this, new Vector4f(255f,0,0,0), new Vector3f(-5,0,0), new Vector3f(-10,0,0),new Vector3f(-10,5,0), new Vector3f(-5,5,0));
        }

        //Boid boid = (Boid) gameItems.get(6);

        //tracking line
        /*
        Vector3f t = new Vector3f(boid.getPos().x, boid.getPos().y, boid.getPos().z);
        if(track.size()<2){
            track.add(t);
        }
        else {
            if(t.distance(track.get(track.size()-1))>0.05f) track.add(t);
            if(track.size()>200){ track.remove(0);};
            for(int i=0; i!=track.size()-1; i++){
                GraphUtils.drawLine(shaderProgram, viewMatrix, new Vector4f(255,0,0,0), track.get(i), track.get(i+1));
            }
        }
         */
        /*
        //boid rays
        RayCast boidL = new RayCast(shaderProgram, boid.getPos(), new Vector3f(worldRay.x, worldRay.y, worldRay.z));
        RayCast boidC = new RayCast(shaderProgram, boid.getPos(), boid.velocity);
        RayCast boidR = new RayCast(shaderProgram, boid.getPos(), new Vector3f(worldRay.x, worldRay.y, worldRay.z));

        boidC.drawScaledRay(1, viewMatrix);
         */

        //end dbz mark ---------------------------------------------------------------------------
        //render each game item
        ArrayList<GameItem> clickedItems = new ArrayList<>();
        for (GameItem gameItem : gameItems) {

            Mesh mesh = gameItem.getMesh();
            //set model view
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

            //if color
            shaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();

            if(mouseInput.isLeftClicked() && ray.intersectsItem(gameItem) ){
                clickedItems.add(gameItem);
            }
        }

        if(!clickedItems.isEmpty()) {
            float d = cameraPos.distance(clickedItems.get(0).getPos());
            for (GameItem item : clickedItems) {
                if (cameraPos.distance(item.getPos()) <= d) clicked = item;
            }
            GraphUtils.drawAABB(this, new Vector4f(255, 255, 0, 0), clicked.getBoundingBox());
        }

        if(clicked != null) GraphUtils.drawAABB(this, new Vector4f(75f,0,15f,0f), clicked.getBoundingBox());

        while (!alienVAOQueue.isEmpty()){

            AlienVAO vao = alienVAOQueue.poll();

            shaderProgram.setUniform("material", new Material(vao.getColor(), 0.5f));
            shaderProgram.setUniform("modelViewMatrix", viewMatrix);

            // Bind our VAO
            glBindVertexArray(vao.getVaoId());

            // Draw
            glDrawElements(vao.getDrawMode(),vao.getVertexCount(), GL_UNSIGNED_INT, 0);

            // Delete the VBOs
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            for (int vboId : vao.getVboIds()) {
                glDeleteBuffers(vboId);
            }

            // Unbind and delete the VAO
            glBindVertexArray(0);
            glDeleteVertexArrays(vao.getVaoId());
        }


        if(devMode) renderGraphUtils();

        renderHud(window, hud);

        shaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, Vector3f ambientLight, DirectionalLight directionalLight) {

        //update light uniforms
        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);


        //directional light and transform to view coords
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform("directionalLight", currDirLight);
    }

    private void renderGraphUtils() {
        //GraphUtils.drawGrid(this, new Vector3f(0,0,0),20);
        GraphUtils.drawAxis(this);
    }

    private void renderHud(Window window, IHud hud) {
        hudShaderProgram.bind();

        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        for (GameItem gameItem : hud.getGameItems()) {
            Mesh mesh = gameItem.getMesh();
            // Set ortohtaphic and model matrix for this HUD item
            Matrix4f projModelMatrix = transformation.getOrtoProjModelMatrix(gameItem, ortho);
            hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
            hudShaderProgram.setUniform("colour", gameItem.getMesh().getMaterial().getAmbientColor());
            hudShaderProgram.setUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);

            // Render the mesh for this HUD item
            mesh.render();
        }

        hudShaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
    /**
     * Adds an AlienVAO object to the alienVAOQueue to be rendered.
     * */
    public void addAlienVAO(AlienVAO alienVAO) {
        this.alienVAOQueue.offer(alienVAO);
    }

    //spaghet
    private GameItem clicked;
    public GameItem getClicked() {
        return clicked;
    }
}

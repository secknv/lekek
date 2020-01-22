package net.sknv.game;

import net.sknv.engine.GameItem;
import net.sknv.engine.MouseInput;
import net.sknv.engine.Utils;
import net.sknv.engine.Window;
import net.sknv.engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import net.sknv.engine.graph.RayCast;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;

    private final Transformation transformation;

    private ShaderProgram shaderProgram;

    private float specularPower;
    private boolean devMode;

    //spaghet
    private ArrayList<Vector3f> track = new ArrayList<>();

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
        devMode = true;
    }

    public void init(Window window) throws Exception {
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

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, MouseInput mouseInput, Camera camera, ArrayList<GameItem> gameItems, Vector3f ambientLight,
                       PointLight[] pointLightList, SpotLight[] spotLightList, DirectionalLight directionalLight) {
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
        renderLights(viewMatrix, ambientLight, pointLightList, spotLightList, directionalLight);

        shaderProgram.setUniform("texture_sampler", 0);

        //dbz mark -------------------------------------------------------------------------------
        Vector3f worldRay = mouseInput.getWorldRay(projectionMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPos();

        //ray casting
        RayCast ray = new RayCast(shaderProgram, cameraPos, new Vector3f(worldRay.x, worldRay.y, worldRay.z));

        //ray casting triangle intersection test
        if(ray.intersectsTriangle(new Vector3f(-5,0,0), new Vector3f(-10,0,0),new Vector3f(-10,5,0))|| ray.intersectsTriangle(new Vector3f(-5,0,0),new Vector3f(-10,5,0), new Vector3f(-5,5,0)) ){
            GraphUtils.drawQuad(shaderProgram, transformation, viewMatrix, new Vector4f(0f,255f,0,0), new Vector3f(-5,0,0), new Vector3f(-10,0,0),new Vector3f(-10,5,0), new Vector3f(-5,5,0));
        } else{
            GraphUtils.drawQuad(shaderProgram, transformation, viewMatrix, new Vector4f(255f,0,0,0), new Vector3f(-5,0,0), new Vector3f(-10,0,0),new Vector3f(-10,5,0), new Vector3f(-5,5,0));
        }

        Boid boid = (Boid) gameItems.get(6);
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

        //boid rays
        RayCast boidL = new RayCast(shaderProgram, boid.getPos(), new Vector3f(worldRay.x, worldRay.y, worldRay.z));
        RayCast boidC = new RayCast(shaderProgram, boid.getPos(), boid.accel);
        RayCast boidR = new RayCast(shaderProgram, boid.getPos(), new Vector3f(worldRay.x, worldRay.y, worldRay.z));

        //boidC.drawScaledRay(1, viewMatrix);

        boid.drawSelfAxis(shaderProgram, viewMatrix);



        //end dbz mark ---------------------------------------------------------------------------

        //render each game item
        for (GameItem gameItem : gameItems) {

            Mesh mesh = gameItem.getMesh();
            //set model view
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

            //if color
            shaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();

            //dbz proof of concept AABB
            //gameItem.getBoundingBox().transform(gameItem);// bb coords are being transformed from local to world every frame...
            //not anymore, bb coords are updated upon movement (done in update like its supposed to)
            if(mouseInput.isLeftClicked() && ray.intersectsItem(gameItem)){
                GraphUtils.drawAABB(shaderProgram, viewMatrix, new Vector4f(255,255,0,0), gameItem.getBoundingBox());
            }
            if(gameItem.nCollisions > 0){
                GraphUtils.drawAABB(shaderProgram, viewMatrix, new Vector4f(255,0,0,0), gameItem.getBoundingBox());
            }
        }

        if(true) renderGraphUtils(viewMatrix);

        shaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, Vector3f ambientLight, PointLight[] pointLightList, SpotLight[] spotLightList, DirectionalLight directionalLight) {

        //update light uniforms
        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);

        //Process point lights
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i=0; i<numLights; i++) {
            //get a copy of light object and transform pos to view coords
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPos();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            shaderProgram.setUniform("pointLights", currPointLight, i);
        }

        //Process spot lights
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i=0; i<numLights; i++) {
            //get a copy of light object and transform pos + dir to view coords
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

            Vector3f lightPos = currSpotLight.getPointLight().getPos();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            shaderProgram.setUniform("spotLights", currSpotLight, i);
        }


        //directional light and transform to view coords
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform("directionalLight", currDirLight);
    }

    private void renderGraphUtils(Matrix4f viewMatrix) {
        //GraphUtils.drawGrid(shaderProgram, transformation, viewMatrix, new Vector3f(0,0,0),20);
        GraphUtils.drawAxis(shaderProgram, transformation, viewMatrix);
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}

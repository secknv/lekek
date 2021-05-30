package net.sknv.game;

import net.sknv.engine.*;
import net.sknv.engine.entities.AbstractGameItem;
import net.sknv.engine.entities.HudElement;
import net.sknv.engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class Renderer {

    private ShaderProgram shaderProgram, hudShaderProgram, skyBoxShaderProgram;

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private float specularPower;

    private final LinkedBlockingQueue<AlienVAO> alienVAOQueue = new LinkedBlockingQueue<>();

    public Renderer() {
        specularPower = 10f;
    }

    public void init() throws Exception {
        setupSceneShader();
        setupHudShader();
        setupSkyBoxShader();
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

    private void setupSkyBoxShader() throws Exception {
        skyBoxShaderProgram = new ShaderProgram();
        skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/sb_vertex.glsl"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/sb_fragment.glsl"));
        skyBoxShaderProgram.link();

        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");
        skyBoxShaderProgram.createUniform("texture_sampler");
        skyBoxShaderProgram.createUniform("ambientLight");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Matrix4f projectionMatrix, Matrix4f viewMatrix, Matrix4f ortho, Scene scene, IHud hud) {
        clear();
        renderScene(projectionMatrix, viewMatrix, scene);
        //renderSkyBox(projectionMatrix, viewMatrix, scene);
        renderHud(ortho, hud);
        renderGraphUtils();
    }

    private void renderScene(Matrix4f projectionMatrix, Matrix4f viewMatrix, Scene scene) {

        Vector3f ambientLight = scene.getSceneLight().getAmbientLight();
        DirectionalLight directionalLight = scene.getSceneLight().getDirectionalLight();

        shaderProgram.bind();
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);
        shaderProgram.setUniform("texture_sampler", 0);

        //update light uniforms
        renderLights(viewMatrix, ambientLight, directionalLight);

        //render each game item
        for (AbstractGameItem gameItem : scene.getGameItems()) {
            gameItem.render(shaderProgram, viewMatrix);
        }

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

    private void renderHud(Matrix4f ortho, IHud hud) {
        hudShaderProgram.bind();

        for (HudElement elem : hud.getHudElements()) {
            elem.render(hudShaderProgram, ortho);
        }

        hudShaderProgram.unbind();
    }

    private void renderSkyBox(Matrix4f projectionMatrix, Matrix4f viewMatrix, Scene scene) {
        SkyBox skyBox = scene.getSkyBox();
        skyBoxShaderProgram.bind();

        // todo: [spaghet] SkyBox ShaderProgram needs ambient light and projection matrix,
        //  these setters are used to provide them without having to change the render method signature.
        //  Bad because calling render without using these setters = fail engine.
        skyBox.setAmbientLight(scene.getSceneLight().getAmbientLight());
        skyBox.setProjectionMatrix(projectionMatrix);

        skyBox.render(skyBoxShaderProgram, viewMatrix);

        skyBoxShaderProgram.unbind();
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
}

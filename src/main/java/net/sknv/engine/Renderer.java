package net.sknv.engine;

import net.sknv.engine.entities.GameItem;
import net.sknv.engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
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

    public void render(Window window, Camera camera, ArrayList<GameItem> gameItems, Vector3f ambientLight,
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

        //render each game item
        for (GameItem gameItem : gameItems) {

            Mesh mesh = gameItem.getMesh();

            //set model view
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

            //if color
            shaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();
        }

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

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}

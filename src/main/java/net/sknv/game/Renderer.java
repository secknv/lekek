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

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

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

        shaderProgram.createMaterialUniform("material");

        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, MouseInput mouseInput, Camera camera, GameItem[] gameItems, Vector3f ambientLight, PointLight pointLight) {
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
        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);

        //get a copy of light obj and transform pos to view coords
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPos();
        Vector4f aux = new Vector4f(lightPos, 1.0f);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shaderProgram.setUniform("pointLight", currPointLight);

        shaderProgram.setUniform("texture_sampler", 0);

        //dbz mark
        if(mouseInput.isLeftClicked()){// ray casting test code (will be moved later)
            //convert from viewport to normalised device space
            Vector4f ray_clip = new Vector4f((float)(2.0f * mouseInput.getPos().x) / Window.getWidth() - 1.0f,
                    (float)(1f - (2.0f * mouseInput.getPos().y) / Window.getHeight()), -1.0f, 1.0f);

            //convert from normalised device space to eye space
            Matrix4f invertedProjection = new Matrix4f();
            projectionMatrix.invert(invertedProjection);
            invertedProjection.transform(ray_clip);
            Vector4f ray_eye = new Vector4f(ray_clip.x,ray_clip.y, -1f, 0f);

            //convert from eye space to world space
            Matrix4f invertedViewMatrix = new Matrix4f();
            viewMatrix.invert(invertedViewMatrix);
            invertedViewMatrix.transform(ray_eye);
            Vector3f ray_world = new Vector3f(ray_eye.x,ray_eye.y, ray_eye.z); //y inverted idk why
            ray_world.normalize();

            Vector3f cameraPos = camera.getPos();
            //System.out.println(cameraPos.x + "x " +  cameraPos.y + "y " + cameraPos.z + "z");

            //camera tracker & quad test
            GraphUtils.drawLine(shaderProgram, (new Vector3f(cameraPos.x - 5f, cameraPos.y, cameraPos.z)) , (new Vector3f(cameraPos.x + 5f, cameraPos.y, cameraPos.z)) );
            GraphUtils.drawLine(shaderProgram, (new Vector3f(cameraPos.x, cameraPos.y - 5f, cameraPos.z)) , (new Vector3f(cameraPos.x,cameraPos.y + 5f, cameraPos.z)) );
            GraphUtils.drawLine(shaderProgram, (new Vector3f(cameraPos.x, cameraPos.y,cameraPos.z - 5f)) , (new Vector3f(cameraPos.x, cameraPos.y, cameraPos.z + 5f)) );

            GraphUtils.drawGrid(shaderProgram, new Vector3f(0,0,0),21);
            //ray casting
            GraphUtils.drawQuad(shaderProgram, new Vector3f(-5,0,0), new Vector3f(-10,0,0),new Vector3f(-10,5,0), new Vector3f(-5,5,0));

            RayCast a = new RayCast(shaderProgram, new Vector3f(-7.5f, 2.5f, 5f), new Vector3f(ray_world.x, ray_world.y, ray_world.z));
            a.drawScaledRay(6, new Vector4f(1f,0f,0f,0f));
        }
        //end dbz mark

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

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}

package net.sknv.game;

import net.sknv.engine.GameItem;
import net.sknv.engine.MouseInput;
import net.sknv.engine.Utils;
import net.sknv.engine.Window;
import net.sknv.engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;

    private ShaderProgram shaderProgram;

    public Renderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        //create shader
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shaderProgram.link();

        //create uniforms for world and projection matrices
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, MouseInput mouseInput, Camera camera, GameItem[] gameItems) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        //update projection matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform("texture_sampler", 0);

        if(mouseInput.isLeftClicked()){// ray casting test code (will be moved later)

            //convert from viewport to normalised device space
            Vector4f ray_clip = new Vector4f((float)(2.0f * mouseInput.getPos().x) / Window.getWidth() - 1.0f,
                    (float)((2.0f * mouseInput.getPos().y) / Window.getHeight() - 1.0f ), -1.0f, 1.0f);

            //convert from normalised device space to eye space
            Matrix4f invertedProjection = new Matrix4f();
            projectionMatrix.invert(invertedProjection);
            invertedProjection.transform(ray_clip);
            Vector4f ray_eye = new Vector4f(ray_clip.x,ray_clip.y, -1f, 0f);

            //convert from eye space to world space
            Matrix4f invertedViewMatrix = new Matrix4f();
            viewMatrix.invert(invertedViewMatrix);
            invertedViewMatrix.transform(ray_eye);
            Vector3f ray_world = new Vector3f(ray_eye.x,ray_eye.y, ray_eye.z);
            ray_world.normalize();

            //normalised world ray
            //System.out.println(ray_world.x+"x " + ray_world.y+"y " + ray_world.z+"z" );

            Vector3f cameraPos = camera.getPos();
            System.out.println(cameraPos.x + "x " +  cameraPos.y + "y " + cameraPos.z + "z");
            GraphUtils.drawLine(new Vector3f(0,3,0), new Vector3f(-3,0,0));
            GraphUtils.drawLine(new Vector3f(3,0,0), new Vector3f(-3,0,0));
            GraphUtils.drawLine(new Vector3f(3,0,0), new Vector3f(0,3,0));
            //GraphUtils.drawLine(new Vector3f(0,3,0), cameraPos);
        }

        //render each game item
        for (GameItem gameItem : gameItems) {
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}

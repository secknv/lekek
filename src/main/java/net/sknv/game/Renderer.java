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
        shaderProgram.createDirectionalLightUniform("directionalLight");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, MouseInput mouseInput, Camera camera, GameItem[] gameItems, Vector3f ambientLight, PointLight pointLight, DirectionalLight directionalLight) {
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

        //directional light and transform to view coords
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform("directionalLight", currDirLight);

        shaderProgram.setUniform("texture_sampler", 0);

        //dbz mark ---------------------------------------------------------------------------
        if(true){
            Vector3f worldRay = mouseInput.getWorldRay(projectionMatrix, viewMatrix);

            Vector3f cameraPos = camera.getPos();
            //System.out.println(cameraPos.x + "x " +  cameraPos.y + "y " + cameraPos.z + "z");

            //grid
            GraphUtils.drawGrid(shaderProgram, new Vector3f(0,0,0),21);

            //camera tracker
            GraphUtils.drawLine(shaderProgram, transformation, viewMatrix, new Vector4f(1,1,0,0),(new Vector3f(cameraPos.x - 5f, cameraPos.y, cameraPos.z)) , (new Vector3f(cameraPos.x + 5f, cameraPos.y, cameraPos.z)) );
            GraphUtils.drawLine(shaderProgram, transformation, viewMatrix, new Vector4f(1,1,0,0),(new Vector3f(cameraPos.x, cameraPos.y - 5f, cameraPos.z)) , (new Vector3f(cameraPos.x,cameraPos.y + 5f, cameraPos.z)) );
            GraphUtils.drawLine(shaderProgram, transformation, viewMatrix, new Vector4f(1,1,0,0),(new Vector3f(cameraPos.x, cameraPos.y,cameraPos.z - 5f)) , (new Vector3f(cameraPos.x, cameraPos.y, cameraPos.z + 5f)) );

            //ray casting
            RayCast ray = new RayCast(shaderProgram, cameraPos, new Vector3f(worldRay.x, worldRay.y, worldRay.z));
            Vector3f intersectionPoint =  ray.intersectPlane(new Vector3f(-5,0,0), new Vector3f(0,0,-1));
            GraphUtils.drawLine(shaderProgram, ray.origin, intersectionPoint);

            //ray casting triangle intersection test
            if(intersectionPoint != null && ( ray.intersectsTriangle(new Vector3f(-5,0,0), new Vector3f(-10,0,0),new Vector3f(-10,5,0))|| ray.intersectsTriangle(new Vector3f(-5,0,0),new Vector3f(-10,5,0), new Vector3f(-5,5,0)) )){
                GraphUtils.drawQuad(shaderProgram,new Vector4f(0f,255f,0,0), new Vector3f(-5,0,0), new Vector3f(-10,0,0),new Vector3f(-10,5,0), new Vector3f(-5,5,0));
            } else{
                GraphUtils.drawQuad(shaderProgram,new Vector4f(255f,0,0,0), new Vector3f(-5,0,0), new Vector3f(-10,0,0),new Vector3f(-10,5,0), new Vector3f(-5,5,0));
            }

        }
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
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}

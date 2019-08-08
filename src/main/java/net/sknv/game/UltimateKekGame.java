package net.sknv.game;

import net.sknv.engine.GameItem;
import net.sknv.engine.IGameLogic;
import net.sknv.engine.Window;
import net.sknv.engine.graph.Mesh;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class UltimateKekGame implements IGameLogic {

    private int displxInc, displyInc, displzInc, scaleInc;

    private final Renderer renderer;

    private GameItem[] gameItems;

    public UltimateKekGame() {
        renderer = new Renderer();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        // Create the Mesh
        float[] positions = new float[]{
                // VO
                -0.5f,  0.5f,  0.5f,
                // V1
                -0.5f, -0.5f,  0.5f,
                // V2
                0.5f, -0.5f,  0.5f,
                // V3
                0.5f,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f,  0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };
        float[] colors = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };

        Mesh mesh = new Mesh(positions, colors, indices);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setPos(0, 0, -2);
        gameItems = new GameItem[] {gameItem};

    }

    @Override
    public void input(Window window) {
        displxInc = 0;
        displyInc = 0;
        displzInc = 0;
        scaleInc = 0;

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            displyInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            displyInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            displxInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            displxInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_A)) {
            displzInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_Q)) {
            displzInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_Z)) {
            scaleInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            scaleInc = 1;
        }
    }

    @Override
    public void update(float interval) {
        for (GameItem item : gameItems) {
            //update pos
            Vector3f itemPos = item.getPos();
            item.setPos(itemPos.x + displxInc*0.01f, itemPos.y + displyInc*0.01f, itemPos.z + displzInc*0.01f);

            //update scale
            float scale = item.getScale();
            scale += scaleInc * 0.05f;
            if (scale < 0) scale = 0;
            item.setScale(scale);

            //update rotation
            float rot = item.getRot().x + 1.5f;
            if (rot > 360) rot = 0;
            item.setRot(rot, rot, rot);
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for(GameItem item : gameItems) item.getMesh().cleanup();
    }
}

package net.sknv.game;

import net.sknv.engine.GameItem;
import net.sknv.engine.IGameLogic;
import net.sknv.engine.Window;
import net.sknv.engine.graph.Mesh;
import net.sknv.engine.graph.Texture;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class UltimateKekGame implements IGameLogic {

    private int displxInc, displyInc, rotxInc, rotyInc, scaleInc;

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
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,
                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,
                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,};
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,
                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,
                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,};
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,};

        Texture texture = new Texture("src/main/resources/textures/lebloq.png");
        Mesh mesh = new Mesh(positions, textCoords, indices, texture);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setPos(0, 0, -2);
        gameItem.setScale(0.5f);
        gameItems = new GameItem[] {gameItem};

    }

    @Override
    public void input(Window window) {
        displxInc = 0;
        displyInc = 0;
        rotxInc = 0;
        rotyInc = 0;
        scaleInc = 0;

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            displyInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            displyInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            displxInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            displxInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_W)) {
            rotxInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            rotxInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_A)) {
            rotyInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            rotyInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_Q)) {
            scaleInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_E)) {
            scaleInc = -1;
        }

    }

    @Override
    public void update(float interval) {
        for (GameItem item : gameItems) {
            //update pos
            Vector3f itemPos = item.getPos();
            item.setPos(itemPos.x + displxInc*0.01f, itemPos.y + displyInc*0.01f, itemPos.z + rotxInc *0.01f);

            //update scale
            float scale = item.getScale();
            scale += scaleInc * 0.05f;
            if (scale < 0) scale = 0;
            item.setScale(scale);


            //update rotation
            float rotx = item.getRot().x;
            rotx += rotxInc * 2f;
            if (rotx > 360) rotx = 0;

            float roty = item.getRot().y;
            roty += rotyInc * 2f;
            if (roty > 360) roty = 0;

            item.setRot(rotx, roty, 0);
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

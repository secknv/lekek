package net.sknv.game;

import net.sknv.engine.GameItem;
import net.sknv.engine.IGameLogic;
import net.sknv.engine.MouseInput;
import net.sknv.engine.Window;
import net.sknv.engine.graph.Camera;
import net.sknv.engine.graph.Mesh;
import net.sknv.engine.graph.OBJLoader;
import net.sknv.engine.graph.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;

public class UltimateKekGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.8f;
    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraInc;

    private final Renderer renderer;
    private final Camera camera;

    private GameItem[] gameItems;

    public UltimateKekGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Mesh kek = OBJLoader.loadMesh("/models/untitled.obj");
        Mesh bunny = OBJLoader.loadMesh("/models/bunny.obj");

        Texture texture = new Texture("src/main/resources/textures/lebloq.png");
        mesh.setTexture(texture);
        kek.setColor(new Vector3f(1f, 0f, 0f));

        float scale = 0.2f;

        GameItem gameItem0 = new GameItem(kek);
        gameItem0.setPos(0, 0, -5);
        gameItem0.setScale(1f);

        GameItem gameItem20 = new GameItem(bunny);
        gameItem20.setPos(-5, 0, -5);
        gameItem20.setScale(1f);

        GameItem gameItem1 = new GameItem(mesh);
        gameItem1.setPos(0, 0, -2);
        gameItem1.setScale(scale);

        GameItem gameItem2 = new GameItem(mesh);
        gameItem2.setPos(0.5f, 0.5f, -2);
        gameItem2.setScale(scale);

        GameItem gameItem3 = new GameItem(mesh);
        gameItem3.setPos(0, 0, -2.5f);
        gameItem3.setScale(scale);

        GameItem gameItem4 = new GameItem(mesh);
        gameItem4.setPos(0.5f, 0, -2.5f);
        gameItem4.setScale(scale);

        gameItems = new GameItem[] {gameItem0, gameItem20,gameItem1, gameItem2, gameItem3, gameItem4};

    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);

        if (window.isKeyPressed(GLFW_KEY_W)) cameraInc.z = -1;
        if (window.isKeyPressed(GLFW_KEY_S)) cameraInc.z = 1;
        if (window.isKeyPressed(GLFW_KEY_A)) cameraInc.x = -1;
        if (window.isKeyPressed(GLFW_KEY_D)) cameraInc.x = 1;
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) cameraInc.y = -1;
        if (window.isKeyPressed(GLFW_KEY_SPACE)) cameraInc.y = 1;

    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        //click testing
        glClearColor(0.0f, 0.0f,0.0f, 0.0f);

        camera.movePos(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        if (mouseInput.isRightClicked()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRot(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        //click testing
        if (mouseInput.isLeftClicked()) {
            glClearColor(1.0f, 0.0f,0.0f, 1.0f);
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for(GameItem item : gameItems) item.getMesh().cleanup();
    }
}

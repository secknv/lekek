package net.sknv.game;

import net.sknv.engine.*;
import net.sknv.engine.GameItem;
import net.sknv.engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.text.NumberFormat;

import static org.lwjgl.glfw.GLFW.*;

public class UltimateKekGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.8f;
    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraInc;
    private Vector2f cameraRotVec;

    private final Renderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private Vector3f ambientLight;
    private boolean menu;

    public UltimateKekGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        initGameItems();
        initLights();


        setKeyCallbacks(window);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.zero();
        if (window.isKeyPressed(GLFW_KEY_W)) cameraInc.z = -1;
        if (window.isKeyPressed(GLFW_KEY_S)) cameraInc.z = (cameraInc.z < 0 ? 0 : 1);
        if (window.isKeyPressed(GLFW_KEY_A)) cameraInc.x = -1;
        if (window.isKeyPressed(GLFW_KEY_D)) cameraInc.x = (cameraInc.x < 0 ? 0 : 1);
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) cameraInc.y = -1;
        if (window.isKeyPressed(GLFW_KEY_SPACE)) cameraInc.y = (cameraInc.y < 0 ? 0 : 1);

        if(cameraInc.length()!=0) cameraInc.normalize();


        if (!menu && glfwGetWindowAttrib(window.getWindowHandle(), GLFW_FOCUSED) == 1) {
            glfwSetCursorPos(window.getWindowHandle(), window.getCenter().x, window.getCenter().y);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        cameraRotVec = mouseInput.getDisplVec();
        camera.moveRotation(cameraRotVec.x * MOUSE_SENSITIVITY, cameraRotVec.y * MOUSE_SENSITIVITY, 0);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems, ambientLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }

    private void initGameItems() throws Exception {
        float reflectance = 1f;

        Mesh cube = OBJLoader.loadMesh("/models/cube.obj");
        Mesh kek = OBJLoader.loadMesh("/models/untitled.obj");

        Texture texture = new Texture("src/main/resources/textures/lebloq.png");
        Material material = new Material(texture, reflectance);
        cube.setMaterial(material);

        kek.setMaterial(new Material(new Vector4f(1f, 0, 0,1f), 0.5f));

        float scale = .25f;

        GameItem gameItem0 = new GameItem(kek);
        gameItem0.setPosition(0, 0, -6);
        gameItem0.setScale(.5f);

        GameItem gameItem1 = new GameItem(cube);
        gameItem1.setPosition(0, 0, .6f);
        gameItem1.setScale(scale);

        GameItem gameItem2 = new GameItem(cube);
        gameItem2.setPosition(0f, 0, 0);
        gameItem2.setScale(scale);

        gameItems = new GameItem[]{gameItem0, gameItem1, gameItem2};
    }

    private void initLights() {
        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
    }
    /**
     * Use the body of this method to set the key callbacks
     * Use key callbacks for single press actions, like opening a menu with "P"
     *
     * For movement keys, where you just want to know if the key IS being pressed,
     * use window.isKeyPressed(key)
     * */
    private void setKeyCallbacks(Window window) {
        window.setKeyCallback((windowHandle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_P && action == GLFW_PRESS) {
                if(menu){
                    menu = false;
                    glfwSetCursorPos(window.getWindowHandle(), window.getCenter().x, window.getCenter().y);
                    glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                } else {
                    menu = true;
                    glfwSetCursorPos(window.getWindowHandle(), window.getCenter().x, window.getCenter().y);
                    glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                }
            }
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(windowHandle, true);
            }
        });
    }

}
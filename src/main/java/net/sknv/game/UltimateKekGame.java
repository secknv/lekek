package net.sknv.game;

import net.sknv.engine.*;
import net.sknv.engine.graph.*;
import net.sknv.engine.physics.PhysicsEngine;
import net.sknv.engine.physics.colliders.OBB;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class UltimateKekGame implements IGameLogic {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private static final float MOUSE_SENSITIVITY = 0.003f;
    private static final float CAMERA_POS_STEP = 0.03f;

    private final Vector3f cameraPosInc;
    private final Vector3f cameraRotInc;

    private final Renderer renderer;
    private final Camera camera;

    private boolean menu = false;
    private boolean usingTerminal = false;

    private Scene scene;
    private Hud hud;

    //collisions stuff
    private PhysicsEngine physicsEngine;
    public GameItem movableItem;

    public UltimateKekGame() {
        renderer = new Renderer();
        camera = new Camera(new Vector3f(), new Vector3f());
        cameraPosInc = new Vector3f();
        cameraRotInc = new Vector3f();
    }

    @Override
    public void init(Window window, MouseInput mouseInput) throws Exception {
        renderer.init(window);
        setKeyCallbacks(window, mouseInput);

        initScene("default");
        initPhysicsEngine();

        // Setup HUD
        hud = new Hud("+");

        //Setup Camera
        camera.setPosition(0.65f, 1.15f, 4.34f);

        //todo temp - figure this out
        if(!scene.getGameItems().isEmpty()) movableItem = scene.getGameItems().get(0);
    }

    public void initScene(String scene) {
        try {
            this.scene = new Scene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initPhysicsEngine() {
        physicsEngine = new PhysicsEngine();
        List<GameItem> gameItems = scene.getGameItems();
        for (GameItem gameItem : gameItems) {
            //todo: temporary, GameItem empty constructor should deprecate and bb should be initialized on a constructor with all params,
            // unless it is decided to not deprecate the empty constructor to support other types of gameItems (Huds, lines, stuff)
            // in that case class inheritance should be pondered
            gameItem.setBoundingBox(new OBB(gameItem));
            try {
                physicsEngine.addGameItem(gameItem);
            } catch (Exception e) {
                System.out.println("object colliding");
            }
        }
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraPosInc.zero();

        if (window.isKeyPressed(GLFW_KEY_W)) cameraPosInc.z = -1;
        if (window.isKeyPressed(GLFW_KEY_S)) cameraPosInc.z = (cameraPosInc.z < 0 ? 0 : 1);
        if (window.isKeyPressed(GLFW_KEY_A)) cameraPosInc.x = -1;
        if (window.isKeyPressed(GLFW_KEY_D)) cameraPosInc.x = (cameraPosInc.x < 0 ? 0 : 1);
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) cameraPosInc.y = -1;
        if (window.isKeyPressed(GLFW_KEY_SPACE)) cameraPosInc.y = (cameraPosInc.y < 0 ? 0 : 1);

        if (cameraPosInc.length() != 0) cameraPosInc.normalize();

        if (renderer.getClicked() != null) movableItem = renderer.getClicked();
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            if (window.isKeyPressed(GLFW_KEY_DOWN)) movableItem.getVelocity().z = 0f;
            else movableItem.getVelocity().z = -.1f;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) movableItem.getVelocity().z = .1f;

        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            if (window.isKeyPressed(GLFW_KEY_RIGHT)) movableItem.getVelocity().x = 0f;
            else movableItem.getVelocity().x = -.1f;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) movableItem.getVelocity().x = .1f;

        if (window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) {
            if (window.isKeyPressed(GLFW_KEY_RIGHT_CONTROL)) movableItem.getVelocity().y = 0f;
            else movableItem.getVelocity().y = .1f;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT_CONTROL)) movableItem.getVelocity().y = -.1f;

        if (window.isKeyPressed(GLFW_KEY_X)) movableItem.rotateEuclidean(new Vector3f((float) (-Math.PI / 200), 0, 0));
        if (window.isKeyPressed(GLFW_KEY_Y)) movableItem.rotateEuclidean(new Vector3f(0, (float) (-Math.PI / 200), 0));
        if (window.isKeyPressed(GLFW_KEY_Z)) movableItem.rotateEuclidean(new Vector3f(0, 0, (float) (-Math.PI / 200)));

        if (window.isKeyPressed(GLFW_KEY_K)) movableItem.setRotationEuclidean(new Vector3f());

        if(mouseInput.isLeftClicked()){
            /*
            Vector3f worldRay = mouseInput.getWorldRay(window, projectionMatrix, viewMatrix);
            Vector3f cameraPos = camera.getPosition();

            //ray casting
            RayCast ray = new RayCast(renderer, new Vector3f(cameraPos), new Vector3f(worldRay.x, worldRay.y, worldRay.z));
             */
        }
    }

    private void moveCamera(Window window, MouseInput mouseInput) {
        // moves camera pos
        if(!(menu || usingTerminal)) camera.movePosition(cameraPosInc.x * CAMERA_POS_STEP, cameraPosInc.y * CAMERA_POS_STEP, cameraPosInc.z * CAMERA_POS_STEP);

        // rotates camera
        if (!(menu || usingTerminal) && glfwGetWindowAttrib(window.getWindowHandle(), GLFW_FOCUSED) == 1) {
            Vector2f rotVec = mouseInput.getDisplVec();
            cameraRotInc.set(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            camera.moveRotation(cameraRotInc);
        }
    }

    @Override
    public void update(Window window, MouseInput mouseInput, float interval) {
        physicsEngine.simulate(scene);
        moveCamera(window, mouseInput);
        hud.rotateCompass(camera.getRotation().y);
    }

    @Override
    public void render(Window window, MouseInput mouseInput) {
        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        //update matrices
        Matrix4f projectionMatrix = Transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);
        Matrix4f ortho = Transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);

        hud.updateSize(window);
        renderer.render(projectionMatrix, viewMatrix, ortho, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for(GameItem item : scene.getGameItems()) item.getMesh().cleanUp();
        hud.cleanup();
    }

    /**
     * Use the body of this method to set the key callbacks
     * Use key callbacks for single press actions, like opening a menu with "P"
     *
     * For movement keys, where you just want to know if the key IS being pressed,
     * use window.isKeyPressed(key)
     * */
    private void setKeyCallbacks(Window window, MouseInput mouseInput) {
        window.setKeyCallback((windowHandle, key, scancode, action, mods) -> {
            if (usingTerminal && (action == GLFW_PRESS || action == GLFW_REPEAT)){ //using hud terminal
                if(key>48 && key<91){
                    hud.getTerminal().addText(String.valueOf((char)Character.toLowerCase(key)));
                } else if (key == 32) hud.getTerminal().addText(" ");
                else if (key == 257) {
                    processTerminal(hud.getTerminal().enter());
                    closeHudTerminal(mouseInput, windowHandle);
                }
                else if (key == 259) hud.getTerminal().backspace();
            }

            if (key == GLFW_KEY_P && action == GLFW_PRESS && !usingTerminal) {
                if(menu){
                    menu = false;
                    mouseInput.setDisabled();
                    glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                } else {
                    menu = true;
                    mouseInput.setEnabled();
                    glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    glfwSetCursorPos(windowHandle, window.getCenter().x, window.getCenter().y);
                }
            }
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                if(usingTerminal){ //closing hud terminal
                    closeHudTerminal(mouseInput, windowHandle);
                } else glfwSetWindowShouldClose(windowHandle, true); //closing game
            }
            if (!usingTerminal && key == GLFW_KEY_T && action == GLFW_PRESS){ //opening hud terminal
                openHudTerminal(window, mouseInput, windowHandle);
            }
        });
    }

    private void openHudTerminal(Window window, MouseInput mouseInput, long windowHandle) {
        usingTerminal = true;
        mouseInput.setEnabled();
        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwSetCursorPos(windowHandle, window.getCenter().x, window.getCenter().y);
        hud.showTerminal();
    }

    private void closeHudTerminal(MouseInput mouseInput, long windowHandle) {
        usingTerminal = false;
        mouseInput.setDisabled();
        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        hud.hideTerminal();
    }

    public void processTerminal(String input) {
        if (input==null) return;

        String[] in = input.split(" ");

        switch (in[0]){
            case "test":
                System.out.println("its working :)");
                break;
            case "savescene":
                String sceneName;
                if(in.length>1) sceneName = in[1]; else sceneName = "unnamed";
                getScene().save(sceneName);
                break;
            case "loadscene":
                if(in.length>1) sceneName = in[1]; else return;
                System.out.println("loading scene - " + sceneName);
                initScene(sceneName);
                initPhysicsEngine();
                System.out.println("scene loaded");
                break;
            case "removeitems":
                scene.getGameItems().clear();
                break;
            case "removeitem":
                scene.getGameItems().remove(movableItem);
                break;
            case "additem":
                try {
                    String model = in[1];
                    Mesh mesh = OBJLoader.loadMesh("/models/" + model + ".obj");
                    Texture texture = new Texture("src/main/resources/textures/lebloq.png");
                    Material material = new Material(texture, 1f);
                    mesh.setMaterial(material);
                    GameItem newItem = new GameItem(mesh);
                    scene.getGameItems().add(newItem);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "quit":
                System.exit(0);
                break;
            default:
                break;
        }
    }

    public Scene getScene(){
        return scene;
    }

    public Hud getHud(){
        return hud;
    }

    private void setScene(Scene scene) {
        this.scene = scene;
    }
}

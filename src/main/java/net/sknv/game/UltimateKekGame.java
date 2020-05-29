package net.sknv.game;

import net.sknv.engine.GameItem;
import net.sknv.engine.IGameLogic;
import net.sknv.engine.MouseInput;
import net.sknv.engine.Window;
import net.sknv.engine.collisions.BoundingBox;
import net.sknv.engine.collisions.OBB;
import net.sknv.engine.collisions.SPCollision;
import net.sknv.engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class UltimateKekGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.8f;
    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraPosInc;
    private final Vector2f cameraRotInc;

    private final Renderer renderer;
    private final Camera camera;

    private ArrayList<GameItem> gameItems;

    private boolean menu = false;

    //light stuff
    private Vector3f ambientLight;
    private DirectionalLight directionalLight;
    private float lightAngle;

    //collisions stuff
    private SPCollision sweepPrune = new SPCollision();
    private OBB testBox;

    public GameItem movableItem;

    public UltimateKekGame() {
        renderer = new Renderer();
        camera = new Camera();
        lightAngle = -90;

        cameraPosInc = new Vector3f(0, 0, 0);
        cameraRotInc = new Vector2f(0, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        setKeyCallbacks(window);

        initLighting();
        initGameItems();

        initCollisions();
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

        if(cameraPosInc.length()!=0) cameraPosInc.normalize();

        if(renderer.getClicked()!=null) movableItem = renderer.getClicked();
        if (window.isKeyPressed(GLFW_KEY_UP)) movableItem.velocity.z -= .1;
        if (window.isKeyPressed(GLFW_KEY_DOWN)) movableItem.velocity.z += .1;
        if (window.isKeyPressed(GLFW_KEY_LEFT)) movableItem.velocity.x -= .1;
        if (window.isKeyPressed(GLFW_KEY_RIGHT)) movableItem.velocity.x += .1;

        if (window.isKeyPressed(GLFW_KEY_X)) movableItem.rotate(new Vector3f((float) (-Math.PI/200),0,0));
        if (window.isKeyPressed(GLFW_KEY_Y)) movableItem.rotate(new Vector3f(0,(float) (-Math.PI/200),0));
        if (window.isKeyPressed(GLFW_KEY_Z)) movableItem.rotate(new Vector3f(0,0,(float) (-Math.PI/200)));

    }

    @Override
    public void update(Window window, MouseInput mouseInput, float interval) {
        updateItems();
        moveCamera(window, mouseInput);
    }

    private void initGameItems() throws Exception{
        float reflectance = 1f;

        Mesh cube = OBJLoader.loadMesh("/models/cube.obj");
        Mesh kek = OBJLoader.loadMesh("/models/untitled.obj");
        Mesh boid = OBJLoader.loadMesh("/models/boid.obj");

        Texture texture = new Texture("src/main/resources/textures/lebloq.png");
        Material material = new Material(texture, reflectance);
        cube.setMaterial(material);
        kek.setMaterial(new Material(new Vector4f(1f, 0, 0,1f), 0.5f));
        boid.setMaterial(new Material(new Vector4f(0f, 1f, 1f, 1f), 0.5f));

        float scale = .25f;

        System.out.println("Creating items");
        GameItem gameItem0 = new GameItem(kek);
        gameItem0.setPos(0, 0, -6);
        gameItem0.setScale(.5f);

        GameItem gameItem1 = new GameItem(cube);
        gameItem1.setPos(0, 0, .5f);
        gameItem1.setScale(scale);

        GameItem gameItem2 = new GameItem(cube);
        gameItem2.setPos(0f, 0, 0);
        gameItem2.setScale(scale);

        GameItem gameItem3 = new GameItem(cube);
        gameItem3.setPos(0.5f, 0, 0);
        gameItem3.setScale(scale);

        GameItem gameItem4 = new GameItem(cube);
        gameItem4.setPos(.5f, 0, .5f);
        gameItem4.setScale(scale);

        /*
        Boid b = new Boid(boid);
        b.setPos(-2, 0, 0);
        b.setScale(.1f);
         */

        GameItem testItem = new GameItem(kek);
        testItem.setPos(2f, 0, 2f);
        testItem.setScale(scale);

        testBox = new OBB(testItem, testItem.getMesh().getMin(), testItem.getMesh().getMax());
        testItem.setBoundingBox(testBox);
        movableItem = testItem;

        gameItems = new ArrayList<>(Arrays.asList(gameItem0, gameItem1, gameItem2, gameItem3, gameItem4, testItem));
    }

    private void initLighting() {
        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

        float lightIntensity = 1.0f;
        Vector3f lightPos = new Vector3f(-1, 0, 0);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPos, lightIntensity);
    }

    private void initCollisions() {
        for (Iterator<GameItem> iterator = gameItems.iterator(); iterator.hasNext();) {
            GameItem gameItem = iterator.next();
            gameItem.getBoundingBox().transform();// converts bb coords from local to world
            try {
                sweepPrune.addItem(gameItem);
            } catch (Exception e){
                System.out.println("object colliding");;
                //iterator.remove();
            }
        }
    }

    private void moveCamera(Window window, MouseInput mouseInput) {
        // moves camera pos
        camera.movePosition(cameraPosInc.x * CAMERA_POS_STEP, cameraPosInc.y * CAMERA_POS_STEP, cameraPosInc.z * CAMERA_POS_STEP);

        // rotates camera
        if (!menu && glfwGetWindowAttrib(window.getWindowHandle(), GLFW_FOCUSED) == 1) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

            glfwSetCursorPos(window.getWindowHandle(), window.getCenter().x, window.getCenter().y);
        }
    }

    private void updateItems() {
        for(GameItem gameItem : gameItems){
            if(gameItem.getVelocity().length() != 0){ //game item has acceleration

                //calculate step
                Vector3f step = gameItem.velocity.mul(0.1f);

                Set<BoundingBox> col = sweepPrune.checkStepCollisions(gameItem, step);

                if(col.isEmpty()){
                    //no collisions, perform movement
                    gameItem.translate(step);
                    gameItem.velocity.zero();
                } else {
                    //collisions
                    Set<BoundingBox> colX = sweepPrune.checkStepCollisions(gameItem, new Vector3f(step.x, 0, 0));
                    Set<BoundingBox> colY = sweepPrune.checkStepCollisions(gameItem, new Vector3f(0, step.y, 0));
                    Set<BoundingBox> colZ = sweepPrune.checkStepCollisions(gameItem, new Vector3f(0, 0, step.z));

                    if(colX.isEmpty() && colY.isEmpty() && colZ.isEmpty()){
                        //collision, not performing the movement
                        gameItem.velocity.zero();
                        GraphUtils.drawAABB(renderer, new Vector4f(255,0,0,0), gameItem.getBoundingBox());
                    } else {
                        //partial collision, perform partial step
                        Vector3f partialStep = new Vector3f();
                        if (colX.isEmpty()) partialStep.x = step.x;
                        if (colY.isEmpty()) partialStep.y = step.y;
                        if (colZ.isEmpty()) partialStep.z = step.z;
                        gameItem.translate(partialStep);
                        gameItem.velocity.zero();

                        if(partialStep.x==0 && partialStep.y==0 && partialStep.z==0) //collision
                        GraphUtils.drawAABB(renderer, new Vector4f(255,0,0,0), gameItem.getBoundingBox());
                    }
                }
            }
        }
    }

    @Override
    public void render(Window window, MouseInput mouseInput) {
        renderer.render(window, mouseInput, camera, gameItems, ambientLight, directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for(GameItem item : gameItems) item.getMesh().cleanUp();
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

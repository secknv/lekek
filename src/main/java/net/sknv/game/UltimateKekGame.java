package net.sknv.game;

import net.sknv.engine.*;
import net.sknv.engine.graph.*;
import net.sknv.engine.physics.colliders.BoundingBox;
import net.sknv.engine.physics.colliders.OBB;
import net.sknv.engine.physics.collisionDetection.SPCollision;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class UltimateKekGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.4f;
    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraPosInc;
    private final Vector2f cameraRotInc;

    private final Renderer renderer;
    private final Camera camera;

    private boolean menu = false;

    //light stuff
    private Vector3f ambientLight;
    private DirectionalLight directionalLight;
    private float lightAngle;

    private Scene scene;
    private Hud hud;

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
    public void init(Window window, MouseInput mouseInput) throws Exception {
        renderer.init(window);
        setKeyCallbacks(window, mouseInput);

        scene = new Scene();

        initGameItems();
        initLighting();

        camera.getPosition().x = 0.65f;
        camera.getPosition().y = 1.15f;
        camera.getPosition().y = 4.34f;

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
        if (window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) movableItem.velocity.y += .1;
        if (window.isKeyPressed(GLFW_KEY_RIGHT_CONTROL)) movableItem.velocity.y -= .1;

        if (window.isKeyPressed(GLFW_KEY_X)) movableItem.rotate(new Vector3f((float) (-Math.PI/200),0,0));
        if (window.isKeyPressed(GLFW_KEY_Y)) movableItem.rotate(new Vector3f(0,(float) (-Math.PI/200),0));
        if (window.isKeyPressed(GLFW_KEY_Z)) movableItem.rotate(new Vector3f(0,0,(float) (-Math.PI/200)));

        if (window.isKeyPressed(GLFW_KEY_K)) movableItem.setRot(0, 0, 0);

    }

    @Override
    public void update(Window window, MouseInput mouseInput, float interval) {
        updateItems();
        moveCamera(window, mouseInput);
        hud.rotateCompass(camera.getRotation().y);
    }

    private void initGameItems() throws Exception{

        float reflectance = 1f;

        // Setup block mesh
        Mesh cube = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("src/main/resources/textures/lebloq.png");
        Material material = new Material(texture, reflectance);
        cube.setMaterial(material);

        // Setup kek mesh
        Mesh kek = OBJLoader.loadMesh("/models/untitled.obj");
        kek.setMaterial(new Material(new Vector4f(1f, 0, 0,1f), 0.5f));

        // Setup boid mesh
        Mesh boid = OBJLoader.loadMesh("/models/boid.obj");
        boid.setMaterial(new Material(new Vector4f(0f, 1f, 1f, 1f), 0.5f));


        // Background game items
        float blockScale = 0.5f;
        float skyBoxScale = 10.0f;
        float extension = 2.0f;

        float startx = extension * (-skyBoxScale + blockScale);
        float startz = extension * (skyBoxScale - blockScale);
        float starty = -1.0f;
        float inc = blockScale * 2;

        float posx = startx;
        float posz = startz;
        float incy = 0.0f;
        int NUM_ROWS = (int)(extension * skyBoxScale * 2 / inc);
        int NUM_COLS = (int)(extension * skyBoxScale * 2/ inc);
        ArrayList<GameItem> gameItems  = new ArrayList<>(NUM_ROWS * NUM_COLS + 10);
        for(int i=0; i<NUM_ROWS; i++) {
            for(int j=0; j<NUM_COLS; j++) {
                GameItem gameItem = new GameItem(cube);
                gameItem.setScale(blockScale);
                incy = Math.random() > 0.9f ? blockScale * 2 : 0f;
                gameItem.setPos(posx, starty + incy, posz);
                gameItems.add(gameItem);

                posx += inc;
            }
            posx = startx;
            posz -= inc;
        }

        // Special game items
        GameItem testItem = new GameItem(kek);
        testItem.setPos(2f, 1, 2f);
        testItem.setScale(blockScale);

        /*
        Boid b = new Boid(boid);
        b.setPos(-2, 0, 0);
        b.setScale(.1f);
         */

        testBox = new OBB(testItem, testItem.getMesh().getMin(), testItem.getMesh().getMax());
        testItem.setBoundingBox(testBox);
        movableItem = testItem;

        // add special items to gameItems array
        // this is here case more than one special items...
        // easier to just add {testItem, myOtherItem, kekItem}
        GameItem[] specialItems = {testItem};
        gameItems.addAll(Arrays.asList(specialItems));

        // set gameItems in scene
        scene.setGameItems(gameItems.toArray(new GameItem[0]));

        // Setup  SkyBox
        // todo: standardize resource paths
        SkyBox skyBox = new SkyBox("/models/skybox.obj", "src/main/resources/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup HUD
        hud = new Hud("+");
    }

    private void initLighting() {
        SceneLight sceneLight = new SceneLight();
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        float lightIntensity = 1.0f;
        Vector3f lightPos = new Vector3f(-1, 0, 0);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        sceneLight.setDirectionalLight(new DirectionalLight(lightColor, lightPos, lightIntensity));

        scene.setSceneLight(sceneLight);
    }

    private void initCollisions() {
        List<GameItem> gameItems = Arrays.asList(scene.getGameItems());
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
        }
    }

    private void updateItems() {
        for(GameItem gameItem : scene.getGameItems()){
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
        hud.updateSize(window);
        renderer.render(window, mouseInput, camera, scene, hud);
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
            if (key == GLFW_KEY_P && action == GLFW_PRESS) {
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
                glfwSetWindowShouldClose(windowHandle, true);
            }

        });
    }
}

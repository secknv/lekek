package net.sknv.game;

import net.sknv.engine.GameItem;
import net.sknv.engine.IGameLogic;
import net.sknv.engine.MouseInput;
import net.sknv.engine.Window;
import net.sknv.engine.collisions.SPCollision;
import net.sknv.engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class UltimateKekGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.8f;
    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraInc;

    private final Renderer renderer;
    private final Camera camera;

    private ArrayList<GameItem> gameItems;

    private boolean menu = false;

    //light stuff
    private Vector3f ambientLight;
    private PointLight[] pointLightList;
    private SpotLight[] spotLightList;
    private DirectionalLight directionalLight;
    private float lightAngle, spotAngle, spotInc;

    //collisions stuff
    private SPCollision sweepPrune = new SPCollision();

    public UltimateKekGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = -90;
        spotAngle = 0;
        spotInc = 1;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        initLighting();
        initGameItems();
        initCollisions();
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.zero();

        if (window.isKeyPressed(GLFW_KEY_W)) cameraInc.z += -1;
        if (window.isKeyPressed(GLFW_KEY_S)) cameraInc.z += 1;
        if (window.isKeyPressed(GLFW_KEY_A)) cameraInc.x += -1;
        if (window.isKeyPressed(GLFW_KEY_D)) cameraInc.x += 1;
        if (window.isKeyPressed(GLFW_KEY_SPACE)) cameraInc.y += 1;
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) cameraInc.y += -1;

        if(cameraInc.length()!=0) cameraInc.normalize();

        Boid b = (Boid) gameItems.get(6);
        b.accel = new Vector3f(0,0, 0f);

        GameItem movableItem = gameItems.get(4);
        if (window.isKeyPressed(GLFW_KEY_UP)) movableItem.accel.z -= .1;
        if (window.isKeyPressed(GLFW_KEY_DOWN)) movableItem.accel.z += .1;
        if (window.isKeyPressed(GLFW_KEY_LEFT)) movableItem.accel.x -= .1;
        if (window.isKeyPressed(GLFW_KEY_RIGHT)) movableItem.accel.x += .1;

        if (window.isKeyPressed(GLFW_KEY_P)) {
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
    }

    @Override
    public void update(Window window, float interval, MouseInput mouseInput) {
        cancerCode();
        collisionTesting();
        movePlayer();
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

        GameItem gameItem0 = new GameItem(kek);
        gameItem0.setPos(0, 0, -6);
        gameItem0.setScale(.5f);

        GameItem gameItem1 = new GameItem(cube);
        gameItem1.setPos(0, 0, .6f);
        gameItem1.setScale(scale);

        GameItem gameItem2 = new GameItem(cube);
        gameItem2.setPos(0f, 0, 0);
        gameItem2.setScale(scale);

        GameItem gameItem3 = new GameItem(cube);
        gameItem3.setPos(0.6f, 0, 0);
        gameItem3.setScale(scale);

        GameItem gameItem4 = new GameItem(cube);
        gameItem4.setPos(2f, 0, 2f);
        gameItem4.setScale(scale);

        GameItem gameItem5 = new GameItem(cube);
        gameItem5.setPos(.6f, 0, .6f);
        gameItem5.setScale(scale);


        Boid b = new Boid(boid);
        b.setPos(-2, 0, 0);
        b.setScale(.1f);

        gameItems = new ArrayList<>(Arrays.asList(new GameItem[]{gameItem0, gameItem1, gameItem2, gameItem3, gameItem4, gameItem5, b}));
    }

    private void initLighting() {
        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

        // Point Light
        Vector3f lightColor = new Vector3f(1, 0.5f, 0);
        Vector3f lightPos = new Vector3f(0, 1, -2);
        float lightIntensity = 1.0f;
        PointLight pointLight = new PointLight(lightColor, lightPos, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0, 0, 0.1f);
        pointLight.setAttenuation(att);
        pointLightList = new PointLight[] {pointLight};

        // Spot Light
        lightColor = new Vector3f(1, 0, 1);
        lightPos = new Vector3f(0, 0, -2);
        lightIntensity = 1.0f;
        pointLight = new PointLight(lightColor, lightPos, lightIntensity);
        att = new PointLight.Attenuation(0, 0, 0.1f);
        pointLight.setAttenuation(att);
        Vector3f coneDir = new Vector3f(0, 0, -1);
        SpotLight spotLight = new SpotLight(pointLight, coneDir, 140);
        spotLightList = new SpotLight[] {spotLight};

        lightPos = new Vector3f(-1, 0, 0);
        lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPos, lightIntensity);
    }

    private void initCollisions() {
        for (Iterator<GameItem> iterator = gameItems.iterator(); iterator.hasNext();) {
            GameItem gameItem = iterator.next();
            gameItem.getBoundingBox().transform(gameItem.getPos());// converts bb coords from local to world
            try {
                sweepPrune.addItem(gameItem);
            } catch (Exception e){
                System.out.println("object colliding");;
                //iterator.remove();
            }
        }
    }

    private void moveCamera(Window window, MouseInput mouseInput) {
        if (!menu && glfwGetWindowAttrib(window.getWindowHandle(), GLFW_FOCUSED) == 1) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRot(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

            glfwSetCursorPos(window.getWindowHandle(), window.getCenter().x, window.getCenter().y);
        }
    }

    private void movePlayer() {
        camera.movePos(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
    }

    private void cancerCode() {
        /*spotAngle += spotInc * 0.05f;
        if (spotAngle > 2) spotInc = -1;
        else if (spotAngle < -2) spotInc = 1;
        double spotAngleRad = Math.toRadians(spotAngle);
        Vector3f coneDir = spotLightList[0].getConeDirection();
        coneDir.y = (float) Math.sin(spotAngleRad);*/

        //update directional light -------------------------------------------------------------------------------------

        //number of seconds for the sun do to one full rotation
        float fullCycleSeconds = 600f;

        lightAngle += 1f/30f * (360/fullCycleSeconds);
        if (lightAngle > 90) {
            directionalLight.setIntensity(1);
            if (lightAngle >= 270) {
                lightAngle = -90;
            }
        }
        else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float)(Math.abs(lightAngle) - 80)/10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        }
        else {
            directionalLight.setIntensity(1);
            directionalLight.setColor(new Vector3f(1, 1, 1));
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float)Math.sin(angRad);
        directionalLight.getDirection().y = (float)Math.cos(angRad);
        // end directional light ---------------------------------------------------------------------------------------
    }

    private void collisionTesting() {
        for(GameItem gameItem : gameItems){
            if(gameItem.accel.length() != 0){ //game item has acceleration
                //check for collisions wip

                Vector3f nextPos = new Vector3f(0,0,0); //move these 3lines to somewhere else in game logic movement calc (?)
                nextPos.add(gameItem.getPos());
                nextPos.add(gameItem.accel.mul(0.1f));

                if(sweepPrune.updateItem(gameItem, nextPos) > 0){
                    //for no collisions
                    //gameItem.setPos(nextPos);
                    //gameItem.accel.zero();
                    //for collision
                    gameItem.accel.zero();
                } else {
                    //perform movement
                    gameItem.setPos(nextPos);
                    gameItem.accel.zero();
                }
            }
        }
    }

    @Override
    public void render(Window window, MouseInput mouseInput) {
        renderer.render(window, mouseInput, camera, gameItems, ambientLight, pointLightList, spotLightList, directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for(GameItem item : gameItems) item.getMesh().cleanUp();
    }
}

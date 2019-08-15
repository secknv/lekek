package net.sknv.game;

import net.sknv.engine.GameItem;
import net.sknv.engine.IGameLogic;
import net.sknv.engine.MouseInput;
import net.sknv.engine.Window;
import net.sknv.engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.glfw.GLFW.*;

public class UltimateKekGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.8f;
    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraInc;

    private final Renderer renderer;
    private final Camera camera;

    private GameItem[] gameItems;

    private Vector3f ambientLight;
    private PointLight pointLight;
    private DirectionalLight directionalLight;
    private float lightAngle;

    public UltimateKekGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        float reflectance = 1f;

        Mesh cube = OBJLoader.loadMesh("/models/cube.obj");
        Mesh kek = OBJLoader.loadMesh("/models/untitled.obj");

        Texture texture = new Texture("src/main/resources/textures/lebloq.png");
        Material material = new Material(texture, reflectance);
        cube.setMaterial(material);
        kek.setMaterial(new Material(new Vector4f(1f, 0, 0,1f), 0.5f));

        float scale = 0.2f;

        GameItem gameItem0 = new GameItem(kek);
        gameItem0.setPos(0, 0, -5);
        gameItem0.setScale(0.005f);

        GameItem gameItem1 = new GameItem(cube);
        gameItem1.setPos(0, 0, -2);
        gameItem1.setScale(scale);

        GameItem gameItem2 = new GameItem(cube);
        gameItem2.setPos(0.5f, 0.5f, -2);
        gameItem2.setScale(scale);

        GameItem gameItem3 = new GameItem(cube);
        gameItem3.setPos(0, 0, -2.5f);
        gameItem3.setScale(scale);

        GameItem gameItem4 = new GameItem(cube);
        gameItem4.setPos(0.5f, 0, -2.5f);
        gameItem4.setScale(scale);

        gameItems = new GameItem[] {gameItem0, gameItem1, gameItem2, gameItem3, gameItem4};

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        Vector3f lightPos = new Vector3f(0, 0, 1);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColor, lightPos, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0, 0, 0.1f);
        pointLight.setAttenuation(att);

        lightPos = new Vector3f(-1, 0, 0);
        lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPos, lightIntensity);
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

        float lightPos = pointLight.getPos().x;
        if (window.isKeyPressed(GLFW_KEY_N)) {
            this.pointLight.getPos().x = lightPos - 0.1f;
        }
        else if (window.isKeyPressed(GLFW_KEY_M)) {
            this.pointLight.getPos().x = lightPos + 0.1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

        camera.movePos(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        if (mouseInput.isRightClicked()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRot(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        //number of seconds for the sun do to one full rotation
        float fullCycleSeconds = 600f;

        lightAngle += 1f/30f * (360/fullCycleSeconds);
        System.out.println("sun angle: " + lightAngle + "°");
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
    }

    @Override
    public void render(Window window, MouseInput mouseInput) {
        renderer.render(window, mouseInput, camera, gameItems, ambientLight, pointLight, directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for(GameItem item : gameItems) item.getMesh().cleanup();
    }
}

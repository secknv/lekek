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
import static org.lwjgl.opengl.GL11.glClearColor;

public class UltimateKekGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.8f;
    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraInc;

    private final Renderer renderer;
    private final Camera camera;

    private GameItem[] gameItems;

    private Vector3f ambientLight;
    private PointLight pointLight;

    public UltimateKekGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
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
        gameItem0.setScale(1f);

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
            this.pointLight.getPos().x = lightPos + 0.1f;
        }
        else if (window.isKeyPressed(GLFW_KEY_M)) {
            this.pointLight.getPos().x = lightPos - 0.1f;
        }
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
        renderer.render(window, camera, gameItems, ambientLight, pointLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for(GameItem item : gameItems) item.getMesh().cleanup();
    }
}

package net.sknv.engine;

import net.sknv.engine.entities.AbstractGameItem;
import net.sknv.engine.entities.Collider;
import net.sknv.engine.entities.Terrain;
import net.sknv.engine.graph.*;
import net.sknv.engine.physics.colliders.OBB;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * This class represents a level.<br>
 * It's responsible for managing:
 * <ul>
 *     <li>{@link Terrain}</li>
 *     <li>Game Items ({@link AbstractGameItem})</li>
 *     <li>{@link SkyBox}</li>
 *     <li>Lighting ({@link SceneLight})</li>
 * </ul>
 */
public class Scene implements Serializable {

    private final static Logger logger = Logger.getLogger(Scene.class.getName());

    private transient Terrain terrain;
    private ArrayList<AbstractGameItem> gameItems = new ArrayList<>();
    private ArrayList<Collider> colliders = new ArrayList<>();
    private SkyBox skyBox;
    private SceneLight sceneLight;

    private Vector3f gravity;

    public Scene() throws Exception {
        logger.info("Initializing default scene.");
        initializeScene();
    }

    public void save(String sceneName){
        try {
            FileOutputStream fileOut = new FileOutputStream("src/main/resources/scenes/" + sceneName + ".ser");
            ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
            outStream.writeObject(this);
            outStream.flush();
            outStream.close();
            fileOut.flush();
            fileOut.close();
            logger.info("Scene file saved - " + sceneName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        setupTerrain();
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.defaultWriteObject();
    }

    public void initializeScene() {

        setupTerrain();
        setupGameItems();
        setupSkyBox();
        setupLighting();

        setGravity(new Vector3f(0,-1f,0));
    }

    private void setupTerrain() {
        float terrainScale = 100;
        int terrainSize = 1;
        float minY = -0.1f;
        float maxY = 0.1f;
        int textInc = 40;
        try {
            terrain = new Terrain(terrainSize, terrainScale, minY, maxY,
                    "src/main/resources/textures/heightmap.png",
                    "src/main/resources/textures/terrain.png", textInc);
        }
        catch (Exception e) {
            logger.severe("Failed to load Terrain files.");
            e.printStackTrace();
        }
    }
    private void setupGameItems() {
        float reflectance = 1f;
        // todo: make try include just the file loading
        try {
            Mesh cubeMesh = OBJLoader.loadMesh("/models/cube.obj");
            Texture blockTexture = new Texture("src/main/resources/textures/lebloq.png");
            cubeMesh.setMaterial(new Material(blockTexture, reflectance));
            Collider block = new Collider(cubeMesh);

            // Setup kek item
            Mesh kekMesh = OBJLoader.loadMesh("/models/untitled.obj");
            kekMesh.setMaterial(new Material(new Vector4f(1f, 0, 0,1f), 0.5f));
            Collider kekItem = new Collider(kekMesh);
            kekItem.setPosition(2, 1, 2);
            // kekItem BB
            OBB testBox = new OBB(kekItem);
            kekItem.setBoundingBox(testBox);

            addColliders(Arrays.asList(kekItem, block));
            addAllGameItems(colliders);
        }
        catch (Exception e) {
            logger.severe("Failed to load GameItem files!");
            e.printStackTrace();
        }
    }

    private void setupSkyBox() {
        float skyBoxScale = 200.0f;
        // todo: make try include just the file loading
        try {
            SkyBox skyBox = new SkyBox("/models/skybox.obj", "src/main/resources/textures/skybox.png");
            skyBox.setScale(skyBoxScale);
            setSkyBox(skyBox);
        } catch (Exception e) {
            logger.severe("Failed to load SkyBox files!");
            e.printStackTrace();
        }

    }
    private void setupLighting() {
        SceneLight sceneLight = new SceneLight();
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));

        float lightIntensity = 1.0f;
        Vector3f lightPos = new Vector3f(-1, 0, 0);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        sceneLight.setDirectionalLight(new DirectionalLight(lightColor, lightPos, lightIntensity));

        setSceneLight(sceneLight);
    }

    public void addGameItem(AbstractGameItem item) {
        gameItems.add(item);
    }
    public void addAllGameItems(Collection<? extends AbstractGameItem> items) {
        gameItems.addAll(items);
    }
    private void addColliders(Collection<? extends Collider> items) {
        colliders.addAll(items);
    }
    public void removeItem(AbstractGameItem item) {
        gameItems.remove(item);
    }
    public void removeAllItems() {
        gameItems.clear();
    }
    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public ArrayList<AbstractGameItem> getGameItems() {
        return gameItems;
    }

    public void setGameItems(ArrayList<AbstractGameItem> gameItems) {
        this.gameItems = gameItems;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void setGravity(Vector3f gravity) {
        this.gravity = gravity;
    }

    public ArrayList<Collider> getColliders() {
        return colliders;
    }
}

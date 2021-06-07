package net.sknv.engine;

import net.sknv.engine.entities.AbstractGameItem;
import net.sknv.engine.entities.Collider;
import net.sknv.engine.graph.*;
import net.sknv.engine.physics.colliders.OBB;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Scene implements Serializable {

    private ArrayList<AbstractGameItem> gameItems;
    private SkyBox skyBox;
    private SceneLight sceneLight;
    private Vector3f gravity;

    public Scene(String scene) throws Exception {
        if (scene.equals("default")) initializeScene();
        else {
            System.out.println("initializing serialized scene");
            Scene dScene = load(scene);
            setGameItems(dScene.getGameItems());
            setSkyBox(dScene.getSkyBox());
            setSceneLight(dScene.getSceneLight());
            setGravity(dScene.getGravity());
        }
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void setGravity(Vector3f g) {
        this.gravity = g;
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

    public void save(String sceneName){
        try {
            FileOutputStream fileOut = new FileOutputStream("src/main/resources/scenes/" + sceneName + ".ser");
            ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
            outStream.writeObject(this);
            outStream.flush();
            outStream.close();
            fileOut.flush();
            fileOut.close();
            System.out.println("Scene file saved - " + sceneName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Scene load(String sceneName) throws IOException, ClassNotFoundException {
        return (Scene) (new ObjectInputStream(new FileInputStream("src/main/resources/scenes/" + sceneName + ".ser")).readObject());
    }

    public void initializeScene() throws Exception {
        //Setup model meshes and materials
        float reflectance = 1f;
        Mesh cube = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("src/main/resources/textures/lebloq.png");
        Material material = new Material(texture, reflectance);
        cube.setMaterial(material);

        // Setup kek mesh
        Mesh kek = OBJLoader.loadMesh("/models/untitled.obj");
        kek.setMaterial(new Material(new Vector4f(1f, 0, 0,1f), 0.5f));

        float blockScale = 0.5f;
        float skyBoxScale = 20.0f;

        //init gameItems
        //background game items


        float startx = 0;
        float startz = 0;
        float starty = -1.0f;
        float inc = blockScale * 2;

        float posx = startx;
        float posz = startz;
        float incy = 0.0f;
        int NUM_ROWS = 10;
        int NUM_COLS = 10;
        ArrayList<AbstractGameItem> blocks  = new ArrayList<>(NUM_ROWS * NUM_COLS + 10);
        for(int i=0; i<NUM_ROWS; i++) {
            for(int j=0; j<NUM_COLS; j++) {
                Collider grass = new Collider(cube);
                grass.setScale(blockScale);
                incy = Math.random() > 0.9f ? blockScale * 2 : 0f;
                grass.setPosition(posx, starty + incy, posz);
                blocks.add(grass);

                posx += inc;
            }
            posx = startx;
            posz -= inc;
        }

        // Special game items
        Collider testItem = new Collider(kek);
        testItem.setPosition(2f, 1, 2f);

        OBB testBox = new OBB(testItem);
        testItem.setBoundingBox(testBox);
        testItem.setScale(blockScale);

        // add special items to gameItems array
        // this is here case more than one special items...
        // easier to just add {testItem, myOtherItem, kekItem}
        Collider[] specialItems = {testItem};
        blocks.addAll(Arrays.asList(specialItems));
        setGameItems(blocks);

        // Setup SkyBox
        // todo: standardize resource paths
        try {
            SkyBox skyBox = new SkyBox("/models/skybox.obj", "src/main/resources/textures/skybox.png");
            skyBox.setScale(skyBoxScale);
            setSkyBox(skyBox);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //initLighting
        SceneLight sceneLight = new SceneLight();
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));

        float lightIntensity = 1.0f;
        Vector3f lightPos = new Vector3f(-1, 0, 0);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        sceneLight.setDirectionalLight(new DirectionalLight(lightColor, lightPos, lightIntensity));

        setSceneLight(sceneLight);
        setGravity(new Vector3f(0,-1f,0));
    }

    public void addGameItem(AbstractGameItem gameItem) {
        ArrayList<AbstractGameItem> items = this.getGameItems();
        items.add(gameItem);
        this.setGameItems(items);
    }
}

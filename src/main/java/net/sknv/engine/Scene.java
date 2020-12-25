package net.sknv.engine;

import net.sknv.engine.graph.*;
import net.sknv.engine.physics.colliders.OBB;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Scene implements Serializable {

    private ArrayList<GameItem> gameItems;
    private SkyBox skyBox;
    private SceneLight sceneLight;
    private Vector3f gravity;

    public Scene(String scene) throws Exception {
        switch (scene){
            case "SERIALIZED":
                System.out.println("initializing serialized scene");
                Scene dScene = (Scene) (new ObjectInputStream(new FileInputStream("src/main/resources/scenes/empty.ser")).readObject());
                this.gameItems = dScene.gameItems;
                this.skyBox = dScene.skyBox;
                this.sceneLight = dScene.sceneLight;
                break;
            case "SCENE1":
                initializeScene();
                break;
        }
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void setGravity(Vector3f g) {
        this.gravity = g;
    }

    public ArrayList<GameItem> getGameItems() {
        return gameItems;
    }

    public void setGameItems(ArrayList<GameItem> gameItems) {
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

    public void load(String sceneName) throws IOException, ClassNotFoundException {
        Scene dScene = (Scene) (new ObjectInputStream(new FileInputStream("src/main/resources/scenes/" + sceneName + ".ser")).readObject());
        this.gameItems = dScene.gameItems;
        this.skyBox = dScene.skyBox;
        this.sceneLight = dScene.sceneLight;
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

        //init gameItems
        //background game items
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

        OBB testBox = new OBB(testItem, testItem.getMesh().getMin(), testItem.getMesh().getMax());
        testItem.setBoundingBox(testBox);

        // add special items to gameItems array
        // this is here case more than one special items...
        // easier to just add {testItem, myOtherItem, kekItem}
        GameItem[] specialItems = {testItem};
        gameItems.addAll(Arrays.asList(specialItems));
        setGameItems(gameItems);

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
}

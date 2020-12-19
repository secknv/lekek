package net.sknv.engine;

import net.sknv.engine.graph.Material;
import net.sknv.engine.graph.Mesh;
import net.sknv.engine.graph.OBJLoader;
import net.sknv.engine.graph.Texture;

public class SkyBox extends GameItem {

    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxTexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0));
        setMesh(skyBoxMesh);
        setPos(0, 0, 0);
    }
}

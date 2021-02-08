package net.sknv.engine;

import net.sknv.engine.entities.GameItemMesh;
import net.sknv.engine.graph.Material;
import net.sknv.engine.graph.Mesh;
import net.sknv.engine.graph.OBJLoader;
import net.sknv.engine.graph.Texture;

public class SkyBox extends GameItemMesh {

    public SkyBox(String objModel, String textureFile) throws Exception {
        //todo problem: SKYBOX NOT WORKING WHEN SERIALIZED
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxTexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }
}

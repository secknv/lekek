package net.sknv.engine.graph;

import net.sknv.engine.GameItem;

public class TextItem extends GameItem {

    private static final float ZPOS = 0.0f;
    private static final int VERTICES_PER_QUAD = 4;
    private String text;

    private final int numCols, numRows;

    public TextItem(String text, String fontFileName, int numCols, int numRows) throws Exception {
        super();
        this.text = text;
        this.numCols = numCols;
        this.numRows = numRows;
        Texture texture = new Texture(fontFileName);
        this.setMesh(buildMesh(texture, numCols, numRows));
    }

    private Mesh buildMesh(Texture texture, int numCols, int numRows) {
        //todo
    }
}

package net.sknv.engine.graph;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private final int id;

    public Texture(String filename) throws Exception{
        this(loadTexture(filename));
    }

    public Texture(int id) {
        this.id = id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    private static int loadTexture(String filename) throws Exception {
        int width, height;

        ByteBuffer buf;

        //load texture file
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load(filename, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file [" + filename + "] not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        //create & bind openGL texture
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        //each pixel in image has RGB and Alpha values. Tell openGL each value is 1byte
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(buf);

        return textureId;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}

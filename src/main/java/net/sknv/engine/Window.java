package net.sknv.engine;

public class Window {

    private final String title;

    private int width, height;

    private long windowHandle;

    private boolean resized, vsync;

    public Window(String title, int width, int height, boolean vsync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vsync = vsync;
        this.resized = false;
    }

    public void init() {

    }
}

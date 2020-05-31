package net.sknv.engine;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 60;

    private final Window window;
    private final Timer timer;
    private final IGameLogic gameLogic;
    private final MouseInput mouseInput;

    public GameEngine(String windowTitle, int width, int height, boolean vsync, IGameLogic gameLogic) throws Exception {
        window = new Window(windowTitle, width, height, vsync);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            cleanup();
        }
    }

    protected void init() throws Exception{
        window.init();
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window, mouseInput);
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;

        int count_fps = 0, count_ups = 0;
        double tst = 0, last = 0;

        while (running && !window.windowShouldClose()) {
            tst = timer.getTime();
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;
            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
                count_ups++;
            }

            render();

            if (!window.isVsync()) {
                sync();
            }
            count_fps++;

            if (tst - last >= 1) {
                window.setTitle("UPS: " + count_ups + " | FPS: " + count_fps);
                count_fps = 0;
                count_ups = 0;
                last = tst;
            }
        }
    }

    protected void cleanup() {
        gameLogic.cleanup();
    }

    private void sync() {
        float loopSlot = 1f/TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException ie) {}
        }
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    protected void update(float interval) {
        gameLogic.update(window, mouseInput, interval);
    }

    protected void render() {
        gameLogic.render(window, mouseInput);
        window.update();
    }
}

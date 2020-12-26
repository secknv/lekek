package net.sknv.engine;

import net.sknv.game.Terminal;
import net.sknv.game.UltimateKekGame;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 60;

    private final Window window;
    private final Timer timer;
    private final UltimateKekGame gameLogic;
    private final MouseInput mouseInput;
    private Terminal terminal;

    public GameEngine(String windowTitle, int width, int height, boolean vsync, IGameLogic gameLogic, Terminal terminal) {
        this.window = new Window(windowTitle, width, height, vsync);
        this.gameLogic = (UltimateKekGame) gameLogic;
        this.mouseInput = new MouseInput();
        this.timer = new Timer();
        this.terminal = terminal;
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

        int count_fps = 0, count_ups = 0;
        double tst, last = 0;

        while (!window.windowShouldClose()) {
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
            catch (InterruptedException ignored) {}
        }
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
        processTerminal(terminal.getInput());
    }

    private void processTerminal(String input) {
        if (input==null) return;

        String[] in = input.split(" ");

        switch (in[0]){
            case "test":
                System.out.println("its working :)");
                break;
            case "savescene":
                String sceneName;
                if(in.length>1) sceneName = in[1]; else sceneName = "unnamed";
                gameLogic.getScene().save(sceneName);
                break;
            case "loadscene":
                if(in.length>1) sceneName = in[1]; else return;
                System.out.println("loading scene - " + sceneName);
                gameLogic.initScene(sceneName);
                gameLogic.initPhysicsEngine();
                System.out.println("scene loaded");
                break;
            case "quit":
                System.exit(0);
                break;
            default:
                break;
        }
    }

    protected void update(float interval) {
        gameLogic.update(window, mouseInput, interval);
    }

    protected void render() {
        gameLogic.render(window, mouseInput);
        window.update();
    }

    public IGameLogic getGameLogic(){
        return gameLogic;
    }
}

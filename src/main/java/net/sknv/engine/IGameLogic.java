package net.sknv.engine;

public interface IGameLogic {

    void init(Window window) throws Exception;
    void input(Window window, MouseInput mouseInput);
    void update(Window window, MouseInput mouseInput, float interval);
    void render(Window window);
    void cleanup();
}

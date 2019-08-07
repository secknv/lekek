package net.sknv.engine;

public interface IGameLogic {

    void init() throws Exception;
    void input(Window window);
    void update(Window window);
    void render(Window window);
    void cleanup();
}

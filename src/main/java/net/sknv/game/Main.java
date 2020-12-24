package net.sknv.game;

import net.sknv.engine.GameEngine;
import net.sknv.engine.IGameLogic;

public class Main {

    public static void main(String[] args) {
        try {
            boolean vsync = true;
            IGameLogic gameLogic = new UltimateKekGame();
            GameEngine gameEngine = new GameEngine("Ultimate Kek Game", 600, 480, vsync, gameLogic);

            Terminal t = new Terminal(gameEngine);
            t.setDaemon(true);
            t.start();

            gameEngine.run();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

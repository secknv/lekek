package net.sknv.game;

import net.sknv.engine.GameEngine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class CommandExecutor {
    private GameEngine gameEngine;
    private UltimateKekGame gameLogic;

    public CommandExecutor(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.gameLogic = (UltimateKekGame) gameEngine.getGameLogic();
    }

    public void execute(String in) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        String[] input = in.split(" ");

        switch (input[0]){
            case "test":
                System.out.println("memeronis");
                break;
            case "savescene":
                String sceneName;
                if(input.length>1) sceneName = input[1]; else sceneName = "unnamed";
                gameLogic.getScene().save(sceneName);
                break;
            case "loadscene":
                if(input.length>1) sceneName = input[1]; else return;
                gameLogic.getScene().load(sceneName);
                break;
            case "quit":
                System.exit(0);
                break;
            default:
                break;
        }
    }

}

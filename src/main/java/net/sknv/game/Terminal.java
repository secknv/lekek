package net.sknv.game;

import net.sknv.engine.GameEngine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class Terminal extends Thread{

    private CommandExecutor commandExecutor;

    public Terminal(GameEngine gameEngine){
        commandExecutor = new CommandExecutor(gameEngine);
    }

    public void run(){
        Scanner s = new Scanner(System.in);
        String in = null;

        while (true) {
            in = s.nextLine();
            try {
                commandExecutor.execute(in);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

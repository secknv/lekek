package net.sknv.engine;

import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d previousPos, currentPos;

    //will hold mouse displacement vector
    private final Vector2f displVec;

    private boolean inWindow = false;
    private boolean leftClicked = false;
    private boolean rightClicked = false;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init(Window window) {
        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });

        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
            inWindow = entered;
        });

        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            leftClicked = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightClicked = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public void input(Window window) {
        displVec.x = 0;
        displVec.y = 0;

        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotatex = deltax != 0;
            boolean rotatey = deltay != 0;

            if (rotatex) {
                displVec.y = (float) deltax;
            }
            if (rotatey) {
                displVec.x = (float) deltay;
            }

            previousPos.x = currentPos.x;
            previousPos.y = currentPos.y;
        }
    }

    public boolean isLeftClicked() {
        return leftClicked;
    }

    public boolean isRightClicked() {
        return rightClicked;
    }
}

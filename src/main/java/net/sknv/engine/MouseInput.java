package net.sknv.engine;

import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d previousPos, currentPos;

    //will hold mouse displacement vector
    private final Vector2f displVec;

    private boolean inWindow = false;
    private boolean leftClicked = false;
    private boolean rightClicked = false;
    // Flags when mouse is in GLFW_CURSOR_DISABLED mode.
    private boolean disabled = true;

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

        glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public void input(Window window) {
        displVec.x = 0;
        displVec.y = 0;
        if (inWindow) {
            double deltaX = currentPos.x - previousPos.x;
            double deltaY = currentPos.y - previousPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;

            if (rotateX) {
                displVec.y = (float) deltaX;
            }
            if (rotateY) {
                displVec.x = (float) deltaY;
            }
        }

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public boolean isLeftClicked() {
        return leftClicked;
    }

    public boolean isRightClicked() {
        return rightClicked;
    }

    public Vector2d getPos() {
        return currentPos;
    }

    public Vector2d getPrevPos() {
        return previousPos;
    }

    public void setDisabled() {
        this.disabled = true;
    }
    public void setEnabled() {
        this.disabled = false;
    }

    public Vector3f getWorldRay(Window window, Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        //convert from viewport to normalised device space
        double posx;
        double posy;

        if (this.disabled) {
            posx = window.getCenter().x;
            posy = window.getCenter().y;
        }
        else {
            posx = getPos().x;
            posy = getPos().y;
        }
        Vector4f ray_clip = new Vector4f((float)(2.0f * posx) / Window.getWidth() - 1.0f,
                (float)(1f - (2.0f * posy) / Window.getHeight()), -1.0f, 1.0f);

        //convert from normalised device space to eye space
        Matrix4f invertedProjection = new Matrix4f();
        projectionMatrix.invert(invertedProjection);
        invertedProjection.transform(ray_clip);
        Vector4f ray_eye = new Vector4f(ray_clip.x,ray_clip.y, -1f, 0f);

        //convert from eye space to world space
        Matrix4f invertedViewMatrix = new Matrix4f();
        viewMatrix.invert(invertedViewMatrix);
        invertedViewMatrix.transform(ray_eye);
        Vector3f ray_world = new Vector3f(ray_eye.x,ray_eye.y, ray_eye.z); //y inverted idk why
        ray_world.normalize();

        return ray_world;
    }
}

package Amalgam;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.opengl.GL11C.glViewport;

public class Callbacks {
    private static boolean firstIteration = true;
    private static boolean[] keysDown = new boolean[350], keysDownThisFrame = new boolean[350];
    private static boolean[] mouseButtonsDown = new boolean[10], mouseButtonsDownThisFrame = new boolean[10];
    private static double mouseX, mouseY, lastMouseX, lastMouseY, mouseDeltaX, mouseDeltaY;
    private static double scrollX, scrollY;
    private static int frameWidth;
    private static int frameHeight;
    public static void keyCallback(long windowID, int keyCode, int scanCode, int action, int mods) {
        if(action == GLFW_PRESS) {
            keysDown[keyCode] = true;
            keysDownThisFrame[keyCode] = true;
        }else if(action == GLFW_RELEASE) {
            keysDown[keyCode] = false;
        }
    }

    public static void mouseButtonCallback(long windowID, int button, int action, int mods) {
        if(action == GLFW_PRESS) {
            mouseButtonsDown[button] = true;
            mouseButtonsDownThisFrame[button] = true;
        }else if(action == GLFW_RELEASE) {
            mouseButtonsDown[button] = false;
        }
    }

    public static void cursorPosCallback(long windowID, double xPos, double yPos) {
        mouseX = xPos;
        mouseY = yPos;
        if(firstIteration) {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            firstIteration = false;
        }
        mouseDeltaX = mouseX - lastMouseX;
        mouseDeltaY = mouseY - lastMouseY;
    }

    public static void scrollCallback(long windowID, double xOffset, double yOffset) {
        scrollX = xOffset;
        scrollY = yOffset;
    }
    public static void frameEnd() {
        Arrays.fill(keysDownThisFrame, false);
        Arrays.fill(mouseButtonsDownThisFrame, false);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        mouseDeltaX = 0;
        mouseDeltaY = 0;
        scrollX = 0;
        scrollY = 0;
    }
    public static boolean isKeyDownThisFrame(int keyCode) {
        return(keysDownThisFrame[keyCode]);
    }
    public static boolean isKeyDown(int keyCode) {
        return(keysDown[keyCode]);
    }
    public static boolean isMouseButtonDownThisFrame(int mouseButtonCode) {
        return(mouseButtonsDownThisFrame[mouseButtonCode]);
    }
    public static boolean isMouseButtonDown(int mouseButtonCode) {
        return(mouseButtonsDown[mouseButtonCode]);
    }
    public static double getMouseX() {
        return(mouseX);
    }
    public static double getMouseY() {
        return(mouseY);
    }
    public static double getMouseDeltaX() {
        return(mouseDeltaX);
    }
    public static double getMouseDeltaY() {
        return(mouseDeltaY);
    }
    public static double getScrollDeltaX() {
        return(scrollX);
    }
    public static double getScrollDeltaY() {
        return(scrollY);
    }
    public static int getFrameWidth() {
        return(frameWidth);
    }
    public static int getFrameHeight() {
        return(frameHeight);
    }

    public static void frameBufferSizeCallback(long windowID, int width, int height) {
        glViewport(0, 0, width, height);
        frameWidth = width;
        frameHeight = height;
    }
}

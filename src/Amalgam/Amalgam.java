package Amalgam;

import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class Amalgam{
    private static long windowID;
    private static boolean cursorVisible = false, fullscreen = false;
    private static int toggleCursorKeyCode = GLFW_KEY_F1, toggleFullscreenKeyCode = GLFW_KEY_F11;
    private static String windowTitle = "Amalgam";
    private static final Vector4f windowBackgroundColor = new Vector4f(0f, 0f, 0f, 1.0f);
    private static double frameStartTime, frameEndTime, deltaTime = 0;
    public static void main(String[] args) {
        new Amalgam();
    }
    private Amalgam(){
        init();
        loop();
        close();
    }
    private void init(){
        System.out.println("LWJGL Version: " + Version.getVersion());
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit()) {
            throw new IllegalStateException("Could not initialize glfw.");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        windowID = glfwCreateWindow(512, 512, windowTitle, MemoryUtil.NULL, MemoryUtil.NULL);
        if(windowID == MemoryUtil.NULL) {
            throw new IllegalStateException("Could not create glfw window.");
        }
        setWindowIcon(windowID);
        glfwMakeContextCurrent(windowID);
        GL.createCapabilities();
        setupInput();
        setFullscreen(fullscreen);
        glfwSwapInterval(1);
        glfwShowWindow(windowID);
        SceneManager.init();
    }
    private static void setWindowIcon(long window) {
        // Load the image (assuming "icon.png" is in the project directory)
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // Load the image using STBImage
            ByteBuffer imageBuffer = STBImage.stbi_load("assets/icons/icon.png", width, height, channels, 4);
            if (imageBuffer == null) {
                throw new IOException("Failed to load image.");
            }

            // Create a GLFW image from the loaded data
            GLFWImage.Buffer icon = GLFWImage.malloc(1);
            GLFWImage img = icon.get(0);
            img.set(width.get(0), height.get(0), imageBuffer);

            // Set the window icon
            org.lwjgl.glfw.GLFW.glfwSetWindowIcon(window, icon);

            // Free the image buffer after setting the icon
            STBImage.stbi_image_free(imageBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setFullscreen(boolean fullscreen) {
        long currentMonitor = glfwGetWindowMonitor(windowID);
        if(currentMonitor == MemoryUtil.NULL) {
            currentMonitor = glfwGetPrimaryMonitor();
        }
        GLFWVidMode vidMode = glfwGetVideoMode(currentMonitor);
        if(fullscreen) {
            glfwSetWindowMonitor(windowID, currentMonitor, 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate());
            Amalgam.fullscreen = true;
        }else {
            int width = (int)(vidMode.width()*0.7f), height = (int)(vidMode.height()*0.7f);
            glfwSetWindowMonitor(windowID, MemoryUtil.NULL, (vidMode.width() - width)/2, (vidMode.height() - height)/2, width, height, vidMode.refreshRate());
            Amalgam.fullscreen = false;
        }
    }
    private void setupInput() {
        setCursorVisible(cursorVisible);
        glfwSetKeyCallback(windowID, Callbacks::keyCallback);
        glfwSetMouseButtonCallback(windowID, Callbacks::mouseButtonCallback);
        glfwSetCursorPosCallback(windowID, Callbacks::cursorPosCallback);
        glfwSetScrollCallback(windowID, Callbacks::scrollCallback);
        glfwSetFramebufferSizeCallback(windowID, Callbacks::frameBufferSizeCallback);
    }
    private void setCursorVisible(boolean visible) {
        if(visible) {
            glfwSetInputMode(windowID, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            glfwSetInputMode(windowID, GLFW_RAW_MOUSE_MOTION, GLFW_FALSE);
            cursorVisible = true;

        }else {
            glfwSetInputMode(windowID, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            if (glfwRawMouseMotionSupported()) {
                glfwSetInputMode(windowID, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
            }
            cursorVisible = false;
        }
    }
    private void loop() {
        frameStartTime = glfwGetTime();
        while(!glfwWindowShouldClose(windowID)) {
            frameStart();
            SceneManager.update(deltaTime);
            frameEnd();
        }
    }
    private void frameStart() {
        glClearColor(windowBackgroundColor.x, windowBackgroundColor.y, windowBackgroundColor.z, windowBackgroundColor.w);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glfwPollEvents();
        if(Callbacks.isKeyDownThisFrame(toggleCursorKeyCode)) {
            setCursorVisible(!cursorVisible);
        }
        if(Callbacks.isKeyDownThisFrame(toggleFullscreenKeyCode)) {
            setFullscreen(!fullscreen);
        }
        glfwSetWindowTitle(windowID, String.valueOf(Amalgam.windowTitle + String.format(" FPS: %.3f", 1/deltaTime)));
    }
    private void frameEnd() {
        glfwSwapBuffers(windowID);
        Callbacks.frameEnd();
        frameEndTime = glfwGetTime();
        deltaTime = frameEndTime - frameStartTime;
        frameStartTime = frameEndTime;
    }
    private void close() {
        glfwFreeCallbacks(windowID);
        glfwDestroyWindow(windowID);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
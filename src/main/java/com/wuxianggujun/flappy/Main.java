package com.wuxianggujun.flappy;

import com.wuxianggujun.flappy.input.Input;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main implements Runnable {

    private final int width = 1280;
    private final int height = 720;

    private Thread thread;
    private boolean running = false;

    private long window;

    public void start() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        running = true;
        thread = new Thread(this, "Game");
        thread.start();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("无法初始化GLFW库！");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE,GLFW_FALSE);//创建后窗口将保持隐藏状态
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);//窗口可调整大小

        window = glfwCreateWindow(width, height, "Flappy", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("无法创建窗口");
        }


        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        glfwSetKeyCallback(window,new Input());
        glfwMakeContextCurrent(window);
        glfwShowWindow(window);


        //获取当前OpenGL上下文的功能信息
        GL.createCapabilities();
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void run() {
        init();
        while (running) {
            update();
            render();

            if (glfwWindowShouldClose(window)) {
                running = false;
            }

        }

    }


    private void update() {
        glfwPollEvents();

    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        glfwSwapBuffers(window);
    }


    public static void main(String[] args) {
        new Main().start();
    }
}
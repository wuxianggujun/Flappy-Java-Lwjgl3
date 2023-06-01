package com.wuxianggujun.flappy;

import com.wuxianggujun.flappy.input.Input;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.*;

public class Main implements Runnable {

    private final int width = 1280;
    private final int height = 720;

    private Thread thread;
    private boolean running = false;

    private long window;

    public void start() {
        running = true;
        thread = new Thread(this, "Game");
        thread.start();
    }

    private void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("无法初始化GLFW库！");
        }

        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        window = glfwCreateWindow(width, height, "Flappy", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("无法创建窗口");
        }

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        glfwSetKeyCallback(window,new Input());
        glfwMakeContextCurrent(window);
        glfwShowWindow(window);


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
        glfwSwapBuffers(window);
    }


    public static void main(String[] args) {
        new Main().start();
    }
}
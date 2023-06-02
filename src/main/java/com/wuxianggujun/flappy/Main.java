package com.wuxianggujun.flappy;

import com.wuxianggujun.flappy.graphics.Shader;
import com.wuxianggujun.flappy.input.Input;
import com.wuxianggujun.flappy.level.Level;
import com.wuxianggujun.flappy.maths.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main implements Runnable {

    private final int width = 1280;
    private final int height = 720;

    private Thread thread;
    private boolean running = false;

    private long window;

    private Level level;

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
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);//创建后窗口将保持隐藏状态
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);//窗口可调整大小

        window = glfwCreateWindow(width, height, "Flappy", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("无法创建窗口");
        }


        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        glfwSetKeyCallback(window, new Input());
        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        //获取当前OpenGL上下文的功能信息
        GL.createCapabilities();


        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);
        Shader.loadAll();


        Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);
        Shader.BG.setUniformMat4f("pr_matrix", pr_matrix);
        Shader.BG.setUniform1i("tex", 1);


        Shader.BIRD.setUniformMat4f("pr_matrix", pr_matrix);
        Shader.BIRD.setUniform1i("tex", 1);


        level = new Level();
    }

    @Override
    public void run() {
        init();

        long lastTime = System.nanoTime();
        double delta = 0.0;
        double ns = 1000000000.0 / 60.0;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1.0) {
                update();
                updates++;
                delta--;
            }
            render();
            frames++;
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(updates + " ups, " + frames + " fps");
                updates = 0;
                frames = 0;
            }
            if (glfwWindowShouldClose(window))
                running = false;
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }


    private void update() {
        glfwPollEvents();
        level.update();
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        level.render();
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            System.out.println("Error = " + error);
        }
        glfwSwapBuffers(window);
    }


    public static void main(String[] args) {
        new Main().start();
    }
}
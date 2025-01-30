package Amalgam;

import java.nio.FloatBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33C.glVertexAttribDivisor;

public class Grid {
    private static Shader shader;
    private static int vao, vbo, ebo, ivbo;
    private static final float[] cubeVertices = {
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f
    };
    private static final int[] cubeIndices = {
            0, 1, 2,
            2, 3, 0,
            6, 5, 4,
            4, 7, 6,
            5, 1, 0,
            0, 4, 5,
            3, 2, 6,
            6, 7, 3,
            0, 3, 7,
            7, 4, 0,
            6, 2, 1,
            1, 5, 6
    };
    private static int count = 64;
    private static float[][][] grid = new float[count][count][count];
    private static int instanceCount = count * count * count;
    public static void init() {

        FloatBuffer instanceData = FloatBuffer.allocate(instanceCount * 9);
        Random random = new Random();
        PerlinNoise noise = new PerlinNoise(random.nextLong());
        for(int x = 0; x < count; x++) {
            for(int y = 0; y < count; y++) {
                for(int z = 0; z < count; z++) {
                    instanceData.put(x);
                    instanceData.put(y);
                    instanceData.put(z);
                    float value = (float)(noise.noise(x/10f + 0.5f,y/10f+ 0.5f,z/10f+ 0.5f) * 1);
                    grid[x][y][z] = value;
                    if(value > 0) {
                        value = 1;
                    }else {
                        value = 0;
                    }
                    instanceData.put(value);
                    instanceData.put(value);
                    instanceData.put(value);

                    instanceData.put(0.1f);
                    instanceData.put(0.1f);
                    instanceData.put(0.1f);
                }
            }
        }
        instanceData.flip();
        float[] floatList = new float[instanceCount * 9];
        instanceData.get(floatList);
        shader = new Shader("assets/shaders/grid.glsl");

        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, cubeIndices, GL_STATIC_DRAW);
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, cubeVertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        ivbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, ivbo);
        glBufferData(GL_ARRAY_BUFFER, floatList, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 9 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribDivisor(1, 1);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 9 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(2);
        glVertexAttribDivisor(2,1);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 9 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(3);
        glVertexAttribDivisor(3,1);
        int newCount = count - 1;
        float[][] cubes = new float[newCount * newCount * newCount][8];
        int index = 0;
        for(int x = 0; x < newCount; x++) {
            for(int y = 0; y < newCount; y++) {
                for(int z = 0; z < newCount; z++) {
                    cubes[index][0] = grid[x][y][z];
                    cubes[index][1] = grid[x][y][z+1];
                    cubes[index][2] = grid[x][y+1][z];
                    cubes[index][3] = grid[x][y+1][z+1];
                    cubes[index][4] = grid[x+1][y][z];
                    cubes[index][5] = grid[x+1][y][z+1];
                    cubes[index][6] = grid[x+1][y+1][z];
                    cubes[index][7] = grid[x+1][y+1][z+1];

                    index++;
                }
            }
        }
        MarchingCubes.init(cubes, newCount);
    }
    public static void render() {
        shader.use();
        shader.uploadMat4f("view", SceneManager.camera.getViewMatrix());
        shader.uploadMat4f("projection", SceneManager.camera.getProjectionMatrix());
        glBindVertexArray(vao);
        //glDrawElements(GL_TRIANGLES, 24, GL_UNSIGNED_INT, 0);
        glDrawElementsInstanced(GL_TRIANGLES, cubeIndices.length, GL_UNSIGNED_INT, 0, instanceCount);
        MarchingCubes.update();
    }
}

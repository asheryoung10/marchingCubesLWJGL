package Amalgam;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class SceneManager {
    private static Chunk chunk;
    private static ArrayList<Chunk> chunks = new ArrayList<Chunk>();
    public static Camera camera = new Camera();;
    public static void init() {
        chunks.add(new Chunk(new Vector3i(0,0,0)));
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        new Thread(new Generator()).start();
        //Grid.init();

    }
    private static int y = 0;
    public static void update(double deltaTime) {
        camera.update(deltaTime);
        if(Callbacks.isKeyDownThisFrame(GLFW_KEY_R)) {
            y++;
            chunks.add(new Chunk(new Vector3i(0, y, 0)));

        }

        for(Chunk chunk : chunks) {
            chunk.render();
        }
        Vector3f position = new Vector3f().set(camera.position);
        position.div(Chunk.chunkSize);
        if(position.x < 0) position.x+=-1f;
        if(position.y < 0) position.y+=-1f;
        if(position.z < 0) position.z+=-1f;
        int x = (int) position.x-1;
        int y = (int) position.y-1;
        int z = (int) position.z-1;
        Vector3i[] positions = new Vector3i[27];
        boolean[] available = new boolean[27];
        int index = 0;
        for(int X = x; X < x + 3; X++) {
            for(int Y = y; Y < y + 3; Y++) {
                for(int Z = z; Z < z + 3; Z++) {
                    positions[index] = new Vector3i(X, Y, Z);
                    index++;
                }
            }
        }

        for(int i = 0; i < positions.length; i++){
            for(Chunk chunk : chunks){
                if(chunk.chunkID.equals(positions[i])) {
                    available[i] = true;
                    break;
                }
            }
        }

        if(!Callbacks.isKeyDown(GLFW_KEY_C)) {
            for(int i = 0; i < positions.length; i++) {
                if(!available[i]) {
                    chunks.add(new Chunk(positions[i]));
                }
            }
        }
        if(!Callbacks.isKeyDown(GLFW_KEY_C)) {
            for (int i = 0; i < chunks.size(); i++) {
                boolean delete = true;
                for (Vector3i pos : positions) {
                    if (chunks.get(i).chunkID.equals(pos)) {
                        delete = false;
                        break;
                    }
                }
                if (delete) {
                    chunks.remove(i);
                    i--;
                }
            }
        }
        boolean doit = false;
        if(Callbacks.isKeyDown(GLFW_KEY_U)) {
            Chunk.isoLevel+=0.05f;
            doit = true;

        }else if(Callbacks.isKeyDown(GLFW_KEY_J)) {
            Chunk.isoLevel-=0.05f;
            doit = true;
        }
        if(doit) {
            ArrayList<Chunk> newChunks = new ArrayList<Chunk>(chunks.size());
            for(Chunk chunk : chunks) {
                newChunks.add(new Chunk(chunk.chunkID));
            }
            chunks = newChunks;
        }
    }
}

package Amalgam;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    Vector3f position;
    Quaternionf rotation;
    float fovY = (float)Math.PI/2;;
    private Matrix4f viewMatrix, projectionMatrix;
    float yaw = 0, pitch = 0, roll = 0;
    public Camera() {
        rotation = new Quaternionf().lookAlong(new Vector3f(0,0,1), new Vector3f(0,1,0));
        position = new Vector3f(0,0,0);
        calculateViewMatrix();
        calculateProjectionMatrix();
    }
    public void calculateViewMatrix() {
        Quaternionf orientation = new Quaternionf(rotation);
        viewMatrix = new Matrix4f().rotate(orientation.conjugate()).translate(-position.x, -position.y, -position.z);
    }
    public void calculateProjectionMatrix() {
        float aspectRatio = Callbacks.getFrameWidth() / (float) Callbacks.getFrameHeight();
        projectionMatrix = new Matrix4f().perspective(fovY, aspectRatio, 0.001f,1000);
    }
    public Matrix4f getViewMatrix() {
        return(viewMatrix);
    }
    public Matrix4f getProjectionMatrix() {
        return(this.projectionMatrix);
    }
    private double normSpeed = 1;
    private double maxSpeed = 100;
    private double speed = normSpeed;
    public void update(double deltaTime) {
        yaw = (float)(Callbacks.getMouseDeltaX() * deltaTime * -0.1);
        pitch = (float)(Callbacks.getMouseDeltaY() * deltaTime * -0.1);
        roll = 0;
        if(Callbacks.isKeyDown(GLFW_KEY_Q)) {
            roll += deltaTime;
        }
        if(Callbacks.isKeyDown(GLFW_KEY_E)) {
            roll -= deltaTime;
        }
        rotation = rotation.rotateX(pitch);
        rotation = rotation.rotateY(yaw);
        rotation = rotation.rotateZ(roll);


        Vector3f addition = new Vector3f();
        if(Callbacks.isKeyDown(GLFW_KEY_W)) {
            addition.z -= 1;
        }
        if(Callbacks.isKeyDown(GLFW_KEY_S)) {
            addition.z += 1;
        }
        if(Callbacks.isKeyDown(GLFW_KEY_A)) {
            addition.x -= 1;
        }
        if(Callbacks.isKeyDown(GLFW_KEY_D)) {
            addition.x += 1;
        }
        if(Callbacks.isKeyDown(GLFW_KEY_SPACE)) {
            addition.y += 1;
        }
        if(Callbacks.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            addition.y -= 1;
        }
        addition.rotate(rotation);
        if(Callbacks.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            speed += (normSpeed * 10 * deltaTime);
            if(speed > maxSpeed) {
                speed = maxSpeed;
            }
        }else {
            speed -= (normSpeed * 10 * deltaTime);
            if(speed < normSpeed) {
                speed = normSpeed;
            }
        }
        position.add(addition.mul((float) (speed * deltaTime)));
        calculateViewMatrix();
        fovY -= Callbacks.getScrollDeltaY() * deltaTime;
        float threshold = 0.001f;
        if(fovY > Math.PI - threshold) {
            fovY = (float) Math.PI - threshold;
        }else if(fovY < threshold) {
            fovY = threshold;
        }
        if(Callbacks.isMouseButtonDownThisFrame(GLFW_MOUSE_BUTTON_3)) {
            fovY = (float) (Math.PI/2);
        }
        calculateProjectionMatrix();
    }
}

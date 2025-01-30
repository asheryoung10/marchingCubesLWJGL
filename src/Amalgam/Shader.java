package Amalgam;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20C.*;

public class Shader {
    private final int programID;
    private int vertexShaderID, fragmentShaderID;
    private String vertexShaderSource, fragmentShaderSource;
    public static int beingUsedID = -1;
    public Shader(String filepath) {
        String sourceLines;
        {
            StringBuilder source = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(filepath));
                String line;
                while ((line = reader.readLine()) != null) {
                    source.append(line).append('\n');
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read shader file: " + filepath, e);
            }
            sourceLines = source.toString();
        }
        {
            vertexShaderSource = sourceLines.split("#type")[1];
            vertexShaderSource = vertexShaderSource.substring(vertexShaderSource.indexOf('\n'));
            fragmentShaderSource = sourceLines.split("#type")[2];
            fragmentShaderSource = fragmentShaderSource.substring(fragmentShaderSource.indexOf('\n'));
        }

        {
            vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertexShaderID, vertexShaderSource);
            glCompileShader(vertexShaderID);
            if(glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                String errorLog = glGetShaderInfoLog(vertexShaderID);
                throw new IllegalStateException("Failed to compile '" + filepath + "' vertex shader: " + errorLog);
            }
        }
        {
            fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragmentShaderID, fragmentShaderSource);
            glCompileShader(fragmentShaderID);
            if(glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                String errorLog = glGetShaderInfoLog(fragmentShaderID);
                throw new IllegalStateException("Failed to compile '" + filepath + "' fragment shader: " + errorLog);
            }
        }
        {
            programID = glCreateProgram();
            glAttachShader(programID, vertexShaderID);
            glAttachShader(programID, fragmentShaderID);
            glLinkProgram(programID);
            if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
                String errorLog = glGetProgramInfoLog(programID);
                throw new IllegalStateException("Failed to link program: " + errorLog);
            }

            glValidateProgram(programID);
            if (glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE) {
                String errorLog = glGetProgramInfoLog(programID);
                throw new IllegalStateException("Failed to validate program: " + errorLog);
            }
        }
    }
    public void use() {
        if(beingUsedID != programID) {
            glUseProgram(programID);
            beingUsedID = programID;
        }
    }
    static FloatBuffer mat4Buffer = BufferUtils.createFloatBuffer(16);
    public void uploadMat4f(String varName, Matrix4f mat4) {
        use();
        int varLocation = glGetUniformLocation(programID, varName);
        mat4Buffer.clear();
        mat4.get(mat4Buffer);
        glUniformMatrix4fv(varLocation, false, mat4Buffer);
    }
    FloatBuffer mat3Buffer = BufferUtils.createFloatBuffer(9);
    public void uploadMat3f(String varName, Matrix3f mat3) {
        use();
        int varLocation = glGetUniformLocation(programID, varName);
        mat3Buffer.clear();
        mat3.get(mat3Buffer);
        glUniformMatrix3fv(varLocation, false, mat3Buffer);
    }
    public void uploadVec4f(String varName, Vector4f vec) {
        use();
        int varLocation = glGetUniformLocation(programID, varName);
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }
    public void uploadVec3f(String varName, Vector3f vec) {
        use();
        int varLocation = glGetUniformLocation(programID, varName);
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }
    public void uploadVec2f(String varName, Vector2f vec) {
        use();
        int varLocation = glGetUniformLocation(programID, varName);
        glUniform2f(varLocation, vec.x, vec.y);
    }
    public void uploadFloat(String varName, float val) {
        use();
        int varLocation = glGetUniformLocation(programID, varName);
        glUniform1f(varLocation, val);
    }
    public void uploadInt(String varName, int val) {
        use();
        int varLocation = glGetUniformLocation(programID, varName);
        glUniform1i(varLocation, val);
    }
    public void uploadTexture(String varName, int slot) {
        use();
        int varLocation = glGetUniformLocation(programID, varName);
        glUniform1i(varLocation, slot);
    }
    public void uploadIntArray(String varName, int[] array) {
        use();
        int varLocation = glGetUniformLocation(programID, varName);
        glUniform1iv(varLocation, array);
    }
}

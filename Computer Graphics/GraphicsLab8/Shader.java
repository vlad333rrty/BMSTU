import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private final int program;
    Shader(String vsName,String fsName){
        program = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);
        int fs = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vs,readShader("shaders/"+vsName));
        glCompileShader(vs);

        if (glGetShaderi(vs,GL_COMPILE_STATUS)!=1) {
            System.out.println(glGetShaderInfoLog(vs));
            throw new RuntimeException("Shader error");
        }

        glShaderSource(fs,readShader("shaders/"+fsName));
        glCompileShader(fs);
        if (glGetShaderi(fs,GL_COMPILE_STATUS)!=1) {
            System.out.println(glGetShaderInfoLog(fs));
            throw new RuntimeException("Shader error");
        }

        glAttachShader(program, vs);
        glAttachShader(program, fs);

        glBindAttribLocation(program,0,"vertex");
        glBindAttribLocation(program,1,"texCoords");
        glBindAttribLocation(program,2,"normal");

        glLinkProgram(program);
        if (glGetProgrami(program,GL_LINK_STATUS)!=1) {
            System.out.println(glGetProgrami(program,GL_LINK_STATUS));
            throw new RuntimeException("Link error");
        }
        glValidateProgram(program);
        if (glGetProgrami(program,GL_VALIDATE_STATUS)!=1) throw new RuntimeException("Validate error");
    }

    void bind(){
        glUseProgram(program);
    }

    private String readShader(String path){
        StringBuilder builder=new StringBuilder();
        try(FileInputStream in=new FileInputStream(path)){
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            for (String line=reader.readLine();line!=null;line=reader.readLine()){
                builder.append(line).append('\n');
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return builder.toString();
    }

    void setUniform(String name,int value){
        int location=glGetUniformLocation(program,name);
        if (location!=-1) glUniform1i(location,value);
    }

    void setUniform(String name,float value){
        int location=glGetUniformLocation(program,name);
        if (location!=-1) glUniform1f(location,value);
    }

    void setUniform(String name, Matrix4f matrix4f){
        int location=glGetUniformLocation(program,name);
        FloatBuffer buffer= BufferUtils.createFloatBuffer(16);
        matrix4f.get(buffer);
        if (location!=-1) glUniformMatrix4fv(location,false,buffer);
    }

    void setUniform(String name, float[] vector3f){
        int location=glGetUniformLocation(program,name);
        FloatBuffer buffer= BufferUtils.createFloatBuffer(3);
        buffer.put(vector3f);
        System.out.println(location);
        if (location!=-1) glUniform3fv(location,buffer);
    }

    void setUniform(String name, float x,float y,float z){
        int location=glGetUniformLocation(program,name);
        if (location!=-1) glUniform3f(location,x,y,z);
    }

    void setUniform(String name, float x,float y){
        int location=glGetUniformLocation(program,name);
        if (location!=-1) glUniform2f(location,x,y);
    }

}

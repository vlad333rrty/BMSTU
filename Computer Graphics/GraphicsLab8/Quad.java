import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
public class Quad {
    private int vao,vbo;
    private float[] coords;
    private int[] indices;
    Quad(float[] coords){
        this.coords=coords;
        //this.indices=indices;
        loadVao();
    }

    int loadVao(){
        vao=glGenVertexArrays();
        glBindVertexArray(vao);
        //bindIndexBuffer();
        storeVbo(0);
        glBindVertexArray(0);
        return vao;
    }

    int getVertexCount(){
        return coords.length/3;
    }

    private void storeVbo(int index){
        vbo=glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vbo);
        glBufferData(GL_ARRAY_BUFFER,toFloatBuffer(coords),GL_STATIC_DRAW);
        glVertexAttribPointer(index,3,GL_FLOAT,false,0,0);
        glBindBuffer(GL_ARRAY_BUFFER,0);
    }

    private void bindIndexBuffer(){
        vbo=glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,toIntBuffer(indices),GL_STATIC_DRAW);
    }

    private IntBuffer toIntBuffer(int[] array){
        IntBuffer buffer=BufferUtils.createIntBuffer(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer toFloatBuffer(float[] array){
        FloatBuffer buffer= BufferUtils.createFloatBuffer(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    void draw(){
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_QUADS,0,getVertexCount());
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
}

import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Texture {
    int width,height,id;
    private ByteBuffer data;
    private final String name;
    static ArrayList<Texture> textureStorage=new ArrayList<>();
    Texture(String name){
        this.name=name;
        id=glGenTextures();
        textureStorage.add(this);
        loadImage();
    }

    private void loadImage(){
        int[] w=new int[1],h=new int[1],c=new int[1];
        data= STBImage.stbi_load("textures/".concat(name),w,h,c,4);
        width=w[0];
        height=h[0];
        assert data != null;
    }

    void bind(int sampler){
        glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE,data);

        glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR_MIPMAP_LINEAR);
        glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

        GL30.glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS,-0.2f);

        glActiveTexture(GL_TEXTURE0+sampler);
        glBindTexture(GL_TEXTURE_2D,id);
    }
}

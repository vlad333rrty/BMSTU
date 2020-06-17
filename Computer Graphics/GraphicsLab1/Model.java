import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.joml.Vector2d;

class Polygon{
    private Vector2d[] coordinates;
    private Vector3d colour;
    private double x=0;
    void draw() {
        if (Window.Input.isPressed(GLFW.GLFW_KEY_SPACE)){
            colour.set(Math.cos(x), Math.sin(x),Math.cos(x)*Math.sin(x));
            x+=0.05;
        }
        if (Window.Input.mouseLeftButton){
            x=0;
            colour.set(Math.cos(x), Math.sin(x),Math.cos(x)*Math.sin(x));
        }
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glColor3d(colour.x,colour.y,colour.z);
        for (Vector2d coordinate : coordinates) {
            GL11.glVertex2d(coordinate.x, coordinate.y);
        }
        GL11.glEnd();
    }

    Polygon(Vector2d[] coordinates){
        this.coordinates=coordinates;
        colour=new Vector3d(1,0,0);
    }
}


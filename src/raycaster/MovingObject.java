package raycaster;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class MovingObject {
    Screen s;
    double x, y, velX, velY;
    
    BufferedImage sprite;
    
    LinkedList<int[]> spots = new LinkedList<>();
    
    public MovingObject(Screen s, double x, double y, BufferedImage sprite){
        this.s = s;
        this.x = x+50;
        this.y = y+50;
        this.sprite = sprite;
    }
    public void tick(){
        x += velX;
        y += velY;
    }
}

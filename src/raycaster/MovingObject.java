package raycaster;
import java.awt.image.BufferedImage;

public class MovingObject {
    Screen s;
    double x, y, velX, velY;
    
    BufferedImage sprite;
    
    public MovingObject(Screen s, double x, double y, BufferedImage sprite){
        this.s = s;
        this.x = x+50;
        this.y = y+50;
        this.sprite = sprite;
    }
}

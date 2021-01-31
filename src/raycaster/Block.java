package raycaster;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Block {
    Screen s;
    
    int x, y, num;
    
    public Block(Screen s, int x, int y, int num){
        this.s = s;
        this.x = x;
        this.y = y;
        this.num = num;
    }
    public void paint(Graphics g){
        g.setColor(Color.black);
        g.drawRect(x, y, 100, 100);
    }
    public void paintMini(Graphics g, BufferedImage tex, int scale, int xOff, int yOff){
        //g.setColor(Color.black);
        //g.fillRect((x/100)*scale + xOff, (y/100)*scale + yOff, scale, scale);
        
        g.drawImage(tex, (x/100)*scale + xOff, (y/100)*scale + yOff, scale, scale, s);
    }
    public Rectangle block(){
        return new Rectangle(x, y, 100, 100);
    }
}
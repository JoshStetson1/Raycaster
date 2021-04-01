package raycaster;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Bullet {
    Screen s;
    double x, y, velX, velY;
    
    int damage;
    String parent;
    
    double[] info;
    
    public Bullet(Screen s, double x, double y, int damage, String parent){
        this.s = s;
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.parent = parent;
    }
    public void tick(){
        x += velX;
        y += velY;
    }
    public void shootBullet(double angle, double x, double y){
        Ray hit = new Ray(s);
        hit.shootRay2(angle, x, y, s.lm.blockLayout);
        info = hit.info;
        
        checkHit(info);
    }
    public void checkHit(double[] info){
        if(info[5] == 2){//enemy was hit
            s.p.takeDamage(damage);
        } else if(info[5] == -1){//enemy was hit
            for(int i = 0; i < s.l.e.size(); i++){
                Enemy enemy = s.l.e.get(i);
                
                int mapX = (int)(info[0]/100);
                int mapY = (int)(info[1]/100);
                int mapEX = (int)(enemy.x/100);
                int mapEY = (int)(enemy.y/100);
                
                if(mapEX == mapX && mapEY == mapY) enemy.takeDamage(damage);
            }
        }
    }
}

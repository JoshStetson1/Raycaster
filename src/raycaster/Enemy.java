package raycaster;
import java.awt.*;

public class Enemy {
    Screen s;
    MovingObject obj;
    
    double x, y, velX, velY;
    double speed = 1;
    
    int mapX, mapY;
    
    double shootBounds = 200;
    
    int health = 100;
    
    Weapon gun;
    
    public Enemy(Screen s, double x, double y){
        this.s = s;
        
        this.x = x + 50;
        this.y = y + 50;
        
        mapX = (int)(x/100);
        mapY = (int)(y/100);
        
        MovingObject obj = new MovingObject(s, x, y, s.sm.objects[4]);
        s.l.mObj.add(obj);
        this.obj = obj;
        
        gun = new Weapon(s, false, 1, 3, 0.5, 25);
    }
    public void tick(){
        setMapPoints();
        
        if(s.p.dist(x, y, s.p.x, s.p.y) > shootBounds) follow(s.p.x, s.p.y);
        else {
            velX = 0;
            velY = 0;
            
            shoot();
        }
        
        x += velX;
        y += velY;
    }
    public void matchObj(){
        obj.x = x;
        obj.y = y;
    }
    public void follow(double fx, double fy){
        double angle = Math.atan2(fy - y, fx - x);
        velX = speed * Math.cos(angle);
        velY = speed * Math.sin(angle);
    }
    public void setMapPoints(){
        if((int)(x/100) != mapX){ s.lm.blockLayout[mapX][mapY] = 0; mapX = (int)(x/100);}
        if((int)(y/100) != mapY){ s.lm.blockLayout[mapX][mapY] = 0; mapY = (int)(y/100);}
        
        s.lm.blockLayout[(int)(x/100)][(int)(y/100)] = -1;
    }
    
    public void shoot(){
        if(s.p.isDead) return;
        
        double angleToPlayer = Math.atan2(s.p.y - y, s.p.x - x);
        gun.shoot(angleToPlayer, x, y, "enemy");
    }
    public void takeDamage(int damage){
        health -= damage;
        if(health <= 0) die();
    }
    public void die(){
        s.l.mObj.remove(obj);
        s.l.e.remove(this);
    }
    
    public Rectangle bounds(){
        return new Rectangle((int)x-50, (int)y-50, 100, 100);
    }
    public Rectangle top(){
        return new Rectangle((int)x-35, (int)y-50, 70, 15);
    }
    public Rectangle bottom(){
        return new Rectangle((int)x-35, (int)y+35, 70, 15);
    }
    public Rectangle right(){
        return new Rectangle((int)x+35, (int)y-35, 15, 70);
    }
    public Rectangle left(){
        return new Rectangle((int)x-50, (int)y-35, 15, 70);
    }
}

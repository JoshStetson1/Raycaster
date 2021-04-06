package raycaster;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Arrays;
import java.util.LinkedList;

public class Player {
    Screen s;
    
    double x, y, velX, velY;
    double dx, dy, pa = Math.PI/2;//players angle and directions of player
    boolean[] moving = new boolean[6];
    int health = 100;
    boolean isDead;
    int mapX, mapY;
    
    RenderWorld world;
    
    double dr = (Math.PI*2)/360;//one degree in radians
    
    int speed = 15;
    int size = 2;
    
    Weapon gun;
    boolean shooting;
    
    public Player(Screen s){
        this.s = s;
        
        world = new RenderWorld(s);
        
        mapX = (int)(x/100);
        mapY = (int)(y/100);
        
        dx = Math.cos(pa)*30;
        dy = Math.sin(pa)*30;
        
        gun = new Weapon(s, true, 1, 3, 0.2, 30);
    }
    public void tick(){
        if(isDead) return;
        
        if((int)(x/100) != mapX){ s.lm.blockLayout[mapX][mapY] = 0; mapX = (int)(x/100);}
        if((int)(y/100) != mapY){ s.lm.blockLayout[mapX][mapY] = 0; mapY = (int)(y/100);}
        
        s.lm.blockLayout[(int)(x/100)][(int)(y/100)] = 2;
        
        x += velX;
        y += velY;
        
        movement();
        //collisions();
        
        if(shooting) gun.shoot(pa, x, y, "player");
    }
    public void movement(){
        if(s.miniMap){//cant move while looking at minimap
            for(int i = 0; i < moving.length; i++) moving[i] = false;
        }
        
        //rotations
        if(moving[4]){//looking left
            pa -= 0.015 *s.deltaTime;
            if(pa < 0) pa+=2*Math.PI;
            dx = Math.cos(pa)*50;
            dy = Math.sin(pa)*50;
        } else if(moving[5]){//looking right
            pa += 0.015 *s.deltaTime;
            if(pa > 2*Math.PI) pa-=2*Math.PI;
            dx = Math.cos(pa)*50;
            dy = Math.sin(pa)*50;
        }
        //movement
        if(moving[0]){//move forwards
            velX = dx/speed *s.deltaTime;
            velY = dy/speed *s.deltaTime;
        } else if(moving[1]){//move backwards
            velX = -dx/speed *s.deltaTime;
            velY = -dy/speed *s.deltaTime;
        }
        if(moving[2]){//move left
            if(moving[0] || moving[1]){//strafe
                velX = ((velX*speed)-(Math.cos(pa+(Math.PI*0.5))*50))/(speed*2) *s.deltaTime;
                velY = ((velY*speed)+(Math.sin(pa-(Math.PI*0.5))*50))/(speed*2) *s.deltaTime;
            } else{
                velX = -(Math.cos(pa+(Math.PI*0.5))*50)/speed *s.deltaTime;
                velY = (Math.sin(pa-(Math.PI*0.5))*50)/speed *s.deltaTime;
            }
        } else if(moving[3]){//move right
            if(moving[0] || moving[1]){//strafe
                velX = ((velX*speed)+(Math.cos(pa+(Math.PI*0.5))*50))/(speed*2) *s.deltaTime;
                velY = ((velY*speed)-(Math.sin(pa-(Math.PI*0.5))*50))/(speed*2) *s.deltaTime;
            } else{
                velX = (Math.cos(pa+(Math.PI*0.5))*50)/speed *s.deltaTime;
                velY = -(Math.sin(pa-(Math.PI*0.5))*50)/speed *s.deltaTime;
            }
        }
        if(!moving[0] && !moving[1] && !moving[2] && !moving[3]){//not moving, slow down
            velX = 0;
            velY = 0;
        }
    }
    public void collisions(){
        int[] front = {(int)((dx*size + x)/100), (int)((dy*size + y)/100)};
        int[] back = {(int)((-dx*size + x)/100), (int)((-dy*size + y)/100)};
        int[] left = {(int)((-(Math.cos(pa+(Math.PI*0.5)))*30*size + x)/100), (int)(((Math.sin(pa-(Math.PI*0.5)))*30*size + y)/100)};
        int[] right = {(int)(((Math.cos(pa+(Math.PI*0.5)))*30*size + x)/100), (int)((-(Math.sin(pa-(Math.PI*0.5)))*30*size + y)/100)};
        
        //forward & backward
        if(moving[0]){
            int pointX = (int)((x+(dx+velX))/100);
            int pointY = (int)((y+(dy+velY))/100);
            
            if(s.lm.level[pointX][(int)(y/100)] != -1) velX = 0;
            if(s.lm.level[(int)(x/100)][pointY] != -1) velY = 0;
        } else if(moving[1]){
            int pointX = (int)((x+(-dx+velX))/100);
            int pointY = (int)((y+(-dy+velY))/100);
            
            if(s.lm.level[pointX][(int)(y/100)] != -1) velX = 0;
            if(s.lm.level[(int)(x/100)][pointY] != -1) velY = 0;
        }
        //left & right
        if(moving[2]){
            int pointX = (int)((x+(-(Math.cos(pa+(Math.PI*0.5)))*50 + velX))/100);
            int pointY = (int)((y+(Math.sin(pa-(Math.PI*0.5)))*50 + velY)/100);
            
            if(s.lm.level[pointX][(int)(y/100)] != -1) velX = 0;
            if(s.lm.level[(int)(x/100)][pointY] != -1) velY = 0;
        } else if(moving[3]){
            int pointX = (int)((x+(Math.cos(pa+(Math.PI*0.5)))*50 + velX)/100);
            int pointY = (int)((y+(-(Math.sin(pa-(Math.PI*0.5)))*50 + velY))/100);
            
            if(s.lm.level[pointX][(int)(y/100)] != -1) velX = 0;
            if(s.lm.level[(int)(x/100)][pointY] != -1) velY = 0;
        }
    }//not in use
    public void takeDamage(int damage){
        health -= damage;
        if(health <= 0) isDead = true;
    }
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        
        world.paint(g);
    }
    
    public int radToDeg(double ang){
        return (int)(ang*360 / (2*Math.PI));
    }
    public double limitFor(double angle){
        if(angle < 0) angle += 2*Math.PI; if(angle > 2*Math.PI) angle -= 2*Math.PI;//limits for new degree
        return angle;
    }
    public double diff(double ang1, double ang2){
        double finalDiff;
        double diff1 = Math.abs(ang1 - ang2);
        double diff2 = Math.abs(Math.PI*2 - diff1);
        
        if(diff1 < diff2) finalDiff = diff1;
        else finalDiff = diff2;
        
        return finalDiff;
    }
    public double dist(double ax, double ay, double bx, double by){
        double num = ((bx-ax)*(bx-ax) + (by-ay)*(by-ay));//pythag
        
        return Math.sqrt(num);
    }
    //for collisions
    public Rectangle bounds(){
        return new Rectangle((int)x-50, (int)y-50, 100, 100);
    }
    public Rectangle top(){
        return new Rectangle((int)x-40, (int)y-50, 80, 10);
    }
    public Rectangle bottom(){
        return new Rectangle((int)x-40, (int)y+40, 80, 10);
    }
    public Rectangle right(){
        return new Rectangle((int)x+40, (int)y-40, 10, 80);
    }
    public Rectangle left(){
        return new Rectangle((int)x-50, (int)y-40, 10, 80);
    }
}
/*

                double distToPlayer = dist(x, y, obj.x, obj.y);
                double objHeight = (100*s.getWidth())/distToPlayer;
                double objY = s.getHeight()/2 - objHeight/2;
                
                //angToPlayer = Math.atan2((y - obj.y), (x - obj.x));
                double angle = limitFor(pa-angToPlayer);
                double xDis = distToPlayer*Math.cos(angle);
                double yDis = distToPlayer*Math.sin(angle);
                double h = (1/Math.cos(dr*30) * yDis);
                double screen = 2*Math.sqrt((h*h) - (yDis*yDis));
                double scale = s.getWidth()/screen;
                double objX = xDis*scale;
                

if(info[5] == -6){
    int mx = (int)(info[1]/100);
    int my = (int)(info[2]/100);
    for(MovingObject obj : s.l.mObj){
        if(obj.mapX == mx && obj.mapY == my){ if(info[3] == dist) obj.drawObj(g, info, ra); }
        else{
            for(int s = 0; s < obj.spots.size(); s++){
                if(obj.spots.get(s)[0] == mx && obj.spots.get(s)[1] == my){
                    if(info[3] == dist) obj.drawObj(g, info, ra);
                }
            }
        }
    }
} else 
*/
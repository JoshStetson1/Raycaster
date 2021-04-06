package raycaster;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Weapon {
    Screen s;
    
    int amount, spread, damage;
    double fireRate;
    boolean isAuto;
    
    Random rand = new Random();
    long nowTime, time;
    
    double degree = Math.PI/180;
    
    public Weapon(Screen s, boolean isAuto, int amount, int spread, double fireRate, int damage){
        this.s = s;
        this.isAuto = isAuto;
        this.amount = amount;
        this.spread = spread;
        this.fireRate = fireRate;
        this.damage = damage;
        
        nowTime = System.nanoTime();
        time = System.nanoTime();
    }
    public void shoot(double angle, double x, double y, String parent){
        if(!canShoot()) return;
        
        for(int i = 0; i < amount; i++){
            Bullet tempBullet = new Bullet(s, x, y, damage, parent);
            
            double spreadAngle = (rand.nextDouble()-0.5) * degree * spread;
            tempBullet.shootBullet(angle + spreadAngle, x, y);
            
            if(parent.equals("player")){//recoil
                double forceX = (rand.nextDouble()-0.5) * spread;
                double forceY = (rand.nextDouble()-0.5) * spread;
                s.p.velX = s.p.velX-((damage/10) * Math.cos(angle)) + forceX;
                s.p.velY = s.p.velY-((damage/10) * Math.sin(angle)) + forceY;
            }
        }
        nowTime = System.nanoTime();
        s.PlaySound(s.shot);
    }
    public boolean canShoot(){
        boolean canShoot = false;
        if(System.nanoTime() > nowTime + 1000000000*fireRate){
            canShoot = true;
        }
        
        return canShoot;
    }
}

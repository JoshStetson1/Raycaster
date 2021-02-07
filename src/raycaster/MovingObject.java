package raycaster;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class MovingObject {
    Screen s;
    double a, x, y, velX, velY, dx, dy, xOff, yOff;
    int mapX, mapY;
    
    boolean[] moving = new boolean[4];
    int speed = 50;
    
    LinkedList<int[]> spots = new LinkedList<>();
    
    public MovingObject(Screen s, double x, double y){
        this.s = s;
        this.x = x+50;
        this.y = y+50;
        
        mapX = (int)(x/100);
        mapY = (int)(y/100);
        
        findPOS();
    }
    public void tick(){
        x += velX;
        y += velY;
        
        moveAndColl();
        findPOS();
    }
    public void moveAndColl(){
        a = Math.atan2(s.p.y - y, s.p.x - x);
        
        dx = Math.cos(a)*50;
        dy = Math.sin(a)*50;
        velX = dx/speed *s.deltaTime;
        velY = dy/speed *s.deltaTime;
        
        int pointX = (int)((x+(dx+velX))/100);
        int pointY = (int)((y+(dy+velY))/100);

        //block collisisons
        if(s.lm.level[pointX][mapY] > 0 || (pointX == (int)(s.p.x/100) && pointY == (int)(s.p.y/100))) velX = 0;
        if(s.lm.level[mapX][pointY] > 0 || (pointX == (int)(s.p.x/100) && pointY == (int)(s.p.y/100))) velY = 0;
    }
    public void findPOS(){
        xOff = x - ((mapX*100)+50);
        yOff = y - ((mapY*100)+50);
        
        int tempMX = (int)(x/100);
        int tempMY = (int)(y/100);
        int offsetX = 0;
        int offsetY = 0;
        
        if(Math.abs(xOff) > 50) offsetX = tempMX-mapX;
        if(Math.abs(yOff) > 50) offsetY = tempMY-mapY;
        
        s.lm.level[mapX][mapY] = -1;
        mapX = mapX+offsetX;
        mapY = mapY+offsetY;
        s.lm.level[mapX][mapY] = -6;
        
        for(int i = 0; i < spots.size(); i++){
            int[] point = spots.get(i);
            s.lm.level[point[0]][point[1]] = -1;
            
            spots.remove(point);
        }
        
        int up = (int)((y-49)/100);
        int down = (int)((y+49)/100);
        int left = (int)((x-49)/100);
        int right = (int)((x+49)/100);
        
        boolean UR = (up != mapY && right != mapX);
        boolean DR = (down != mapY && right != mapX);
        boolean UL = (up != mapY && left != mapX);
        boolean DL = (down != mapY && left != mapX);
        
        int mx = mapX;
        int my = mapY;
        
        if(up != my && s.lm.level[mx][up] == -1){ s.lm.level[mx][up] = -6; int[] point = {mx, up}; spots.add(point);}
        if(down != my && s.lm.level[mx][down] == -1){ s.lm.level[mx][down] = -6; int[] point = {mx, down}; spots.add(point);}
        if(left != mx && s.lm.level[left][my] == -1){ s.lm.level[left][my] = -6;  int[] point = {left, my}; spots.add(point);}
        if(right != mx && s.lm.level[right][my] == -1){ s.lm.level[right][my] = -6; int[] point = {right, my}; spots.add(point);}
        
        if(UR && s.lm.level[right][up] == -1){ s.lm.level[right][up] = -6; int[] point = {right, up}; spots.add(point);}
        if(DR && s.lm.level[right][down] == -1){ s.lm.level[right][down] = -6;  int[] point = {right, down}; spots.add(point);}
        if(UL && s.lm.level[left][up] == -1){ s.lm.level[left][up] = -6; int[] point = {left, up}; spots.add(point);}
        if(DL && s.lm.level[left][down] == -1){ s.lm.level[left][down] = -6; int[] point = {left, down}; spots.add(point);}
    }
    public void drawObj(Graphics g, double[] info, double ra){
        if(info[5] < 0) info[5] = -1*info[5]-2;
        
        int middleX = (int)(info[1]/100) * 100 + 50;//middle of block
        int middleY = (int)(info[2]/100) * 100 + 50;//middle of block
        
        
        //---------Y distance/ scale----------
        double distA = Math.atan2(s.p.y - y, s.p.x - x);//angle of distance
        double horzPa = s.p.pa+(0.5*Math.PI);//horizontal angle relative to pa angle

        double angX = distA - horzPa;
        if(angX < 0) angX += 2*Math.PI; if(angX > 2*Math.PI) angX -= 2*Math.PI;

        double disT = s.p.dist(s.p.x, s.p.y, x, y);//distance from player to center of block
        disT = disT * Math.sin(angX);

        
        //---------X distance----------
        double distAng = Math.atan2(middleY - y, middleX - x);//angle of distance
        double horzPAng = s.p.pa+(0.5*Math.PI);//horizontal angle relative to pa angle

        double ang = distAng - horzPAng;
        if(ang < 0) ang += 2*Math.PI; if(ang > 2*Math.PI) ang -= 2*Math.PI;

        double dis = s.p.dist(x, y, middleX, middleY);//distance from player to center of block
        dis = dis * Math.cos(ang);
        
        
        //---------tex distance----------
        double horzA = ra+(0.5*Math.PI);//horizontal angle relative to ra angle
        double hitA = Math.atan2(info[2] - y, info[1] - x);//angle of hit point to center of block

        double angle = horzA - hitA;//angle between the horizontal angle and hit point
        if(angle < 0) angle += 2*Math.PI; if(angle > 2*Math.PI) angle -= 2*Math.PI;

        double distance = s.p.dist(info[1], info[2], x, y);//distance from hit point to center of lock
        distance = distance*Math.cos(angle);//the x distance from hit point to center of block relative to the rays angle
        
        double lineH = (100*s.getWidth())/disT;
        double lineY = (s.getHeight()/2)-(lineH/2);
        
        double loc = 50+(int)distance;

        BufferedImage img = (s.sm.grabSubTex(s.sm.objects[(int)info[5]], loc, s.p.res));
        if(img != null) g.drawImage(img, (int)info[0]*s.p.res, (int)lineY, s.p.res, (int)lineH, s);
    }
}

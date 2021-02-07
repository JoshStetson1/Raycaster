package raycaster;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;

public class Player {
    Screen s;
    
    double x, y, velX, velY;
    double dx, dy, pa = 0;//players angle and directions of player
    boolean[] moving = new boolean[6];
    
    double dr = (Math.PI*2)/360;//one degree in radians
    int res = 2;//resolution
    int resG = 2;//ground resolution
    
    int speed = 7;
    int size = 2;
    
    BufferedImage sunset;
    
    public Player(Screen s){
        this.s = s;
        
        dx = Math.cos(pa)*30;
        dy = Math.sin(pa)*30;
        
        sunset = s.sm.loadImage("paint\\sunset.jpg");
    }
    public void tick(){
        x += velX;
        y += velY;
        
        movement();
    }
    public void movement(){
        if(s.miniMap){//cant move while looking at minimap
            for(int i = 0; i < moving.length; i++) moving[i] = false;
        }
        int[] front = {(int)((dx*size + x)/100), (int)((dy*size + y)/100)};
        int[] back = {(int)((-dx*size + x)/100), (int)((-dy*size + y)/100)};
        int[] left = {(int)((-(Math.cos(pa+(Math.PI*0.5)))*30*size + x)/100), (int)(((Math.sin(pa-(Math.PI*0.5)))*30*size + y)/100)};
        int[] right = {(int)(((Math.cos(pa+(Math.PI*0.5)))*30*size + x)/100), (int)((-(Math.sin(pa-(Math.PI*0.5)))*30*size + y)/100)};
        
        //rotations
        if(moving[4]){//looking left
            pa -= 0.017 *s.deltaTime;
            if(pa < 0) pa+=2*Math.PI;
            dx = Math.cos(pa)*30;
            dy = Math.sin(pa)*30;
        } else if(moving[5]){//looking right
            pa += 0.017 *s.deltaTime;
            if(pa > 2*Math.PI) pa-=2*Math.PI;
            dx = Math.cos(pa)*30;
            dy = Math.sin(pa)*30;
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
            if(moving[0] || moving[1]){
                velX = ((velX*speed)-(Math.cos(pa+(Math.PI*0.5))*30))/(speed*5) *s.deltaTime;
                velY = ((velY*speed)+(Math.sin(pa-(Math.PI*0.5))*30))/(speed*5) *s.deltaTime;
            } else{
                velX = -(Math.cos(pa+(Math.PI*0.5))*30)/speed *s.deltaTime;
                velY = (Math.sin(pa-(Math.PI*0.5))*30)/speed *s.deltaTime;
            }
        } else if(moving[3]){//move right
            if(moving[0] || moving[1]){
                velX = ((velX*speed)+(Math.cos(pa+(Math.PI*0.5))*30))/(speed*5) *s.deltaTime;
                velY = ((velY*speed)-(Math.sin(pa-(Math.PI*0.5))*30))/(speed*5) *s.deltaTime;
            } else{
                velX = (Math.cos(pa+(Math.PI*0.5))*30)/speed *s.deltaTime;
                velY = -(Math.sin(pa-(Math.PI*0.5))*30)/speed *s.deltaTime;
            }
        }
        if(!moving[0] && !moving[1] && !moving[2] && !moving[3]){//not moving, slow down
            velX = 0;
            velY = 0;
        }
    }
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        
        moveSky(g);
        
        draw3DWorld(g);
        
        //g.setColor(Color.red);
        //g.fillRect(s.getWidth()/2 -5, s.getHeight()/2 - 5, 10, 10);
    }
    public void moveSky(Graphics g){
        double circleW = s.getWidth()*(2*Math.PI);
        double pAngle = pa*s.getWidth();
        
        double scale = sunset.getWidth()/circleW;
        double imgX = pAngle*scale;
        double width = (s.getWidth()/circleW) * sunset.getWidth();
        double imgW = s.getWidth()/width;
        
        if(imgX + width > sunset.getWidth()) width = sunset.getWidth()-imgX;
        if(width <= 1) width = 1;
        
        BufferedImage sample = sunset.getSubimage((int)imgX, 0, (int)width, sunset.getHeight());
        g.drawImage(sample, 0, 0, (int)(imgW*width), s.getHeight(), s);
        
        if(width < (s.getWidth()/circleW) * sunset.getWidth()){//loop around
            double width2 = ((s.getWidth()/circleW) * sunset.getWidth()) - width;
            if(width2 <= 1) width2 = 1;
            
            BufferedImage sample2 = sunset.getSubimage(0, 0, (int)width2, sunset.getHeight());
            g.drawImage(sample2, s.getWidth()-(int)(imgW*width2)-1, 0, (int)(imgW*width2), s.getHeight(), s);
        }
    }
    public void draw3DWorld(Graphics g){
        //r means the ray, rx = x coordinate of the ray
        double ra, rx, ry, xOff=0, yOff=0, disT=0;
        //ray angle, x point where ray will hit, y point where ray will hit, x offset to check next line, y offset to check next line, final distance of that ray
        int blockT = 0;//final block type
        int levelHeight = s.lm.levelImg.getHeight()*100;
        int levelWidth = s.lm.levelImg.getWidth()*100;
        
        ra = pa-dr*30;//starting angle
        if(ra < 0) ra += 2*Math.PI; if(ra > 2*Math.PI) ra -= 2*Math.PI;//limits for that angle
        
        for(int r = 0; r < 600/res; r++){//making rays
            LinkedList<double[]> objects = new LinkedList<>();
            
            //-----------------Horizontal Lines Check---------------------
            int dof = 0;//how far you can see
            double disH = 10000, hx = x, hy = y;
            //distance of horizontal ray, and points for horizontal ray
            double aTan = -1/Math.tan(ra);//inverse of tan
            int blockH = 0;//what block the horizontal line hit

            if(ra == 0 || ra == Math.PI){//looking left or right
                rx = x; ry = y; dof = levelHeight;
            } else if(ra < Math.PI){//looking down
                ry = (int)(y/100)*100+100;//getting next horizontal line
                rx = (y-ry)*aTan+x;

                yOff = 100;
                xOff = -yOff*aTan;
            } else{//looking up
                ry = (int)(y/100)*100 -0.0000001;//getting next horizontal line
                rx = (y-ry)*aTan+x;

                yOff = -100;
                xOff = -yOff*aTan;
            }

            while(dof < levelHeight/100){
                int mx = (int)(rx/100), my = (int)(ry/100);
                //limits so it doesnt go out of map array
                if(mx < 0) mx = 0; if(my < 0) my = 0;
                if(mx > levelWidth/100 -1) mx = levelWidth/100 -1;
                if(my > levelHeight/100 -1) my = levelHeight/100 -1;

                if(s.lm.level[mx][my] != -1){//if is a block
                    if(s.lm.level[mx][my] < 0){
                        double[] info = {r, rx, ry, dist(x, y, rx, ry), dof, s.lm.level[mx][my]};
                        objects.add(info);
                        
                        rx += xOff;
                        ry += yOff;
                        dof++;
                    } else{
                        hx = rx;
                        hy = ry;
                        disH = dist(x, y, hx, hy);
                        dof = levelHeight/100;
                        blockH = s.lm.level[mx][my];
                    }
                } else{
                    rx += xOff;
                    ry += yOff;
                    dof++;
                }
            }

            //-----------------Vertical Lines Check----------------------
            dof = 0;//reset depth of feild for vertical line
            double disV = 10000, vx = x, vy = y;//distance of vertical ray, and points of vertical ray
            double nTan = -Math.tan(ra);//negative tan
            int blockV = 0;//what block the vertical line hit

            if(ra == 0 || ra == Math.PI){//looking up or down
                rx = x; ry = y; dof = levelWidth;
            } else if(ra < Math.PI/2 || ra > 1.5*Math.PI){//looking right
                rx = (int)(x/100)*100+100;
                ry = (x-rx)*nTan+y;

                xOff = 100;
                yOff = -xOff*nTan;
            } else{//looking left
                rx = (int)(x/100)*100 -0.0000001;
                ry = (x-rx)*nTan+y;

                xOff = -100;
                yOff = -xOff*nTan;
            }

            while(dof < levelWidth/100){
                int mx = (int)(rx/100), my = (int)(ry/100);
                //limits so it doesnt go out of map array
                if(mx < 0) mx = 0; if(my < 0) my = 0;
                if(mx > levelWidth/100 -1) mx = levelWidth/100 -1;
                if(my > levelHeight/100 -1) my = levelHeight/100 -1;

                if(s.lm.level[mx][my] != -1){//is a block
                    if(s.lm.level[mx][my] < 0){
                        double[] info = {r, rx, ry, dist(x, y, rx, ry), dof, s.lm.level[mx][my]};
                        objects.add(info);
                        
                        rx += xOff;
                        ry += yOff;
                        dof++;
                    } else{
                        vx = rx;
                        vy = ry;
                        disV = dist(x, y, vx, vy);
                        dof = levelWidth/100;
                        blockV = s.lm.level[mx][my];
                    }
                } else{
                    rx += xOff;
                    ry += yOff;
                    dof++;
                }
            }

            //------------Finding what line is bigger---------------
            if(disH < disV){//horizontal ray is smaller than vertical one
                rx = hx; ry = hy; disT = disH; blockT = blockH;
            }
            else if(disV < disH){//vertical ray is smaller than horizontal one
                rx = vx; ry = vy; disT = disV; blockT = blockV;
            }
            //------------Drawing Rays---------------
            double loc = 0;//location where the ray hit the block
            boolean flip = false;

            //fixing weird camera effect
            double ca = pa-ra;
            if(ca < 0) ca += 2*Math.PI; if(ca > 2*Math.PI) ca -= 2*Math.PI;
            disT = disT*Math.cos(ca);

            if(disH < disV){
                loc = -1*(((int)(rx/100)*100)-(int)rx);
                if(ra < Math.PI) flip = true;
            }
            else if(disV < disH){
                loc = -1*(((int)(ry/100)*100)-(int)ry);
                if(ra > Math.PI/2 && ra < 1.5*Math.PI) flip = true;
            }

            double lineH = (100*s.getWidth())/disT;
            double lineY = (s.getHeight()/2)-(lineH/2);
            
            //------------Drawing Ceiling And Ground---------------
            if(r % resG == 0) CeilingAndGround(g, r, ra, lineY, lineH);
            
            //------------Drawing Walls---------------
            BufferedImage img;
            if(flip) img = (s.sm.grabSubTex(s.sm.flippedTex[blockT], loc, res));
            else img = (s.sm.grabSubTex(s.sm.textures[blockT], loc, res));

            g.drawImage(img, r*res, (int)lineY, res, (int)lineH, s);
            
            //------------Drawing Objects---------------
            double[] distances = new double[objects.size()];
            for(int i = 0; i < objects.size(); i++) distances[i] = objects.get(i)[3];
            
            Arrays.sort(distances);
            
            for(int i = distances.length-1; i >= 0; i--){
                double dist = distances[i];
                
                if(disT/Math.cos(ca) >= dist){
                    for(int h = 0; h < objects.size(); h++){
                        double[] info = objects.get(h);
                        if(info[5] == -3){
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
                        } else if(info[3] == dist) drawObject(g, info[0], info[1], info[2], info[3], -1*info[5]-2, ra);
                    }
                }
            }
                
            ra+=dr/10 * res;//add another degree
            if(ra < 0) ra += 2*Math.PI; if(ra > 2*Math.PI) ra -= 2*Math.PI;//limits for new degree
        }
    }
    public void drawObject(Graphics g, double r, double rx, double ry, double disT, double blockT, double ra){
        int middleX = (int)(rx/100) * 100 + 50;//middle of block
        int middleY = (int)(ry/100) * 100 + 50;//middle of block
        double distA = Math.atan2(y - middleY, x - middleX);//angle of distance
        double horzPa = pa+(0.5*Math.PI);//horizontal angle relative to pa angle

        double angX = distA - horzPa;
        if(angX < 0) angX += 2*Math.PI; if(angX > 2*Math.PI) angX -= 2*Math.PI;

        disT = dist(x, y, middleX, middleY);//distance from player to center of block
        disT = disT * Math.sin(angX);

        double horzA = ra+(0.5*Math.PI);//horizontal angle relative to ra angle
        double hitA = Math.atan2(ry - middleY, rx - middleX);//angle of hit point to center of block

        double angle = horzA - hitA;//angle between the horizontal angle and hit point
        if(angle < 0) angle += 2*Math.PI; if(angle > 2*Math.PI) angle -= 2*Math.PI;

        double distance = dist(rx, ry, middleX, middleY);//distance from hit point to center of lock
        distance = distance*Math.cos(angle);//the x distance from hit point to center of block relative to the rays angle
        
        double lineH = (100*s.getWidth())/disT;
        double lineY = (s.getHeight()/2)-(lineH/2);
        
        double loc = 50+(int)distance;

        BufferedImage img = (s.sm.grabSubTex(s.sm.objects[(int)blockT], loc, res));
        if(img != null) g.drawImage(img, (int)r*res, (int)lineY, res, (int)lineH, s);
    }
    public void CeilingAndGround(Graphics g, int r, double ra, double lineY, double lineH){
        for(int yy = (int)(lineY+lineH); yy < s.getHeight(); yy++){
            double mid = yy - s.getHeight()/2;
            double raFix = Math.cos(pa-ra);

            double tx = x/2 + Math.cos(ra)*145*100/mid/raFix;
            double ty = y/2 + Math.sin(ra)*145*100/mid/raFix;

            int mx = (int)(tx/50);
            int my = (int)(ty/50);

            try{
                //----------Floor-------------
                int tex = s.lm.floor[mx][my];
                
                BufferedImage texure = s.sm.textures[tex];
                int texSize = texure.getHeight();

                tx = (int)(tx)%50;//break into blocks
                ty = (int)(ty)%50;

                tx = (tx/50) * texSize;//find location on the texure relative to block
                ty = (ty/50) * texSize;

                tx = Math.abs(tx);//get absolute value so no negatives
                ty = Math.abs(ty);
                
                BufferedImage pixel = texure.getSubimage((int)tx, (int)ty, 1, 1);
                g.setColor(new Color(pixel.getRGB(0, 0)));
                g.fillRect(r*res, yy, res*resG, res);//floor
                
                //----------Ceiling-------------
                if(s.lm.floor[mx][my] >= 0){
                    tex = s.lm.ceiling[mx][my];
                    texure = s.sm.textures[tex];
                    texSize = texure.getHeight();

                    tx = (int)(tx)%50;//break into blocks
                    ty = (int)(ty)%50;

                    tx = (tx/50) * texSize;//find location on the texure relative to block
                    ty = (ty/50) * texSize;

                    tx = Math.abs(tx);//get absolute value so no negatives
                    ty = Math.abs(ty);

                    pixel = texure.getSubimage((int)tx, (int)ty, 1, 1);
                    g.setColor(new Color(pixel.getRGB(0, 0)));
                    g.fillRect(r*res, s.getHeight()/2 - (yy-s.getHeight()/2), res*resG, res);//ceiling
                }
            } catch(Exception e){ }

        }
    }
    
    public double dist(double ax, double ay, double bx, double by){
        double num = ((bx-ax)*(bx-ax) + (by-ay)*(by-ay));//pythag
        
        return Math.sqrt(num);
    }
    //for collisions
    public Rectangle top(){
        return new Rectangle((int)x-5, (int)y-25, 10, 25);
    }
    public Rectangle bottom(){
        return new Rectangle((int)x-5, (int)y, 10, 25);
    }
    public Rectangle left(){
        return new Rectangle((int)x-25, (int)y-5, 25, 10);
    }
    public Rectangle right(){
        return new Rectangle((int)x, (int)y-5, 25, 10);
    }
}
/*
//------------Drawing Objects---------------
            LinkedList<double[]> objects1;
            LinkedList<double[]> objects2;
            
            if(objectsH.size() >= objectsV.size()){
                objects1 = objectsH;
                objects2 = objectsV;
            } else{
                objects1 = objectsV;
                objects2 = objectsH;
            }
            //System.out.println(objectsH.size() + " " + objectsV.size());
            for(int h = objects1.size()-1; h >= 0; h--){
                double[] info1 = objects1.get(h);
                int mapXH = (int)(info1[1]/100);
                int mapYH = (int)(info1[2]/100);
                drawObject(g, info1[0], info1[1], info1[2], info1[3], info1[5], ra);
                boolean drawn = false;
                
                if(disT/Math.cos(ca) >= info1[3]){//not going to draw if drawn block is closer
                    for(int v = objects2.size()-1; v >= 0; v--){
                        double[] info2 = objects2.get(v);
                        drawObject(g, info2[0], info2[1], info2[2], info2[3], info2[5], ra);
                        int mapXV = (int)(info2[1]/100);
                        int mapYV = (int)(info2[2]/100);

                        if(disT/Math.cos(ca) >= info2[3]){
                            drawn = true;
                            if(mapXH == mapXV && mapYH == mapYV){//if they hit the same block
                                //if(info1[3] > info2[3]) drawObject(g, info1[0], info1[1], info1[2], info1[3], info1[5], ra);
                                //else drawObject(g, info2[0], info2[1], info2[2], info2[3], info2[5], ra);
                            }
                        }
                    }
                }
*/
package raycaster;

import java.util.LinkedList;

public class Ray {
    Screen s;
    double dr = (Math.PI*2)/360;//one degree in radians
    double info[] = new double[6];
    LinkedList<double[]> objects;
    
    public Ray(Screen s){
        this.s = s;
    }
    public void shootRay(double ra, double x, double y, int[][] map){
        //r means the ray, rx = x coordinate of the ray
        double xOff=0, yOff=0, rx=0, ry=0;
        //ray angle, x point where ray will hit, y point where ray will hit, x offset to check next line, y offset to check next line, final distance of that ray
        int levelHeight = s.lm.levelImg.getHeight()*100;
        int levelWidth = s.lm.levelImg.getWidth()*100;
        
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
                hx = rx;
                hy = ry;
                disH = dist(x, y, hx, hy);
                dof = levelHeight/100;
                blockH = s.lm.level[mx][my];
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
                vx = rx;
                vy = ry;
                disV = dist(x, y, vx, vy);
                dof = levelWidth/100;
                blockV = s.lm.level[mx][my];
            } else{
                rx += xOff;
                ry += yOff;
                dof++;
            }
        }

        //------------Finding what line is bigger---------------
        if(disH < disV){//horizontal ray is smaller than vertical one
            info[0] = hx; info[1] = hy; info[4] = disH; info[5] = blockH;
        }
        else if(disV < disH){//vertical ray is smaller than horizontal one
            info[0] = vx; info[1] = vy; info[4] = disV; info[5] = blockV;
        }
        info[2] = disH;
        info[3] = disV;
    }
    public void shootRay2(double ra, double x, double y, int[][] map){
        //System.out.println(ra + " " + x + " " + y + " " + map);
        
        //r means the ray, rx = x coordinate of the ray
        double xOff=0, yOff=0, rx=0, ry=0;
        //ray angle, x point where ray will hit, y point where ray will hit, x offset to check next line, y offset to check next line, final distance of that ray
        int levelHeight = s.lm.levelImg.getHeight()*100;
        int levelWidth = s.lm.levelImg.getWidth()*100;
        
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

        //casting
        while(dof < levelHeight/100){
            int mx = (int)(rx/100), my = (int)(ry/100);
            //limits so it doesnt go out of map array
            if(mx < 0) mx = 0; if(my < 0) my = 0;
            if(mx > levelWidth/100 -1) mx = levelWidth/100 -1;
            if(my > levelHeight/100 -1) my = levelHeight/100 -1;

            if(map[mx][my] != 0){//if is a block
                hx = rx;
                hy = ry;
                disH = dist(x, y, hx, hy);
                dof = levelHeight/100;
                blockH = map[mx][my];
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

        //casting
        while(dof < levelWidth/100){
            int mx = (int)(rx/100), my = (int)(ry/100);
            //limits so it doesnt go out of map array
            if(mx < 0) mx = 0; if(my < 0) my = 0;
            if(mx > levelWidth/100 -1) mx = levelWidth/100 -1;
            if(my > levelHeight/100 -1) my = levelHeight/100 -1;

            if(map[mx][my] != 0){//is a block
                vx = rx;
                vy = ry;
                disV = dist(x, y, vx, vy);
                dof = levelWidth/100;
                blockV = map[mx][my];
            } else{
                rx += xOff;
                ry += yOff;
                dof++;
            }
        }

        //------------Finding what line is bigger---------------
        if(disH < disV){//horizontal ray is smaller than vertical one
            info[0] = hx; info[1] = hy; info[4] = disH; info[5] = blockH;
        }
        else if(disV < disH){//vertical ray is smaller than horizontal one
            info[0] = vx; info[1] = vy; info[4] = disV; info[5] = blockV;
        }
        info[2] = disH;
        info[3] = disV;
    }
    public double dist(double ax, double ay, double bx, double by){
        double num = ((bx-ax)*(bx-ax) + (by-ay)*(by-ay));//pythag

        return Math.sqrt(num);
    }
    /*
    public void shootRay(int r, double ra, double x, double y, boolean hitObj, int[][] map){
        objects = new LinkedList<>();
        
        //r means the ray, rx = x coordinate of the ray
        double xOff=0, yOff=0, rx=0, ry=0;
        //ray angle, x point where ray will hit, y point where ray will hit, x offset to check next line, y offset to check next line, final distance of that ray
        int levelHeight = s.lm.levelImg.getHeight()*100;
        int levelWidth = s.lm.levelImg.getWidth()*100;
        
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
                if(hitObj){
                    hx = rx;
                    hy = ry;
                    disH = dist(x, y, hx, hy);
                    dof = levelHeight/100;
                    blockH = s.lm.level[mx][my];
                } else{
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
                if(hitObj){
                    vx = rx;
                    vy = ry;
                    disV = dist(x, y, vx, vy);
                    dof = levelWidth/100;
                    blockV = s.lm.level[mx][my];
                } else{
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
                }
            } else{
                rx += xOff;
                ry += yOff;
                dof++;
            }
        }

        //------------Finding what line is bigger---------------
        if(disH < disV){//horizontal ray is smaller than vertical one
            info[0] = hx; info[1] = hy; info[4] = disH; info[5] = blockH;
        }
        else if(disV < disH){//vertical ray is smaller than horizontal one
            info[0] = vx; info[1] = vy; info[4] = disV; info[5] = blockV;
        }
        info[2] = disH;
        info[3] = disV;
    }
    
    
            //------------Drawing Objects---------------
            
            double[] distances = new double[objects.size()];
            for(int i = 0; i < objects.size(); i++) distances[i] = objects.get(i)[3];
            Arrays.sort(distances);
            
            for(int i = distances.length-1; i >= 0; i--){
                double dist = distances[i];
                
                if(disT/Math.cos(ca) >= dist){
                    for(int h = 0; h < objects.size(); h++){
                        double[] info = objects.get(h);
                        if(info[3] == dist) drawObject(g, info[0], info[1], info[2], info[3], -1*info[5]-2, ra, dark);
                    }
                }
            }
    
    public void drawObject(Graphics g, double r, double rx, double ry, double disT, double blockT, double ra, float dark){
        int middleX = (int)(rx/100) * 100 + 50;//middle of block
        int middleY = (int)(ry/100) * 100 + 50;//middle of block
        double distA = Math.atan2(y - middleY, x - middleX);//angle of distance
        double horzPa = pa+(0.5*Math.PI);//horizontal angle relative to pa angle

        double angX = limitFor(distA - horzPa);

        disT = dist(x, y, middleX, middleY);//distance from player to center of block
        disT = disT * Math.sin(angX);

        double horzA = ra+(0.5*Math.PI);//horizontal angle relative to ra angle
        double hitA = Math.atan2(ry - middleY, rx - middleX);//angle of hit point to center of block

        double angle = limitFor(horzA - hitA);//angle between the horizontal angle and hit point

        double distance = dist(rx, ry, middleX, middleY);//distance from hit point to center of lock
        distance = distance*Math.cos(angle);//the x distance from hit point to center of block relative to the rays angle
        
        double lineH = (100*s.getWidth())/disT;
        double lineY = (s.getHeight()/2)-(lineH/2) + (height/disT);
        
        double loc = 50+(int)distance;

        BufferedImage img = (s.sm.grabSubTex(s.sm.objects[(int)blockT], loc, res, 100));
        if(img != null){
            //RescaleOp op = new RescaleOp(new float[]{dark, dark, dark, 1f}, new float[]{0, 0, 0, 0}, null);
            //img = op.filter(img, null);
            g.drawImage(img, (int)r*res, (int)lineY, res, (int)lineH, s);
        }
    }
    */
}

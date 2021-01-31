package raycaster;

public class Ray {
    Screen s;
    double dr = (Math.PI*2)/360;//one degree in radians
    double rx, ry, blockT, disT;
    
    public Ray(Screen s){
        this.s = s;
    }
    public void shootRay(double ra, double x, double y){
        //r means the ray, rx = x coordinate of the ray
        double xOff=0, yOff=0;
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
            rx = hx; ry = hy; disT = disH; blockT = blockH;
        }
        else if(disV < disH){//vertical ray is smaller than horizontal one
            rx = vx; ry = vy; disT = disV; blockT = blockV;
        }
    }
    public double dist(double ax, double ay, double bx, double by){
        double num = ((bx-ax)*(bx-ax) + (by-ay)*(by-ay));//pythag

        return Math.sqrt(num);
    }
}

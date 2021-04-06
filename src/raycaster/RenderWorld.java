package raycaster;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;

public class RenderWorld {
    Screen s;
    
    double x, y, pa;
    
    double dr = (Math.PI*2)/360;//one degree in radians
    int res = 2;//resolution
    int resG = 2;//ground resolution
    
    int height = -7500;
    
    int light = 500;
    int depth = 900;
    
    BufferedImage sunset;
    
    public RenderWorld(Screen s){
        this.s = s;
        
        sunset = s.sm.loadImage("paint\\sunset.jpg");
    }
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        
        x = s.p.x;
        y = s.p.y;
        pa = s.p.pa;
        
        moveSky(g);
        
        draw3DWorld(g);
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
        double ra;//ray angle
        ra = pa-dr*30;//starting angle
        if(ra < 0) ra += 2*Math.PI; if(ra > 2*Math.PI) ra -= 2*Math.PI;//limits for that angle
        
        double[] wallDists = new double[600/res];
        for(int r = 0; r < 600/res; r++){//making rays
            Ray ray = new Ray(s);
            ray.shootRay(ra, x, y, s.lm.level);
            double rx = ray.info[0], ry = ray.info[1];
            double disH = ray.info[2], disV = ray.info[3], disT = ray.info[4];
            int blockT = (int)ray.info[5];
            
            //fixing weird camera effect
            double ca = pa-ra;
            if(ca < 0) ca += 2*Math.PI; if(ca > 2*Math.PI) ca -= 2*Math.PI;
            disT = disT*Math.cos(ca);

            double loc = 0;//location where the ray hit the block
            boolean flip = false;
            if(disH < disV){
                loc = -1*(((int)(rx/100)*100)-(int)rx);
                if(ra < Math.PI) flip = true;
            }
            else if(disV < disH){
                loc = -1*(((int)(ry/100)*100)-(int)ry);
                if(ra > Math.PI/2 && ra < 1.5*Math.PI) flip = true;
            }

            double lineH = (100*s.getWidth())/disT;
            double lineY = (s.getHeight()/2)-(lineH/2) + (height/disT);
            
            //------------Drawing Ceiling And Ground---------------
            
            if(r % resG == 0) CeilingAndGround(g, r, ra, lineY, lineH);
            
            //------------Drawing Walls---------------
            
            BufferedImage img;
            if(flip) img = (s.sm.grabSubTex(s.sm.flippedTex[blockT], loc, res, 100));
            else img = (s.sm.grabSubTex(s.sm.textures[blockT], loc, res, 100));
            //shading
            float dark = (float)(depth-(disT/Math.cos(ca)))/light;
            if(dark > 1) dark = 1;
            
            //RescaleOp op = new RescaleOp(new float[]{dark, dark, dark}, new float[]{0, 0, 0}, null);
            //img = op.filter(img, null);
            g.drawImage(img, r*res, (int)lineY, res, (int)lineH, s);
            
            wallDists[r] = disT/Math.cos(ca);
            ra+=dr/10 * res;//add another degree
            if(ra < 0) ra += 2*Math.PI; if(ra > 2*Math.PI) ra -= 2*Math.PI;//limits for new degree
        }
        drawObjects(g, wallDists);
    }
    public void drawObjects(Graphics g, double[] wallDists){
        LinkedList<MovingObject> objToRender = new LinkedList<>();
        int renderNum = 0;
        for(MovingObject obj : s.l.mObj){
            double angToPlayer = Math.atan2((obj.y - y), (obj.x - x));
            if(Math.abs(diff(pa, angToPlayer)) < dr*35){//is in players feild of veiw
                objToRender.add(obj);
                renderNum++;
            }
        }
        double[] distances = new double[renderNum];
        for(int i = 0; i < objToRender.size(); i++){
            MovingObject obj = objToRender.get(i);
            double distToPlayer = dist(x, y, obj.x, obj.y);
            distances[i] = distToPlayer;
        }
        Arrays.sort(distances);
        LinkedList<MovingObject> objInOrder = new LinkedList<>();
        for(int i = distances.length-1; i >= 0; i--){
            double dist = distances[i];
            
            for(MovingObject obj : objToRender){
                double distToPlayer = dist(x, y, obj.x, obj.y);
                if(distToPlayer == dist) objInOrder.add(obj);
            }
        }
        
        for(MovingObject obj : objInOrder){
            double angToPlayer = Math.atan2((obj.y - y), (obj.x - x));
            double distToPlayer = dist(x, y, obj.x, obj.y);

            double ang = limitFor(-(pa-angToPlayer));
            
            double yDis = Math.cos(ang)*distToPlayer;
            double objSize = (100*s.getWidth())/yDis;
            double objY = s.getHeight()/2 - objSize/2  + (height/distToPlayer);
            
            ///*
            double xDis = Math.abs(diff(pa, angToPlayer)) * distToPlayer;
            if(Math.sin(ang)*distToPlayer < 0) xDis *= -1;
            
            double screenSpace = (60*dr) * distToPlayer;
            double scale = s.getWidth()/screenSpace;
            double objX = s.getWidth()/2 + (s.getWidth() - (screenSpace-xDis) * scale) - (objSize/2);
            
            //if(obj == s.l.mObj.get(5)) System.out.println(Math.sin(ang)*distToPlayer);
            //*/
            /*
            double xDis = Math.sin(ang)*distToPlayer;
            double screenSpace = 2*(Math.tan(dr*30)*yDis);
            double scale = (s.getWidth()) / screenSpace;
            double objX = (s.getWidth()/2) + (xDis*scale) - (objSize/2);
            */
            
            for(int xx = 0; xx < (int)objSize; xx++){
                int loc = ((int)objX + xx)/res;
                if(loc < wallDists.length && loc > 0){
                    if(!(wallDists[loc] < distToPlayer)){
                        g.drawImage(s.sm.grabSubTex(obj.sprite, xx, 1, (int)objSize), (int)objX + xx, (int)objY, 1, (int)objSize, s);
                        wallDists[loc] = distToPlayer;
                    }
                }
            }
        }
    }
    public void CeilingAndGround(Graphics g, int r, double ra, double lineY, double lineH){
        for(int yy = (int)(lineY+lineH); yy < s.getHeight(); yy++){
            double mid = yy - s.getHeight()/2;
            double raFix = Math.cos(pa-ra);
            
            try{
                double tx = x/2 + Math.cos(ra)*(145+(height/200))*100/mid/raFix;
                double ty = y/2 + Math.sin(ra)*(145+(height/200))*100/mid/raFix;
                double dist = dist(x/2, y/2, tx, ty);

                int mx = (int)(tx/50);
                int my = (int)(ty/50);
                //----------Floor-------------
                int tex = s.lm.floor[mx][my];
                
                BufferedImage texture = s.sm.textures[tex];
                int texSize = texture.getHeight();

                tx = (int)(tx)%50;//break into blocks
                ty = (int)(ty)%50;

                tx = (tx/50) * texSize;//find location on the texure relative to block
                ty = (ty/50) * texSize;

                tx = Math.abs(tx);//get absolute value so no negatives
                ty = Math.abs(ty);
                
                float dark = (float)(depth-(dist*2))/(light*2);
                dark = 1;
                
                g.setColor(changeColor(new Color((texture.getRGB((int)tx, (int)ty))), dark));
                g.fillRect(r*res, yy, res*resG, res);//floor
                
                //----------Ceiling-------------
                tx = x/2 + Math.cos(ra)*(145-(height/200))*100/mid/raFix;
                ty = y/2 + Math.sin(ra)*(145-(height/200))*100/mid/raFix;

                mx = (int)(tx/50);
                my = (int)(ty/50);
            
                if(s.lm.floor[mx][my] >= 0){
                    tex = s.lm.ceiling[mx][my];
                    texture = s.sm.textures[tex];
                    texSize = texture.getHeight();

                    tx = (int)(tx)%50;//break into blocks
                    ty = (int)(ty)%50;

                    tx = (tx/50) * texSize;//find location on the texure relative to block
                    ty = (ty/50) * texSize;

                    tx = Math.abs(tx);//get absolute value so no negatives
                    ty = Math.abs(ty);

                    dark = (float)(depth-(dist*3))/(light*3);
                    dark = 1;
                    
                    g.setColor(changeColor(new Color((texture.getRGB((int)tx, (int)ty))), dark));
                    g.fillRect(r*res, s.getHeight()/2 - (yy-s.getHeight()/2), res*resG, res);//ceiling
                }
            } catch(Exception e){ }
        }
    }
    public Color changeColor(Color color, double change){
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        
        double red2 = red*change;
        if(red2 > 255) red2 = 255; if(red2 < 0) red2 = 0;
        double green2 = green*change;
        if(green2 > 255) green2 = 255; if(green2 < 0) green2 = 0;
        double blue2 = blue*change;
        if(blue2 > 255) blue2 = 255; if(blue2 < 0) blue2 = 0;
        
        return new Color((int)red2, (int)green2, (int)blue2);
    }
    
    public int radToDeg(double ang){
        return (int)(ang*360 / (2*Math.PI));
        //return (int)(ang*60 / (2*Math.PI))/60;
    }
    public double limitFor(double angle){
        if(angle < 0) angle += 2*Math.PI; if(angle > 2*Math.PI) angle -= 2*Math.PI;//limits for new degree
        return angle;
    }
    public double diff(double ang1, double ang2){
        double finalDiff;
        double diff1 = ang1 - ang2;
        double diff2 = Math.PI*2 - diff1;
        
        if(Math.abs(diff1) < Math.abs(diff2)) finalDiff = diff1;
        else finalDiff = diff2;
        
        return finalDiff;
    }
    
    public double dist(double ax, double ay, double bx, double by){
        double num = ((bx-ax)*(bx-ax) + (by-ay)*(by-ay));//pythag
        
        return Math.sqrt(num);
    }
}
package raycaster;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class List {
    Screen s;
    
    LinkedList<Block> b = new LinkedList<>();
    
    public List(Screen s){
        this.s = s;
    }
    public void tick(){
        for(int i = 0; i < b.size(); i++){
            Block tempBlock = b.get(i);
            //collisions
            if(s.p.top().intersects(tempBlock.block())) s.p.y = tempBlock.y+125;
            if(s.p.bottom().intersects(tempBlock.block())) s.p.y = tempBlock.y-25;
            if(s.p.left().intersects(tempBlock.block())) s.p.x = tempBlock.x+125;
            if(s.p.right().intersects(tempBlock.block())) s.p.x = tempBlock.x-25;
        }
        
    }
    public void paint(Graphics g){//for 2d testing
        for(int i = 0; i < b.size(); i++){
            Block tempBlock = b.get(i);
            tempBlock.paint(g);
        }
    }
    public void miniMap(Graphics g){
        g.setColor(new Color(255, 255, 255, 150));
        g.fillRect(0, 0, s.getWidth(), s.getHeight());
        
        for(int mx = 0; mx < s.lm.floorImg.getWidth(); mx++){
            for(int my = 0; my < s.lm.floorImg.getHeight(); my++){
                int scale = 0, xOff = 0, yOff = 0;
                if(s.lm.levelImg.getWidth() > s.lm.levelImg.getHeight()){
                    scale = s.getWidth()/s.lm.levelImg.getWidth();
                    yOff = s.getHeight()/2 - (s.lm.levelImg.getHeight()*scale)/2;
                }
                else{
                    scale = s.getHeight()/s.lm.levelImg.getHeight();
                    xOff = s.getWidth()/2 - (s.lm.levelImg.getWidth()*scale)/2;
                }
                int tex = s.lm.floor[mx][my];
                if(tex >= 0){
                    g.drawImage(s.sm.textures[tex], mx*scale + xOff, my*scale + yOff, scale, scale, s);
                }
            }
        }
        
        for(int i = 0; i < b.size(); i++){
            Block tempBlock = b.get(i);
            
            g.setColor(Color.black);
            int scale = 0, xOff = 0, yOff = 0;
            if(s.lm.levelImg.getWidth() > s.lm.levelImg.getHeight()){
                scale = s.getWidth()/s.lm.levelImg.getWidth();
                yOff = s.getHeight()/2 - (s.lm.levelImg.getHeight()*scale)/2;
            }
            else{
                scale = s.getHeight()/s.lm.levelImg.getHeight();
                xOff = s.getWidth()/2 - (s.lm.levelImg.getWidth()*scale)/2;
            }
            BufferedImage texture;
            int mx = (int)(tempBlock.x/100);
            int my = (int)(tempBlock.y/100);
            int tex = s.lm.level[mx][my];
            
            if(tex >= 0) texture = s.sm.textures[tex];
            else{
                tex = -tex-2;
                texture = s.sm.objects[tex];
            }
            
            tempBlock.paintMini(g, texture, scale, xOff, yOff);
        }
    }
}
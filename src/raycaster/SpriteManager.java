package raycaster;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public final class SpriteManager {
    Screen s;
    
    BufferedImage[] textures = new BufferedImage[10];
    BufferedImage[] flippedTex = new BufferedImage[10];
    
    BufferedImage[] objects = new BufferedImage[4];
    
    public SpriteManager(Screen s){
        this.s = s;
        
        init();
    }
    public void init(){
        //making/loading textures
        textures[0] = loadImage("paint\\textures\\fence.png");
        flippedTex[0] = flipImage(textures[0]);
        textures[1] = loadImage("paint\\textures\\wood.png");
        flippedTex[1] = flipImage(textures[1]);
        textures[2] = loadImage("paint\\textures\\stone.jpg");
        flippedTex[2] = flipImage(textures[2]);
        textures[3] = loadImage("paint\\textures\\redbrick.png");
        flippedTex[3] = flipImage(textures[3]);
        textures[4] = loadImage("paint\\textures\\eagle.png");
        flippedTex[4] = flipImage(textures[4]);
        textures[5] = loadImage("paint\\textures\\bluestone.png");
        flippedTex[5] = flipImage(textures[5]);
        textures[6] = loadImage("paint\\textures\\colorstone.png");
        flippedTex[6] = flipImage(textures[6]);
        textures[7] = loadImage("paint\\textures\\grass.png");
        flippedTex[7] = flipImage(textures[7]);
        textures[8] = loadImage("paint\\textures\\wood2.png");
        flippedTex[8] = flipImage(textures[8]);
        textures[9] = loadImage("paint\\textures\\wood3.png");
        flippedTex[9] = flipImage(textures[9]);
        
        objects[0] = loadImage("paint\\objects\\tree.png");
        objects[1] = loadImage("paint\\objects\\barrel.png");
        objects[2] = loadImage("paint\\objects\\fire.png");
        objects[3] = loadImage("paint\\objects\\greenlight.png");
    }
    
    public BufferedImage loadImage(String path){
        BufferedImage tempImage = null;
        try {
            tempImage = ImageIO.read(new FileInputStream(path));
        } catch (IOException ex) {
            System.out.println("Could not load " + path);
            //ex.printStackTrace();
        }
        return tempImage;
    }
    public BufferedImage grabSubTex(BufferedImage tex, double x, int width){
        BufferedImage img;
        try{
            double imgX = (x/100)*tex.getWidth();//getting location relative to image
            double imgW = ((double)width/100)*tex.getWidth();//getting width relative to image
            if(imgW < 1) imgW = 1;

            if((imgX+imgW) > tex.getWidth()) imgW = tex.getWidth()-(int)imgX;
            img = tex.getSubimage((int)imgX, 0, (int)imgW, tex.getHeight());
        } catch(Exception e){
            img = new BufferedImage(width, tex.getHeight(), BufferedImage.TRANSLUCENT);
        }
        
        return img;
    }
    public BufferedImage flipImage(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                flipped.setRGB((width-1)-x, y, img.getRGB(x, y));
            }
        }
        return flipped;
    }
    public void rotateImage(BufferedImage img, double degree, int x, int y, int[] point, int w, int h, Graphics2D g2){
        g2.rotate(degree, point[0], point[1]);
        g2.drawImage(img, x, y, w, h, s);
        g2.rotate(-degree, point[0], point[1]);
    }
}
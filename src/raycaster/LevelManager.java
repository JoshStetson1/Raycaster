package raycaster;
import java.awt.image.BufferedImage;

public class LevelManager {
    Screen s;
    
    BufferedImage levelImg, floorImg, ceilingImg;
    
    int[][] level, floor, ceiling;
    
    public LevelManager(Screen s){
        this.s = s;
    }
    public void loadLevel(String l, String f, String c){
        levelImg = s.sm.loadImage("paint\\levels\\" + l + ".png");
        floorImg = s.sm.loadImage("paint\\levels\\" + f + ".png");
        ceilingImg = s.sm.loadImage("paint\\levels\\" + c + ".png");
        
        createLevel(levelImg);
        floor = create(floorImg);
        ceiling = create(ceilingImg);
    }
    public void createLevel(BufferedImage img){
        int w = img.getWidth();
        int h = img.getHeight();
        level = new int[w][h];//making level array
        
        //go through the level image
        for(int yy = 0; yy < h; yy++){
            for(int xx = 0; xx < w; xx++){
                int pixel = img.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                
                boolean addBlock = true;
                
                //blocks
                if(red == 255 && green == 255 && blue == 0) level[xx][yy] = 0;
                else if(red == 0 && green == 0 && blue == 0) level[xx][yy] = 1;
                else if(red == 127 && green == 127 && blue == 127) level[xx][yy] = 2;
                else if(red == 255 && green == 0 && blue == 0) level[xx][yy] = 3;
                else if(red == 255 && green == 0 && blue == 255) level[xx][yy] = 4;
                else if(red == 0 && green == 255 && blue == 255) level[xx][yy] = 5;
                else if(red == 127 && green == 64 && blue == 0) level[xx][yy] = 6;
                else if(red == 0 && green == 255 && blue == 0) level[xx][yy] = 7;
                
                //objects
                else if(red == 191 && green == 191 && blue == 191) level[xx][yy] = -2;
                else if(red == 191 && green == 127 && blue == 0) level[xx][yy] = -3;
                else if(red == 255 && green == 127 && blue == 0) level[xx][yy] = -4;
                
                //other
                else if(red == 0 && green == 0 && blue == 255){//pixel is blue, set player position
                    s.p.x = xx*100 + 50;
                    s.p.y = yy*100 + 50;
                    level[xx][yy] = -1;
                    addBlock = false;
                } else{
                    level[xx][yy] = -1;
                    if(red != 255) System.out.println(red + " " + green + " " + blue);
                    addBlock = false;
                }

                if(addBlock) s.l.b.add(new Block(s, xx*100, yy*100, s.l.b.size()+1));
            }
        }
    }
    public int[][] create(BufferedImage img){
        int w = img.getWidth();
        int h = img.getHeight();
        int[][] level = new int[w][h];//making level array
        
        //go through the level image
        for(int yy = 0; yy < h; yy++){
            for(int xx = 0; xx < w; xx++){
                int pixel = img.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if(red == 255 && green == 255 && blue == 0) level[xx][yy] = 0;
                else if(red == 0 && green == 0 && blue == 0) level[xx][yy] = 1;
                else if(red == 127 && green == 127 && blue == 127) level[xx][yy] = 2;
                else if(red == 255 && green == 0 && blue == 0) level[xx][yy] = 3;
                else if(red == 255 && green == 0 && blue == 255) level[xx][yy] = 4;
                else if(red == 0 && green == 255 && blue == 255) level[xx][yy] = 5;
                else if(red == 89 && green == 77 && blue == 64) level[xx][yy] = 6;
                else if(red == 0 && green == 255 && blue == 0) level[xx][yy] = 7;
                else if(red == 191 && green == 127 && blue == 0) level[xx][yy] = 8;
                else if(red == 166 && green == 127 && blue == 77) level[xx][yy] = 9;
                else level[xx][yy] = -1;
            }
        }
        
        return level;
    }
}
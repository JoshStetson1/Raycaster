package raycaster;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public final class Screen extends JPanel implements ActionListener, KeyListener, MouseListener{
    JFrame frame;
    
    Timer t = new Timer(10, this);
    SpriteManager sm = new SpriteManager(this);
    Player p = new Player(this);
    List l = new List(this);
    LevelManager lm = new LevelManager(this);
    
    int posX, posY;//position of window
    boolean miniMap;
    
    long nowTime, pastTime;
    int frames, fps = 80;
    double deltaTime = 1;
    
    public Screen(JFrame frame){
        this.frame = frame;
        
        init();
        
        t.start();
    }
    public void init(){
        posX = frame.getX();
        posY = frame.getY();
        
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        
        lm.loadLevel("level3", "level3Ground", "level3Top");
        
        nowTime = System.nanoTime();
    }
    public void actionPerformed(ActionEvent e) {//is called every frame
        //frame coordinates are updated incase window is moved
        posX = frame.getX();
        posY = frame.getY();
        
        p.tick();
        l.tick();
        
        repaint();
    }
    public void paint(Graphics g){
        g.clearRect(0, 0, getWidth(), getHeight());
        Graphics2D g2 = (Graphics2D)g;
        
        p.paint(g);
        //l.paint(g);
        if(miniMap) l.miniMap(g);
        
        FPS(g);
    }
    public void FPS(Graphics g){
        frames++;
        if(System.nanoTime() > nowTime+1000000000){
            fps = frames;
            frames = 0;
            nowTime = System.nanoTime();
        }
        //delta time
        deltaTime = System.nanoTime() - pastTime;
        deltaTime = deltaTime/10000000;
        pastTime = System.nanoTime();
        
        if(deltaTime > 5.5) deltaTime = 5.5;
        
        g.setColor(Color.white);
        g.setFont(new Font("arial", Font.BOLD, 20));
        g.drawString("FPS: " + Integer.toString(fps), 0, 20);
    }
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if(key == KeyEvent.VK_SPACE) miniMap = !miniMap;
        
        if(key == KeyEvent.VK_W) p.moving[0] = true;
        if(key == KeyEvent.VK_S) p.moving[1] = true;
        if(key == KeyEvent.VK_LEFT) p.moving[2] = true;
        if(key == KeyEvent.VK_RIGHT) p.moving[3] = true;
        
        if(key == KeyEvent.VK_A) p.moving[4] = true;
        if(key == KeyEvent.VK_D) p.moving[5] = true;
        
        if(key == KeyEvent.VK_UP) p.size++;
        if(key == KeyEvent.VK_DOWN) p.size--;
    }
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if(key == KeyEvent.VK_W) p.moving[0] = false;
        if(key == KeyEvent.VK_S) p.moving[1] = false;
        if(key == KeyEvent.VK_LEFT) p.moving[2] = false;
        if(key == KeyEvent.VK_RIGHT) p.moving[3] = false;
        
        if(key == KeyEvent.VK_A) p.moving[4] = false;
        if(key == KeyEvent.VK_D) p.moving[5] = false;
    }
    public void moveMouse(Point p) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        // Search the devices for the one that draws the specified point.
        for (GraphicsDevice device: gs) {
            GraphicsConfiguration[] configurations = device.getConfigurations();
            for (GraphicsConfiguration config: configurations) {
                Rectangle bounds = config.getBounds();
                if(bounds.contains(p)) {
                    // Set point to screen coordinates.
                    Point b = bounds.getLocation(); 
                    Point s = new Point(p.x - b.x, p.y - b.y);

                    try {
                        Robot r = new Robot(device);
                        r.mouseMove(s.x, s.y);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
        }
        // Couldn't move to the point, it may be off screen.
        return;
    }
    public void mousePressed(MouseEvent e) {
        Ray shot = new Ray(this);
        shot.shootRay(p.pa, p.x, p.y);
        
        if(shot.blockT == -3){
            int mx = (int)(shot.rx/100);
            int my = (int)(shot.ry/100);
            
            lm.level[mx][my] = -1;
        }
    }
    public void mouseReleased(MouseEvent e) {}
    
    public void keyTyped(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
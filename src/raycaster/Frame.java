package raycaster;
import javax.swing.*;

public class Frame {
    public static void main(String[] args) {
        JFrame f = new JFrame("Raycaster");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(606, 635);
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        Screen s = new Screen(f);
        f.add(s);
        f.setVisible(true);
    }
}
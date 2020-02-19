package tetris;

import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 *
 * @author Jesse Bordoe
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        TetrisApplet app = new TetrisApplet();

        JFrame frame = new JFrame("737R15");
        frame.add(app);
        frame.setContentPane(app);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(350,600);
        frame.setVisible(true);

        app.init();
        app.start();
    }
}
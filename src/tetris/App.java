package tetris;

import java.applet.*;
import java.awt.*;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;

/**
 *
 * @author Jesse Bordoe
 */
public class App extends JFrame {

    private BufferedImage gridImg;
    GridRenderer gridRenderer;
    /* Tetris game object */
    Game game;
    /** width of grid, in blocks */
    final int gridWidth = 10;
    /** height of grid, in blocks */
    final int gridHeight = 22;
    /** width of blocks, in pixels */
    final int blockWidth = 20;
    /** height of blocks, in pixels */
    final int blockHeight = 20;
    double[] dumbWeights = new double[]{
        0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0
    };

    static final short[] weightsToDisplay = new short[]{
        AIWeights.HEIGHT,
        AIWeights.GAPS,
        AIWeights.LINE,
        AIWeights.BLOCKS,
    };

    static final HashMap<Short, Double> weightTextFieldMap;
    static {
        weightTextFieldMap = new HashMap<Short, Double>();
        //weightTextFieldMap.put(AIWeights.HEIGHT, )
    }
    /** frames-per-second */
    final int frameRate = 20000;

    boolean reload = false;

    // GUI STUFF
    JLabel gridLabel;

    public App() {
        super("737R15");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        App app = new App();
        app.setupGUI();
        while (true) {
            app.runGame();
        }
    }

    public void runGame() {
        PlayerInterface player = new AIPlayer(dumbWeights);
        //KeyboardPlayer player = new KeyboardPlayer();
        //addKeyListener(player.getKeyAdapter());
        
        gridRenderer = new SimpleGridRenderer();
        gridRenderer.setBlockHeight(blockHeight);
        gridRenderer.setBlockWidth(blockWidth);

        game = new Game(gridWidth, gridHeight, player, Game.PlayState.AI);
        game.initGame();
        game.setState(Game.GameState.Play);
        game.setPlayState(Game.PlayState.AI);
        updateGridImage();

        long nextFrame;
        long sleepTime;
        while (true) {
            if (reload) {
                reload = false;
                break;
            }
            nextFrame = System.currentTimeMillis() + (1000 / frameRate);
            game.nextFrame();
            // TODO: generate new bufferedImage BEFORE sleep
            sleepTime = nextFrame - System.currentTimeMillis();

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            updateGridImage();

        }
    }

    public void setupGUI() {

        final JPanel gridPanel = new JPanel(new BorderLayout());

        int gridImgWidth = gridWidth * blockWidth;
        int gridImgHeight = gridHeight * blockHeight;

        gridImg = new BufferedImage(gridImgWidth, gridImgHeight, BufferedImage.TYPE_INT_RGB);
        gridLabel = new JLabel(new ImageIcon(gridImg));

        gridPanel.add(gridLabel, BorderLayout.CENTER);

        final JPanel textPanel = new JPanel(new FlowLayout());
        //TODO: should have a vector of fields that can map easily to the right weight
        JPanel weight1Label = new JPanel(new BorderLayout());
        TextField weight1 = new TextField(8);
        weight1Label.add(new Label("Line count"), BorderLayout.NORTH);
        weight1Label.add(weight1, BorderLayout.CENTER);

        JPanel weight2Label = new JPanel(new BorderLayout());
        TextField weight2 = new TextField(8);
        weight2Label.add(new Label("Max height"), BorderLayout.NORTH);
        weight2Label.add(weight2, BorderLayout.CENTER);

        JPanel weight3Label = new JPanel(new BorderLayout());
        TextField weight3 = new TextField(8);
        weight3Label.add(new Label("Total blocks"), BorderLayout.NORTH);
        weight3Label.add(weight3, BorderLayout.CENTER);

        JPanel weight4Label = new JPanel(new BorderLayout());
        TextField weight4 = new TextField(8);
        weight4Label.add(new Label("Gap count"), BorderLayout.NORTH);
        weight4Label.add(weight4, BorderLayout.CENTER);

        textPanel.add(weight1Label);
        textPanel.add(weight2Label);
        textPanel.add(weight3Label);
        textPanel.add(weight4Label);

        gridPanel.add(textPanel, BorderLayout.SOUTH);

        final JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                game.setState(Game.GameState.Paused);
            }
        });

        final JButton goButton = new JButton("Play");
        goButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                game.setState(Game.GameState.Play);
            }
        });
        final JButton resetButton = new JButton("Reload");
        resetButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                reload = true;
            }
        });

        final JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.add(stopButton);



        buttonPanel.add(goButton);
        buttonPanel.add(resetButton);

        gridPanel.add(buttonPanel, BorderLayout.EAST);

        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(gridPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);

        requestFocus();

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void updateGridImage() {
        BufferedImage gridImage = gridRenderer.renderGrid(game);
        gridLabel.setIcon(new ImageIcon(gridImage));
    }

//        private BufferedImage getDecoratedGridImage() {
//        BufferedImage image = new BufferedImage(gridImgWidth, gridImgHeight, BufferedImage.TYPE_INT_RGB);
//        BufferedImage gridImage = getGridImage();
//        Graphics2D finalG = image.createGraphics();
//        finalG.drawImage(gridImage, null, gridX, gridY);
//        //print statistics
//        finalG.setPaint(Color.white);
//        finalG.setFont(new Font("Serif", Font.BOLD, 20));
//        finalG.drawString("Jesse's Tetris AI", 400, 100);
//        finalG.drawString("Turns: " + turns, 400, 120);
//        finalG.drawString("Lines: " + rows, 400, 140);
//        return image;
//    }
}

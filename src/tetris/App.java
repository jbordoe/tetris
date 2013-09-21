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
    
    AIWeights weights = new AIWeights();

    static final short[] weightsToDisplay = new short[]{
        AIWeights.HEIGHT,
        AIWeights.GAPS,
        AIWeights.LINE,
        AIWeights.BLOCKS,
        AIWeights.COL_TRANS
    };
    
    public static HashMap<Short, TextField> textFieldWeightMap = new HashMap<Short, TextField>();

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
        
        final JPanel textPanel = getWeightsPanel();

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
    
    private JPanel getWeightsPanel() {
        int weightCount = weightsToDisplay.length;
        
        int cols = 4;
        int rows = new Double(Math.ceil(weightCount/cols)).intValue();
        
        final JPanel weightInputPanel = new JPanel( new GridLayout(cols, rows, 2 ,2) );
        
        for (int i = 0; i < weightCount; i++) {
            short weightKey = weightsToDisplay[i];
            AIWeights.Weight weightData = weights.getWeightData(weightKey);
            String weightLabel = weightData.getLabel();
            
            JPanel weightPanel = new JPanel(new BorderLayout());
            TextField weightText = new TextField(8);
            
            textFieldWeightMap.put(weightKey, weightText);
            
            weightPanel.add(new Label(weightLabel), BorderLayout.NORTH);
            weightPanel.add(weightText, BorderLayout.CENTER);
            
            int currentCol = i % cols;
            int currentRow = new Double(Math.ceil(i/cols)).intValue();
            
            weightInputPanel.add(weightPanel);            
        }      
        JPanel textPanel = new JPanel(new FlowLayout());
        textPanel.add(weightInputPanel);
        
        return textPanel;
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

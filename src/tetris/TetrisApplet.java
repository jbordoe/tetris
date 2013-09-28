package tetris;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Jesse Bordoe
 */
public class TetrisApplet extends JApplet {

    private BufferedImage gridImg;
    GridRenderer gridRenderer;
    /* Tetris game object */
    Game game;
    /** width of grid, in blocks */
    final int gridWidth = 10;
    /** height of grid, in blocks */
    final int gridHeight = 22;
    /** width of blocks, in pixels */
    final int blockWidth = 10;
    /** height of blocks, in pixels */
    final int blockHeight = 10;
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

    /** target frames-per-second */
    static int framesPerSecond = 24;
    static final int MAX_FPS = 2000;
    static final int MIN_FPS = 1;

    boolean reload = false;

    // GUI STUFF
    JLabel gridLabel;

    /**
     * @param args the command line arguments
     */
    @Override
    public void init() {
        setupGUI();     
    }
    
    @Override
    public void start() {
        while (true) {
            runGame();
        }
    }

    public void runGame() {
        
        weights = readWeightInput();
        PlayerInterface player = new AIPlayer(weights.getWeights());
        
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
            nextFrame = System.currentTimeMillis() + (1000 / framesPerSecond);
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

        this.setSize(250,450);
        JPanel gridPanel = new JPanel(new BorderLayout());

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

        final JPanel buttonPanel = new JPanel(new GridLayout(0, 1,15,15));
        buttonPanel.add(stopButton);
        buttonPanel.add(goButton);
        buttonPanel.add(resetButton);
        gridPanel.add(buttonPanel, BorderLayout.EAST);
        
        JSlider fpsSlider = new JSlider(JSlider.VERTICAL, MIN_FPS, MAX_FPS, framesPerSecond);
        fpsSlider.addChangeListener(new SliderListener());
        this.add(fpsSlider, BorderLayout.WEST);

        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.add(gridPanel);

        this.setVisible(true);
        

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
            TextField weightText = new TextField("0.0", 2);
            
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
    
    public AIWeights readWeightInput() {       
        AIWeights newWeights = new AIWeights();
        Iterator it = textFieldWeightMap.keySet().iterator();
        while (it.hasNext()) {
            short weightKey = (Short)it.next();
            
            String weightValueText = textFieldWeightMap.get(weightKey).getText();
            double weightValue = Double.parseDouble(weightValueText);
            
            newWeights.setWeight(weightKey, weightValue);
        }       
        return newWeights;
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
    class SliderListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
                framesPerSecond = (int) source.getValue();
        }
    }
}

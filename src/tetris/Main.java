//package tetris;
//
//
///**
// *
// * @author Jesse Bordoe
// */
//public class Main extends {
//
//    private static Game game;
//    private static BufferedImage grid;
//    private static JLabel gridLabel;
//    private static boolean running = true;
//
//    public Main() {
//        super("737R15");
//        game = new Game();
//        grid = game.getGridImage();
//        gridLabel = new JLabel(new ImageIcon(grid));
//        final JPanel gridPanel = new JPanel();
//        gridPanel.add(gridLabel, BorderLayout.CENTER);
//        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//        add(gridPanel);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        pack();
//        setResizable(false);
//        setVisible(true);
//
//		this.addWindowListener(new WindowAdapter() {
//            @Override
//			public void windowClosing(WindowEvent e) {
//				System.exit(0);
//			}
//		});
//    }
//
//    public void gameLoop(){
//        while(running){
//            try { Thread.sleep(100); } catch (Exception e) {}
//            updateGrid();
//        }
//    }
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(final String[] args){
//                Main tetris = new Main();
//                tetris.gameLoop();
//    }
//
//    private static void updateGrid(){
//        gridLabel.setIcon(new ImageIcon(game.getGridImage()));
//    }
//
//
//}

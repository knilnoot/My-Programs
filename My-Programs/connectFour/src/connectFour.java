/**
 * Created by covingtonbr441 on 9/16/2016.
 */
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.lang.Math;
public class connectFour extends JFrame {
    public connectFour () {
        add (new panel());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setTitle("ConnectFour");
        setResizable(true);
        setVisible(true);
        System.out.println("Program Started!");
    }

    public static void main(String[] args) {
        new connectFour ();
    }
}
class panel extends JPanel implements  ActionListener {
    public static Color blueishbg = new Color (100, 150, 255, 255);
    public static Color bubblythingy = new Color (255, 255, 255, 255);
    public static Color orange = new Color (255, 50, 0);
    public static byte [] [] slots = new byte [7] [6];
    public static byte column = 3;
    public static byte player = 1;
    public static boolean title = true;
    public static int xTiles, yTiles, d1Tiles, d2Tiles = 1;
    public static int bg1 = 100;
    public static int bg2 = 2;
    public static int bgt = 150;
    public static int [] [] bgtv;
    public static double [] [] bgd;
    public static boolean placingReady = false;
    public static boolean randomloop = true;
    public static int numoftiles = 0;
    public static boolean bot1 = false;
    public static boolean bot2 = false;
    public static boolean colorBlindMode = false;
    public static int count = 0;
    public static boolean pWon = false;
    public static int bgFade = 0;
    public static byte arrowAnimation = 0;
    public static boolean arrowMoveDown = true;
    public static boolean help = false;
    public static int konami = 0;
    public static int moveBoardDown = 400;
    public static Font f = new Font("Comic Sans MS", Font.BOLD, 30);
    public static Font f2 = new Font("Comic Sans MS", Font.BOLD, 20);
    public static Font f3 = new Font("Times New Roman", Font.PLAIN, 12);
    private Timer timer;
    // bgtv[][0] = x coord
    // bgtv[][1] = size
    //bgd[][0] = y coord
    //bgd[][1] = speed
    //bgd[][2] = x coord flux (or something like that)

    public panel () {
        bgtv = new int [bg1] [bg2];
        bgd = new double [bg1] [3];
        for (int i = 0; i < bg1; i++) {
            bgtv [i] [0] = (int) (Math.random() * 650 -44.5);
            bgd [i] [0] = (Math.random() * 650 - 24.5);
            bgtv [i] [1] = (int) (Math.random() * 40 + 10.5);
            bgd [i] [1] = (Math.random() * 5 + 1);
            bgd [i] [2] = Math.random() * 100 - 50;
        }
        addKeyListener(new TAdapter ());
        setBackground(Color.black);
        setFocusable(true);
        initGame ();
    }
    public void initGame () {
        timer = new Timer(30, this);
        timer.start ();
        //System.out.println("debug");
    }
    public void reset () {
        pWon = false;
        blueishbg = new Color(100, 150, 255, 255);
        bgFade = 0;
        bot1 = false;
        bot2 = false;
        numoftiles = 0;
        player = 1;
        column = 3;
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 6; j++)
                slots [i] [j] = 0;
    }
    public void update () {
        count++;
        if (!title) {
            if (moveBoardDown > 0)
                moveBoardDown *= .9;
        }
        if (column < 0)
            column = 6;
        if (column > 6)
            column = 0;
        if (placingReady) {
            putDownTile();
            placingReady = false;
        }
        if((player == 1 && bot1 == true) || (player == 2 && bot2 == true)) {
            randomlyPlace();
        }
        for (int i = 0; i < bg1; i++){
            bgd [i] [0] -= bgd [i] [1];
            if(bgd [i] [0] < -bgtv [i] [1]) {
                bgd [i] [0] = 601;
                bgtv [i] [0] = (int) (Math.random() * 650 - 44.5);
                bgtv [i] [1] = (int) (Math.random() * 40 + 10.5);
                bgd [i] [1] = (Math.random() * 5 + 1);
                bgd [i] [2] = Math.random() * 100 - 50;
            }
        }
    }
    /*class myComponent extends JComponent {
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int fontSize = 20;
        Font f = new Font("Comic Sans MS", Font.BOLD, fontSize);
        g2.setFont(f);
        g2.drawString("Hello World", 300, 300);
    }
}*/
    public void paint (Graphics g) {
        //System.out.println("paint");
        super.paint(g);
        if (pWon) {
            if (bgFade < 255)
                bgFade += 2;
            if (bgFade > 255)
                bgFade = 255;
            if(player == 1)
                g.setColor(Color.green);
            else if (player == 2)
                g.setColor(orange);
            else
                g.setColor(Color.black);
            g.fillRect(0, 0, 600, 600);
            blueishbg = new Color(100, 150, 255, 255 - bgFade);
        }
        g.setColor(blueishbg);
        g.fillRect(0, 0, 600, 600);
        for (int i = 0; i < bg1; i++) {
            bubblythingy = new Color (255, 255, 255, bgt);
            g.setColor(bubblythingy);
            g.fillOval((int)(bgtv [i] [0] + Math.sin(count / (bgd [i] [2] * 2)) * bgd [i] [2]), (int) bgd [i] [0], bgtv [i] [1], bgtv [i] [1]);
        }
        g.setColor(Color.gray);
        g.fillOval(142, 250 - moveBoardDown + 400, 300, 200);
        g.setColor(Color.white);
        g.setFont(f);
        g.drawString("ConnectFour", 202, 320 - moveBoardDown + 400);
        g.setFont(f2);
        g.drawString("By: Bryce Covington", 192, 340 - moveBoardDown + 400);
        g.drawString("Press ENTER to start", 187, 360 - moveBoardDown + 400);
        g.setFont(f3);
        paintBoard(g);
        g.setColor(Color.black);
        if (!help && !title)
            g.drawString("press H to toggle help", 5, 10);
        else if (!title) {
            g.setColor(Color.white);
            g.fillRect(0, 0, 600, 60);
            g.setColor(Color.black);
            g.drawString("use \"a\", \"d\", or side arrow keys to move. To place a tile press Enter, \"s\", space bar, or the down arrow key.", 5, 10);
            g.drawString("Use \"r\" to reset, \"z\" to randomly place, and \"c\" to enable colorblind mode.", 5, 25);
            g.drawString("konami code is a thing in this game", 5, 40);
        }
        /*examples:
		 *g.drawRect(xposition, yposition, xlength, ylength);
		 *g.fillRect(xposition, yposition, xlength, ylength);
		 *g.drawString("This is some text" + varable if you want, xposition, yposition);
		 *g.setColor(Color.whatever);
		 */
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
    public void paintBoard (Graphics g) {
        g.setColor(Color.yellow);
        g.fillRect(211, 243 - moveBoardDown, 162, 142);
        g.setColor(Color.black);
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 6; j++)
                g.fillOval(i * 22 + 213, j * 22 + 248 - moveBoardDown, 24, 24);
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 6; j++) {
                if (slots [i] [j] == 0)
                    g.setColor(Color.white);
                if (slots [i] [j] == 1)
                    g.setColor(Color.green);
                if (slots [i] [j] == 2)
                    g.setColor(Color.red);
                g.fillOval(i * 22 + 215, 360 - j * 22 - moveBoardDown, 20, 20);
                g.setColor(Color.black);
                if (colorBlindMode && slots [i] [j] != 0)
                    g.drawString(slots [i] [j] + "", i * 22 + 222 - moveBoardDown, 375 - j * 22);
            }
        if ((int)(count / 5) % 5 > 2)
            arrowAnimation = 3;
        else
            arrowAnimation = 0;
        arrowAnimation = (byte) (3 - (int)(count / 2) % 5);
        g.setColor(Color.black);
        g.drawRect(219 + column * 22, 199 + arrowAnimation - moveBoardDown, 11, 31);
        g.fillRect(220 + column * 22, 200 + arrowAnimation - moveBoardDown, 10, 30);
        for (int i = 0; i < 10;i++) {
            g.drawRect(215 + i + column * 22, 230 + i + arrowAnimation - moveBoardDown, 20 - i * 2, 1);
        }
        if (player == 1)
            g.setColor(Color.green);
        else if (player == 2)
            g.setColor(Color.red);
        else
            g.setColor(Color.white);
        //if (arrowAnimation > 4)
        //    arrowMoveDown
        g.fillRect(220 + column * 22, 200 + arrowAnimation - moveBoardDown, 10, 30);
        for (int i = 0; i < 10;i++) {
            g.fillRect(215 + i + column * 22, 230 + i + arrowAnimation - moveBoardDown, 20 - i * 2, 1);
        }
    }
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }
    public void putDownTile () {
        for (int i = 0; i < 6; i++) {
            if (slots [column] [i] == 0) {
                numoftiles++;
                slots [column] [i] = player;
                checkTiles (column, i, 1, 0);
                checkTiles (column, i, 0, 1);
                checkTiles (column, i, 1, 1);
                checkTiles (column, i, 1, -1);
                checkTiles (column, i, -1, 1);
                checkTiles (column, i, -1, 0);
                checkTiles (column, i, 0, -1);
                checkTiles (column, i, -1, -1);
                i = 100;
                if (d1Tiles > 3 || d2Tiles > 3 || xTiles > 3 || yTiles > 3) {
                    pWon = true;
                    System.out.println("player " + player + " wins!");
                }
                xTiles = 1;
                yTiles = 1;
                d1Tiles = 1;
                d2Tiles = 1;
                if (!pWon) {
                    if (numoftiles == 42) {
                        player = 3;
                        pWon = true;
                    }
                    else if (player == 1)
                        player = 2;
                    else
                        player = 1;
                }
            }
        }
    }
    public void checkTiles (byte c, int r, int mx, int my) {

        for (int i = 1; i < 4; i++) {
            if (c + mx * i > 6 || c + mx * i < 0) {
                //System.out.println("debug");
                i = 100;
            }
            else if (r + my * i > 5 || r + my * i < 0) {
                //System.out.println("debug2");
                //System.out.println(r);
                //System.out.println(my);
                //System.out.println(i);
                i = 100;
            }
            else if (slots [c + mx * i] [r + my * i] == player){
                if (mx != 0 && my != 0){
                    if (mx + my == 0)
                        d1Tiles++;
                    else
                        d2Tiles++;
                }
                else if (mx != 0) {
                    xTiles++;
                }
                else if (my != 0) {
                    yTiles++;
                }
            }
            else
                i = 100;
        }
    }
    public void randomlyPlace () {
        if (numoftiles < 42)
            while (randomloop) {
                column = (byte) (Math.random() * 6 + .5);
                for (int i = 0; i < 6; i++)
                    if (slots [column] [i] == 0)
                        randomloop = false;

            }
        randomloop = true;
        putDownTile();
    }
    private class TAdapter extends KeyAdapter {
        public void keyPressed (KeyEvent e){
            int key = e.getKeyCode();


            if (key == KeyEvent.VK_UP && konami == 0) {
                konami++;
            }
            else if (key == KeyEvent.VK_UP && konami == 1) {
                konami++;
            }
            else if (key == KeyEvent.VK_DOWN && konami == 2) {
                konami++;
            }
            else if (key == KeyEvent.VK_DOWN && konami == 3) {
                konami++;
            }
            else if (key == KeyEvent.VK_LEFT && konami == 4) {
                konami++;
            }
            else if (key == KeyEvent.VK_RIGHT && konami == 5) {
                konami++;
            }
            else if (key == KeyEvent.VK_LEFT && konami == 6) {
                konami++;
            }
            else if (key == KeyEvent.VK_RIGHT && konami == 7) {
                konami++;
            }
            else if (key == KeyEvent.VK_B && konami == 8) {
                konami++;
            }
            else if (key == KeyEvent.VK_A && konami == 9) {
                konami++;
            }
            else if (key == KeyEvent.VK_ENTER && konami == 10) {
                pWon = true;
                konami = 0;
            }
            else
                konami = 0;
            //System.out.println(konami);

            if (konami < 1) {
                if (title && key == KeyEvent.VK_ENTER)
                    title = false;
                else if (title) {
                }
                else if ((player == 1 && bot1 == true) || (player == 2 && bot2 == true)) {
                    if (key == KeyEvent.VK_R) {
                        reset();
                        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                    }
                }
                else if (pWon) {
                    if (key == KeyEvent.VK_R) {
                        reset();
                        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                    }
                    if (key == KeyEvent.VK_1) {
                        player = 1;
                    }
                    if (key == KeyEvent.VK_2) {
                        player = 2;
                    }
                    if (key == KeyEvent.VK_C) {
                        colorBlindMode = !colorBlindMode;
                    }
                }
                else {
                    if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                        column--;
                    }
                    if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                        column++;
                    }
                    if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_SPACE) {
                        placingReady = true;
                    }
                    if (key == KeyEvent.VK_R) {
                        reset();
                        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                    }/*
                if (key == KeyEvent.VK_1) {
                    player = 1;
                }
                if (key == KeyEvent.VK_2) {
                    player = 2;
                }*/
                    if (key == KeyEvent.VK_C) {
                        colorBlindMode = !colorBlindMode;
                    }
                    if (key == KeyEvent.VK_Z) {
                        randomlyPlace();
                    }
                    if (key == KeyEvent.VK_H) {
                        help = !help;
                    }
                /*if (key == KeyEvent.VK_9) {
                    bot1 = !bot1;
                }
                if (key == KeyEvent.VK_0) {
                    bot2 = !bot2;
                }*/
                }
            }
        }
    }
}
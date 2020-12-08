/**
 * Created by covingtonbr441 on 11/15/2016.
 */
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.lang.Math;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Tetris2 extends JFrame {
    public Tetris2 () {
        add (new TetrisPanel(this));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        setLocationRelativeTo(null);
        setTitle("Tetris");
        setResizable(true);
        setVisible(true);
        System.out.println("Program Started!");
    }

    public static void main(String[] args) {
        new Tetris2 ();
    }
}
class TetrisPanel extends JPanel implements  ActionListener {
    public Tetris2 tet;
    public int count = 1;
    public int rotateNum = 3;
    public int ShapeY, ShapeX, rNum, score, buffer, storage, intForSwapping, displayShapeX, displayShapeY, rowsCleared, cheatNum, frameX, frameY = 0;
    public int squareSize, bgSortMaxLocation, rowClearEffect, titleFade, bgRotate, fallSpeedCount = 0;
    public int [] [] board = new int [10] [20];
    public int [] [] [] shapes = new int [7] [4] [8];
    // 1st dimension = which shape?
    // 2nd dimension = rotation
    // 3rd dimension = x and y values for placement
    // puff stands for "Prevent Unintended Fast Falling(?)"
    public boolean placeNextTime, a, d, s, w, gameOver, resetDialog, puff, isCheating, drill, bgEffBool = false;
    public boolean moveOK, rowIsFull, canRotate, canSwap, frameResized, title, musicOn, hints = true;
    public Color [] shapeColors = {new Color (0, 200, 200), Color.blue, new Color (255, 150, 0), Color.yellow, Color.green, Color.magenta, Color.red};
    public int [] cheating = {KeyEvent.VK_I, KeyEvent.VK_M, KeyEvent.VK_A, KeyEvent.VK_C, KeyEvent.VK_H, KeyEvent.VK_E, KeyEvent.VK_A, KeyEvent.VK_T, KeyEvent.VK_E, KeyEvent.VK_R};
    public double [] [] bg;
    public double bgSpeedX, bgSpeedY = 1;
    public Color bgFade = Color.black;
    public double [] bgSortMaxValue = new double [3];
    public static Clip clip;
    public File Music = new File ("Questy.wav");
    public int [] titleName = {0,0, 1,0, 2,0, 5,0, 6,0, 7,0, 10,0, 11,0, 12,0, 15,0, 16,0, 20,0, 23,0, 24,0, 25,0,/**/ 1,1, 5,1, 11,1, 15,1, 17,1, 23,1,/**/ 1,2, 5,2, 6,2, 7,2, 11,2, 15,2, 16,2, 20,2, 23,2, 24,2, 25,2,/**/ 1,3, 5,3, 11,3, 15,3, 17,3, 20,3, 25,3,/**/ 1,4, 5,4, 6,4, 7,4, 11,4, 15,4, 17,4, 20,4, 23,4, 24,4, 25,4};
    //public int [] [] bgSquares = {{0, 1, 1, 0, 0}, {0, 0, 1, 1, 0}};
    public int [] [] bgTemp = new int [2] [5];
    public int [] [] fallSpeed = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 19, 29}, {48, 43, 38, 33, 28, 23, 18, 13, 8, 6, 5, 4, 3, 2, 1}};
    public long FrameCount = 0;
    private Timer timer;
    public TetrisPanel (Tetris2 tet) {
        bgRotate = -90;
        this.tet = tet;
        makeShapes();
        musicOn = true;
        title = true;
        drill = false;
        canSwap = true;
        moveOK = true;
        placeNextTime = false;
        storage = -1;
        buffer = rand ();
        ShapeX = 4;
        ShapeY = 0;
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 20; j++)
                board [i] [j] = 0;
        rNum = rand ();
        for (int i = 0; i < 8; i+=2)
            board [ShapeX + shapes [rNum] [3] [i]] [ShapeY + shapes [rNum] [rotateNum] [i + 1]] = 1;
        addKeyListener(new TAdapter ());
        setBackground(Color.black);
        setFocusable(true);
        initGame ();
    }
    public void initGame () {
        timer = new Timer(30, this);
        timer.start ();
        musics(Music);
    }
    public static void musics (File Sound) {
        try {
            clip = AudioSystem.getClip();
            clip.open (AudioSystem.getAudioInputStream(Sound));
            clip.stop();
            //Thread.sleep(clip.getMicrosecondLength()/1000);
        } catch(Exception e){
        }
    }
    public void update () {
        if (!gameOver && !resetDialog) {
            if (w)
                rotateShape();
            if (a)
                moveside(-1);
            if (d)
                moveside(1);
            if (s)
                movedown();
            for (int i = 0; i < fallSpeed[0].length; i++)
                if (fallSpeed[0][i] < rowsCleared / 10)
                    fallSpeedCount = i;
            if (count > fallSpeed [1] [fallSpeedCount])
                movedown();
        }
        if (bgRotate > -90)
            bgRotate = (int) ((bgRotate + 90) * .9 - 93);
        else
            bgRotate = -90;
        if (gameOver)
            clip.stop();
        if (!isCheating)
            drill = false;
        if (musicOn && !gameOver)
            clip.start();
        else
            clip.stop();
        a = false;
        d = false;
        w = false;
        s = false;
        count++;
    }
    public void paint (Graphics g) {
        //System.out.println("paint");
        super.paint(g);
        if (frameX != (int) (tet.getWidth() - 17) || frameY != (int) (tet.getHeight() - 40))
            frameResized = true;
        frameX = (int) (tet.getWidth() - 17);
        frameY = (int) (tet.getHeight() - 40);
        g.setColor(Color.white);
        //g.drawRect(0, 0, frameX, frameY);
        if (frameX < frameY)
            squareSize = frameX;
        else
            squareSize = frameY;
        squareSize /= 25;
        if (frameResized) {
            frameResized = false;
            setBg();
        }
        g.setFont(new Font ("arial", Font.BOLD, squareSize));
        refreshBg();
        bgFade = new Color(fade(bgFade.getRed(), shapeColors[rNum].getRed()), fade(bgFade.getGreen(), shapeColors[rNum].getGreen()), fade(bgFade.getBlue(), shapeColors[rNum].getBlue()));
        for (int i = 0; i < bg.length; i++) {
            g.setColor(new Color(depth(bg[i][2], bgFade.getRed()), depth(bg[i][2], bgFade.getGreen()), depth(bg[i][2], bgFade.getBlue())));
            for (int j = 0; j < 5; j++) {
                bgTemp [0] [j] = (int) (Math.sin(Math.toRadians(j * 90 + bgRotate + 45)) * bg [i] [2] / Math.sqrt(2) + bg [i] [0] + bg [i] [2] / 2);
                bgTemp [1] [j] = (int) (Math.cos(Math.toRadians(j * 90 + bgRotate + 45)) * bg [i] [2] / Math.sqrt(2) + bg [i] [1] + bg [i] [2] / 2);

                //bgTemp [0] [j] = (int) (bgSquares [0] [j] * bg [i] [2] + bg [i] [0] + (Math.sin(Math.toRadians())));
                //bgTemp [1] [j] = (int) (bgSquares [1] [j] * bg [i] [2] + bg [i] [1]);
            }
            g.fillPolygon(bgTemp [0], bgTemp [1], 5);
            //g.fillRect((int) bg[i][0], (int) bg[i][1], (int) bg[i][2], (int) bg[i][2]);
        }
        if (!title) {
            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 20; j++) {
                    if (board[i][j] == 0)
                        g.setColor(new Color (255, 255, 255, titleFade));
                    else if (board[i][j] == 1)
                        g.setColor(new Color (shapeColors[rNum].getRed(), shapeColors[rNum].getGreen(), shapeColors[rNum].getBlue(), titleFade));
                    else if (board[i][j] >= 2 && board[i][j] <= 8)
                        g.setColor(new Color (shapeColors[board[i][j] - 2].getRed(), shapeColors[board[i][j] - 2].getGreen(), shapeColors[board[i][j] - 2].getBlue(), titleFade));
                    else
                        g.setColor(new Color (0, 0, 0, titleFade));
                    g.fillRect(i * squareSize + frameX / 2 - squareSize * 5, j * squareSize + frameY / 2 - squareSize * 10, (int) (squareSize * .99), (int) (squareSize * .99));
                    //g.fillRect(i * 24 + 174, j * 24 + 42, 20, 20);
                }
            g.setColor(new Color (255, 255, 255, titleFade));
            g.drawString("Hold", (int) (frameX * .2) - g.getFontMetrics().stringWidth("Hold") / 2, (int) (frameY * .15));
            g.drawString("Next", (int) (frameX * .8) - g.getFontMetrics().stringWidth("Next") / 2, (int) (frameY * .15));
            g.setColor(new Color (128, 128, 128, titleFade));
            g.fillRect((int) (frameX * .2) - squareSize, (int) (frameY * .2) - squareSize, squareSize * 2, squareSize * 2);
            g.fillRect((int) (frameX * .8) - squareSize, (int) (frameY * .2) - squareSize, squareSize * 2, squareSize * 2);
            if (storage >= 0)
                g.setColor(new Color(shapeColors[storage].getRed(), shapeColors[storage].getGreen(), shapeColors[storage].getBlue(), titleFade));
            if (storage == 0) {
            } else if (storage == 3)
                displayShapeY = 2;
            else {
                displayShapeX = 1;
                displayShapeY = 1;
            }
            for (int i = 0; i < 8; i += 2)
                if (storage >= 0)
                    g.fillRect(shapes[storage][rotateNum][i] * squareSize / 2 + (int) (frameX * .2006) - squareSize + displayShapeX * squareSize / 4, shapes[storage][rotateNum][i + 1] * squareSize / 2 + (int) (frameY * .2015) - squareSize + displayShapeY * squareSize / 4, (int) (squareSize * .45), (int) (squareSize * .45));
            g.setColor(new Color(shapeColors[buffer].getRed(), shapeColors[buffer].getGreen(), shapeColors[buffer].getBlue(), titleFade));
            displayShapeX = 0;
            displayShapeY = 0;
            if (buffer == 0) {
            } else if (buffer == 3)
                displayShapeY = 2;
            else {
                displayShapeX = 1;
                displayShapeY = 1;
            }
            for (int i = 0; i < 8; i += 2)
                g.fillRect(shapes[buffer][rotateNum][i] * squareSize / 2 + (int) (frameX * .8) - squareSize + displayShapeX * squareSize / 4, shapes[buffer][rotateNum][i + 1] * squareSize / 2 + (int) (frameY * .2015) - squareSize + displayShapeY * squareSize / 4, (int) (squareSize * .45), (int) (squareSize * .45));
            displayShapeX = 0;
            displayShapeY = 0;
            g.setColor(new Color(255, 255, 255, titleFade));
            //g.drawString(placeNextTime + "", 20, 20);
            //g.drawString(ShapeX + ", " + ShapeY, 20, 30);
            g.drawString("Score: " + score, (int) (frameX * .005), (int) (frameY * .03));
            //g.drawString("bgRotate: " + bgRotate, (int) (frameX * .005), (int) (frameY * .06));
            if (hints) {
                g.setFont(new Font ("rod", Font.PLAIN, squareSize));
                g.drawString("Hints:", (int) (frameX * .2) - g.getFontMetrics().stringWidth("Hints:") / 2, (int) (frameY * .35));
                g.setFont(new Font ("rod", Font.PLAIN, squareSize / 2));
                g.drawString("Use \"WASD\" or arrowkeys for movement", (int) (frameX * .05), (int) (frameY * .37));
                g.drawString("Use the SpaceBar to put your piece in the hold slot", (int) (frameX * .05), (int) (frameY * .39));
                g.drawString("press \"R\" to pull up the reset dialog or pause", (int) (frameX * .05), (int) (frameY * .41));
                g.drawString("\"M\" can be used to stop the music", (int) (frameX * .05), (int) (frameY * .43));
                g.drawString("you can turn this dialog off or on with \"H\"", (int) (frameX * .05), (int) (frameY * .45));
                g.drawString("you might become a cheater if you say you are", (int) (frameX * .05), (int) (frameY * .47));
                g.setFont(new Font ("arial", Font.BOLD, squareSize));
                g.drawString("Made From Scratch By Bryce Covington", (int) (frameX * .5) - g.getFontMetrics().stringWidth("Made From Scratch By Bryce Covington") / 2, (int) (frameY * .95));
            }
            if (hints && isCheating) {
                g.setColor(new Color(0, 255, 0, titleFade));
                g.setFont(new Font ("rod", Font.PLAIN, squareSize));
                g.drawString("Cheat Hints:", (int) (frameX * .8) - g.getFontMetrics().stringWidth("Cheat Hints:") / 2, (int) (frameY * .35));
                g.setFont(new Font ("rod", Font.PLAIN, squareSize / 2));
                g.drawString("1-7 changes your piece", (int) (frameX * .65), (int) (frameY * .37));
                g.drawString("use \"page up \" or \"page down\" to change your score", (int) (frameX * .65), (int) (frameY * .39));
                g.drawString("\"Delete\" activates Drill Mode", (int) (frameX * .65), (int) (frameY * .41));
                g.drawString("use \"G\" to toggle if you've lost or not", (int) (frameX * .65), (int) (frameY * .43));
                g.drawString("just in case you forgot, \"imacheater\" starts Cheat Mode", (int) (frameX * .65), (int) (frameY * .45));
                g.drawString("you can't deactivate cheat mode unless you reset", (int) (frameX * .65), (int) (frameY * .47));
                g.setFont(new Font ("arial", Font.BOLD, squareSize));
            }
            g.setColor(new Color(255, 255, 255, titleFade));
            if (resetDialog) {
                g.setColor(Color.darkGray);
                g.fillRect((int) (frameX * .5) - squareSize * 10, (int) (frameY * .5) - squareSize * 10, squareSize * 20, squareSize * 20);
                g.setColor(Color.white);
                //g.getFontMetrics().stringWidth(text);
                if (gameOver) {
                    g.drawString("Game Over!", (int) (frameX * .5) - g.getFontMetrics().stringWidth("Game Over!") / 2, (int) (frameY * .4));
                    g.drawString("Do you want to reset?", (int) (frameX * .5) - g.getFontMetrics().stringWidth("Do you want to reset?") / 2, (int) (frameY * .45));
                } else
                    g.drawString("Are you sure you want to reset?", (int) (frameX * .5) - g.getFontMetrics().stringWidth("Are you sure you want to reset?") / 2, (int) (frameY * .4));
                g.drawString("Press ENTER to reset", (int) (frameX * .5) - g.getFontMetrics().stringWidth("Press ENTER to reset") / 2, (int) (frameY * .51));
                g.drawString("and press R to continue", (int) (frameX * .5) - g.getFontMetrics().stringWidth("and press R to continue") / 2, (int) (frameY * .56));
            }
            if (isCheating) {
                g.setColor(new Color (0, 255, 0, titleFade));
                g.drawString("Cheating is enabled!", frameX - g.getFontMetrics().stringWidth("Cheating is enabled!"), (int) (frameY * .99));
            }
            if (drill)
                g.drawString("Drill mode enabled!", frameX - g.getFontMetrics().stringWidth("Drill mode enabled!"), (int) (frameY * .95));
            //g.fillRect (tet.getWidth() - 200, tet.getHeight() - 200, 10, 10);
            if (titleFade < 255)
                titleFade = (int) ((titleFade + 1) * 1.5);
            if (titleFade > 255)
                titleFade = 255;
        }
        else {
            //title = false;
            for (int i = 0; i < bg.length && titleName.length <= bg.length; i+=2) {
                //bg [i] [2] = Math.random() * Math.random() * (squareSize * 3 / 4 ) + squareSize / 4;
                bg [i] [2] = Math.random() * Math.random() * (squareSize - 1) + 1;
                bg[i][0] = frameX / 2 - squareSize * 12 + titleName[i % 100] * squareSize - bg[i][2] / 2;
                bg[i][1] = frameY / 2 - squareSize * 1.5 + titleName[(i + 1) % 100] * squareSize - bg[i][2] / 2;
            }
            g.setColor(Color.white);
            if (titleName.length > bg.length) {
                g.setFont(new Font ("Rod", Font.PLAIN, frameX / 4));
                g.drawString("Tetris", 0, frameY / 2);
            }
        }
        g.setColor(new Color (255, 255, 255, 255 - titleFade));
        if (FrameCount > 300 && titleFade != 255)
            g.drawString("press any key to start", (int) (frameX * .5) - g.getFontMetrics().stringWidth("press any key to start") / 2, (int) (frameY * .15));
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
    public void actionPerformed(ActionEvent e) {
        FrameCount++;
        if (!title)
            update();
        repaint();
    }
    public void setBg () {
        if (count < 3) {
            bgSpeedX = 0;
            bgSpeedY = 1;
        }
        //bg = new double [(int) (frameX * frameY / (squareSize * squareSize))] [3];
        bg = new double [squareSize * squareSize] [4];
        //System.out.println(bg.length);
        //the first dimension is which block is selected
        //the second contains values for the particular block
        //value zero and one are coordinates
        //value two effects the size, as well as the falling speed. Hopefully this will give the background a three dimensional effect
        for (int i = 0; i < bg.length; i++) {
            bg [i] [0] = Math.random() * frameX;
            if (title)
                bg [i] [1] = Math.random() * frameY - frameY;
            else
                bg [i] [1] = Math.random() * frameY;
            bg [i] [2] = Math.random() * Math.random() * Math.random() * Math.random() * (squareSize - 1) + 1;
        }
    }
    public void refreshBg () {
        if (bgEffBool) {
            bgSpeedY *= 1.5;
            bgEffBool = false;
            for (int i = 0; i < bg.length; i++)
                if (bg [i] [1] < frameY)
                    bgEffBool = true;
            if (!bgEffBool) {
                bgEffect();
                bgSpeedY = 1;
            }
        }
        else if (!title){
            if (bgSpeedY > 1) {
                bgSpeedY *= .9;
                if (bgSpeedY < 1)
                    bgSpeedY = 1;
            } else if (placeNextTime && bgSpeedY != .5) {
                bgSpeedY = bgSpeedY * .75;
                if (bgSpeedY < .5)
                    bgSpeedY = .5;
            } else if (bgSpeedY > .5 && bgSpeedY != 1)
                bgSpeedY += .05;
            if (bgSpeedY > 25)
                bgSpeedY = 25;
        }
        else {
            bgSpeedY = 0;
        }
        if (bgSpeedX < 0.002 && bgSpeedX > -0.002 && bgSpeedX != 0) {
            bgSpeedX = 0;
        }
        else if (bgSpeedX != 0) {
            bgSpeedX *= .9;
        }
        if (bgSpeedX > 25)
            bgSpeedX = 25;
        if (bgSpeedX < -25)
            bgSpeedX = -25;
        for (int i = 0; i < bg.length; i++) {
            bg [i] [0] += bgSpeedX * bg [i] [2] / 5;
            bg [i] [1] += bgSpeedY * bg [i] [2] / 5;
            if (bg [i] [1] > frameY + squareSize && !gameOver && !bgEffBool) {
                bg [i] [0] = Math.random() * frameX;
                bg [i] [2] = Math.random() * Math.random() * (squareSize - 1) + 1;
                bg [i] [1] = 0 - bg [i] [2];
            }
            if (bg [i] [0] > frameX && !gameOver) {
                bg [i] [1] = Math.random() * frameY;
                bg [i] [2] = Math.random() * Math.random() * (squareSize - 1) + 1;
                bg [i] [0] = 0 - bg [i] [2];
            }
            if (bg [i] [0] < 0 - bg [i] [2] && !gameOver) {
                bg [i] [1] = Math.random() * frameY;
                bg [i] [2] = Math.random() * Math.random() * (squareSize - 1) + 1;
                bg [i] [0] = frameX;
            }
        }
        for (int i = 0; i < bg.length; i++) {
            bgSortMaxLocation = i;
            bgSortMaxValue = bg [i];
            for (int j = 0; j < bg [0].length; j++)
                bgSortMaxValue [j] = bg [i] [j];
            for (int j = i; j < bg.length; j++) {
                if (bgSortMaxValue [2] > bg [j] [2]) {
                    bgSortMaxLocation = j;
                    bgSortMaxValue = bg [j];
                    for (int k = 0; k < bg [0].length; k++)
                        bgSortMaxValue [k] = bg [j] [k];
                }
            }
            for (int j = bgSortMaxLocation; j > i; j--)
                bg [j] = bg [j - 1];
            bg [i] = bgSortMaxValue;
        }
    }
    public int fade (int fadeFrom, int fadeTo) {
        if (fadeFrom < fadeTo) {
            fadeFrom += 15;
            if (fadeFrom > fadeTo)
                fadeFrom = fadeTo;
        }
        if (fadeFrom > fadeTo) {
            fadeFrom -= 15;
            if (fadeFrom < fadeTo)
                fadeFrom = fadeTo;
        }
        return fadeFrom;
    }
    public int depth (double size, int value) {
        double simplifyOrSomething = size / squareSize;
        if ((int) (value * simplifyOrSomething) + 10 > 255)
            return 255;
        return (int) (value * simplifyOrSomething) + 10;
    }
    public void bgEffect () {
        for (int i = 0; i < bg.length; i++)
            bg [i] [1] = 0 - bg [i] [2];
        for (int i = 0; i < bg.length && titleName.length <= bg.length; i+=2) {
            bg [i] [2] = squareSize;
            bg[i][0] = frameX / 2 - squareSize * 12 + titleName[i % 100] * squareSize - bg[i][2] / 2;
            bg[i][1] = 0 - squareSize * 7 + titleName[(i + 1) % 100] * squareSize - bg[i][2] / 2;
        }
    }
    public void moveside (int num) {
        //System.out.println("debug");
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 1) {
                    if (i + num < 0 || i + num > board.length - 1)
                        moveOK = false;
                    else if (board [i + num] [j] >= 2 && !drill)
                        moveOK = false;
                    //board[i][j + 1] = board[i][j];
                    //board[i][j] = 0;
                }
            }
        if (moveOK && num > 0) {
            for (int i = board.length - 1; i >= 0; i--)
                for (int j = board[0].length - 1; j >= 0; j--)
                    if (board[i][j] == 1) {
                        board[i + num][j] = 1;
                        board[i][j] = 0;
                    }
        }
        if (moveOK && num < 0) {
            for (int i = 0; i < board.length; i++)
                for (int j = 0; j < board[0].length; j++)
                    if (board[i][j] == 1) {
                        board[i + num][j] = 1;
                        board[i][j] = 0;
                    }
        }
        if (moveOK) {
            bgSpeedX += num * 10;
            ShapeX += num;
        }
        moveOK = true;
    }
    public void movedown () {
        count = 0;
        //System.out.println("debug");
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 1) {
                    if (j == board[i].length - 1) {
                        moveOK = false;
                    }
                    else if (board [i] [j + 1] >= 2 && !drill) {
                        moveOK = false;
                    }
                    //board[i][j + 1] = board[i][j];
                    //board[i][j] = 0;
                }
            }
        if (moveOK) {
            bgSpeedY += 5;
            placeNextTime = false;
            for (int i = board.length - 1; i >= 0; i--)
                for (int j = board[0].length - 1; j >= 0; j--)
                    if (board[i][j] == 1) {
                        board[i][j + 1] = 1;
                        board[i][j] = 0;
                    }
            ShapeY += 1;
        }
        else if (placeNextTime) {
            bgSpeedY = .51;
            newdrop();
            placeNextTime = false;
        }
        else
            placeNextTime = true;
        moveOK = true;
    }
    public void newdrop () {
        score++;
        bgFade = shapeColors [rNum];
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board [i] [j] == 1)
                    board [i] [j] = 2 + rNum;
        ShapeX = 4;
        ShapeY = 0;
        rNum = buffer;
        buffer = rand ();
        rowClearEffect = 0;
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[j][i] == 0)
                    rowIsFull = false;
            }
            if (rowIsFull)
                removeRow(i);
            rowIsFull = true;
        }
        if (rowClearEffect > 3) {
            bgEffBool = true;
        }
        for (int i = 0; i < 8; i+=2)
            if (board [ShapeX + shapes [rNum] [rotateNum] [i]] [ShapeY + shapes [rNum] [rotateNum] [i + 1]] != 0) {
                gameOver = true;
                resetDialog = true;
            }
        for (int i = 0; i < 8; i += 2)
            board[ShapeX + shapes[rNum][rotateNum][i]][ShapeY + shapes[rNum][rotateNum][i + 1]] = 1;
        if (s)
            puff = true;
        canSwap = true;
    }
    public void removeRow(int row) {
        rowClearEffect++;
        for (int i = 0; i < board.length; i++)
            board [i] [row] = 0;
        for (int i = row; i > 0; i--)
            for (int j = 0; j < board.length; j++)
                board [j] [i] = board [j] [i - 1];
        for (int i = 0; i < board.length; i++)
            board [i] [0] = 0;
        score += 100;
        rowsCleared++;
    }
    public void rotateShape () {
        rotateNum++;
        if(rotateNum > 3)
            rotateNum = 0;
        for (int i = 0; i < 8; i+=2) {
            //System.out.println(ShapeX + shapes[rNum][rotateNum][i] + ", " + ShapeY + shapes[rNum][rotateNum][i + 1] + " // " + shapes[rNum][rotateNum][i + 1]);
            if (ShapeX + shapes[rNum][rotateNum][i] < 0 || ShapeX + shapes[rNum][rotateNum][i] > 9 || ShapeY + shapes[rNum][rotateNum][i + 1] > 19)
                canRotate = false;
            else if (board[ShapeX + shapes[rNum][rotateNum][i]][ShapeY + shapes[rNum][rotateNum][i + 1]] >= 2 && !drill)
                canRotate = false;
        }
        rotateNum--;
        if(rotateNum < 0)
            rotateNum = 3;
        if(canRotate) {
            bgRotate += 90;
            for (int i = 0; i < board.length; i++)
                for (int j = 0; j < board[i].length; j++)
                    if (board [i] [j] == 1)
                        board [i] [j] = 0;
            rotateNum++;
            if(rotateNum > 3)
                rotateNum = 0;
            for (int i = 0; i < 8; i += 2)
                board[ShapeX + shapes[rNum][rotateNum][i]][ShapeY + shapes[rNum][rotateNum][i + 1]] = 1;
        }
        canRotate = true;
    }
    public void reset () {
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 20; j++)
                board [i] [j] = 0;
        ShapeX = 4;
        ShapeY = 0;
        rNum = rand ();
        for (int i = 0; i < 8; i+=2)
            board [ShapeX + shapes [rNum] [rotateNum] [i]] [ShapeY + shapes [rNum] [rotateNum] [i + 1]] = 1;
        count = 1;
        score = 0;
        storage = -1;
        gameOver = false;
        canSwap = true;
        buffer = rand ();
        a = false;
        d = false;
        s = false;
        w = false;
        rowsCleared = 0;
        cheatNum = 0;
        isCheating = false;
        clip.setFramePosition(0);
        clip.start();
        musicOn = true;
        fallSpeedCount = 0;
    }
    public int rand () {
        return (int) (Math.random() * 7);
    }
    private class TAdapter extends KeyAdapter {
        public void keyPressed (KeyEvent e){
            int key = e.getKeyCode();
            //System.out.println("key pressed!");
            if (resetDialog) {
                if (key == KeyEvent.VK_R)
                    resetDialog = false;
                if (key == KeyEvent.VK_ENTER) {
                    resetDialog = false;
                    reset();
                }
            }
            else if (!resetDialog && !title) {
                if (key == KeyEvent.VK_A && !(cheatNum == 2 || cheatNum == 6) || key == KeyEvent.VK_LEFT) {
                    a = true;
                }
                if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                    d = true;
                }
                if (key == KeyEvent.VK_S  || key == KeyEvent.VK_DOWN && !puff) {
                    s = true;
                }
                if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
                    w = true;
                }
                if (key == KeyEvent.VK_R) {
                    resetDialog = true;
                }
                if (key == KeyEvent.VK_SPACE && canSwap) {
                    ShapeX = 4;
                    ShapeY = 0;
                    for (int i = 0; i < board.length; i++)
                        for (int j = 0; j < board[i].length; j++)
                            if (board[i][j] == 1)
                                board[i][j] = 0;
                    bgFade = shapeColors [rNum];
                    if (storage < 0) {
                        storage = rNum;
                        rNum = buffer;
                        buffer = rand ();
                    } else {
                        intForSwapping = rNum;
                        rNum = storage;
                        storage = intForSwapping;
                    }
                    for (int i = 0; i < 8; i += 2)
                        board[ShapeX + shapes[rNum][rotateNum][i]][ShapeY + shapes[rNum][rotateNum][i + 1]] = 1;
                    canSwap = false;
                }
                if (key == KeyEvent.VK_M) {
                    musicOn = !musicOn;
                }
                if (key == KeyEvent.VK_H) {
                    hints = !hints;
                }
            }
            if (key == cheating [cheatNum] && !isCheating) {
                cheatNum++;
                if (cheatNum == cheating.length) {
                    isCheating = true;
                    cheatNum = 0;
                    resetDialog = false;
                }
            }
            else if (key != cheating [cheatNum] && !isCheating) {
                cheatNum = 0;
                if (title)
                    bgSpeedY = 1;
                title = false;
                if (!title && !gameOver && musicOn) {
                    clip.start();
                    clip.loop(1000);
                    clip.setLoopPoints(0, clip.getFrameLength() - 55500);
                }
            }
            if (isCheating) {
                title = false;
                if (key >= 49 && key <= 55) {
                    rNum = key - 49;
                    for (int i = 0; i < 4; i++)
                        rotateShape ();
                }
                if (key == KeyEvent.VK_PAGE_UP) {
                    rowsCleared++;
                    score += 100;
                }
                if (key == KeyEvent.VK_PAGE_DOWN) {
                    rowsCleared--;
                    score -= 1000;
                    if (rowsCleared < 0 || score < 0) {
                        score = 0;
                        rowsCleared = 0;
                    }
                }
                if (key == KeyEvent.VK_DELETE)
                    drill = !drill;
                if (key == KeyEvent.VK_G)
                    gameOver = !gameOver;
            }
        }
        public void keyReleased (KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                puff = false;
            }
        }
    }
    public void makeShapes () {
        shapes [0] [0] [0] = 0;//
        shapes [0] [0] [1] = 1;
        shapes [0] [0] [2] = 1;
        shapes [0] [0] [3] = 1;
        shapes [0] [0] [4] = 2;
        shapes [0] [0] [5] = 1;
        shapes [0] [0] [6] = 3;
        shapes [0] [0] [7] = 1;

        shapes [1] [0] [0] = 0;//
        shapes [1] [0] [1] = 0;
        shapes [1] [0] [2] = 0;
        shapes [1] [0] [3] = 1;
        shapes [1] [0] [4] = 1;
        shapes [1] [0] [5] = 1;
        shapes [1] [0] [6] = 2;
        shapes [1] [0] [7] = 1;

        shapes [2] [0] [0] = 2;
        for (int i = 1; i < 8; i++) {
            shapes [2] [0] [i] = shapes [1] [0] [i];
        }


        shapes [4] [0] [0] = 1;//
        shapes [4] [0] [1] = 0;
        shapes [4] [0] [2] = 2;
        shapes [4] [0] [3] = 0;
        shapes [4] [0] [4] = 0;
        shapes [4] [0] [5] = 1;
        shapes [4] [0] [6] = 1;
        shapes [4] [0] [7] = 1;

        shapes [5] [0] [0] = 1;//
        shapes [5] [0] [1] = 0;
        shapes [5] [0] [2] = 0;
        shapes [5] [0] [3] = 1;
        shapes [5] [0] [4] = 1;
        shapes [5] [0] [5] = 1;
        shapes [5] [0] [6] = 2;
        shapes [5] [0] [7] = 1;

        for (int i = 0; i < 8; i += 2) {
            shapes [6] [0] [i] = shapes [4] [0] [i] * -1 + 2;
            shapes [6] [0] [i + 1] = shapes [4] [0] [i + 1];
        }
        for (int i = 0; i < 7; i++){
            for (int j = 1; j < 4; j++)
                for (int k = 0; k < 8; k += 2) {
                    shapes [i] [j] [k] = shapes [i] [j - 1] [k + 1] * -1 + (int) (3 - i * .001);
                    shapes [i] [j] [k + 1] = shapes [i] [j - 1] [k];
                }
        }

        for (int i = 0; i < 4; i++) {
            shapes [3] [i] [0] = 1;
            shapes [3] [i] [1] = 0;
            shapes [3] [i] [2] = 2;
            shapes [3] [i] [3] = 0;
            shapes [3] [i] [4] = 1;
            shapes [3] [i] [5] = 1;
            shapes [3] [i] [6] = 2;
            shapes [3] [i] [7] = 1;
        }
    }
}
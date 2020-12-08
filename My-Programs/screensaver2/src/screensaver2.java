import javax.swing.*;
        import java.awt.event.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.awt.event.KeyAdapter;
        import java.awt.event.KeyEvent;
        import java.awt.image.BufferedImage;
import java.util.Random;

public class screensaver2 extends JFrame {

    public screensaver2() {
        add(new Board(this));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        setLocationRelativeTo(null);
        setTitle("JFrame");
        setCursor(getToolkit().createCustomCursor(
                new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
                new Point(),
                null ) );
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new screensaver2(); //Runs the constuctor
    }
}


class Board extends JPanel implements ActionListener {

    public int count = 0;
    public int wait = 2000;
    public int waitcount = 2000;
    /*public int eventcount = 0;
    public int event = -1;
    public int eventonenum = -1;*/
    public int mutation = 1;
    public int numofmutations = 3;
    public int edgenum = 0;
    public int numofedgetypes = 5;
    public int displayX = 0;
    public int displayY = 0;
    public int squareSize;
    public double edgerand = 0;
    public int edgespeed = 0;
    public int edgenumx = 0;
    public int edgenumy = 0;
    public double edgevelocity = 0;
    public boolean debugmode = false;
    public double debug1 = 0;
    public double debug2 = 0;
    public double [] [] squares;
    public double [] setUp = new double [4];
    public Color fadeFromLn = Color.black;
    public Color fadeToLn = fadeFromLn;
    private Timer timer;
    public screensaver2 SS;

    public Board(screensaver2 SS) {
        this.SS = SS;
        displayX = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 1;
        displayY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 1;
        squareSize = displayY / 300;
        System.out.println(displayX + ", " + displayY);
        squares = new double [(displayX + displayY) * 5] [4];
        //squares slots:
        //0 = x
        //1 = y
        //2 = xSpeed
        //3 = ySpeed
        makeLine();
        MouseButtonRecogn a = new MouseButtonRecogn();
        addMouseListener(a);
        addMouseMotionListener(a);
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);
        initGame();
    }

    public void initGame() {
        timer = new Timer(30, this);
        timer.start();
    }

    public void update () {
        waitcount++;
        if (true) {
        /*eventcount++;
        if (event == -1) {
            eventcount = 0;
            event = (int) (Math.random() * 1) + 1;
            if (event == 1) {
                eventonenum = (int) (Math.random() * 4);
            }
            else
                eventonenum = -1;
        }*/
        } //old event stuff

        if (fadeToLn.getRed() == fadeFromLn.getRed() && fadeToLn.getGreen() == fadeFromLn.getGreen() && fadeToLn.getBlue() == fadeFromLn.getBlue()) {
            if (fadeFromLn.getRed() == 0 && fadeFromLn.getGreen() == 0 && fadeFromLn.getBlue() == 0) {
                waitcount = 0;
                makeLine();
            }
            if (waitcount >= wait)
                fadeToLn = new Color (0,0,0);
            else
                for (int i = 0; i < 1; i++) {
                    fadeToLn = new Color ((int) (Math.random() + .5) * 255, (int) (Math.random() + .5) * 255, (int) (Math.random() + .5) * 255);
                    if ((fadeToLn.getRed() == 0 && fadeToLn.getGreen() == 0 && fadeToLn.getBlue() == 0) || (fadeToLn.getRed() == fadeFromLn.getRed() && fadeToLn.getGreen() == fadeFromLn.getGreen() && fadeToLn.getBlue() == fadeFromLn.getBlue()))
                        i--;
                }
        }
        else {
            fadeFromLn = new Color (fadeStuff(fadeFromLn.getRed(), fadeToLn.getRed()), fadeStuff(fadeFromLn.getGreen(), fadeToLn.getGreen()), fadeStuff(fadeFromLn.getBlue(), fadeToLn.getBlue()));
        }
        if (edgenum == 0 || (mutation == 0 && edgenum != 0 && waitcount == 0)) {
            System.out.println(edgenum);
                for (int i = 0; i < squares.length; i++) {
                    if (squares[i][0] < 0) {
                        squares[i][0] += displayX - squareSize;
                    }
                    if (squares[i][0] > displayX - squareSize) {
                        squares[i][0] -= displayX - squareSize;
                    }
                    if (squares[i][1] < 0) {
                        squares[i][1] += displayY - squareSize;
                    }
                    if (squares[i][1] > displayY - squareSize) {
                        squares[i][1] -= displayY - squareSize;
                    }
                    squares[i][0] += squares[i][2];
                    squares[i][1] += squares[i][3];
                }
        }
        if (edgenum == 1) {
            for (int y = 0; y < 1; y++)
                for (int i = 0; i < squares.length; i++) {
                    if (squares[i][0] < 0) {
                        squares[i][2] = Math.random();
                    }
                    if (squares[i][0] > displayX - squareSize) {
                        squares[i][2] = -Math.random();
                    }
                    if (squares[i][1] < 0) {
                        squares[i][3] = Math.random();
                    }
                    if (squares[i][1] > displayY - squareSize) {
                        squares[i][3] = -Math.random();
                    }
                    squares[i][0] += squares[i][2];
                    squares[i][1] += squares[i][3];
                }
        }
        if (edgenum == 2) {
            for (int y = 0; y < 1; y++)
                for (int i = 0; i < squares.length; i++) {
                    if (squares[i][0] < 0) {
                        squares[i][2] = 1;
                    }
                    if (squares[i][0] > displayX - squareSize) {
                        squares[i][2] = -1;
                    }
                    if (squares[i][1] < 0) {
                        squares[i][3] = 1;
                    }
                    if (squares[i][1] > displayY - squareSize) {
                        squares[i][3] = -1;
                    }
                    squares[i][0] += squares[i][2];
                    squares[i][1] += squares[i][3];
                }
            }
        if (edgenum == 3) {
            if (count % edgespeed == 0)
                edgerand = Math.random() * 2 - 1;
            for (int i = 0; i < squares.length; i++) {
                if (squares[i][0] < 0) {
                    squares[i][2] = -squares[i][2];
                    squares[i][3] = edgerand;
                }
                if (squares[i][0] > displayX - squareSize) {
                    squares[i][2] = -squares[i][2];
                    squares[i][3] = edgerand;
                }
                if (squares[i][1] < 0) {
                    squares[i][3] = -squares[i][3];
                    squares[i][2] = edgerand;
                }
                if (squares[i][1] > displayY - squareSize) {
                    squares[i][3] = -squares[i][3];
                    squares[i][2] = edgerand;
                }
                squares[i][0] += squares[i][2];
                squares[i][1] += squares[i][3];
            }
        }
        if (edgenum == 4) {
            double bot = 10;
            double top = 0;
            //System.out.println(Math.atan((squares[0][0] - edgenumx) / (squares[0][1] - edgenumy)));
            for (int i = 0; i < squares.length; i++) {
                if (squares[i][0] < 0 || squares[i][0] > displayX - squareSize || squares[i][1] < 0 || squares[i][1] > displayY - squareSize) {
                    double edgeang = Math.atan((squares[i][1] - edgenumy) / (squares[i][0] - edgenumx));
                    if (squares[i][0] - edgenumx > 0)
                        edgeang += Math.PI;
                    squares[i][2] = Math.cos(edgeang) * edgevelocity;
                    squares[i][3] = Math.sin(edgeang) * edgevelocity;
                    if (edgeang < bot)
                        bot = edgeang;
                    if (edgeang > top)
                        top = edgeang;
                }
                debug1 = bot;
                debug2 = top;
                squares[i][0] += squares[i][2];
                squares[i][1] += squares[i][3];
            }
        }
        /*if (event == 1) {
            boolean complete = true;
            for (int i = 0; i < squares.length; i++) {
                if (eventonenum == 0 && squares [i] [0] < eventcount - wait) {
                    squares [i] [0] = -10000;
                    squares[i][2] = 0;
                    squares[i][3] = 0;
                }
                else if (eventonenum == 1 && squares [i] [1] < eventcount - wait) {
                    squares [i] [0] = -10000;
                    squares[i][2] = 0;
                    squares[i][3] = 0;
                }
                else if (eventonenum == 2 && squares [i] [0] > displayX - squareSize + wait - eventcount) {
                    squares [i] [0] = -10000;
                    squares[i][2] = 0;
                    squares[i][3] = 0;
                }
                else if (eventonenum == 3 && squares [i] [1] > displayY - squareSize + wait - eventcount) {
                    squares [i] [0] = -10000;
                    squares[i][2] = 0;
                    squares[i][3] = 0;
                }
                else if (squares [i] [0] > -10)
                    complete = false;
            }
            if (complete) {
                event = -1;
                fadeFromLn = Color.black;
                makeLine();
            }
        } //this is the boring screen swipe
        */
    }

    public void paint(Graphics g) {
        super.paint(g);
        /*g.setColor(new Color (fadeFromLn.getRed(), fadeFromLn.getGreen(), fadeFromLn.getBlue(), 10));
        for (int i = 0; i < squares.length; i++)
            g.fillRect((int) squares [i] [0] - squareSize, (int) squares [i] [1] - squareSize, squareSize * 3, squareSize * 3); */
        g.setColor(fadeFromLn);
        if (mutation == 0)
            for (int i = 0; i < squares.length; i++)
                g.fillRect((int) squares[i][0], (int) squares[i][1], squareSize * 2, squareSize * 2);
        //if (mutation == 1)
        else
            for (int i = 0; i < squares.length; i++)
                g.fillRect((int) squares[i][0], (int) squares[i][1], squareSize, squareSize);
        /*if (mutation == 2) {
            int skips = 1;
            int [] x = new int [squares.length/skips];
            int [] y = new int [squares.length/skips];
            for (int i = 0; i < squares.length / skips; i+=skips) {
                x [i/skips] = (int) squares [i] [0];
                y [i/skips] = (int) squares [i] [1];
            }
            g.fillPolygon(x, y,x.length);
        }*/
        if (debugmode) {
            g.drawString(debug1 + "", 0, 20);
            g.drawString(debug2 + "", 0, 40);
        }


        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    public void actionPerformed(ActionEvent e) {
        count++;
        update();
        repaint();
    }
    public void makeLine () {
        setUp [0] = (int) (Math.random() * (displayX - squareSize));
        setUp [1] = (int) (Math.random() * (displayY - squareSize));
        int rand = (int) (Math.random() * numofmutations);
        edgenum = (int) (Math.random() * numofedgetypes);
        //edgenum = 4;
        //rand = 1;
        if (edgenum == 3) {
            edgerand = Math.random() * 2 - 1;
            edgespeed = (int) (Math.random() * 50) + 70;
        }
        if (edgenum == 4) {
            edgevelocity = Math.random() * 3 + .5;
            edgerand = 0;
            if (edgevelocity >= 2.5 && Math.random() < .5)
                edgerand = Math.random() / 10;
            edgerand -= edgerand / 2;
            edgenumx = (int) (Math.random() * (displayX - squareSize));
            edgenumy = (int) (Math.random() * (displayY - squareSize));
        }
        if (rand == 0) {
            mutation = 0;
            wait = 2000;
            double variation = Math.random();
            double rotang = Math.random() * 360;
            if (variation < .75 && variation >= .5)
                rotang = Math.random() * 3 + 179;
            if (variation < .5 && variation >= .25)
                rotang = (int) (Math.random() * 2) * 150 - 30 - (Math.random() * 90);
            System.out.println(variation);
            for (int i = 0; i < squares.length; i++) {
                double mult = 1;
                double angrand = Math.random() * 360;
                if (variation < .25)
                    mult = Math.random() * Math.random() * Math.random() * Math.random() * Math.random() * Math.random() * Math.random() * Math.random() * 1000;
                else if (variation < .5) {
                    mult = Math.random() * Math.random() * Math.random() * Math.random() * Math.random() * Math.random() * 500 - 250;
                }
                else if (variation < .75)
                    mult = 950 + Math.random() * 100;

                double xrand = Math.cos(Math.toRadians(angrand)) * mult;
                double yrand = Math.sin(Math.toRadians(angrand)) * mult;
                double xmoverand = Math.cos(Math.toRadians(angrand + rotang)) * mult;
                double ymoverand = Math.sin(Math.toRadians(angrand + rotang)) * mult;
                //setUp [0] = (int) (displayX + squareSize) / 2 + xrand;
                //setUp [1] = (int) (displayY + squareSize) / 2 + yrand;
                setUp[2] = xmoverand / mult;
                setUp[3] = ymoverand / mult;
                squares[i][0] = setUp[0] + xrand;
                squares[i][1] = setUp[1] + yrand;
                squares[i][2] = setUp[2];
                squares[i][3] = setUp[3];
                //setUp[0] += setUp[2];
                //setUp[1] += setUp[3];
            }
        }
        else if (rand == 1) {
            mutation = 2;
            wait = 2000;
            setUp[2] = (int) (Math.random() + .5) * 2 - 1;
            setUp[3] = (int) (Math.random() + .5) * 2 - 1;
            for (int i = 0; i < squares.length; i++) {
                if (setUp[0] < 0)
                    setUp[2] = 1;
                if (setUp[0] > displayX - squareSize)
                    setUp[2] = -1;
                if (setUp[1] < 0)
                    setUp[3] = 1;
                if (setUp[1] > displayY - squareSize)
                    setUp[3] = -1;
                squares[i][0] = setUp[0];
                squares[i][1] = setUp[1];
                squares[i][2] = -setUp[2];
                squares[i][3] = setUp[3];
                setUp[0] += setUp[2];
                setUp[1] += setUp[3];
            }
        }
        else if (rand == 2){
            mutation = 1;
            wait = 2000;
            if (edgenum == 1)
                wait = 4000;
            setUp[2] = Math.random() * 2 - 1;
            setUp[3] = Math.random() * 2 - 1;
            for (int i = 0; i < squares.length; i++) {
                if (setUp[0] < 0)
                    setUp[2] = Math.random();
                if (setUp[0] > displayX - squareSize)
                    setUp[2] = -Math.random();
                if (setUp[1] < 0)
                    setUp[3] = Math.random();
                if (setUp[1] > displayY - squareSize)
                    setUp[3] = -Math.random();
                squares[i][0] = setUp[0];
                squares[i][1] = setUp[1];
                squares[i][2] = -setUp[2];
                squares[i][3] = setUp[3];
                setUp[0] += setUp[2];
                setUp[1] += setUp[3];
            }
        }
    }
    public int fadeStuff (int fFrom, int fTo) {
        if (fTo == fFrom)
            return fTo;
        if (fTo < fFrom)
            return fFrom - 1;
        return fFrom + 1;
    }

    class MouseButtonRecogn extends MouseAdapter {

        public void mouseMoved(MouseEvent event) {
            //System.out.println("debug1");
            if (count > 60 && !debugmode) {
                System.out.println("Mouse Moved!");
                System.exit(0);
            }
        }

        @Override
        public void /*mouseClicked*/mousePressed(MouseEvent event) {
            //System.out.println("debug2");
            if (count > 60 && !debugmode) {
                System.out.println("Mouse Clicked!");
                System.exit(0);
            }
        }
    }
    private class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_L)
                debugmode = !debugmode;
            else if (debugmode){
                if (key == KeyEvent.VK_R)
                    waitcount = wait;
                System.out.println(mutation);
            }
            else {
                System.out.println("Key Pressed");
                System.exit(0);
            }
        }
    }
}
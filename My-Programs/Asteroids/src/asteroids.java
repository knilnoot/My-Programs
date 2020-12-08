/**
 * Created by covingtonbr441 on 1/18/2017.
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class asteroids extends JFrame {

    public asteroids() {
        add(new Board(this));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        setLocationRelativeTo(null);
        setTitle("JFrame");

        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new asteroids(); //Runs the constuctor
    }
}


class Board extends JPanel implements ActionListener {

    public int count = 0;
    public int displayX = 0;
    public int displayY = 0;
    public int squareDisplay;
    public ship shp = new ship ();
    public boolean left, right, forwards, brakes, shoot, shift = false;
    private Timer timer;
    public asteroids ast;
    public int invincible = 200;
    public boolean dead = false;
    public double temp = 0;
    public int score = 0;
    public ArrayList<shot> shots = new ArrayList<shot>();
    public ArrayList<asteroid> astrds = new ArrayList<asteroid>();

    public Board(asteroids ast) {
        displayX = ast.getWidth();
        displayY = ast.getHeight();
        shp.x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
        shp.y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2;
        this.ast = ast;
        MouseButtonRecogn a = new MouseButtonRecogn();
        addMouseListener(a);
        addMouseMotionListener(a);
        addKeyListener(new TAdapter());
        setBackground(Color.darkGray);
        setFocusable(true);
        initGame();
    }

    public void initGame() {
        timer = new Timer(30, this);
        timer.start();
    }

    public void update () {
        if (!dead)
            invincible--;
        else {
            score = 0;
            invincible = 200;
        }
        displayX = ast.getWidth();
        displayY = ast.getHeight();
        squareDisplay = displayX;
        if (displayY < displayX)
            squareDisplay = displayY;
        if (shp.reloadCount < 0 && shoot) {
            shp.reloadCount = shp.reloadLength;
            shots.add(0, new shot(shp.rot, shp.shipPaintArr[0][0], shp.shipPaintArr[1][0]));
        }
        if (astrds.size() == 0) {
            for (int i = 0; i < 25; i++)
                astrds.add(0, new asteroid(displayX, displayY));
            invincible = 200;
        }
        for (int i = 0; i < astrds.size(); i++)
            astrds.get(i).update();
        for (int i = 0; i < shots.size(); i++) {
            shots.get(i).update();
            if (shots.get(i).lifeTime <= 0) {
                score -= 10;
                shots.remove(i);
                if (i > 0)
                    i--;
            }
        }
        for (int i = 0; i < shots.size(); i++) {
                shots.get(i).x = screenLoopX(shots.get(i).x, squareDisplay / 50);
                shots.get(i).y = screenLoopY(shots.get(i).y, squareDisplay / 50);
        }
        shp.x = screenLoopX(shp.x, squareDisplay / 50);
        shp.y = screenLoopY(shp.y, squareDisplay / 50);
        for (int i = 0; i < astrds.size(); i++) {
            astrds.get(i).x = screenLoopX(astrds.get(i).x, astrds.get(i).size);
            astrds.get(i).y = screenLoopY(astrds.get(i).y, astrds.get(i).size);
        }
        checkCollisions();
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (dead) {
            g.setColor(Color.gray);
            g.fillRect(0, 0, displayX, displayY);
        }
        g.setColor(Color.yellow);
        for (int i = 0; i < shots.size(); i++)
            g.fillOval((int) shots.get(i).x, (int) shots.get(i).y, 3, 3);
        g.setColor(Color.gray);
        for (int i = 0; i < astrds.size(); i++)
            g.fillOval((int) (astrds.get(i).x - astrds.get(i).size / 2), (int) (astrds.get(i).y - astrds.get(i).size / 2), (int) astrds.get(i).size, (int) astrds.get(i).size);
        shp.paint(g, left, right, forwards, brakes, squareDisplay / 80, invincible);
        if (dead) {
            g.setFont(new Font ("Comic Sans MS", Font.BOLD, squareDisplay / 10));
            g.setColor(Color.black);
            g.drawString("press R to restart", displayX / 2 - g.getFontMetrics().stringWidth("press R to restart") / 2, displayY / 2);
        }
        g.setFont(new Font ("Comic Sans MS", Font.BOLD, squareDisplay / 50));
        g.drawString("Score: " + score, 10, 0 + g.getFontMetrics().getHeight());
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    public void actionPerformed(ActionEvent e) {
        count++;
        update();
        repaint();
    }
    public double screenLoopX (double x, double size) {
        if (x < 0 - size / 2)
            x += displayX + size;
        if (x > displayX + size / 2)
            x -= displayX + size;
        return x;
    }
    public double screenLoopY (double y, double size) {
        if (y < 0 - size / 2)
            y += displayY + size;
        if (y > displayY + size / 2)
            y -= displayY + size;
        return y;
    }
    public void checkCollisions () {
        for (int i = 0; i < astrds.size(); i++) {
            for (int j = 0; j < shots.size(); j++) {
                //System.out.println(i + ", " + j);
                if (astrds.size() != 0 && Math.sqrt(Math.pow(shots.get(j).x - astrds.get(i).x, 2) + Math.pow(shots.get(j).y - astrds.get(i).y, 2)) < astrds.get(i).size / 2) {
                    while (astrds.get(i).size > 100) {
                        temp = (int) (Math.random() * 4) + 2;
                        if (astrds.get(i).size / temp > astrds.get(i).minSize) {
                            for (int k = 1; k <= temp; k++)
                                astrds.add(new asteroid(astrds.get(i).x, astrds.get(i).y, astrds.get(i).size / temp));
                            astrds.get(i).size = 0;
                        }
                    }
                    score += 10;
                    astrds.remove(i);
                    shots.remove(j);
                    if (i != 0)
                        i--;
                    j--;
                }
            }
            for (int j = 0; j < 4 && invincible < 0; j++) {
                if (astrds.size() != 0 && Math.sqrt(Math.pow(shp.shipPaintArr [0] [j] - astrds.get(i).x, 2) + Math.pow(shp.shipPaintArr [1] [j] - astrds.get(i).y, 2)) < astrds.get(i).size / 2) {
                    dead = true;
                }
            }
        }
    }
    class MouseButtonRecogn extends MouseAdapter {

        public void mouseMoved(MouseEvent event) {

        }

        @Override
        public void /*mouseClicked*/mousePressed(MouseEvent event) {

        }
    }

    private class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            System.out.println(key);
            if (!shift) {
                if (key == KeyEvent.VK_LEFT)
                    left = true;
                if (key == KeyEvent.VK_RIGHT)
                    right = true;
                if (key == KeyEvent.VK_UP)
                    forwards = true;
                if (key == KeyEvent.VK_DOWN)
                    brakes = true;
                if (key == KeyEvent.VK_SPACE)
                    shoot = true;
            }
            if (key == KeyEvent.VK_SHIFT)
                shift = true;
            if (key == KeyEvent.VK_R) {
                dead = false;
                score = 0;
                for (int i = astrds.size() - 1; i >= 0; i--)
                    astrds.remove(i);
            }
        }
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (!shift) {
                if (key == KeyEvent.VK_LEFT)
                    left = false;
                if (key == KeyEvent.VK_RIGHT)
                    right = false;
                if (key == KeyEvent.VK_UP)
                    forwards = false;
                if (key == KeyEvent.VK_DOWN)
                    brakes = false;
                if (key == KeyEvent.VK_SPACE)
                    shoot = false;
            }
            if (key == KeyEvent.VK_SHIFT)
                shift = false;
        }
    }
}
class ship {
    public double [] sCoords = {0, 1.5, 1, -1, 0, -.5, -1, -1, 0, 1.5}, fCoords = {0, -.5, .5, -.5, 0, -2, -.5, -.5, 0, 0};
    //public double [] sCoords = {5, 5, 5, -5, -5, -5, -5, 5, 5, 5};
    public double x, y, xSpeed, ySpeed, moveDecay, rot, rotVel, rotDec, rotFix;
    public int [] [] shipPaintArr = new int [4] [5];
    public int reloadCount, reloadLength = 0;
    public ship () {
        rotVel = 0;
        rotDec = .8;
        rot = 0;
        reloadLength = 10;
    }
    public void paint (Graphics g, boolean left, boolean right, boolean forward, boolean brakes, int shipsize, int invincible) {
        if (reloadCount != -1)
            reloadCount--;
        if (left && right) {}
        else if (left && rotVel >= 0)
            rotVel = rotVel * 1.2 + 1;
        else if (left)
            rotVel = rotVel * .8 + 1;
        else if (right && rotVel <= 0)
            rotVel = rotVel * 1.2 - 1;
        else if (right)
            rotVel = rotVel * .8 - 1;
        rot += rotVel;
        if (rot != 0)
            rotVel *= rotDec;

        if (forward) {
            xSpeed += Math.sin(Math.toRadians(rot));
            ySpeed += Math.cos(Math.toRadians(rot));
        }
        moveDecay = .98;
        if (brakes)
            moveDecay = .9;
        x += xSpeed;
        y += ySpeed;
        xSpeed *= moveDecay;
        ySpeed *= moveDecay;

        for (int i = 0; i < 5; i++) {
            rotFix = 0;
            if (sCoords [i * 2 + 1] < 0)
                //if (i == 3)
                rotFix = Math.toRadians(180);
            //if (i == 1)
            //    rotFix = Math.toRadians(-90);
            shipPaintArr [0] [i] = (int) (Math.sqrt(Math.pow(sCoords [i * 2], 2) + Math.pow(sCoords [i * 2 + 1], 2)) * Math.sin(Math.toRadians(rot) + Math.atan(sCoords [i * 2] / sCoords [i * 2 + 1]) + rotFix) * shipsize) + (int) x;
            shipPaintArr [1] [i] = (int) (Math.sqrt(Math.pow(sCoords [i * 2], 2) + Math.pow(sCoords [i * 2 + 1], 2)) * Math.cos(Math.toRadians(rot) + Math.atan(sCoords [i * 2] / sCoords [i * 2 + 1]) + rotFix) * shipsize) + (int) y;
            //System.out.print(shipPaintArr [0] [i] + ", " + shipPaintArr [1] [i] + " ||| ");
            //System.out.println(Math.toDegrees(Math.atan(sCoords [i * 2] / sCoords [i * 2 + 1]) + rotFix));
        }
        for (int i = 0; i < 5; i++) {
            rotFix = 0;
            if (sCoords [i * 2 + 1] < 0)
                rotFix = Math.toRadians(180);
            shipPaintArr [2] [i] = (int) (Math.sqrt(Math.pow(fCoords [i * 2], 2) + Math.pow(fCoords [i * 2 + 1], 2)) * Math.sin(Math.toRadians(rot) + Math.atan(fCoords [i * 2] / fCoords [i * 2 + 1]) + rotFix) * shipsize) + (int) x;
            shipPaintArr [3] [i] = (int) (Math.sqrt(Math.pow(fCoords [i * 2], 2) + Math.pow(fCoords [i * 2 + 1], 2)) * Math.cos(Math.toRadians(rot) + Math.atan(fCoords [i * 2] / fCoords [i * 2 + 1]) + rotFix) * shipsize) + (int) y;
        }
        //System.out.println();
        if (invincible % 5 != 4) {
            g.setColor(Color.red);
            if (forward)
                g.fillPolygon(shipPaintArr[2], shipPaintArr[3], 5);
            g.setColor(Color.white);
            g.fillPolygon(shipPaintArr[0], shipPaintArr[1], 5);
        }
        /*g.setColor(Color.red);
        g.fillRect(shipPaintArr [0] [0], shipPaintArr [1] [0], 4, 3);
        g.setColor(Color.green);
        g.fillRect(shipPaintArr [0] [1], shipPaintArr [1] [1], 3, 3);
        g.setColor(Color.blue);
        g.fillRect(shipPaintArr [0] [2], shipPaintArr [1] [2], 2, 3);
        g.setColor(Color.white);
        g.fillRect(shipPaintArr [0] [3], shipPaintArr [1] [3], 1, 3);
        for (int i = 0; i < 5; i++)
            g.fillRect((int) (sCoords [i * 2] * 20) + 600, (int) (sCoords [i * 2 + 1] * 20) + 500, 4, 4);*/
        //g.setColor(Color.gray);
        //g.drawRect(300, 300, 1, 1);
        //this is some text
    }
}
class shot {
    public double rot, x, y, speed, lifeTime;
    public shot (double rot, double x, double y) {
        this.rot = rot;
        this.x = x;
        this.y = y;
        speed = 5;
        lifeTime = 100;
    }
    public void update () {
        lifeTime--;
        x += Math.sin(Math.toRadians(rot)) * speed;
        y += Math.cos(Math.toRadians(rot)) * speed;
    }
}
class asteroid {
    double x, y, xSpeed, ySpeed, size, maxSize, minSize;
    public asteroid (int displayX, int displayY) {
        x = Math.random() * displayX;
        y = Math.random() * displayY;
        maxSize = 200;
        minSize = 30;
        size = Math.random() * (maxSize - minSize) + minSize;
        xSpeed = (Math.random() - .5) * 6;
        ySpeed = (Math.random() - .5) * 6;
    }
    public asteroid (double x, double y, double size) {
        this.x = x;
        this.y = y;
        maxSize = 200;
        minSize = 30;
        this.size = size;
        xSpeed = (Math.random() - .5) * 6;
        ySpeed = (Math.random() - .5) * 6;
    }
    public void update () {
        x += xSpeed;
        y += ySpeed;
    }
}
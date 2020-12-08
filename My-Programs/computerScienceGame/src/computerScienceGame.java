/**
 * Created by covingtonbr441 on 5/2/2017.
 */
import javafx.scene.input.MouseButton;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;

public class computerScienceGame extends JFrame {

    public computerScienceGame() {
        add(new Board(this));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        setLocationRelativeTo(null);
        setTitle("JFrame");
        setResizable(true);
        setVisible(true);
    }

    public static void main(String[] args) {
        new computerScienceGame();
    }
}


class Board extends JPanel implements ActionListener {

    public int count, frameX, frameY , displayX, displayY, tlcX, tlcY = 0;
    public double pixel = 0.0;
    public int mouseX, mouseY, mAngle, px, py, pl, pw, distance, debug1, debug2, xPoint, yPoint = 0;
    public boolean moving, w, a, s, d, firstClick, clicking, paused, attacking = false;
    public ArrayList<wall> level = new ArrayList<wall>();
    public wall previewPlace = new wall (0, 0, 0, 0);
    private Timer timer;
    public computerScienceGame csg;
    public int coreHealth = 100;
    public ArrayList<enemy> enemies = new ArrayList<>();
    public int zoomAmount = 50000;
    public ImageObserver imgObsrvr;

    public Board(computerScienceGame csg){
        this.csg = csg;
        MouseButtonRecogn a = new MouseButtonRecogn();
        addMouseListener(a);
        addMouseWheelListener(a);
        addMouseMotionListener(a);
        addKeyListener(new TAdapter());
        setBackground(Color.magenta);
        setFocusable(true);
        distance = 1000;
        py = -6000;
        pw = 1000;
        pl = 1000;
        firstClick = true;
        //level.add(new wall (1000, 1000, 20000, 4000));
        initGame();
    }

    public void initGame(){
        timer = new Timer(30, this);
        timer.start();
    }
    public void refreshFrame () {
        frameX = csg.getWidth() - 16;
        frameY = csg.getHeight() - 39;
        if (frameX * 3 > frameY * 4) {
            displayX = (int)(frameY * (4.0 / 3.0));
            displayY = frameY;
        }
        else {
            displayX = frameX;
            displayY = (int) (frameX * .75);
        }
        tlcX = frameX / 2 - displayX / 2;
        tlcY = frameY / 2 - displayY / 2;
        pixel = displayY / (zoomAmount * 1.0);
    }

    public void update () {
        refreshFrame();
        calculatePlayerDirection();
        movePlayer();
        for (int i = 0; i < level.size(); i++)
            if (!level.get(i).solid) {
                Rectangle rect1 = new Rectangle(level.get(i).x, level.get(i).y, level.get(i).w, level.get(i).l);
                Rectangle rect2 = new Rectangle(px - pw / 2, py - pl / 2, pw, pl);
                if (!rect1.intersects(rect2)) {
                    level.get(i).solid = true;
                }
            }
        if (count >= 20) {
            count = 0;
            double angle = Math.random() * 360;
            enemies.add(new enemy(Math.cos(Math.toRadians(angle)) * 100000, Math.sin(Math.toRadians(angle)) * 100000, -Math.cos(Math.toRadians(angle)) * 100, -Math.sin(Math.toRadians(angle)) * 100, Math.random() * 10000 + 400));
        }
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
            if (attacking && attackCollision(enemies.get(i).intX, enemies.get(i).intY, (int) enemies.get(i).size, (int) enemies.get(i).size, true)) {
                enemies.remove(i);
                i--;
            }
            else if (levelCollision(enemies.get(i).intX, enemies.get(i).intY, (int) enemies.get(i).size, (int) enemies.get(i).size, true)) {
                enemies.remove(i);
                i--;
            }
            else if (coreCollision(enemies.get(i).intX, enemies.get(i).intY, (int) enemies.get(i).size, (int) enemies.get(i).size)) {
                enemies.remove(i);
                i--;
                coreHealth--;
            }
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.lightGray);
        g.fillRect(tlcX, tlcY, displayX, displayY);
        g.setColor(Color.gray);
        g.fillRect((int) (tlcX + displayY / pixel / 2 * 4 / 3 * pixel + previewPlace.x * pixel - px * pixel), (int) (tlcY + displayY / pixel / 2 * pixel + previewPlace.y * pixel - py * pixel), (int) (previewPlace.w * pixel), (int) (previewPlace.l * pixel));
        for (int i = 0; i < level.size(); i++) {
            if (level.get(i).solid)
                g.setColor(Color.darkGray);
            else
                g.setColor(Color.gray);
            if (!(tlcX + displayY / pixel / 2 * 4 / 3 * pixel + level.get(i).x * pixel - px * pixel > displayX + tlcX ||
                    tlcY + displayY / pixel / 2 * pixel + level.get(i).y * pixel - py * pixel > displayY + tlcY ||
                    tlcX + displayY / pixel / 2 * 4 / 3 * pixel + level.get(i).x * pixel - px * pixel + level.get(i).w * pixel < tlcX||
                    tlcY + displayY / pixel / 2 * pixel + level.get(i).y * pixel - py * pixel + level.get(i).l * pixel < tlcY))
            g.fillRect((int) (tlcX + displayY / pixel / 2 * 4 / 3 * pixel + level.get(i).x * pixel - px * pixel), (int) (tlcY + displayY / pixel / 2 * pixel + level.get(i).y * pixel - py * pixel), (int) (level.get(i).w * pixel), (int) (level.get(i).l * pixel));
        }
        g.setColor(Color.red);
        for (int i = 0; i < enemies.size(); i++) {
            if (!(tlcX + displayY / pixel / 2 * 4 / 3 * pixel + enemies.get(i).x * pixel - px * pixel > displayX + tlcX ||
                    tlcY + displayY / pixel / 2 * pixel + enemies.get(i).y * pixel - py * pixel > displayY + tlcY ||
                    tlcX + displayY / pixel / 2 * 4 / 3 * pixel + enemies.get(i).x * pixel - px * pixel + enemies.get(i).size * pixel < tlcX||
                    tlcY + displayY / pixel / 2 * pixel + enemies.get(i).y * pixel - py * pixel + enemies.get(i).size * pixel < tlcY))
            g.fillRect((int) (tlcX + displayY / pixel / 2 * 4 / 3 * pixel + enemies.get(i).x * pixel - px * pixel), (int) (tlcY + displayY / pixel / 2 * pixel + enemies.get(i).y * pixel - py * pixel), (int) (enemies.get(i).size * pixel), (int) (enemies.get(i).size * pixel));
        }
        g.setColor(Color.green);
        if (attacking)
            g.setColor(Color.cyan);
        g.fillRect((int) (tlcX + displayY / pixel / 2 * 4 / 3 * pixel - (pw / 2) * pixel), (int) (tlcY + displayY / pixel / 2 * pixel - (pl / 2) * pixel), (int) (pw * pixel), (int) (pl * pixel));
        g.setColor(Color.blue);
        g.fillRect((int) (tlcX + displayY / pixel / 2 * 4 / 3 * pixel - 5000 * pixel - px * pixel), (int) (tlcY + displayY / pixel / 2 * pixel - 5000 * pixel - py * pixel), (int) (10000 * pixel), (int) (10000 * pixel));
        /*for (int i = 0; i < level.size(); i++)
            g.fillRect((int) (tlcX + level.get(i).x * pixel), (int) (tlcY + level.get(i).y * pixel), (int) (level.get(i).w * pixel), (int) (level.get(i).l * pixel));
        g.fillRect((int) ((mouseX  - tlcX) * pixel), (int) ((mouseY - tlcY) * pixel), (int) (1000 * pixel), (int) (1000 * pixel));
        g.setColor(Color.green);
        g.fillRect((int) (tlcX + (px - pw / 2) * pixel), (int) (tlcY + (py - pl / 2) * pixel), (int) (pw * pixel), (int) (pl * pixel));
        g.setColor(Color.black);
        g.drawRect((int) (tlcX + (px - pw / 2) * pixel), (int) (tlcY + (py - pl / 2) * pixel), (int) (pw * pixel), (int) (pl * pixel));*/

        g.setColor(Color.black);
        g.fillRect(0, 0, tlcX, displayY);
        g.fillRect(tlcX + displayX, 0, tlcX, displayY);
        g.fillRect(0, 0, displayX, tlcY);
        g.fillRect(0, tlcY + displayY, displayX, tlcY);
        g.setColor(Color.white);
        if (level.size() > 0)
            g.drawString(level.get(level.size() - 1).w + ", " + level.get(level.size() - 1).l, 10, 10);
        g.drawString(level.size() + "", 10, 30);
        g.drawString(coreHealth + "", 10, 50);

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    public void actionPerformed(ActionEvent e) {
        count++;
        if (!paused)
            update();
        repaint();
    }

    public void calculatePlayerDirection () {
        if (w ^ s) {
            moving = true;
            if (a ^ d) {
                if (w && d)
                    mAngle = 45;
                else if (w && a)
                    mAngle = 135;
                else if (s && a)
                    mAngle = 225;
                else
                    mAngle = 315;
            }
            else if (w)
                mAngle = 90;
            else
                mAngle = 270;
        }
        else if (a ^ d) {
            moving = true;
            if (d)
                mAngle = 0;
            else
                mAngle = 180;
        }
        else
            moving = false;
    }

    public void movePlayer () {
        double i = 0;
        while (i < distance && moving) {
            boolean straight = mAngle % 10 == 0;
            px += (int) (Math.cos(Math.toRadians(mAngle)) * .9 * 2);
            if (levelCollision(px - pw / 2, py - pl / 2, pw, pl, false) || coreCollision(px - pw / 2, py - pl / 2, pw, pl)) { //check collisions
                px -= (int) (Math.cos(Math.toRadians(mAngle)) * .9 * 2);
                straight = true;
            }
            py -= (int) (Math.sin(Math.toRadians(mAngle)) * .9 * 2);
            if (levelCollision(px - pw / 2, py - pl / 2, pw, pl, false) || coreCollision(px - pw / 2, py - pl / 2, pw, pl)) { //check collisions
                py += (int) (Math.sin(Math.toRadians(mAngle)) * .9 * 2);
                straight = true;
            }
            if (straight)
                i++;
            else
                i += Math.sqrt(2);
        }
        if (clicking)
            previewPlace = calcWall(xPoint, yPoint, (int) ((mouseX - tlcX) / pixel - displayY / pixel * 2 / 3 + px), (int) ((mouseY - tlcY) / pixel - displayY / pixel / 2 + py));
    }

    public boolean levelCollision (int x, int y, int w, int l, boolean destroy) {
        debug1 = pw;
        debug2 = pl;
        for (int i = 0; i < level.size(); i++) {
            Rectangle rect1 = new Rectangle(level.get(i).x, level.get(i).y, level.get(i).w, level.get(i).l);
            Rectangle rect2 = new Rectangle(x, y, w, l);
            if (rect1.intersects(rect2) && level.get(i).solid) {
                if (destroy && !(level.get(i).x < x && level.get(i).y < y && level.get(i).x + level.get(i).w > x + w && level.get(i).y + level.get(i).l > y + l))
                    level.remove(i);
                /*System.out.println(rect1);
                System.out.println(rect2);
                System.out.println(px + ", " + py + ", " + pw + ", " + pl);
                System.out.println("-----------------------------------------------------------");*/
                return true;
            }
            /*if (x > level.get(i).x + level.get(i).w || level.get(i).x < x + w){
                //System.out.println("one");
            }
            else if (y < level.get(i).y + level.get(i).l || level.get(i).y < y + l){
                //System.out.println("two");
            }
            else {
                System.out.println("shoot...");
                return true;
            }*/
        }
        return false;
    }
    public boolean attackCollision (int x, int y, int w, int l, boolean destroy) {
        debug1 = pw;
        debug2 = pl;
            Rectangle rect1 = new Rectangle(px - pw / 2, py - pl / 2, pw, pl);
            Rectangle rect2 = new Rectangle(x, y, w, l);
            if (rect1.intersects(rect2)) {
                return true;
            }
        return false;
    }

    public boolean coreCollision (int x, int y, int w, int l) {
        Rectangle rect1 = new Rectangle(-5000, -5000, 10000, 10000);
        Rectangle rect2 = new Rectangle(x, y, w, l);
        if (rect1.intersects(rect2))
            return true;
        return false;
    }

    public wall calcWall (int x1, int y1, int x2, int y2) {
        int x, y, w, l, xx, yy = 0;
        if (x1 < x2) {
            x = x1;
            xx = x2;
        }
        else {
            x = x2;
            xx = x1;
        }
        if (y1 < y2) {
            y = y1;
            yy = y2;
        }
        else {
            y = y2;
            yy = y1;
        }
        w = xx - x;
        l = yy - y;
        return new wall (x, y, w, l);
    }
    class MouseWheelStuff implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e){
            int amount = e.getWheelRotation();
            zoomAmount = amount * (1 + amount / 2);
            System.out.println(zoomAmount);
        }
    }
    class MouseButtonRecogn extends MouseAdapter {
        public void mouseMoved(MouseEvent event) {
            mouseX = event.getX();
            mouseY = event.getY();
        }
        public void mouseDragged(MouseEvent event) {
            if (event.getModifiers() == 16 && clicking) {
                mouseX = event.getX();
                mouseY = event.getY();
                previewPlace = calcWall(xPoint, yPoint, (int) ((mouseX - tlcX) / pixel - displayY / pixel * 2 / 3 + px), (int) ((mouseY - tlcY) / pixel - displayY / pixel / 2 + py));
            }
        }
        @Override
        public void /*mouseClicked*/mousePressed(MouseEvent event) {
            //if (firstClick) {
            if (event.getModifiers() == 4) {
                attacking = true;
                distance = 2000;
            }
            if (event.getModifiers() == 16 && !attacking) {
                firstClick = false;
                xPoint = (int) ((mouseX - tlcX) / pixel - displayY / pixel * 2 / 3 + px);
                yPoint = (int) ((mouseY - tlcY) / pixel - displayY / pixel / 2 + py);
                clicking = true;
            }
            /*}
            else {
                firstClick = true;
            }*/
        }
        public void mouseReleased(MouseEvent event) {
            wall c = calcWall(xPoint, yPoint, (int) ((mouseX - tlcX) / pixel - displayY / pixel * 2 / 3 + px), (int) ((mouseY - tlcY) / pixel - displayY / pixel / 2 + py));
            if (event.getModifiers() == 16 && c.w != 0 && c.l != 0 && clicking) {
                level.add(c);
            }
            if (event.getModifiers() == 16 && clicking) {
                clicking = false;
                previewPlace = new wall(0, 0, 0, 0);
            }
            if (event.getModifiers() == 4) {
                attacking = false;
                distance = 1000;
            }
        }
    }

    private class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W)
                w = true;
            if (key == KeyEvent.VK_A)
                a = true;
            if (key == KeyEvent.VK_S)
                s = true;
            if (key == KeyEvent.VK_D)
                d = true;
            if (key == KeyEvent.VK_P)
                paused = !paused;
            /*if (key == KeyEvent.VK_PAGE_UP) {
                zoomAmount *= 1.1;
            }
            if (key == KeyEvent.VK_PAGE_DOWN) {
                zoomAmount *= .9;
            }*/
        }
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W)
                w = false;
            if (key == KeyEvent.VK_A)
                a = false;
            if (key == KeyEvent.VK_S)
                s = false;
            if (key == KeyEvent.VK_D)
                d = false;
        }
    }
}
class wall {
    public boolean solid = false;
    public int x, y, l, w;
    public wall (int x, int y, int w, int l) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.l = l;
    }
}
class enemy {
    public double x, y, xs, ys, size = 0;
    public int intX, intY = 0;
    public enemy (double x, double y, double xs, double ys, double size) {
        this.xs = xs;
        this.ys = ys;
        this.size = size;
        this.x = x - size / 2;
        this.y = y - size / 2;
        intX = (int) x;
        intY = (int) y;
    }
    public void update () {
        x += xs;
        y += ys;
        intX = (int) x;
        intY = (int) y;
    }
}
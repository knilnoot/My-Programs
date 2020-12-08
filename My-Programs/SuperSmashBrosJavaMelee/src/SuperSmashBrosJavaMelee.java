import java.awt.*;
import java.awt.event.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.*;

public class SuperSmashBrosJavaMelee extends JPanel implements ActionListener
{
    private Timer timer;
    private MakeJFrame jFrame;
    private float gameZoom, windowZoom, zoom, camX, camY, originalScreenScale, screenScale, debug, anchorX, anchorY, camShiftX, camShiftY, camXDestination, camYDestination, zoomDestination  = 0;
    private int frameX, frameY, mouseX, mouseY, playerSwap = 0;
    private boolean lClicking, rClicking, deleting, gameStarted, placingBlastZone = false;
    private LevelBlock blockPreview, deleteTarget, notBlastZone;
    private List<LevelBlock> level = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private List<Sprite> playerIndicators = new ArrayList<>();
    public SuperSmashBrosJavaMelee(MakeJFrame jFrame)
    {
        MouseListener a = new MouseListener();
        addMouseListener(a);
        addMouseWheelListener(a);
        addMouseMotionListener(a);
        addKeyListener (new TAdapter ());
        setBackground (Color.red);
        setFocusable (true);
        this.jFrame = jFrame;
        gameZoom = 1;
        windowZoom = 1;
        originalScreenScale = 1920;
        loadLevel("./levelFiles/obstacleCourse.txt");
        players.add(new CrazyMan(-300, -500));
        players.add(new CrazyMan(0, -500));
        gameStarted = true;
        playerIndicators.add(new Sprite("./Sprites/playerIndicators/player1/", 4));
        playerIndicators.add(new Sprite("./Sprites/playerIndicators/player2/", 4));
        playerIndicators.add(new Sprite("./Sprites/playerIndicators/player3/", 4));
        playerIndicators.add(new Sprite("./Sprites/playerIndicators/player4/", 4));
        playerIndicators.add(new Sprite("./Sprites/playerIndicators/player5/", 4));
        playerIndicators.add(new Sprite("./Sprites/playerIndicators/player6/", 4));
        playerIndicators.add(new Sprite("./Sprites/playerIndicators/player7/", 4));
        playerIndicators.add(new Sprite("./Sprites/playerIndicators/player8/", 4));
        playerIndicators.add(new Sprite("./Sprites/playerIndicators/player9/", 4));
        playerIndicators.add(new Sprite("./Sprites/playerIndicators/player10/", 4));
        /*
        for(int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
            {
                level.add(new LevelBlock(i * 100, j * 100, 100, 100));
            }
            */
        initGame ();
    }
    public void initGame ()
    {
        timer = new Timer (30, this);
        timer.start ();
    }
    public void gameLogic ()
    {
        if (!gameStarted) {
            if (deleting) {
                deleteTarget = null;
                for (int i = 0; i < level.size(); i++)
                    if (blockContainsPoint(level.get(i), calcScreenToWorldX(mouseX), calcScreenToWorldY(mouseY)))
                        deleteTarget = level.get(i);
            }
            if (!deleting && deleteTarget != null)
                deleteTarget = null;
        }
        else {
            playerToLevelCollision();
            checkBlastZone();
            attackCollision();
        }
    }
    public void playerToLevelCollision () {
        for (int i = 0; i < players.size(); i++) {
            float xSteps = (int)(players.get(i).getxVelocity());
            float ySteps = (int)(players.get(i).getyVelocity());
            int totalSteps = 100;
            int checkThreshold = 1;
            float xTracker = 0;
            float yTracker = 0;
            float xTotal = 0; //results
            float yTotal = 0;
            boolean landed = false; //keep track of being airborne
            for(float j = 0; j < totalSteps; j++) {
                xTracker += xSteps / totalSteps;
                if (Math.abs(xTracker) >= checkThreshold) {
                    xTotal += xTracker;
                }
                int intersectsWith = -1;
                float shortestDistance = 0;
                Rectangle r1 = new Rectangle((int)(players.get(i).getX() + xTotal), (int)(players.get(i).getY() + yTotal), (int)players.get(i).getWidth(), (int)players.get(i).getHeight());
                for (int k = 0; k < level.size(); k++) {
                    Rectangle r2 = new Rectangle((int) level.get(k).getX(), (int) level.get(k).getY(), (int) level.get(k).getDx(), (int) level.get(k).getDy());
                    if (r1.intersects(r2)) {
                        float checkDistance;
                        if (xSteps >= 0)
                            checkDistance = (float)(r2.getX() - (r1.getX() - xTracker + r1.getWidth()));
                        else
                            checkDistance = (float)((r2.getX() + r2.getWidth()) - (r1.getX() - xTracker));
                        if (intersectsWith == -1 || Math.abs(checkDistance) < Math.abs(shortestDistance)) {
                            shortestDistance = checkDistance;
                            intersectsWith = k;
                        }
                    }
                }
                if (intersectsWith != -1) {
                    xSteps = 0;
                    xTotal = xTotal - xTracker + shortestDistance;
                    if (players.get(i).canBounce(players.get(i).getxVelocity()))
                        players.get(i).setxVelocity(-players.get(i).getxVelocity());
                    else
                        players.get(i).setxVelocity(0);
                }

                yTracker += ySteps / totalSteps;
                if (Math.abs(yTracker) >= checkThreshold) {
                    yTotal += yTracker;
                }
                intersectsWith = -1;
                shortestDistance = 0;
                r1 = new Rectangle((int)(players.get(i).getX() + xTotal), (int)(players.get(i).getY() + yTotal), (int)players.get(i).getWidth(), (int)players.get(i).getHeight());
                for (int k = 0; k < level.size(); k++) {
                    Rectangle r2 = new Rectangle((int) level.get(k).getX(), (int) level.get(k).getY(), (int) level.get(k).getDx(), (int) level.get(k).getDy());
                    if (r1.intersects(r2)) {
                        float checkDistance;
                        if (ySteps >= 0) {
                            players.get(i).resetJumps();
                            if (!players.get(i).canBounce(players.get(i).getyVelocity() / 2))
                                landed = true;
                            checkDistance = (float) (r2.getY() - (r1.getY() - yTracker + r1.getHeight()));
                        }
                        else
                            checkDistance = (float)((r2.getY() + r2.getHeight()) - (r1.getY() - yTracker));
                        if (intersectsWith == -1 || Math.abs(checkDistance) < Math.abs(shortestDistance)) {
                            shortestDistance = checkDistance;
                            intersectsWith = k;
                        }
                    }
                }
                if (intersectsWith != -1) {
                    ySteps = 0;
                    yTotal = yTotal - yTracker + shortestDistance;
                    if (players.get(i).canBounce(players.get(i).getyVelocity()) && players.get(i).getyVelocity() < 0)
                        players.get(i).setyVelocity(-players.get(i).getyVelocity());
                    else if (players.get(i).canBounce(players.get(i).getyVelocity() / 2))
                        players.get(i).setyVelocity(-players.get(i).getyVelocity());
                    else
                        players.get(i).setyVelocity(0);
                }

                if (Math.abs(xTracker) >= checkThreshold)
                    xTracker = 0;
                if (Math.abs(yTracker) >= checkThreshold)
                    yTracker = 0;
            }
            if (landed)
                players.get(i).setAirborne(false);
            else
                players.get(i).setAirborne(true);
            players.get(i).addX(xTotal);
            players.get(i).addY(yTotal);
            players.get(i).update();
        }
    }
    public void checkBlastZone () {
        for (int i = 0; i < players.size(); i++) {
            Rectangle r1 = new Rectangle((int) (players.get(i).getX()), (int) (players.get(i).getY()), (int) players.get(i).getWidth(), (int) players.get(i).getHeight());
            Rectangle r2 = new Rectangle((int) notBlastZone.getX(), (int) notBlastZone.getY(), (int) notBlastZone.getDx(), (int) notBlastZone.getDy());
            if (!r1.intersects(r2)) {
                players.get(i).kill();
            }
        }
    }
    public void attackCollision () {
    for (int i = 0; i < players.size(); i++) {
        for (int ii = 0; ii < players.get(i).getAttackSize(); ii++) {
            Attack attack = players.get(i).getAttack(ii);
            float xSteps = (int) (attack.getNextX() - attack.getX());
            float ySteps = (int) (attack.getNextY() - attack.getY());
            int totalSteps = 100;
            int checkThreshold = 1;
            float xTracker = 0;
            float yTracker = 0;
            float xTotal = 0; //results
            float yTotal = 0;
            boolean checkX = true;
            boolean checkY = true;
            for (float j = 0; (j < totalSteps || j < 1) && !attack.isOver(); j++) {
                xTracker += xSteps / totalSteps;
                if (Math.abs(xTracker) >= checkThreshold) {
                    checkX = true;
                    xTotal += xTracker;
                }
                int intersectsWith = -1;
                float shortestDistance = 0;
                boolean levelIsCloser = true;
                Rectangle r1 = new Rectangle((int) (attack.getX() + xTotal), (int) (attack.getY() + yTotal), (int) attack.getWidth(), (int) attack.getHeight());
                for (int k = 0; k < level.size() && checkX && attack instanceof Projectile; k++) {
                    Rectangle r2 = new Rectangle((int) level.get(k).getX(), (int) level.get(k).getY(), (int) level.get(k).getDx(), (int) level.get(k).getDy());
                    if (r1.intersects(r2)) {
                        float checkDistance;
                        if (xSteps >= 0)
                            checkDistance = (float) (r2.getX() - (r1.getX() - xTracker + r1.getWidth()));
                        else
                            checkDistance = (float) ((r2.getX() + r2.getWidth()) - (r1.getX() - xTracker));
                        if (intersectsWith == -1 || Math.abs(checkDistance) < Math.abs(shortestDistance)) {
                            shortestDistance = checkDistance;
                            intersectsWith = k;
                        }
                    }
                }
                for (int k = 0; k < players.size() && checkX; k++) {
                    Rectangle r2 = new Rectangle((int) (players.get(k).getX() + xTotal), (int) (players.get(k).getY() + yTotal), (int) players.get(k).getWidth(), (int) players.get(k).getHeight());
                    if (r1.intersects(r2) && k != i) {
                        float checkDistance;
                        if (xSteps >= 0)
                            checkDistance = (float) (r2.getX() - (r1.getX() - xTracker + r1.getWidth()));
                        else
                            checkDistance = (float) ((r2.getX() + r2.getWidth()) - (r1.getX() - xTracker));
                        if (intersectsWith == -1 || Math.abs(checkDistance) < Math.abs(shortestDistance)) {
                            levelIsCloser = false;
                            shortestDistance = checkDistance;
                            intersectsWith = k;
                        }
                    }
                }
                if (checkX && intersectsWith != -1) {
                    xSteps = 0;
                    xTotal = xTotal - xTracker + shortestDistance;
                    if (!levelIsCloser && ((attack instanceof Projectile && !attack.isOver()) || !(attack instanceof Projectile))) {
                        if (attack instanceof AnchoredOnPlayer && !((AnchoredOnPlayer) attack).checkHasHit(intersectsWith))
                            players.get(intersectsWith).hitPlayer(attack);
                        if (!(attack instanceof AnchoredOnPlayer))
                            players.get(intersectsWith).hitPlayer(attack);
                    }
                    if (attack instanceof Projectile)
                        ((Projectile) attack).kill();
                    if (attack instanceof AnchoredOnPlayer)
                        ((AnchoredOnPlayer) attack).addHasHit(intersectsWith);
                }

                yTracker += ySteps / totalSteps;
                if (Math.abs(yTracker) >= checkThreshold) {
                    checkY = true;
                    yTotal += yTracker;
                }

                //   Y

                intersectsWith = -1;
                shortestDistance = 0;
                levelIsCloser = true;
                r1 = new Rectangle((int) (attack.getX() + xTotal), (int) (attack.getY() + yTotal), (int) attack.getWidth(), (int) attack.getHeight());
                for (int k = 0; k < level.size() && checkY && attack instanceof Projectile; k++) {
                    Rectangle r2 = new Rectangle((int) level.get(k).getX(), (int) level.get(k).getY(), (int) level.get(k).getDx(), (int) level.get(k).getDy());
                    if (r1.intersects(r2)) {
                        float checkDistance;
                        if (ySteps >= 0) {
                            checkDistance = (float) (r2.getY() - (r1.getY() - yTracker + r1.getHeight()));
                        } else
                            checkDistance = (float) ((r2.getY() + r2.getHeight()) - (r1.getY() - yTracker));
                        if (intersectsWith == -1 || Math.abs(checkDistance) < Math.abs(shortestDistance)) {
                            shortestDistance = checkDistance;
                            intersectsWith = k;
                        }
                    }
                }
                for (int k = 0; k < players.size() && checkY; k++) {
                    Rectangle r2 = new Rectangle((int) (players.get(k).getX() + xTotal), (int) (players.get(k).getY() + yTotal), (int) players.get(k).getWidth(), (int) players.get(k).getHeight());
                    if (r1.intersects(r2) && k != i) {
                        float checkDistance;
                        if (ySteps >= 0) {
                            checkDistance = (float) (r2.getY() - (r1.getY() - yTracker + r1.getHeight()));
                        } else
                            checkDistance = (float) ((r2.getY() + r2.getHeight()) - (r1.getY() - yTracker));
                        if (intersectsWith == -1 || Math.abs(checkDistance) < Math.abs(shortestDistance)) {
                            levelIsCloser = false;
                            shortestDistance = checkDistance;
                            intersectsWith = k;
                        }
                    }
                }
                if (checkY && intersectsWith != -1) {
                    ySteps = 0;
                    yTotal = yTotal - yTracker + shortestDistance;
                    if (!levelIsCloser && ((attack instanceof Projectile && !attack.isOver()) || !(attack instanceof Projectile))) {
                        if (attack instanceof AnchoredOnPlayer && !((AnchoredOnPlayer) attack).checkHasHit(intersectsWith)) {
                            players.get(intersectsWith).hitPlayer(attack);
                        }
                        if (!(attack instanceof AnchoredOnPlayer))
                            players.get(intersectsWith).hitPlayer(attack);
                    }
                    if (attack instanceof Projectile)
                        ((Projectile) attack).kill();
                    if (attack instanceof AnchoredOnPlayer)
                        ((AnchoredOnPlayer) attack).addHasHit(intersectsWith);
                }

                if (Math.abs(xTracker) >= checkThreshold) {
                    checkX = false;
                    xTracker = 0;
                }
                if (Math.abs(yTracker) >= checkThreshold) {
                    checkY = false;
                    yTracker = 0;
                }
            }
        attack.addX(xTotal);
        attack.addY(yTotal);
        players.get(i).setAttackAtIndex(attack, ii);
        }
    }
}
    public void paint (Graphics g)
    {
        super.paint(g);
        if (gameStarted) {
            findCameraDestination();
            moveCamera();
        }
        updateFrame();
        paintStage(g);
        paintEdges(g);
        g.setColor(Color.white);
        for (int i = 0; i < players.size(); i++) {
            g.drawString("Player " + (i + 1), 20, i * 40 + 10);
            g.drawString(players.get(i).getDamage() + " %", 20, i * 40 + 20);
            g.drawString("deaths: " + players.get(i).getDeathCount(), 20, i * 40 + 30);
        }
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
    private void findCameraDestination() {
        float playerXSum = 0;
        float playerYSum = 0;
        float maximumDistance = 0;
        for (int i = 0; i < players.size(); i++) {
            playerXSum += players.get(i).getCX();
            playerYSum -= players.get(i).getCY();
        }
        camXDestination = playerXSum / players.size();
        camYDestination = playerYSum / players.size();
        for (int i = 0; i < players.size(); i++) {
            float checkDistance = Math.abs(players.get(i).getCX() - camXDestination);
            if (checkDistance > maximumDistance)
                maximumDistance = checkDistance;
            checkDistance = (float)(Math.abs(players.get(i).getCY() + camYDestination) * 1.33333);
            if (checkDistance > maximumDistance)
                maximumDistance = checkDistance;
        }
        zoomDestination = maximumDistance / 400;
    }
    private void  moveCamera() {
        camX = (camX + camXDestination) / 2;
        camY = (camY + camYDestination) / 2;
        gameZoom = (gameZoom + zoomDestination) / 2;
    }
    private void updateFrame() {
        frameX = jFrame.getWidth() - 13;
        frameY = jFrame.getHeight() - 35;
        int oldScreenScale = (int)(screenScale);
        if (screenScale == 0)
        {
            camX = 0;
            camY = 0;
        }
        if (frameX < frameY * 1.33333)
            screenScale = frameX;
        else
            screenScale = (int)(frameY * 1.33333);
        /*if (originalScreenScale < 0)
            originalScreenScale = screenScale;
        if (originalScreenScale > 0)*/
        windowZoom = originalScreenScale / screenScale - 1;
    }
    private void paintEdges (Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, (int)(frameX - screenScale - (frameX - screenScale) / 2), frameY);
        g.fillRect(0, 0, frameX, (int) (frameY - screenScale * .75 - (frameY - screenScale * .75) / 2));
        g.fillRect((int)(frameX - (frameX - screenScale) / 2), 0, (int)(frameX - screenScale - (frameX - screenScale) / 2), frameY);
        g.fillRect(0, (int) (frameY - (frameY - screenScale * .75) / 2), frameX, (int) (frameY - screenScale * .75 - (frameY - screenScale * .75) / 2));
    }
    private void paintStage (Graphics g)
    {
        zoom = gameZoom + windowZoom;
        g.setColor(new Color(200, 200, 200));
        if (notBlastZone != null)
            paintLevelBlock(g, notBlastZone);
        for (int i = 0; i < players.size(); i++) {
            int x = calcWorldToScreenX(players.get(i).getX() + players.get(i).getSpriteXOffset());
            int y = calcWorldToScreenY(players.get(i).getY() + players.get(i).getSpriteYOffset());
            int width = (int) (players.get(i).getSpriteWidth() / zoom);
            int height = (int) (players.get(i).getSpriteHeight() / zoom);
            for (int j = 0; j < players.get(i).getAttackSize(); j++) {
                Attack attack = players.get(i).getAttack(j);
                int ax = calcWorldToScreenX(attack.getX());
                int ay = calcWorldToScreenY(attack.getY());
                int aWidth = (int) (attack.getWidth() / zoom);
                int aHeight = (int) (attack.getHeight() / zoom);
                g.setColor(Color.green);
                g.drawRect(ax, ay, aWidth, aHeight);
            }
            if (players.get(i).getFacingLeft())
                g.drawImage(players.get(i).getSprite(), x, y, width, height, null);
            else
                g.drawImage(players.get(i).getSprite(), x + width, y, -width, height, null);
            for (int j = 0; j < players.get(i).getAttackSize(); j++) {
                Attack attack = players.get(i).getAttack(j);
                int ax = calcWorldToScreenX(attack.getX());
                int ay = calcWorldToScreenY(attack.getY());
                int aWidth = (int) (attack.getWidth() / zoom);
                int aHeight = (int) (attack.getHeight() / zoom);
                if (attack.getFacingLeft() && attack.getSprite() != null)
                    g.drawImage(attack.getSprite(), ax, ay, aWidth, aHeight, null);
                else if (attack.getSprite() != null)
                    g.drawImage(attack.getSprite(), ax + aWidth, ay, -aWidth, aHeight, null);
            }
        }
        if (zoom <= 0)
            zoom = (float) .000000001 + -zoom;
        paintLevel(g);
        for (int i = 0; i < playerIndicators.size(); i++) { //max player size is ten for now
            playerIndicators.get(i).update();
            if (players.size() > i) {
                int ax = calcWorldToScreenX((float)(players.get(i).getX() + players.get(i).getWidth() / 2 - 22.5));
                int ay = calcWorldToScreenY((players.get(i).getY() - 95));
                int aWidth = (int) (45 / zoom);
                int aHeight = (int) (85 / zoom);
                g.drawImage(playerIndicators.get(i).getImage(), ax, ay, aWidth, aHeight, null);
            }
        }
    }
    private void paintLevel (Graphics g) {
        g.setColor(Color.gray);
        for (int i = 0; i < level.size(); i++) {
            paintLevelBlock(g, level.get(i));
        }
        if (blockPreview != null) {
            g.setColor(new Color(150, 150, 255, 150));
            paintLevelBlock(g, calcBlock(calcScreenToWorldX(mouseX), calcScreenToWorldY(mouseY), anchorX, anchorY));
        }
        if(deleteTarget != null) {
            g.setColor(Color.red);
            paintLevelBlock(g, deleteTarget);
        }
        g.setColor(Color.blue);
        paintLevelBlock(g, new LevelBlock(0, 0, 10, 10));
    }
    private void paintLevelBlock (Graphics g, LevelBlock levelBlock) {
        int x = calcWorldToScreenX(levelBlock.getX());
        int y = calcWorldToScreenY(levelBlock.getY());
        int dx = (int) (levelBlock.getDx() / zoom);
        int dy = (int) (levelBlock.getDy() / zoom);
        g.fillRect(x, y, dx, dy);
    }
    public void actionPerformed(ActionEvent e)
    {
        gameLogic();
        repaint ();
    }

    private void loadLevel (String file) {
        level = new ArrayList<>();
        try {
            Scanner input = new Scanner(new File(file));
            notBlastZone = new LevelBlock(input.nextFloat(), input.nextFloat(), input.nextFloat(), input.nextFloat());
            while (input.hasNext()) {
                float x = input.nextFloat();
                float y = input.nextFloat();
                float dx = input.nextFloat();
                float dy = input.nextFloat();
                level.add(new LevelBlock(x, y, dx, dy));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private LevelBlock calcBlock (float x1, float y1, float x2, float y2) {
        float x, y, w, l, xx, yy = 0;
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
        return new LevelBlock (x, y, w, l);
    }

    private boolean blockContainsPoint(LevelBlock levelBlock, float x, float y) {
        return levelBlock.getX() <= x && levelBlock.getX() + levelBlock.getDx() >= x && levelBlock.getY() <= y && levelBlock.getY() + levelBlock.getDy() >= y;
    }

    private int calcWorldToScreenX (float worldX) {
        return (int) ((worldX - camX) / zoom + frameX / 2);
    }
    private int calcWorldToScreenY (float worldY) {
        return (int) ((worldY + camY) / zoom + frameY / 2);
    }
    private float calcScreenToWorldX (int screenX) {
        return (screenX - frameX / 2) * zoom + camX;
    }
    private float calcScreenToWorldY (int screenY) {
        return (screenY - frameY / 2) * zoom - camY;
    }

    class MouseListener extends MouseAdapter {
        public void mouseMoved(MouseEvent event) {
            mouseX = event.getX();
            mouseY = event.getY();
        }
        public void mouseDragged(MouseEvent event) {
            mouseX = event.getX();
            mouseY = event.getY();
            if (event.getModifiers() == 4 && rClicking && !gameStarted) {
                mouseX = event.getX();
                mouseY = event.getY();
                camX -= (mouseX - camShiftX) * zoom;
                camY += (mouseY - camShiftY) * zoom;
                camShiftX = mouseX;
                camShiftY = mouseY;
            }
        }

        @Override
        public void mousePressed(MouseEvent event) {
            if (event.getModifiers() == 4 && !gameStarted) {
                camShiftX = mouseX;
                camShiftY = mouseY;
                rClicking = true;
            }

            if (event.getModifiers() == 16 && !deleting && !gameStarted) {
                float x = calcScreenToWorldX(mouseX);
                float y = calcScreenToWorldY(mouseY);
                blockPreview = new LevelBlock(x, y, 0, 0);
                anchorX = x;
                anchorY = y;
                lClicking = true;
            }
        }
        public void mouseReleased(MouseEvent event) {
            if(!gameStarted) {
                if (event.getModifiers() == 16 && deleting) {
                    for (int i = 0; i < level.size() && deleting; i++)
                        if (level.get(i) == deleteTarget) {
                            level.remove(i);
                        }
                    deleteTarget = null;
                }
                if (event.getModifiers() == 16 && lClicking)
                    blockPreview = calcBlock(calcScreenToWorldX(mouseX), calcScreenToWorldY(mouseY), anchorX, anchorY);
                if (event.getModifiers() == 16 && blockPreview != null && blockPreview.getDx() != 0 && blockPreview.getDy() != 0 && lClicking) {
                    if (!placingBlastZone)
                        level.add(blockPreview);
                    else
                        notBlastZone = blockPreview;
                    blockPreview = null;
                }
                if (event.getModifiers() == 16 && lClicking) {
                    lClicking = false;
                    blockPreview = null;
                }
                if (event.getModifiers() == 4) {
                    camShiftX = mouseX;
                    camShiftY = mouseY;
                }
            }
        }
        public void mouseWheelMoved(MouseWheelEvent e){
            float amount = e.getWheelRotation();
            gameZoom += e.getWheelRotation() * gameZoom / 10;
        }
    }

    private class TAdapter extends KeyAdapter
    {
        public void keyPressed (KeyEvent e)
        {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ENTER && players.size() >= 2) {
                gameStarted = !gameStarted;
                deleteTarget = null;
                blockPreview = null;
            }
            if (!gameStarted) {
                /*if (key == KeyEvent.VK_UP) {
                    camY += 5 + gameZoom * 5;
                }
                if (key == KeyEvent.VK_DOWN) {
                    camY -= 5 + gameZoom * 5;
                }
                if (key == KeyEvent.VK_LEFT) {
                    camX -= 5 + gameZoom * 5;
                }
                if (key == KeyEvent.VK_RIGHT) {
                    camX += 5 + gameZoom * 5;
                }
                if (key == KeyEvent.VK_O) {
                    gameZoom -= gameZoom / 10;
                }
                if (key == KeyEvent.VK_P) {
                    gameZoom += gameZoom / 10;
                }*/
                if (key == KeyEvent.VK_DELETE) {
                    deleting = !deleting;
                }
                if (key == KeyEvent.VK_B) {
                    placingBlastZone = !placingBlastZone;
                }
                if (key == KeyEvent.VK_S) {
                    JFileChooser fileSelect = new JFileChooser();
                    //FileNameExtensionFilter extension = new FileNameExtensionFilter("Text file", ".txt");
                    //fileSelect.setFileFilter(extension);
                    fileSelect.setCurrentDirectory(new File("./levelFiles"));
                    int returnVal = fileSelect.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        try {
                            FileWriter fWriter = new FileWriter(new File(fileSelect.getSelectedFile() + ""));
                            PrintWriter pWriter = new PrintWriter(fWriter);
                            pWriter.println(notBlastZone.getX() + " " + notBlastZone.getY() + " " + notBlastZone.getDx() + " " + notBlastZone.getDy());
                            for (int i = 0; i < level.size(); i++) {
                                pWriter.println(level.get(i).getX() + " " + level.get(i).getY() + " " + level.get(i).getDx() + " " + level.get(i).getDy());
                            }
                            pWriter.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                if (key == KeyEvent.VK_L) {
                    JFileChooser fileSelect = new JFileChooser();
                    //FileNameExtensionFilter extension = new FileNameExtensionFilter("Text file", ".txt");
                    //fileSelect.setFileFilter(extension);
                    fileSelect.setCurrentDirectory(new File("./levelFiles"));
                    int returnVal = fileSelect.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        loadLevel(fileSelect.getSelectedFile() + "");
                    }
                }
                if (key == KeyEvent.VK_P && players.size() < 10) {
                    players.add(new CrazyMan(calcScreenToWorldX(mouseX), calcScreenToWorldY(mouseY)));
                }
            }
            else {

                //player one

                if (key == KeyEvent.VK_Q) { //shield
                }
                if (key == KeyEvent.VK_E) { //grab
                }
                if (key == KeyEvent.VK_R) { //pause
                    if (playerSwap < players.size())
                        playerSwap++;
                    if (playerSwap >= players.size())
                        playerSwap = 0;
                }
                if (key == KeyEvent.VK_T) { //dpad up
                }
                if (key == KeyEvent.VK_F) { //dpad left
                }
                if (key == KeyEvent.VK_H) { //dpad down
                }
                if (key == KeyEvent.VK_G) { //dpad right
                }
                if (key == KeyEvent.VK_W) { //move up
                }
                if (key == KeyEvent.VK_A) { //move left
                    players.get(playerSwap).setMovingLeft(true);
                }
                if (key == KeyEvent.VK_D) { //move right
                    players.get(playerSwap).setMovingRight(true);
                }
                if (key == KeyEvent.VK_S) { //move down
                }
                if (key == KeyEvent.VK_Z) { //Y
                    players.get(playerSwap).jump();
                }
                if (key == KeyEvent.VK_X) { //X
                    players.get(playerSwap).poke();
                }
                if (key == KeyEvent.VK_V) { //B
                    players.get(playerSwap).throwLollipop();
                }
                if (key == KeyEvent.VK_C) { //A
                    players.get(playerSwap).shout();
                }

                //player two

                if (key == KeyEvent.VK_I) { //shield
                }
                if (key == KeyEvent.VK_U) { //grab
                }
                if (key == KeyEvent.VK_Y) { //pause
                }
                if (key == KeyEvent.VK_J) { //dpad up
                }
                if (key == KeyEvent.VK_B) { //dpad left
                }
                if (key == KeyEvent.VK_N) { //dpad down
                }
                if (key == KeyEvent.VK_M) { //dpad right
                }
                if (key == KeyEvent.VK_UP) { //move up
                }
                if (key == KeyEvent.VK_LEFT) { //move left
                    players.get(1).setMovingLeft(true);
                }
                if (key == KeyEvent.VK_RIGHT) { //move right
                    players.get(1).setMovingRight(true);
                }
                if (key == KeyEvent.VK_DOWN) { //move down
                }
                if (key == KeyEvent.VK_P) { //Y
                    players.get(1).jump();
                }
                if (key == KeyEvent.VK_O) { //X
                    players.get(1).poke();
                }
                if (key == KeyEvent.VK_K) { //B
                    players.get(1).throwLollipop();
                }
                if (key == KeyEvent.VK_L) { //A
                    players.get(1).shout();
                }
            }
        }
        public void keyReleased (KeyEvent e) {
            int key = e.getKeyCode();
            if (gameStarted) {

                //player one

                if (key == KeyEvent.VK_Q) { //shield
                }
                if (key == KeyEvent.VK_E) { //grab
                }
                if (key == KeyEvent.VK_R) { //pause
                }
                if (key == KeyEvent.VK_T) { //dpad up
                }
                if (key == KeyEvent.VK_F) { //dpad left
                }
                if (key == KeyEvent.VK_H) { //dpad down
                }
                if (key == KeyEvent.VK_G) { //dpad right
                }
                if (key == KeyEvent.VK_W) { //move up
                }
                if (key == KeyEvent.VK_A) { //move left
                    players.get(playerSwap).setMovingLeft(false);
                }
                if (key == KeyEvent.VK_D) { //move right
                    players.get(playerSwap).setMovingRight(false);
                }
                if (key == KeyEvent.VK_S) { //move down
                }
                if (key == KeyEvent.VK_Z) { //Y
                    players.get(playerSwap).setCanJump(true);
                }
                if (key == KeyEvent.VK_X) { //X
                }
                if (key == KeyEvent.VK_V) { //B
                }
                if (key == KeyEvent.VK_C) { //A
                }

                //player two

                if (key == KeyEvent.VK_I) { //shield
                }
                if (key == KeyEvent.VK_U) { //grab
                }
                if (key == KeyEvent.VK_Y) { //pause
                }
                if (key == KeyEvent.VK_J) { //dpad up
                }
                if (key == KeyEvent.VK_B) { //dpad left
                }
                if (key == KeyEvent.VK_N) { //dpad down
                }
                if (key == KeyEvent.VK_M) { //dpad right
                }
                if (key == KeyEvent.VK_UP) { //move up
                }
                if (key == KeyEvent.VK_LEFT) { //move left
                    players.get(1).setMovingLeft(false);
                }
                if (key == KeyEvent.VK_RIGHT) { //move right
                    players.get(1).setMovingRight(false);
                }
                if (key == KeyEvent.VK_DOWN) { //move down
                }
                if (key == KeyEvent.VK_P) { //Y
                    players.get(1).setCanJump(true);
                }
                if (key == KeyEvent.VK_O) { //X
                }
                if (key == KeyEvent.VK_K) { //B
                }
                if (key == KeyEvent.VK_L) { //A
                }
            }
        }
    }
}
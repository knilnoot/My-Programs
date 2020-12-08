import javax.imageio.ImageIO;
import javax.sound.midi.MidiChannel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CandleMan extends Player{
    private List<Sprite> sprites = new ArrayList<Sprite>();
    private List<Attack> attacks = new ArrayList<Attack>();
    private Move currentMove;
    private float xSpawn;
    private float ySpawn;
    private int deathCount = 0;
    private float x;
    private float y;
    private float spriteXOffset;
    private float spriteYOffset;
    private float height;
    private float width;
    private float spriteHeight;
    private float spriteWidth;
    private int damage;
    private float xVelocity = 0;
    private float yVelocity = 0;
    private float bounceThreshold = 31;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean movingLeftHasPriority = false;
    private boolean canJump = true;
    public int spriteNumber;
    public boolean facingLeft = false;
    public boolean airborne = false;
    private int maximumJumps = 3;
    private int jumpCount = 0;
    private int burnCount = 10;
    private int burnDelay = 10;

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getCX() {
        return x + width / 2;
    }
    public float getCY() {
        return y + height / 2;
    }
    public float getHeight() {
        return height;
    }
    public float getWidth() {
        return width;
    }
    public float getxVelocity() {
        return xVelocity;
    }
    public float getyVelocity() {
        return yVelocity;
    }
    public int getSpriteNumber() {
        return spriteNumber;
    }
    public float getSpriteXOffset() {
        return spriteXOffset;
    }
    public float getSpriteYOffset() {
        return spriteYOffset;
    }
    public float getSpriteHeight() {
        return spriteHeight;
    }
    public float getSpriteWidth() {
        return spriteWidth;
    }
    public boolean canBounce(float speed) {
        if (currentMove instanceof MHelpless && Math.abs(speed) > bounceThreshold)
            return true;
        return false;
    }
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }
    public int getDamage() {
        return damage;
    }
    public int getDeathCount() { return deathCount;}
    public void setxVelocity(float xVelocity) {
        this.xVelocity = xVelocity;
    }
    public void setyVelocity(float yVelocity) {
        this.yVelocity = yVelocity;
    }
    public void setMovingLeft(boolean input) {
        movingLeft = input;
        if (input)
            movingLeftHasPriority = true;
    }
    public void setMovingRight(boolean input) {
        movingRight = input;
        if (input)
            movingLeftHasPriority = false;
    }
    public void setFacingLeft(boolean input) {
        facingLeft = input;
    }
    public boolean getFacingLeft() {
        return facingLeft;
    }
    public void addX(float x) {
        this.x += x;
    }
    public void addY(float y) {
        this.y += y;
    }
    public void resetJumps () {
        jumpCount = 0;
    }
    public void jump() {
        if (canJump && jumpCount < maximumJumps) {
            if(airborne && jumpCount == 0) {
                jumpCount++;
            }
            jumpCount++;
            currentMove = new MJump(this);
            sprites.get(4).reset();
        }
        canJump = false;
    }
    public void setAirborne (boolean input) {
        airborne = input;
    }
    public void throwLollipop() {
        if (!(currentMove instanceof MThrowLollipop)) {
            currentMove = new MThrowLollipop(this);
        }
    }
    public void shout() {
        if (!(currentMove instanceof MShout)) {
            currentMove = new MShout(this);
        }
    }
    public void poke() {
        if (!(currentMove instanceof MPoke)) {
            currentMove = new MPoke(this);
        }
    }
    public void setCanJump (boolean input) {
        canJump = input;
    }
    public void addAttack (Attack attack) {
        attacks.add(attack);
    }
    public void kill () {
        xVelocity = 0;
        yVelocity = 0;
        x = xSpawn;
        y = ySpawn;
        attacks = new ArrayList<>();
        currentMove = new MIdle();
        damage = 0;
        jumpCount = 0;
        jumpCount = 0;
        deathCount++;
    }

    public CandleMan (float x, float y) {
        this.x = x;
        this.y = y;
        xSpawn = x;
        ySpawn = y;
        spriteXOffset = -45;
        spriteYOffset = -60;
        height = 100;
        width = 70;
        spriteHeight = 160;
        spriteWidth = 160;
        spriteNumber = 0;
        currentMove = new MIdle();
        sprites.add(new Sprite("Sprites/candleManIdle/", 2));
        sprites.add(new Sprite("Sprites/candleManRunning/", 1));
        sprites.add(new Sprite("Sprites/dancingGuy/", 6));
        sprites.add(new Sprite("Sprites/candleManFalling/", 1, 8));
        sprites.add(new Sprite("Sprites/candleManJump/", 1, 12));
        sprites.add(new Sprite("Sprites/throwLeft/", 2));
        sprites.add(new Sprite("Sprites/spinAttack/", 1));
        sprites.add(new Sprite("Sprites/candleManHelpless/", 2));
        sprites.add(new Sprite("Sprites/shoutLeft/", 3));
        sprites.add(new Sprite("Sprites/pokeLeft/", 2));
    }
    public void hitPlayer (Attack attack) {
        if (!attack.isOver()) {
            damage += attack.getDamage();
            if (attack.getStrength() > currentMove.getArmor()) {
                float launchAngle = attack.getLaunchAngle();
                float launchSpeed = attack.getKnockback() * damage / 8;
                float xDirection = (float) Math.cos(Math.toRadians(launchAngle));
                if (attack.getFacingLeft())
                    xDirection *= -1;
                float yDirection = (float) -Math.sin(Math.toRadians(launchAngle));
                xVelocity = launchSpeed * xDirection;
                yVelocity = launchSpeed * yDirection;
                currentMove = new MHelpless((int) (launchSpeed * .5));
            }
        }
    }
    public void update() {
        spriteNumber = 0;
        if (!airborne && currentMove instanceof MHelpless)
            currentMove = new MIdle();
        if (!(currentMove instanceof MHelpless))
            Walking();
        else if(!airborne)
            xVelocity *= .9;
        else
            xVelocity *= .96;
        if (!currentMove.isOver())
            currentMove.update();
        if (currentMove.isOver())
            currentMove = new MIdle();
        yVelocity += 5  ;
        if (yVelocity > 60) {
            yVelocity *= .91;
            if (yVelocity < 60) {
                yVelocity = 60;
                if (currentMove instanceof MHelpless && yVelocity >= 0)
                    currentMove = new MIdle();
            }
        }

        burnCount++;
        if (burnCount >= burnDelay) {
            burnCount = 0;
            attacks.add(new ACandle(findAttackX(-5), y, this));
        }

        updateAttacks();
        updateSprite();
    }
    private void Walking() {
        if (!movingLeft)
            movingLeftHasPriority = false;
        if (!movingRight)
            movingLeftHasPriority = true;
        if (movingLeft && movingLeftHasPriority && !(currentMove instanceof cantMove)) {
            if (currentMove instanceof MIdle)
                spriteNumber = 1;
            if (!facingLeft)
                currentMove = new MTurn(this);
            else {
                //facingLeft = true;
                xVelocity -= 4;
                if (xVelocity < -20)
                    xVelocity = -20;
                if (xVelocity > 0)
                    xVelocity = 0;
            }
        }
        if (movingRight && !movingLeftHasPriority && !(currentMove instanceof cantMove)) {
            if (currentMove instanceof MIdle)
                spriteNumber = 1;
            if (facingLeft)
                currentMove = new MTurn(this);
            else {
                //facingLeft = false;
                xVelocity += 4;
                if (xVelocity > 20)
                    xVelocity = 20;
                if (xVelocity < 0)
                    xVelocity = 0;
            }
        }
        if (((!movingLeft && !movingRight) || currentMove instanceof cantMove) && !airborne)
            xVelocity = 0;
    }
    private void updateAttacks() {
        for (int i = 0; i < attacks.size(); i++) {
            if (attacks.get(i).isOver()) {
                attacks.remove(i);
                i--;
            }
            else
                attacks.get(i).update();
        }
    }
    private void updateSprite() {
        if (spriteNumber >= sprites.size() || spriteNumber < 0)
            spriteNumber = 0;
        if ((airborne && currentMove instanceof MIdle) || currentMove instanceof MJump) {
            spriteNumber = 4;
            if(yVelocity > -10)
            spriteNumber = 3;
        }
        if (currentMove instanceof MThrowLollipop)
            spriteNumber = 5;
        if (currentMove instanceof MPoke)
            spriteNumber = 9;
        if (currentMove instanceof MShout)
            spriteNumber = 8;
        if (currentMove instanceof MHelpless)
            spriteNumber = 7;
        for(int i = 0; i < sprites.size(); i++)
            if(spriteNumber != i)
                sprites.get(i).reset();
        sprites.get(spriteNumber).update();
    }
    public BufferedImage getSprite() {
        if (spriteNumber >= sprites.size())
            spriteNumber = 0;
        return sprites.get(spriteNumber).getImage();
    }
    public int getAttackSize () {
        return attacks.size();
    }
    public Attack getAttack (int index) {
        return attacks.get(index);
    }
    public float findAttackX (float input) {
        if(facingLeft)
            return getX() - input;
        return getX() + getWidth() + input;
    }
    public int getBurnDelay() {
        return burnDelay;
    }
    public void setAttackAtIndex(Attack attack, int index) {
        attacks.set(index, attack);
    }
}
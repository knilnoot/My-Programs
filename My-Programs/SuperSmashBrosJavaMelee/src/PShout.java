import java.awt.image.BufferedImage;

public class PShout extends Projectile{
    private Sprite sprite = new Sprite("Sprites/shout/", 2);
    private float x, y, height, width, xVelocity, yVelocity, gravity = 0;
    private boolean facingLeft, dead = false;
    public PShout (float x, float y, boolean facingLeft) {
        this.x = x;
        this.y = y;
        this.facingLeft = facingLeft;
        height = 100;
        width = 100;
        if (facingLeft)
            xVelocity = -20;
        else {
            this.x -= width;
            xVelocity = 20;
        }
        gravity = 0;
    }
    public boolean getReflectable() {
        return true;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public void addX(float input) {
        x += input;
    }
    public void addY(float input) {
        y += input;
    }
    public float getHeight() {
        return height;
    }
    public float getWidth() {
        return width;
    }
    public float getNextX() {
        return x + xVelocity;
    }
    public float getNextY() {
        return y + yVelocity;
    }
    public boolean getFacingLeft() {
        return facingLeft;
    }
    public float getLaunchAngle() {return 20;}
    public int getDamage() {
        return 2;
    }
    public float getKnockback() {
        return 10;
    }
    public float getStrength() {
        return 60;
    }
    public BufferedImage getSprite() {
        return sprite.getImage();
    }
    public boolean isOver() {
        return dead;
    }
    public void kill() {
        dead = true;
    }
    public void update() {
        x += xVelocity;
        y += yVelocity;
        yVelocity += gravity;
        sprite.update();
    }
}

import java.awt.image.BufferedImage;

public class PLollipop extends Projectile{
    private Sprite sprite = new Sprite("Sprites/lollypop/", 2);
    private float x, y, height, width, xVelocity, yVelocity, gravity = 0;
    private boolean facingLeft, dead = false;
    public PLollipop (float x, float y, boolean facingLeft) {
        this.x = x;
        this.y = y;
        this.facingLeft = facingLeft;
        height = 70;
        width = 70;
        if (facingLeft)
            xVelocity = -10;
        else {
            this.x -= width;
            xVelocity = 10;
        }
        yVelocity = -20;
        gravity = 1;
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
    public float getLaunchAngle() {return 45;}
    public int getDamage() {
        return 10;
    }
    public float getKnockback() {
        return 10;
    }
    public float getStrength() {
        return 40;
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

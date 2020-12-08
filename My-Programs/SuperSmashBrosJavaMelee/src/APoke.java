import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class APoke extends Attack implements AnchoredOnPlayer{
    private Player player;
    private List<Integer> hasHit = new ArrayList<>();
    private float x, y, xOffset, yOffset, height, width, duration, count = 0;
    private boolean facingLeft, dead = false;
    public APoke (float x, float y, Player player) {
        this.x = x;
        this.y = y;
        this.player = player;
        xOffset = x - player.getCX();
        yOffset = y - player.getY();
        facingLeft = player.getFacingLeft();
        height = 30;
        width = 50;
        if (!facingLeft)
            xOffset -= width;
        duration = 2;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public void addX(float input) {}
    public void addY(float input) {}
    public float getHeight() {
        return height;
    }
    public float getWidth() {
        return width;
    }
    public float getNextX() {
        return xOffset + player.getCX();
    }
    public float getNextY() {
        return yOffset + player. getY();
    }
    public boolean getFacingLeft() {
        return facingLeft;
    }
    public float getLaunchAngle() {return 35;}
    public int getDamage() {
        return 10;
    }
    public float getKnockback() {
        return 1;
    }
    public float getStrength() {
        return 100;
    }
    public BufferedImage getSprite() {
        return null;
    }
    public boolean isOver() {
        return count >= duration;
    }
    public boolean checkHasHit(int input) {
        for (int i = 0; i < hasHit.size(); i++)
            if (input == hasHit.get(i))
                return true;
        return false;
    }
    public void addHasHit(int input) {
        hasHit.add(input);
    }

    public void update() {
        count++;
        x = getNextX();
        y = getNextY();
    }
}

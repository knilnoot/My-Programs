import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ACandle extends Attack implements AnchoredOnPlayer{
    private Player player;
    private List<Integer> hasHit = new ArrayList<>();
    private float x, y, xOffset, yOffset, height, width, duration, count = 0;
    private boolean facingLeft, dead = false;
    public ACandle (float x, float y, Player player) {
        this.x = x;
        this.y = y;
        this.player = player;
        xOffset = x - player.getCX();
        yOffset = y - player.getY();
        facingLeft = player.getFacingLeft();
        height = 40;
        width = 55;
        if (!facingLeft)
            xOffset -= width;
        if (player instanceof CandleMan)
            duration = ((CandleMan) player).getBurnDelay();
        else
            duration = 10;
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
    public float getLaunchAngle() {return 80;}
    public int getDamage() {
        return 1;
    }
    public float getKnockback() {
        return 4;
    }
    public float getStrength() {
        return 20;
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
        facingLeft = player.getFacingLeft();
        count++;
        x = getNextX();
        y = getNextY();
    }
}

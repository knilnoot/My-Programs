import java.awt.image.BufferedImage;

public abstract class Attack{
    abstract float getX();
    abstract float getY();
    abstract void addX(float input);
    abstract void addY(float input);
    abstract float getNextX();
    abstract float getNextY();
    abstract float getHeight();
    abstract float getWidth();
    abstract boolean getFacingLeft();
    abstract float getLaunchAngle();
    abstract BufferedImage getSprite();
    abstract void update();
    abstract int getDamage();
    abstract float getKnockback();
    abstract float getStrength();//used to determine if an attack will pierce armor
    abstract boolean isOver();
}
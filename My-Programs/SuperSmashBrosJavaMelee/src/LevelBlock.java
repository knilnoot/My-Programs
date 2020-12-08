import java.util.logging.Level;

public class LevelBlock {
    private float x, y, dx, dy;
    public LevelBlock ()
    {
        x = -5;
        y = -5;
        dx = 10;
        dy = 10;
    }
    public LevelBlock (float x, float y)
    {
        this.x = x;
        this.y = y;
        dx = 10;
        dy = 10;
    }
    public LevelBlock (float x, float y, float dx, float dy)
    {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }
    public float getX()
    {
        return x;
    }
    public float getCx()
    {
        return x + dx / 2;
    }
    public float getY()
    {
        return y;
    }
    public float getCy()
    {
        return y + dy / 2;
    }
    public float getDx()
    {
        return dx;
    }
    public float getDy()
    {
        return dy;
    }
    public void setX(float x)
    {
        this.x = x;
    }
    public void setY(float y)
    {
        this.y = y;
    }
    public void setDx(float dx)
    {
        this.dx = dx;
    }
    public void setDy(float dy)
    {
        this.dy = dy;
    }
    public void addX(float input)
    {
        x += input;
    }
    public void addY(float input)
    {
        y += input;
    }
    public void addDx(float input)
    {
        dx += input;
    }
    public void addDy(float input)
    {
        dy += input;
    }
}

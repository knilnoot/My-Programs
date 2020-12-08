import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Sprite {
    private String path;
    private int fps, frameNumber, delayCount, loopBackTo = 0;
    private boolean hasLooped = false;
    private BufferedImage image;
    public Sprite (String path, int fps) {
        this.path = path;
        this.fps = fps;
        update();
    }
    public Sprite (String path, int fps, int loopBackTo) {
        this.path = path;
        this.fps = fps;
        this.loopBackTo = loopBackTo;
        update();
    }
    public void reset () {
        frameNumber = 0;
        hasLooped = false;
    }
    public void update () {
        try {
            image = ImageIO.read(new File(path + frameNumber + ".png"));
        }
        catch (IOException e) {
            try {
                image = ImageIO.read(new File(path + "0" + frameNumber + ".png"));
            }
            catch (IOException reee) {
                hasLooped = true;
                frameNumber = loopBackTo;
                try {
                    image = ImageIO.read(new File(path + frameNumber + ".png"));
                } catch (IOException wow___howDidThisExceptionEvenHappen_youProbablyRuinedThePngFilesYouBastard___wow___just_Wow___whatKindOfIdiotWouldRemoveTheSpritesForThisGame____iGuessThisVariableMightBeALittleBitTooLong) {
                    System.out.println("Uh oh, I think there's an issue with this Sprite: " + path);
                }
            }
        }
        delayCount++;
        if (delayCount >= fps) {
            frameNumber++;
            delayCount = 0;
        }
    }
    public BufferedImage getImage() {
        return image;
    }
    public boolean getHasLooped () {
        return hasLooped;
    }
}

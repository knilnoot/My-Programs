import javax.swing.JFrame;
import java.awt.Toolkit;

public class MakeJFrame extends JFrame
    {
    public MakeJFrame() {
        add (new SuperSmashBrosJavaMelee(this));
        windowSettings();
    }
    public void windowSettings () {
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setSize ((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        setLocationRelativeTo (null);
        setTitle ("JFrame");
        setResizable (true);
        setVisible (true);
        System.out.println("Program Started!");
    }
    public static void main(String [] args)
    {
        new MakeJFrame();
    }
}

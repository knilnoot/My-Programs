public class MHelpless extends Move{
    private int duration, count = 0;
    public MHelpless(int duration) {
        this.duration = duration;
    }
    public void update() { count++;}
    public float getArmor() {
        return 10;
    }
    public boolean isOver() {
        return count >= duration;
    }
}

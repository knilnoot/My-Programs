public class MJump extends Move implements cantMove{
    private Player player;
    private int duration, count = 0;
    public MJump(Player player) {
        count = 0;
        duration = 3;
        this.player = player;
    }
    public void update() {
        if(count == 1)
            player.setyVelocity(-60);
        count++;
    }
    public float getArmor() {
        return 10;
    }
    public boolean isOver() {
        return count >= duration;
    }
}

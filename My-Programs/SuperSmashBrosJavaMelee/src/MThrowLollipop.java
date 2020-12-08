public class MThrowLollipop extends Move implements cantMove{
    private Player player;
    private int duration, count = 0;
    public MThrowLollipop(Player player) {
        duration = 30;
        this.player = player;
    }
    public void update() {
        if (count == 20) {
            player.addAttack(new PLollipop(player.findAttackX(70), player.getY() + 60, player.getFacingLeft()));
        }
        count++;
    }
    public float getArmor() {
        return 10;
    }
    public boolean isOver() {
        return count >= duration;
    }
}

public class MShout extends Move implements cantMove{
    private Player player;
    private int duration, count = 0;
    public MShout(Player player) {
        duration = 21;
        this.player = player;
    }
    public void update() {
        if (count == 12) {
            player.addAttack(new PShout(player.findAttackX(70), player.getY(), player.getFacingLeft()));
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

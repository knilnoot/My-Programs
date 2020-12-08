public class MPoke extends Move implements cantMove{
    private Player player;
    private int duration, count = 0;
    public MPoke(Player player) {
        duration = 12;
        this.player = player;
    }
    public void update() {
        if (count == 8) {
            player.addAttack(new APoke(player.findAttackX(30), player.getY() + 60, player));
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

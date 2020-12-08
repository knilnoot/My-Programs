public class MTurn extends Move implements cantMove{
    private Player player;
    private int duration, count = 0;
    public MTurn(Player player) {
        duration = 1;
        this.player = player;
    }
    public void update() {
        if(count == 0)
            player.setFacingLeft(!player.getFacingLeft());
        count++;
    }
    public float getArmor() {
        return 10;
    }
    public boolean isOver() {
        return count >= duration;
    }
}

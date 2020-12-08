/*
 This abstract class is extended by all the moves (the format for their names is "M*insertMoveNameHere*")
 Moves can be ones you specifically input, like MPoke, or ones that are not ones you input, like MHelpless
 */
public abstract class Move {
    abstract void update();
    abstract float getArmor(); //armor prevents any knockback, but not damage.
    abstract boolean isOver();
}

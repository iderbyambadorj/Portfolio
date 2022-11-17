/**
 * Interface that moving entities implement
 * @author Ider Byambadorj
 */
public interface Movable {
    /**
     * Method that moves the entity given the direction
     * @param xMove     displacement in x direction
     * @param yMove     displacement in y direction
     */
    void move(double xMove, double yMove);
}

import bagel.DrawOptions;
import bagel.Image;

/**
 * Class used to represent projectiles fired by pirates and blackbeard.
 * @author Ider Byambadorj
 */
public class Projectile implements Movable{
    // Variables
    private double x;
    private double y;
    private final double speed;
    private final int damage;
    private final Image image;
    private final double rotation;
    private boolean exploded = false;

    // Constants
    private final DrawOptions ROTATION = new DrawOptions();
    private final static int X_DIFF = 20;
    private final static int Y_DIFF = 29;

    public Projectile(double x, double y, double speed, int damage,
                      Image image, double rotation) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.damage = damage;
        this.image = image;
        this.rotation = rotation;
        ROTATION.setRotation(rotation);
    }

    /**
     * Performs state update of the projectile
     * @param sailor    sailor to get the world edges coordinates.
     */
    public void update(Sailor sailor) {
        move(speed*Math.cos(rotation), speed*Math.sin(rotation));
        image.drawFromTopLeft(x, y, ROTATION);
        if (isOutOfBound(sailor)) {
            explode();
        }
    }

    // Implemented method from Movable interface
    public void move(double xMove, double yMove){
        x += xMove;
        y += yMove;
    }

    /**
     * Getter function of exploded
     * @return exploded
     */
    public boolean isExploded() {
        return exploded;
    }

    /**
     * Setter function of exploded
     */
    public void explode() {
        exploded = true;
    }

    /**
     * Getter function of damage
     * @return damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Getter function of x
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Getter function of y
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * Method used to check whether the projectile has gone out of bound
     * @param sailor    sailor to get world edges coordinates
     * @return boolean  true if projectile has gone out of bound
     */
    public boolean isOutOfBound(Sailor sailor) {
        double leftEdge = sailor.getTopLeftBorder().x;
        double topEdge = sailor.getTopLeftBorder().y;
        double rightEdge = sailor.getBottomRightBorder().x + X_DIFF;
        double bottomEdge = sailor.getBottomRightBorder().y + Y_DIFF;

        return (x < leftEdge || x > rightEdge ||
                y < topEdge || y > bottomEdge);
    }
}

import bagel.Image;

/**
 * Class used to represent pirate
 * @author Ider Byambadorj
 */
public class Pirate extends Enemy {
    public Pirate(double x, double y) {
        super(x, y, 1);
        // Images
        LEFT_IMAGE = new Image("res/pirate/pirateLeft.png");
        RIGHT_IMAGE = new Image("res/pirate/pirateRight.png");
        LEFT_HIT = new Image("res/pirate/pirateHitLeft.png");
        RIGHT_HIT = new Image("res/pirate/pirateHitRight.png");
        PROJECTILE = new Image("res/pirate/pirateProjectile.png");
        currentImage = LEFT_IMAGE;
    }
}

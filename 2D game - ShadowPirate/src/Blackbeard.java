import bagel.Image;

/**
 * Class used to represent blackbeard in level 1
 * @author Ider Byambadorj
 */
public class Blackbeard extends Enemy{
    public Blackbeard(double x, double y) {
        super(x, y,2);
        LEFT_IMAGE = new Image("res/blackbeard/blackbeardLeft.png");
        RIGHT_IMAGE = new Image("res/blackbeard/blackbeardRight.png");
        LEFT_HIT = new Image("res/blackbeard/blackbeardHitLeft.png");
        RIGHT_HIT = new Image("res/blackbeard/blackbeardHitRight.png");
        PROJECTILE = new Image("res/blackbeard/blackbeardProjectile.png");
        currentImage = LEFT_IMAGE;
    }
}

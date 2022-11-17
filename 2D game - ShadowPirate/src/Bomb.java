import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Class used to represent bombs in level 1
 * Child class of Block
 * @author Ider Byambadorj
 */

public class Bomb extends Block {
    private final static Image BOMB = new Image("res/bomb.png");
    private final static Image EXPLOSION = new Image("res/explosion.png");
    private final static double COOL_DOWN = 0.5;
    private final static int FPS = 60;

    public boolean normal=true;
    public boolean exploding=false;
    private boolean exploded = false;
    private int timeCount;

    public Bomb(int startX, int startY) {
        super(startX, startY);
    }

    /**
     * Performs state update. Draws the image
     */
    @Override
    public void update() {
        if (normal) {
            BOMB.drawFromTopLeft(x, y);
        } else if (exploding) {
            EXPLOSION.drawFromTopLeft(x, y);
        }
    }

    /**
     * Method used to get the surrounding rectangle
     * @return Rectangle    Surrounding rectangle
     */
    @Override
    public Rectangle getBoundingBox(){
        if (normal) {
            return BOMB.getBoundingBoxAt(new Point(x+WIDTH/2, y+HEIGHT/2));
        } else {
            return EXPLOSION.getBoundingBoxAt(new
                    Point(x+WIDTH/2, y+HEIGHT/2));
        }
    }

    /**
     * Method used to change the bomb status and implement cool down.
     */
    public void explode() {
        if (normal) {
            timeCount = ShadowPirate.frameCount;
            normal=false;
            exploding=true;
        }
        if (((ShadowPirate.frameCount - timeCount)==
                COOL_DOWN*FPS)&&exploding) {
            timeCount = Integer.MAX_VALUE;
            exploding=false;
            exploded=true;
        }
    }

    /**
     * Getter function of exploded
     * @return exploded     status of the bomb
     */
    public boolean isExploded() {
        return exploded;
    }

    /**
     * Getter function of damage
     * @return 10   Damage Point of the bomb
     */
    public int getDamage() {
        return 10;
    }
}

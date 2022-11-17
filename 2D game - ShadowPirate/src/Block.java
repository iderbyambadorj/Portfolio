import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Class used to represent the block
 *
 * Sample solution of Project 1 is used and modified.
 * Credit of solution goes to Tharun Dharmawickrema.
 *
 * @author Ider Byambadorj
 */
public class Block{
    private final static Image BLOCK = new Image("res/block.png");
    protected final static double WIDTH = 32;
    protected final static double HEIGHT = 40;
    protected final int x;
    protected final int y;

    public Block(int startX, int startY){
        this.x = startX;
        this.y = startY;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        BLOCK.drawFromTopLeft(x, y);
    }

    /**
     * Method used to get rectangle of the object
     * @return Rectangle that surrounds the object
     */
    public Rectangle getBoundingBox(){
        return BLOCK.getBoundingBoxAt(new Point(x+WIDTH/2, y+HEIGHT/2));
    }
}
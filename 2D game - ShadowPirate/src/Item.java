import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Class used to represent three items: potion, elixir and sword
 * @author Ider Byambadorj
 */
public class Item {
    // Variables to hold all information
    private final String name;
    private final int x;
    private final int y;
    private final Image IMAGE;
    public final Image ICON;
    private final static double WIDTH = 32;
    private final static double HEIGHT = 40;
    private boolean pickedUp = false;

    public Item(String name, int x, int y, String image, String icon) {
        this.name = name;
        this.x = x;
        this.y = y;
        IMAGE = new Image(image);
        ICON = new Image(icon);
    }

    /**
     * Performs state update. Draws the image
     */
    public void update() {
        IMAGE.drawFromTopLeft(x, y);
    }

    /**
     * Method used to get surrounding Rectangle
     * @return Rectangle    surrounding rectangle
     */
    public Rectangle getBoundingBox(){
        return IMAGE.getBoundingBoxAt(new Point(x+WIDTH/2, y+HEIGHT/2));
    }

    /**
     * Getter function of name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter function of pickedUp
     * @return pickedUp
     */
    public boolean isPickedUp() {
        return pickedUp;
    }

    /**
     * Setter function of pickedUp
     */
    public void setPickedUp() {
        pickedUp=true;
    }
}

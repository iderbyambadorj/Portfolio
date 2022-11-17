import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;


/**
 * Class used to represent the sailor / player.
 *
 * Sample solution of Project 1 is used and modified.
 * Credit of solution goes to Tharun Dharmawickrema.
 *
 * @author Ider Byambadorj
 */
public class Sailor implements Movable {
    // Constants for images
    private final static Image SAILOR_LEFT = new
            Image("res/sailor/sailorLeft.png");
    private final static Image SAILOR_RIGHT = new
            Image("res/sailor/sailorRight.png");
    private final static Image SAILOR_HIT_LEFT = new
            Image("res/sailor/sailorHitLeft.png");
    private final static Image SAILOR_HIT_RIGHT = new
            Image("res/sailor/sailorHitRight.png");
    private final static double WIDTH = 40;
    private final static double HEIGHT = 58;

    // Constants and variables for sailor health and movement
    private final static int MOVE_SIZE = 1;
    private static int MAX_HEALTH_POINTS = 100;
    private static int DAMAGE_POINTS = 15;
    private final static int THRESHOLD = 1;

    // Constants for log
    private final static int PIRATE_DAMAGE = 10;
    private final static String BOMB_MESSAGE =
            "Bomb inflicts 10 damage points on " +
                    "Sailor. Sailor's current health: ";
    private final static String PIRATE_MESSAGE =
            "Pirate inflicts 10 damage points on Sailor. " +
                    "Sailor's current health: ";
    private final static String BLACKBEARD_MESSAGE =
            "Blackbeard inflicts 20 damage points on Sailor. " +
                    "Sailor's current health: ";
    private final static String ITEM_MESSAGE1 = "Sailor finds ";
    private final static String ITEM_MESSAGE2 = ". Sailor's current health: ";
    private final static String SWORD_MESSAGE = "Sailor finds Sword. " +
            "Sailor's damage points increased to 30";

    // World edges
    private int BOTTOM_EDGE;
    private int TOP_EDGE;
    private int LEFT_EDGE;
    private int RIGHT_EDGE;

    // Constants for rendering health
    private final static int HEALTH_X = 10;
    private final static int HEALTH_Y = 25;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 30;
    private final static Font FONT = new
            Font("res/wheaton.otf", FONT_SIZE);
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    // Constants for rendering inventory
    private final static int ITEM_Y_OFFSET = 5;
    private final static int ITEM_HEIGHT = 40;

    // Variables
    private int healthPoints;
    private int oldX;
    private int oldY;
    private int x;
    private int y;
    private Image currentImage;

    // Variables and constants for states and cool down
    private boolean attack = false;
    private boolean coolDown = false;
    private boolean hit = false;
    private int timeCount;
    private final static int FPS = 60;
    private final static double ATTACK_TIME = 1;
    private final static double COOL_DOWN = 2;

    // ArrayList to hold all items in the inventory
    private final ArrayList<Item> inventory = new ArrayList<>();

    public Sailor(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = SAILOR_RIGHT;
        COLOUR.setBlendColour(GREEN);
    }

    /**
     * Method that performs state update
     * @param input Keyboard input
     */
    public void update(Input input, ArrayList<Pirate>
            pirates, Blackbeard blackbeard) {
        if ((!coolDown && input.wasPressed(Keys.S)) || attack) {
            attack(pirates, blackbeard);
        }
        if (coolDown) {
            // wait for 2 seconds
            coolDown();
        }

        // store old coordinates every time the sailor moves
        if (input.isDown(Keys.UP)) {
            setOldPoints();
            move(0, -MOVE_SIZE);
        } else if (input.isDown(Keys.DOWN)) {
            setOldPoints();
            move(0, MOVE_SIZE);
        } else if (input.isDown(Keys.LEFT)) {
            setOldPoints();
            move(-MOVE_SIZE, 0);
            currentImage = SAILOR_LEFT;
        } else if (input.isDown(Keys.RIGHT)) {
            setOldPoints();
            move(MOVE_SIZE, 0);
            currentImage = SAILOR_RIGHT;
        }

        // Attack
        if (attack) {
            if (currentImage.equals(SAILOR_LEFT)) {
                currentImage = SAILOR_HIT_LEFT;
            } else if (currentImage.equals(SAILOR_RIGHT)) {
                currentImage = SAILOR_HIT_RIGHT;
            }
        }
        renderInventory();
        currentImage.drawFromTopLeft(x, y);
        renderHealthPoints();
        checkOutOfBound();
    }

    /**
     * Method that checks for collisions between sailor and blocks
     * @param blocks ArrayList of blocks
     */
    public void checkBlocks(ArrayList<Block> blocks) {
        // check collisions
        Rectangle sailorBox = getSailorBox();
        for (Block current : blocks) {
            Rectangle blockBox = current.getBoundingBox();
            if (sailorBox.intersects(blockBox)) {
                moveBack();
            }
        }
    }

    /**
     * Method used to check for collisions between sailor and bombs
     * @param bombs     ArrayList of bombs
     */
    public void checkBombs(ArrayList<Bomb> bombs) {
        Rectangle sailorBox = getSailorBox();
        for (Bomb current : bombs) {
            Rectangle blockBox = current.getBoundingBox();
            if (sailorBox.intersects(blockBox)) {
                moveBack();
                if (current.normal) {
                    healthPoints -= current.getDamage();
                    System.out.println(BOMB_MESSAGE+healthPoints+"/"
                            +MAX_HEALTH_POINTS);
                    current.explode();
                }
            }
            if (current.exploding) {
                current.explode();
            }
        }
    }

    /**
     * Method that checks for collisions between sailor and items on the ground
     * @param items     ArrayList of items
     */
    public void checkItems(ArrayList<Item> items) {
        Rectangle sailorBox = getSailorBox();
        for (Item item : items) {
            Rectangle itemBox = item.getBoundingBox();
            if (sailorBox.intersects(itemBox)) {
                pickItem(item);
                inventory.add(item);
            }
        }
    }

    /**
     * Method that checks for collisions between sailor and projectile
     * @param projectiles   ArrayList of projectiles
     */
    public void checkProjectiles(ArrayList<Projectile> projectiles) {
        for (Projectile projectile: projectiles) {
            if (projectile.getX() > x-THRESHOLD &&
                    projectile.getX() < x+WIDTH+THRESHOLD) {
                if (projectile.getY() > y-THRESHOLD &&
                        projectile.getY() < y+HEIGHT+THRESHOLD) {
                    if (!projectile.isExploded()) {
                        healthPoints -= projectile.getDamage();
                        if (projectile.getDamage()==PIRATE_DAMAGE) {
                            System.out.println(PIRATE_MESSAGE+healthPoints+
                                    "/"+MAX_HEALTH_POINTS);
                        } else {
                            System.out.println(BLACKBEARD_MESSAGE+healthPoints
                                    +"/"+MAX_HEALTH_POINTS);
                        }
                        projectile.explode();
                    }
                }
            }
        }

    }

    // Implemented method from Movable interface
    public void move(double xMove, double yMove) {
        x += xMove;
        y += yMove;
    }

    /**
     * Method that checks if sailor is dead
     */
    public boolean isDead() {
        return healthPoints <= 0;
    }

    /**
     * Method that sets the top left border
     * @param top  top edge
     * @param left left edge
     */
    public void setTopLeftBorder(int top, int left) {
        TOP_EDGE = top;
        LEFT_EDGE = left;
    }

    /**
     * Method that sets the bottom right border
     * @param bottom bottom edge
     * @param right  right edge
     */
    public void setBottomRightBorder(int bottom, int right) {
        BOTTOM_EDGE = bottom;
        RIGHT_EDGE = right;
    }

    /**
     * Getter function of top left border
     */
    public Point getTopLeftBorder() {
        return new Point(LEFT_EDGE, TOP_EDGE);
    }

    /**
     * Getter function of bottom right border
     */
    public Point getBottomRightBorder() {
        return new Point(RIGHT_EDGE, BOTTOM_EDGE);
    }

    /**
     * Getter function of x
     * @return x
     */
    public int getX() {
        return this.x;
    }

    /**
     * Getter function of y
     * @return y
     */
    public int getY() {
        return this.y;
    }

    /**
     * Getter function of image
     * @return currentImage
     */
    public Image getImage() {
        return this.currentImage;
    }

    /**
     * Method to add item to inventory
     * @param item  Item to be picked up
     */
    public void pickItem(Item item) {
        switch (item.getName()) {
            case "Potion":
                healthPoints = Math.min(healthPoints + 25, MAX_HEALTH_POINTS);
                System.out.println(ITEM_MESSAGE1+"Potion"+ITEM_MESSAGE2+
                        healthPoints+"/"+MAX_HEALTH_POINTS);
                break;
            case "Elixir":
                MAX_HEALTH_POINTS += 35;
                healthPoints = MAX_HEALTH_POINTS;
                System.out.println(ITEM_MESSAGE1+"Elixir"+ITEM_MESSAGE2+
                        healthPoints+"/"+MAX_HEALTH_POINTS);
                break;
            case "Sword":
                DAMAGE_POINTS += 15;
                System.out.println(SWORD_MESSAGE);
                break;
        }
        item.setPickedUp();
    }

    /**
     * Method to get surrounding Rectangle
     * @return Rectangle
     */
    public Rectangle getSailorBox() {
        return currentImage.getBoundingBoxAt(new
                Point(x + WIDTH / 2, y + HEIGHT / 2));
    }

    /**
     * Method to attack enemies and implement cool down
     * @param pirates       ArrayList of Pirate
     * @param blackbeard    ArrayList of Blackbeard
     */
    private void attack(ArrayList<Pirate> pirates, Blackbeard blackbeard) {
        if (!attack) {
            timeCount = ShadowPirate.frameCount;
            attack = true;
            hit = false;
        }
        if ((ShadowPirate.frameCount - timeCount) == ATTACK_TIME * FPS) {
            attack = false;
            timeCount = 0;
            if (currentImage.equals(SAILOR_HIT_LEFT)) {
                currentImage = SAILOR_LEFT;
            } else if (currentImage.equals(SAILOR_HIT_RIGHT)) {
                currentImage = SAILOR_RIGHT;
            }
            coolDown();
        }
        if (!hit) {
            Rectangle sailorBox = getSailorBox();
            for (Pirate pirate : pirates) {
                Rectangle pirateBox = pirate.getEnemyBox();
                if (sailorBox.intersects(pirateBox)) {
                    pirate.getAttacked(DAMAGE_POINTS);
                    hit = true;
                }
            }
            if (blackbeard != null) {
                Rectangle blackbeardBox = blackbeard.getEnemyBox();
                if (sailorBox.intersects(blackbeardBox)) {
                    blackbeard.getAttacked(DAMAGE_POINTS);
                    hit = true;
                }
            }
        }
    }

    /**
     * Method to implement cool down
     */
    private void coolDown() {
        if (!coolDown) {
            timeCount = ShadowPirate.frameCount;
            coolDown = true;
        }
        if ((ShadowPirate.frameCount - timeCount) == COOL_DOWN * FPS) {
            coolDown = false;
            timeCount = Integer.MAX_VALUE;
        }
    }

    /**
     * Method that stores the old coordinates of the sailor
     */
    private void setOldPoints() {
        oldX = x;
        oldY = y;
    }

    /**
     * Method that moves the sailor back to its previous position
     */
    private void moveBack() {
        x = oldX;
        y = oldY;
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    private void renderHealthPoints() {
        double percentageHP = ((double) healthPoints / MAX_HEALTH_POINTS) * 100;
        if (percentageHP <= RED_BOUNDARY) {
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY) {
            COLOUR.setBlendColour(ORANGE);
        } else {
            COLOUR.setBlendColour(GREEN);
        }
        FONT.drawString(Math.round(percentageHP) + "%",
                HEALTH_X, HEALTH_Y, COLOUR);
    }

    /**
     * Method that renders the inventory
     */
    private void renderInventory() {
        for (int i=0; i<inventory.size(); i++) {
            inventory.get(i).ICON.drawFromTopLeft(HEALTH_X,
                    HEALTH_Y+FONT_SIZE+2*ITEM_Y_OFFSET+
                            i*(ITEM_Y_OFFSET+ITEM_HEIGHT));
        }
    }

    /**
     * Method that moves the sailor back if it has gone out-of-bound
     */
    private void checkOutOfBound() {
        if ((y > BOTTOM_EDGE) || (y < TOP_EDGE) || (x < LEFT_EDGE) ||
                (x > RIGHT_EDGE)) {
            moveBack();
        }
    }
}
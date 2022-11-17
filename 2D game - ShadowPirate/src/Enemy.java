import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class used to hold all attributes and methods
 * related to the enemy
 * @author Ider Byambadorj
 */
abstract class Enemy implements Movable{
    // Speed
    Random random = new Random();
    private final static double MIN_SPEED=0.2;
    private final static double MAX_SPEED=0.7;
    private final double speed = MIN_SPEED+(MAX_SPEED-MIN_SPEED)
            *random.nextDouble();

    // Messages for log
    private final static String SAILOR_ATTACK1 = "Sailor inflicts ";
    private final static String SAILOR_ATTACK2 = " damage points on ";
    private final static String SAILOR_ATTACK3 = "'s current health: ";
    private final static int PIRATE_HEALTH = 45;

    // Variables
    private final int MAX_HEALTH;
    private int healthPoints;
    private final int damagePoints;
    private final double projectileSpeed;
    private double x;
    private double y;
    private double oldX;
    private double oldY;
    private final int attackRange;
    private final boolean xDirection = random.nextBoolean();
    private boolean reverseDirection = random.nextBoolean();

    // Constants for rendering Health points
    private final static int HEALTH_Y_OFFSET=6;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 15;
    private final static Font FONT = new
            Font("res/wheaton.otf", FONT_SIZE);
    private final DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    // Constants and variables for Images
    private final static double WIDTH = 40;
    private final static double HEIGHT = 58;
    protected Image LEFT_IMAGE;
    protected Image RIGHT_IMAGE;
    protected Image LEFT_HIT;
    protected Image RIGHT_HIT;
    protected Image PROJECTILE;
    protected Image currentImage;

    // World edges
    private int BOTTOM_EDGE;
    private int TOP_EDGE;
    private int LEFT_EDGE;
    private int RIGHT_EDGE;

    // Variables for cool down
    private int timeCount;
    private int attackCount;
    private final static int FPS = 60;
    private final static double INVINCIBLE_CD = 1.5;
    private final double ATTACK_CD;
    private boolean attackable=true;
    private boolean invincible=false;

    public Enemy(double x, double y, int magnifier) {
        // Most pirates and blackbeard attributes differ by a factor of 2
        // Used magnifier variable to distinguish those attributes.

        MAX_HEALTH = 45 * magnifier;
        healthPoints = MAX_HEALTH;
        damagePoints = 10 * magnifier;
        attackRange = 50 * magnifier;
        projectileSpeed = 0.4 * magnifier;
        ATTACK_CD = 3.0/magnifier;
        COLOUR.setBlendColour(GREEN);
        this.x = x;
        this.y = y;
    }

    /**
     * Performs state update.
     */
    public void update() {
        // Movement of the enemy
        if (xDirection) {
            if (!reverseDirection) {
                setOldPoints();
                move(speed, 0);
                currentImage = RIGHT_IMAGE;
            } else {
                setOldPoints();
                move(-speed, 0);
                currentImage = LEFT_IMAGE;
            }
        } else {
            if (!reverseDirection) {
                setOldPoints();
                move(0, speed);
            } else {
                setOldPoints();
                move(0, -speed);
            }
        }
        // Invincible state cool down
        if (invincible) {
            if (currentImage.equals(LEFT_IMAGE)) {
                currentImage = LEFT_HIT;
            } else if (currentImage.equals(RIGHT_IMAGE)) {
                currentImage = RIGHT_HIT;
            }
            coolDown();
        }
        // Attack state cool down
        if (!attackable) {
            attackCoolDown();
        }
        // Draws the image and health points
        currentImage.drawFromTopLeft(x, y);
        renderHealthPoints();
        checkOutOfBound();
    }

    /**
     * Method that checks for collisions between enemy and blocks
     * @param blocks    ArrayList of blocks
     */
    public void checkBlocks(ArrayList<Block> blocks){
        // check collisions
        Rectangle enemyBox = currentImage.getBoundingBoxAt(new
                Point(x+WIDTH/2, y+HEIGHT/2));
        for (Block current : blocks) {
            Rectangle blockBox = current.getBoundingBox();
            if (enemyBox.intersects(blockBox)) {
                turnAround();
                break;
            }
        }
    }

    /**
     * Method that checks for collisions between enemy and bombs
     * @param bombs     ArrayList of bombs
     */
    public void checkBombs(ArrayList<Bomb> bombs) {
        Rectangle enemyBox = getEnemyBox();
        for (Bomb bomb: bombs) {
            Rectangle blockBox = bomb.getBoundingBox();
            if (enemyBox.intersects(blockBox)) {
                turnAround();
                break;
            }
        }
    }

    /**
     * Method used to get the surrounding Rectangle
     * @return Rectangle    Surrounding rectangle
     */
    public Rectangle getEnemyBox() {
        return currentImage.getBoundingBoxAt(new
                Point(x+WIDTH/2, y+HEIGHT/2));
    }

    // Implemented method from Movable interface
    public void move(double xMove, double yMove){
        x += xMove;
        y += yMove;
    }

    /**
     * Method used to check if the enemy is dead
     * @return True     Enemy is dead
     */
    public boolean isDead() {
        return healthPoints <= 0;
    }

    /**
     * Method that sets the top left border
     * @param top   top edge
     * @param left  left edge
     */
    public void setTopLeftBorder(int top, int left) {
        TOP_EDGE = top;
        LEFT_EDGE = left;
    }

    /**
     * Method that sets the bottom right border
     * @param bottom    bottom edge
     * @param right     right edge
     */
    public void setBottomRightBorder(int bottom, int right) {
        BOTTOM_EDGE = bottom;
        RIGHT_EDGE = right;
    }

    /**
     * Method used to change the health points and implement
     * cool down of invincible state.
     * @param damagePoint   Damage Point of the sailor
     */
    public void getAttacked(int damagePoint) {
        if (!invincible) {
            healthPoints -= damagePoint;
            coolDown();
            if (MAX_HEALTH==PIRATE_HEALTH) {
                System.out.println(SAILOR_ATTACK1+damagePoint+
                        SAILOR_ATTACK2+"Pirate. Pirate"+SAILOR_ATTACK3+
                        healthPoints+"/"+MAX_HEALTH);
            } else {
                System.out.println(SAILOR_ATTACK1+damagePoint+
                        SAILOR_ATTACK2+"Blackbeard. Blackbeard"+SAILOR_ATTACK3+
                        healthPoints+"/"+MAX_HEALTH);
            }
        }
    }

    /**
     * Method used to get the attack range of the enemy. If the sailor is
     * within this range, it fires projectile.
     * @return Rectangle    200x200 rectangle for pirate
     *                      400x400 rectangle for blackbeard, same as the one
     *                      in the demo video.
     */
    public Rectangle getRangeBox() {
        return new Rectangle(x+WIDTH/2-attackRange,
                y+HEIGHT/2-attackRange, attackRange*2, attackRange*2);
    }

    /**
     * Method used to fire projectile towards the sailor
     * @param sailor        Sailor to calculate the angle
     * @return Projectile   Projectile
     */
    public Projectile fireProjectile(Sailor sailor) {
        // Calculate the rotation angle
        if ((double)sailor.getY()==y) {
            return new Projectile(x+WIDTH/2, y+WIDTH/2, projectileSpeed,
                    damagePoints, PROJECTILE, 0);
        }
        double rotation = Math.atan(
                Math.abs((double)sailor.getY()-y)/Math.abs((sailor.getX()-x)));
        if (sailor.getX()<=x && sailor.getY()>y) {
            rotation = Math.PI - rotation;
        } else if (sailor.getX()>=x && sailor.getY()<y) {
            rotation = -rotation;
        } else if (sailor.getX()<=x && sailor.getY()<y) {
            rotation -= Math.PI;
        }

        // Fire the projectile
        attackCoolDown();
        return new Projectile(x+WIDTH/2, y+WIDTH/2, projectileSpeed,
                damagePoints, PROJECTILE, rotation);
    }

    /**
     * Getter function of attack state
     * @return attackable   Attack state of the enemy
     */
    public boolean canAttack() {
        return attackable;
    }

    /**
     * Method that stores the old coordinates of the sailor
     */
    private void setOldPoints(){
        oldX = x;
        oldY = y;
    }

    /**
     * Method that turns the entity in the opposite direction
     */
    private void turnAround(){
        x = oldX;
        y = oldY;
        reverseDirection= !reverseDirection;
    }

    /**
     * Method that moves the sailor back if it has gone out-of-bound
     */
    private void checkOutOfBound(){
        if ((y > BOTTOM_EDGE) || (y < TOP_EDGE) || (x < LEFT_EDGE) ||
                (x > RIGHT_EDGE)) {
            turnAround();
        }
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    private void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/MAX_HEALTH) * 100;
        double HEALTH_X = x;
        double HEALTH_Y = y-HEALTH_Y_OFFSET;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%",
                HEALTH_X, HEALTH_Y, COLOUR);
    }

    /**
     * Method used to implement cool down of invincible state
     */
    private void coolDown() {
        if (!invincible) {
            timeCount=ShadowPirate.frameCount;
            invincible=true;
        }
        if ((ShadowPirate.frameCount-timeCount)==INVINCIBLE_CD*FPS) {
            invincible = false;
            timeCount = Integer.MAX_VALUE;
            if (currentImage.equals(LEFT_HIT)) {
                currentImage = LEFT_IMAGE;
            } else if (currentImage.equals(RIGHT_HIT)) {
                currentImage = RIGHT_IMAGE;
            }
        }
    }

    /**
     * Method used to implement cool down of attack state
     */
    private void attackCoolDown() {
        if (attackable) {
            attackCount=ShadowPirate.frameCount;
            attackable = false;
        } else if ((ShadowPirate.frameCount-attackCount)==ATTACK_CD*FPS) {
            attackable=true;
            attackCount = Integer.MAX_VALUE;
        }
    }
}

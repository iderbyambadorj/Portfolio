import bagel.*;
import bagel.util.Rectangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Abstract class used to hold all attributes and
 * methods related to the both levels
 * @author Ider Byambadorj
 */
abstract class Level {

    // Constants related to the background
    protected final static String START_MESSAGE = "PRESS SPACE TO START";
    protected final static String START_MESSAGE2 = "PRESS S TO ATTACK";
    protected final static String END_MESSAGE = "GAME OVER";
    protected String WIN_MESSAGE;
    protected String INSTRUCTION_MESSAGE;
    protected Image BACKGROUND_IMAGE = new Image("res/background0.png");
    protected final static int INSTRUCTION_OFFSET = 70;
    protected final static int FONT_SIZE = 55;
    protected final static int FONT_Y_POS = 402;
    protected final Font FONT = new Font("res/wheaton.otf", FONT_SIZE);

    // Constant for Level 0, Treasure coordinate in Level 1
    protected int WIN_X;
    protected int WIN_Y;

    // Game states
    protected final static int GAME_END = -1;
    protected final static int GAME_START = 0;
    protected final static int LEVEL0 = 1;
    protected final static int LEVEL0WIN = 2;
    protected final static int LEVEL1START = 3;
    protected final static int LEVEL1 = 4;
    protected final static int GAME_WIN = 5;

    // Entities
    protected Sailor sailor;
    protected final ArrayList<Block> blocks = new ArrayList<>();
    protected final ArrayList<Pirate> pirates = new ArrayList<>();
    protected final ArrayList<Projectile> projectiles = new ArrayList<>();
    public Level() {
    }

    /**
     * Performs the state update.
     * @param input     Keyboard input
     */
    public void update(Input input) {
        // Draw start screen messages
        if (ShadowPirate.gameState==GAME_START ||
                ShadowPirate.gameState==LEVEL1START){
            drawStartScreen(input);
        }
        // Draw end screen messages
        if (ShadowPirate.gameState==GAME_END){
            drawEndScreen(END_MESSAGE);
        }
        if (ShadowPirate.gameState==LEVEL0WIN ||
                ShadowPirate.gameState==GAME_WIN) {
            drawEndScreen(WIN_MESSAGE);
        }
    }

    /**
     * Reads the world from csv file.
     * @param fileName  csv file directory.
     */
    public void readCSV(String fileName){
        try (BufferedReader reader = new BufferedReader(new
                FileReader(fileName))){

            // Read sailor
            String line;
            if ((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                if (sections[0].equals("Sailor")){
                    sailor = new Sailor(Integer.parseInt(sections[1]),
                            Integer.parseInt(sections[2]));
                }
            }

            // Read other entities
            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                switch (sections[0]) {
                    case "Block":
                        blocks.add(new Block(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2])));
                        break;
                    case "Pirate":
                        pirates.add(new Pirate(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2])));
                        break;
                    case "TopLeft":
                        setTopLeft(Integer.parseInt(sections[2]),
                                Integer.parseInt(sections[1]));
                        break;
                    case "BottomRight":
                        setBottomRight(Integer.parseInt(sections[2]),
                                Integer.parseInt(sections[1]));
                        break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Method used to draw start screen of level.
     * Allow player to start the game using input.
     * @param input     Keyboard input
     */
    public void drawStartScreen(Input input){
        FONT.drawString(START_MESSAGE, (Window.getWidth()/2.0 -
                        (FONT.getWidth(START_MESSAGE)/2.0)),
                        (FONT_Y_POS - INSTRUCTION_OFFSET));
        FONT.drawString(START_MESSAGE2, (Window.getWidth()/2.0 -
                        (FONT.getWidth(START_MESSAGE2)/2.0)), FONT_Y_POS);
        FONT.drawString(INSTRUCTION_MESSAGE, (Window.getWidth()/2.0 -
                        (FONT.getWidth(INSTRUCTION_MESSAGE)/2.0)),
                        (FONT_Y_POS + INSTRUCTION_OFFSET));
    }

    /**
     * Method used to draw end screen messages.
     * @param message   Message to be printed in the screen
     */
    protected void drawEndScreen(String message){
        FONT.drawString(message, (Window.getWidth()/2.0 -
                (FONT.getWidth(message)/2.0)), FONT_Y_POS);
    }

    /**
     * Method used to check if sailor has won
     * @param sailor    The sailor
     * @return          True if sailor has won
     */
    protected boolean hasWon(Sailor sailor) {
        return (sailor.getX() >= WIN_X) && (sailor.getY() > WIN_Y);
    }

    /**
     * Method to set world edges for all moving entities
     * @param top       y value of top left corner
     * @param left      x value of top left corner
     */
    protected void setTopLeft(int top, int left) {
        sailor.setTopLeftBorder(top, left);
        for (Pirate pirate : pirates) {
            pirate.setTopLeftBorder(top, left);
        }
    }

    /**
     * Method to set world edges for all moving entities
     * @param bottom    y value of bottom right corner
     * @param right     x value of bottom right corner
     */
    protected void setBottomRight(int bottom, int right) {
        sailor.setBottomRightBorder(bottom, right);
        for (Pirate pirate : pirates) {
            pirate.setBottomRightBorder(bottom, right);
        }
    }

    /**
     * Method used to fire projectiles towards the sailor
     */
    protected void attackSailor() {
        for (Pirate pirate: pirates) {
            if (pirate.canAttack()) {
                Rectangle enemyRange = pirate.getRangeBox();
                if (enemyRange.intersects(sailor.getSailorBox())) {
                    projectiles.add(pirate.fireProjectile(sailor));
                }
            }
        }
    }
}

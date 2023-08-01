import bagel.*;

/**
 * Skeleton Code for SWEN20003 Project 2, Semester 1, 2022
 *
 *
 * @author Ider Byambadorj
 */
public class ShadowPirate extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "ShadowPirate";

    // Constants and variables for cool down
    private final static int FPS = 60;
    private final static int END_COOL_DOWN = 3;
    public static int frameCount = 0;

    // Game states
    private final static int GAME_END = -1;
    private final static int GAME_START = 0;
    private final static int LEVEL0 = 1;
    private final static int LEVEL0WIN = 2;
    private final static int LEVEL1START = 3;
    private final static int LEVEL1 = 4;
    private final static int GAME_WIN = 5;

    // Constants and variables for levels
    public static int gameState=GAME_START;
    private static boolean isOnCoolDown=false;
    private static int coolDownCount;
    private final Level level0 = new Level0();
    private final Level level1 = new Level1();
    private Level currentLevel=level0;

    public ShadowPirate() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        gameState = GAME_START;
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowPirate game = new ShadowPirate();
        game.run();
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    public void update(Input input) {
        // Allow player to skip to Level 1
        if (gameState==GAME_START && input.wasPressed(Keys.W)) {
            gameState = LEVEL1START;
        }
        // Implement 3 second cool down on Level 0 end screen
        if (gameState==LEVEL0WIN) {
            endCoolDown();
        }
        // Change the level
        if (gameState>=LEVEL1START) {
            currentLevel=level1;
        }
        // Exit the game if ESC was pressed
        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        // Perform state update
        currentLevel.update(input);
        frameCount++;
    }

    /**
     * Method used to implement cool down of end screen messages
     */
    private void endCoolDown() {
        if (!isOnCoolDown) {
            coolDownCount = frameCount;
            isOnCoolDown = true;
        }
        if ((frameCount - coolDownCount)==END_COOL_DOWN*FPS) {
            gameState=LEVEL1START;
            coolDownCount=0;
            isOnCoolDown=false;
        }
    }
}

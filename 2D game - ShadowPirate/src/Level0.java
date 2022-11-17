import bagel.*;

/**
 * Class used to represent Level 0.
 * @author Ider Byambadorj
 */
public class Level0 extends Level {
    public Level0() {
        super();
        readCSV("res/level0.csv");
        WIN_MESSAGE = "LEVEL COMPLETE";
        INSTRUCTION_MESSAGE = "USE ARROW KEYS TO FIND LADDER";
        WIN_X = 990;
        WIN_Y = 630;
    }

    /**
     * Performs a state update.
     * @param input     Keyboard input
     */
    @Override
    public void update(Input input) {
        super.update(input);
        if (ShadowPirate.gameState==LEVEL0){
            // Background and blocks
            BACKGROUND_IMAGE.draw(Window.getWidth()/2.0,
                    Window.getHeight()/2.0);
            for (Block block : blocks) {
                block.update();
            }

            // Pirate state update
            for (Pirate pirate : pirates) {
                pirate.update();
                pirate.checkBlocks(blocks);
            }
            pirates.removeIf(Pirate::isDead);

            // Pirate attack
            attackSailor();
            for (Projectile projectile: projectiles) {
                projectile.update(sailor);
            }

            // Sailor update
            sailor.update(input, pirates, null);
            sailor.checkBlocks(blocks);
            sailor.checkProjectiles(projectiles);
            projectiles.removeIf(Projectile::isExploded);
            if (sailor.isDead()){
                ShadowPirate.gameState=GAME_END;
            }
            if (hasWon(sailor)){
                ShadowPirate.gameState=LEVEL0WIN;
            }
        }
        // End message
        if (ShadowPirate.gameState==LEVEL0WIN) {
            drawEndScreen(WIN_MESSAGE);
        }
    }

    /**
     * Method used to draw end screen messages.
     * Overridden super class method.
     * Allow player to start the game using input.
     * @param input     Keyboard input.
     */
    @Override
    public void drawStartScreen(Input input){
        super.drawStartScreen(input);
        if (input.wasPressed(Keys.SPACE)){
            ShadowPirate.gameState = LEVEL0;
        }
    }
}

import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class used to represent Level 1
 * @author Ider Byambadorj
 */
public class Level1 extends Level {
    public Level1() {
        super();
        readCSV("res/level1.csv");
        WIN_MESSAGE = "CONGRATULATIONS!";
        INSTRUCTION_MESSAGE = "FIND THE TREASURE";
        BACKGROUND_IMAGE = new Image("res/background1.png");
    }

    // Information that is only available for Level 1
    private static final Image TREASURE = new Image("res/treasure.png");
    private Blackbeard blackbeard;
    private final ArrayList<Bomb> bombs = new ArrayList<>();
    private final ArrayList<Item> items = new ArrayList<>();

    /**
     * Performs a state update.
     * @param input     Keyboard input
     */
    @Override
    public void update(Input input) {
        super.update(input);
        if (ShadowPirate.gameState==GAME_WIN) {
            drawEndScreen(WIN_MESSAGE);
        }
        if (ShadowPirate.gameState==LEVEL1){
            // Background and bombs
            BACKGROUND_IMAGE.draw(Window.getWidth()/2.0,
                    Window.getHeight()/2.0);
            for (Bomb bomb: bombs) {
                bomb.update();
            }
            bombs.removeIf(Bomb::isExploded);

            // Pirate state update
            for (Pirate pirate : pirates) {
                pirate.update();
                pirate.checkBombs(bombs);
            }
            pirates.removeIf(Pirate::isDead);

            // Blackbeard state update
            if (blackbeard!=null) {
                blackbeard.update();
                blackbeard.checkBombs(bombs);
                if (blackbeard.isDead()) {
                    blackbeard=null;
                }
            }

            // Items state update
            for (Item item: items) {
                item.update();
            }

            // Attacking sailor
            attackSailor();
            for (Projectile projectile: projectiles) {
                projectile.update(sailor);
            }

            // Sailor state update
            sailor.update(input, pirates, blackbeard);
            sailor.checkBombs(bombs);
            sailor.checkItems(items);
            sailor.checkProjectiles(projectiles);
            sailor.checkProjectiles(projectiles);
            projectiles.removeIf(Projectile::isExploded);
            items.removeIf(Item::isPickedUp);

            if (sailor.isDead()){
                ShadowPirate.gameState=GAME_END;
            }
            // Drawing Treasure image
            TREASURE.drawFromTopLeft(WIN_X, WIN_Y);
            if (hasWon(sailor)){
                ShadowPirate.gameState=GAME_WIN;
            }
        }
    }

    /**
     * Reads the world from csv file.
     * @param fileName  csv file directory.
     */
    @Override
    public void readCSV(String fileName){
        try (BufferedReader reader = new BufferedReader(new
                FileReader(fileName))){

            String line;
            if ((line = reader.readLine()) != null){
                // Reading sailor
                String[] sections = line.split(",");
                if (sections[0].equals("Sailor")){
                    sailor = new Sailor(Integer.parseInt(sections[1]),
                            Integer.parseInt(sections[2]));
                }
            }

            while((line = reader.readLine()) != null){
                // Reading other entities
                String[] sections = line.split(",");
                switch (sections[0]) {
                    case "Block":
                        bombs.add(new Bomb(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2])));
                        break;
                    case "Pirate":
                        pirates.add(new Pirate(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2])));
                        break;
                    case "Potion":
                        items.add(new Item("Potion",
                                Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]),
                                "res/items/potion.png",
                                "res/items/potionIcon.png"));
                        break;
                    case "Elixir":
                        items.add(new Item("Elixir",
                                Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]),
                                "res/items/elixir.png",
                                "res/items/elixirIcon.png"));
                        break;
                    case "Sword":
                        items.add(new Item("Sword",
                                Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]),
                                "res/items/sword.png",
                                "res/items/swordIcon.png"));
                        break;
                    case "Blackbeard":
                        blackbeard = new Blackbeard(
                                Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]));
                        break;
                    case "TopLeft":
                        setTopLeft(Integer.parseInt(sections[2]),
                                Integer.parseInt(sections[1]));
                        break;
                    case "BottomRight":
                        setBottomRight(Integer.parseInt(sections[2]),
                                Integer.parseInt(sections[1]));
                        break;
                    case "Treasure":
                        WIN_X = Integer.parseInt(sections[1]);
                        WIN_Y = Integer.parseInt(sections[2]);
                        break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
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
            ShadowPirate.gameState = LEVEL1;
        }
    }

    /**
     * Method used to check if sailor has won
     * @param sailor    The sailor
     * @return          True if sailor has won
     */
    @Override
    protected boolean hasWon(Sailor sailor) {
        Rectangle sailorBox = sailor.getImage().getBoundingBoxAt(new
                Point(sailor.getX()+sailor.getImage().getWidth()/2,
                sailor.getY()+sailor.getImage().getHeight()/2));
        Rectangle treasureBox = TREASURE.getBoundingBoxAt(new Point(
                WIN_X+TREASURE.getWidth()/2,
                WIN_Y+TREASURE.getHeight()/2));
        return (sailorBox.intersects(treasureBox));
    }

    /**
     * Method to set world edges for all moving entities
     * @param top       y value of top left corner
     * @param left      x value of top left corner
     */
    @Override
    protected void setTopLeft(int top, int left) {
        super.setTopLeft(top, left);
        blackbeard.setTopLeftBorder(top, left);
    }

    /**
     * Method to set world edges for all moving entities
     * @param bottom    y value of bottom right corner
     * @param right     x value of bottom right corner
     */
    @Override
    protected void setBottomRight(int bottom, int right) {
        super.setBottomRight(bottom, right);
        blackbeard.setBottomRightBorder(bottom, right);
    }

    @Override
    protected void attackSailor() {
        // Pirates attacking sailor
        for (Pirate pirate: pirates) {
            if (pirate.canAttack()) {
                Rectangle enemyBox = pirate.getRangeBox();
                if (enemyBox.intersects(sailor.getSailorBox())) {
                    projectiles.add(pirate.fireProjectile(sailor));
                }
            }
        }
        // Blackbeard attacking sailor
        if (blackbeard!=null) {
            if (blackbeard.canAttack()) {
                Rectangle enemyBox = blackbeard.getRangeBox();
                if (enemyBox.intersects(sailor.getSailorBox())) {
                    projectiles.add(blackbeard.fireProjectile(sailor));
                }
            }
        }
    }
}

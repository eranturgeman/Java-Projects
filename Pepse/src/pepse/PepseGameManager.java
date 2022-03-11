package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.*;
import danogl.util.Vector2;
import pepse.util.NumericCounter;
import pepse.world.Avatar;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.WorldGenerator;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.hell.HellBat;
import pepse.world.trees.Tree;
import java.awt.*;
import java.util.Random;

/**
 * The game manager.
 * Responsible to initialize the game, keeping track on the game objects (and update them)
 * Includes the main function
 * @author eran_turgeman, elay_aharoni
 */
public class PepseGameManager extends GameManager {
    //================================== public constants ==================================
    /**
     * The addition to the Tree layer to put the leaves
     */
    public static final int LEAF_ADDITION = 1;
    /**
     *  The addition for the Leaf layer to put the falling leaves
     */
    public static final int FALLING_LEAF_ADDITION = 1;
    /**
     * The addition for the bottom Terrain blocks to separate them from the top 2 rows
     */
    public static final int BOTTOM_TERRAIN_LAYER_ADDITION = 1;
    /**
     * The hell door object tag.
     */
    public static final String DOOR_TAG = "hellDoor";
    /**
     * The Trees layer in the game
     */
    public static final int TREES_LAYER = Layer.BACKGROUND + 10;
    //================================== private constants ==================================
    //colors
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);
    private static final Color BASIC_SKY_COLOR = Color.decode("#80c6E5");
    
    //constant values
    private static final int randomSeed = new Random().nextInt(10);
    private static final int NIGHT_CYCLE = 30;
    private static final int SUN_CYCLE = 30;
//    private static final int TERRAIN_SECTION = 200;
    private static final int NUM_TERRAIN_SECTIONS = 6;
    private static final int FRAME_RATE = 40;
    private static final int DOOR_RANDOM_PARAMETER = 5000;
    private static final int NUMERIC_COUNTER_HEIGHT = 30;
    private static final int NUMERIC_COUNTER_WIDTH = 50;
    private static final int GENERATOR_SECTIONS_CHECK_RANGE = 4;
    private static final String KILL_STRING = "Kills";
    private static final String LIFE_STRING = "Lives";
    private static final String LOSE_MESSAGE = "You LOSE!\nPlay again?";
    
    //layers
    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int TOP_TERRAIN_LAYER = Layer.STATIC_OBJECTS;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final float COUNTER_POSITION_MULT_FACTOR = 2.2f;
    private static final int COUNTERS_HEIGHT_ADDITION = 20;
    
    //================================== private fields ==================================
    //fields
    private int curWorldMin;
    private int curWorldMax;
    private boolean doorExists;
    private int doorMarkPosition;
    private boolean hellMode;
    private Terrain terrain;
    private Tree trees;
    private Avatar avatar;
    private Random random;
    private GameObject sky;
    private GameObject sun;
    private GameObject sunHalo;
    private WorldGenerator worldGenerator;
    private NumericCounter killCounter;
    private NumericCounter lifeCounter;
    private WindowController windowController;
    private int terrainSectionSize;
    
    //================================== public methods ==================================
    /**
     * Game initializer
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param soundReader a SoundReader instance for reading sound clips from files for rendering event sounds
     * @param inputListener an InputListener instance for reading user input.
     * @param windowController controls visual rendering of the game window and object renderables.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(FRAME_RATE); //TODO del
        
        GameObject dummyBlock = new GameObject(Vector2.ZERO, Vector2.ONES ,
                new RectangleRenderable(BASIC_SKY_COLOR));
        gameObjects().addGameObject(dummyBlock, TREES_LAYER + LEAF_ADDITION + FALLING_LEAF_ADDITION);
        this.hellMode = false;
        this.killCounter = null;
        this.lifeCounter = null;
        this.windowController = windowController;
        this.terrainSectionSize = (int)windowController.getWindowDimensions().x() / NUM_TERRAIN_SECTIONS;
        
        
    
        //Creating sky
        sky = Sky.create(this.gameObjects(), windowController.getWindowDimensions(), SKY_LAYER);
    
        //Creating ground blocks
        terrain = new Terrain(this.gameObjects(), TOP_TERRAIN_LAYER,
                windowController.getWindowDimensions(), randomSeed);
    
        Vector2 avatarStartingPosition = new Vector2(
                windowController.getWindowDimensions().x() / 2,
                terrain.groundHeightAt(windowController.getWindowDimensions().x() / 2) - Avatar.AVATAR_SIZE);
    
        terrain.createInRange((int)(avatarStartingPosition.x() - NUM_TERRAIN_SECTIONS * terrainSectionSize),
                (int)(avatarStartingPosition.x() + NUM_TERRAIN_SECTIONS * terrainSectionSize));
    
        //Creating night
        Night.create(gameObjects(), NIGHT_LAYER, windowController.getWindowDimensions(),
                NIGHT_CYCLE);
    
        //Creating sun
        sun = Sun.create(gameObjects(), SUN_LAYER, windowController.getWindowDimensions(),
                SUN_CYCLE);
    
        //Create sun halo
        sunHalo = SunHalo.create(gameObjects(), SUN_LAYER, sun, HALO_COLOR);
    
        //Creating trees
        trees = new Tree(gameObjects(), terrain::groundHeightAt, randomSeed, TREES_LAYER,
                avatarStartingPosition);
        trees.createInRange((int)avatarStartingPosition.x() - NUM_TERRAIN_SECTIONS * terrainSectionSize,
                (int)avatarStartingPosition.x() + NUM_TERRAIN_SECTIONS * terrainSectionSize);
    
        //Creating avatar
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER, avatarStartingPosition, inputListener,
                imageReader);
        setCamera(new Camera(avatar,
                windowController.getWindowDimensions().mult(0.5f).subtract(avatarStartingPosition) ,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        
        //Creating door
        this.doorExists = false;
        this.random = new Random(randomSeed);
        this.doorMarkPosition = this.random.nextInt(DOOR_RANDOM_PARAMETER) + DOOR_RANDOM_PARAMETER;
        if(random.nextBoolean()){
            this.doorMarkPosition = -1 * doorMarkPosition;
        }
        
        //world generator
        this.worldGenerator = new WorldGenerator(terrainSectionSize, terrain, trees, sun,
                sunHalo, sky, imageReader, gameObjects(), windowController.getWindowDimensions(),
                AVATAR_LAYER, TOP_TERRAIN_LAYER,
                TOP_TERRAIN_LAYER + BOTTOM_TERRAIN_LAYER_ADDITION, TREES_LAYER,
                TREES_LAYER + LEAF_ADDITION,
                TREES_LAYER + LEAF_ADDITION + FALLING_LEAF_ADDITION);
        
        //Layers collisions
        gameObjects().layers().shouldLayersCollide(
                TREES_LAYER + LEAF_ADDITION + FALLING_LEAF_ADDITION,
                TOP_TERRAIN_LAYER,
                true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TOP_TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TREES_LAYER, true);
        
        curWorldMin = (int)avatarStartingPosition.x() - NUM_TERRAIN_SECTIONS * terrainSectionSize;
        curWorldMax = (int)avatarStartingPosition.x() + NUM_TERRAIN_SECTIONS * terrainSectionSize;
        
        /*
         * Dear Player! The Hell Gate is something you need to look for!
         * If you want to make your life easier you may un-comment the next 2  lines the x-coordinate of the
         * avatar and the x-coordinate of the gate will be printed to the screen.
         * Remember: Negative = go left, Positive = go right.
         * Good LUCK!
         */
        //System.out.println(String.format("Hell Gate X coordinate: %d",doorMarkPosition));
        //System.out.println(String.format("Avatar X coordinate: %f", avatarStartingPosition.x()));
    }
    
    
    /**
     * Updates the game's frame
     * @param deltaTime The time elapsed, in seconds, since the last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(avatar.getCenter().x() < curWorldMin + GENERATOR_SECTIONS_CHECK_RANGE * terrainSectionSize){
            worldGenerator.generateLandscapeToLeft(curWorldMin);
            curWorldMin -= terrainSectionSize;
            worldGenerator.deleteInRange(curWorldMax - terrainSectionSize, curWorldMax);
            curWorldMax -= terrainSectionSize;
            if(this.doorMarkPosition >= curWorldMin && !doorExists){
                doorExists = true;
                worldGenerator.createHellDoor(curWorldMin);
            }
            if(hellMode){
                worldGenerator.generateHellCreatures(curWorldMin - terrainSectionSize, curWorldMin);
            }
        }
        
        if(avatar.getCenter().x() > curWorldMax - GENERATOR_SECTIONS_CHECK_RANGE * terrainSectionSize){
            worldGenerator.generateLandscapeToRight(curWorldMax);
            curWorldMax += terrainSectionSize;
            worldGenerator.deleteInRange(curWorldMin, curWorldMin + terrainSectionSize);
            curWorldMin += terrainSectionSize;
            if(this.doorMarkPosition <= curWorldMax && !doorExists){
                doorExists = true;
                worldGenerator.createHellDoor(curWorldMax);
            }
            if(hellMode){
                worldGenerator.generateHellCreatures(curWorldMax, curWorldMax + terrainSectionSize);
            }
        }
        
        if(!hellMode && avatar.isDoorTouched() ){
            worldGenerator.changeToHell();
            this.hellMode = true;
            createCounters();
        }
        if(hellMode){
            killCounter.update(deltaTime);
            lifeCounter.update(deltaTime);
            checkForGameEnd();
        }
    }
    
    /**
     * Main function to run the game
     * @param args command line args
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
    
    //================================== private methods ==================================
    /*
     * Creating the kills and life counters
     */
    private void createCounters(){
        this.killCounter = new NumericCounter(new Vector2(0, COUNTERS_HEIGHT_ADDITION) ,
                new Vector2(NUMERIC_COUNTER_WIDTH, NUMERIC_COUNTER_HEIGHT),
                null, gameObjects(), KILL_STRING, HellBat::getCounterValue);
        
        this.lifeCounter = new NumericCounter(
                new Vector2(0, 2 * COUNTERS_HEIGHT_ADDITION + NUMERIC_COUNTER_HEIGHT),
                new Vector2(NUMERIC_COUNTER_WIDTH, NUMERIC_COUNTER_HEIGHT),
                null, gameObjects(), LIFE_STRING, avatar::getCounterValue);
    }
    
    /*
     * checks id the game was ended and allows to restart the game
     */
    private void checkForGameEnd() {
        if(lifeCounter.getValue() <= 0){
            if(windowController.openYesNoDialog(LOSE_MESSAGE)){
                windowController.resetGame();
            }else{
                windowController.closeWindow();
            }
        }
    }
}

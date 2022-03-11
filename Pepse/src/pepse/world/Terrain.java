package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import java.awt.*;

/**
 * a class that represents the ground terrain of the game
 * @author eran_turgeman, elay_aharoni
 */
public class Terrain {
    //================================== public constants ==================================
    /**
     * the tag for the first two layers of the terrain
     */
    public static final String TOP_TERRAIN_LAYER_TAG = "topGroundBlock";
    /**
     * the tag for the rest of the terrain layers
     */
    public static final String BOTTOM_TERRAIN_LAYER_TAG = "bottomGroundBlock";
    
    //================================== private constants ==================================
    private static final Color BASE_GROUND_COLOR = new Color(212,123,74);
    private static final int GROUND_COLOR_DIFFERENCE_DELTA = 10;
    private static final float WAVE_HEIGHT = 10;
    private static final float TERRAIN_SCREEN_PARTIAL = 0.75f;
    private static final int BLOCKS_IN_COLUMN = 20;
    
    //================================== private fields ==================================
    private final GameObjectCollection gameObjects;
    private final int layerToInsertTopLayers;
    private final Vector2 windowDimensions;
    private final NoiseGenerator noiseGenerator;
    private final float groundHeightAtX0;
    private final int layerToInsertBottomLayers;
    
    //================================== public methods ==================================
    /**
     * a constructor for a terrain object
     * @param gameObjects - the game objects colletion
     * @param groundLayer - the layer to insert the ground block in
     * @param windowDimensions - the window dimensions
     * @param seed - the random seed to use
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer,
                   Vector2 windowDimensions,
                   int seed){
        this.gameObjects = gameObjects;
        this.layerToInsertTopLayers = groundLayer;
        this.layerToInsertBottomLayers = groundLayer + PepseGameManager.BOTTOM_TERRAIN_LAYER_ADDITION;
        this.groundHeightAtX0 = windowDimensions.y() * TERRAIN_SCREEN_PARTIAL;
        this.windowDimensions = windowDimensions;
        this.noiseGenerator = new NoiseGenerator(seed);
    }
    
    /**
     * method that returns the height of the terrain in a given x coordinate according some calculations
     * using perlin noise (for randomness)
     * @param x - the x coordinate to check the height in
     * @return - the height in the given cordinate
     */
    public float groundHeightAt(float x){
        float noise = (WAVE_HEIGHT * (float) noiseGenerator.noise(x / Block.SIZE)) * Block.SIZE;
        float value = noise + groundHeightAtX0;
        return ((int)(value / Block.SIZE)) * Block.SIZE;
    }
    
    /**
     * method that creates ground blocks in a given range
     * @param minX - the start position to create the ground blocks in
     * @param maxX - the end position to create the ground blocks in
     */
    public void createInRange(int minX, int maxX){
        int roundedMinX = (int)Math.floor(minX / Block.SIZE) * Block.SIZE;
        int roundedMaxX = (int)Math.floor(maxX/ Block.SIZE) * Block.SIZE;
        
        for(int x = roundedMinX; x <= roundedMaxX; x += Block.SIZE){
            float height = groundHeightAt(x);
            createBlocksColumn(x, (int)height);
        }
    }
    
    //================================== private methods ==================================
    /*
     * Creates a single column of ground blocks in a given coordinate
     */
    private void createBlocksColumn(int topLeftX, int height) {
        int topLeftY = height;
        for(int counter = 0; counter < BLOCKS_IN_COLUMN; counter++){
            RectangleRenderable blockImage = new RectangleRenderable(
                    ColorSupplier.approximateColor(BASE_GROUND_COLOR, GROUND_COLOR_DIFFERENCE_DELTA));
            Block block = new Block(new Vector2(topLeftX, topLeftY), blockImage);
            if(counter < 2){
                block.setTag(TOP_TERRAIN_LAYER_TAG);
                gameObjects.addGameObject(block, layerToInsertTopLayers);
            }else{
                block.setTag(BOTTOM_TERRAIN_LAYER_TAG);
                gameObjects.addGameObject(block, layerToInsertBottomLayers);
            }
            topLeftY += Block.SIZE;
        }
    }
}

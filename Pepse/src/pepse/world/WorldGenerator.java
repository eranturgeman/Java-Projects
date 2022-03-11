package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.hell.HellBat;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;
import java.awt.*;
import java.util.Random;

/**
 * This class represents to generate the infinite world throughout the game
 */
public class WorldGenerator {
    //================================== public constants ==================================
    /**
     * Hell dor object tag
     */
    public static final String DOOR_TAG = "hellDoor";
    
    //================================== private constants ==================================
    private static final String HELL_IMAGE_PATH = "assets/hell.jpg";
    private static final String HELL_SUN_IMAGE_PATH = "assets/sun.png";
    
    private static final String PORTAL_IMAGE1 = "assets/portal01.png";
    private static final String PORTAL_IMAGE2 = "assets/portal04.png";
    private static final String PORTAL_IMAGE3 = "assets/portal08.png";
    private static final String PORTAL_IMAGE4 = "assets/portal12.png";
    private static final String PORTAL_IMAGE5 = "assets/portal16.png";
    private static final String PORTAL_IMAGE6 = "assets/portal20.png";
    private static final String PORTAL_IMAGE7 = "assets/portal24.png";
    private static final String PORTAL_IMAGE8 = "assets/portal28.png";
    private static final String PORTAL_IMAGE9 = "assets/portal32.png";
    private static final String PORTAL_IMAGE10 = "assets/portal36.png";
    private static final String PORTAL_IMAGE11 = "assets/portal40.png";
    private static final String PORTAL_IMAGE12 = "assets/portal44.png";
    private static final String PORTAL_IMAGE13 = "assets/portal48.png";
    private static final String PORTAL_IMAGE14 = "assets/portal52.png";
    private static final String PORTAL_IMAGE15 = "assets/portal56.png";
    private static final String PORTAL_IMAGE16 = "assets/portal60.png";
    private static final String PORTAL_IMAGE17 = "assets/portal64.png";
    private static final String[] PORTAL_IMAGES = new String[] {PORTAL_IMAGE1, PORTAL_IMAGE2, PORTAL_IMAGE3,
    PORTAL_IMAGE4, PORTAL_IMAGE5, PORTAL_IMAGE6, PORTAL_IMAGE7, PORTAL_IMAGE8, PORTAL_IMAGE9,
            PORTAL_IMAGE10, PORTAL_IMAGE11, PORTAL_IMAGE12, PORTAL_IMAGE13, PORTAL_IMAGE14, PORTAL_IMAGE15,
            PORTAL_IMAGE16, PORTAL_IMAGE17};
    
    private static final float PORTAL_CLIPS_TIME = 0.1f;
    private static final int RANDOM_BOUND = 40;
    private static final int BAT_SCREEN_PARTIAL_BOUND = 4;
    private static final Color HELL_HALO_COLOR = new Color(100, 100, 0, 20);
    private static final Vector2 DOOR_SIZE = new Vector2(200,200);
    
    //================================== private fields ==================================
    private final int terrainSection;
    private final int avatarLayer;
    private final int topTerrainLayer;
    private final int bottomTerrainLayer;
    private final int stumpLayer;
    private final int leafLayer;
    private final int fallingLeafLayer;
    private final Terrain terrain;
    private final Tree trees;
    private final GameObject sun;
    private final GameObject halo;
    private final GameObject sky;
    private final Renderable hellBackground;
    private final Renderable hellSun;
    private final Renderable doorImage;
    private final GameObjectCollection gameObjects;
    private final Random random;
    private final Vector2 windowDimensions;
    private final ImageReader imageReader;
    
    //================================== public methods ==================================
    /**
     * Constructor for a world generator object
     * @param terrainSection size of sections the generator generates new ground parts
     * @param terrain Terrain object
     * @param trees Tree object
     * @param sun the sun object in the game
     * @param halo the sun halo object in the game
     * @param sky the sky object in the game
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param gameObjects collection of the objects in the game
     * @param windowDimensions size of the game window
     * @param avatarLayer the layer the avatar is placed at
     * @param topTerrainLayer layer the 2 top rows of ground blocks are at
     * @param bottomTerrainLayer layer the bottoms rows of ground blocks are at
     * @param stumpLayer layer of the tree stumps
     * @param leafLayer layer of the leaves
     * @param fallingLeafLayer layer of the falling leaves
     */
    public WorldGenerator(int terrainSection,
                          Terrain terrain,
                          Tree trees,
                          GameObject sun,
                          GameObject halo,
                          GameObject sky,
                          ImageReader imageReader,
                          GameObjectCollection gameObjects,
                          Vector2 windowDimensions,
                          int avatarLayer,
                          int topTerrainLayer,
                          int bottomTerrainLayer,
                          int stumpLayer,
                          int leafLayer,
                          int fallingLeafLayer){
        this.terrainSection = terrainSection;
        this.terrain = terrain;
        this.trees = trees;
        this.sun = sun;
        this.halo = halo;
        this.sky = sky;
        this.gameObjects = gameObjects;
        this.avatarLayer = avatarLayer;
        this.topTerrainLayer = topTerrainLayer;
        this.bottomTerrainLayer = bottomTerrainLayer;
        this.stumpLayer = stumpLayer;
        this.leafLayer = leafLayer;
        this.fallingLeafLayer = fallingLeafLayer;
        this.random = new Random();
        this.windowDimensions = windowDimensions;
        this.imageReader = imageReader;
    
        this.hellBackground = imageReader.readImage(HELL_IMAGE_PATH, false);
        this.hellSun = imageReader.readImage(HELL_SUN_IMAGE_PATH, true);
        this.doorImage = new AnimationRenderable(PORTAL_IMAGES, imageReader, true, PORTAL_CLIPS_TIME);
    }
    
    /**
     * Changes the landscape to hell
     */
    public void changeToHell(){
        sun.renderer().setRenderable(hellSun);
        halo.renderer().setRenderable(new OvalRenderable(HELL_HALO_COLOR));
        sky.renderer().setRenderable(hellBackground);
    }
    
    /**
     * generates world to the left side
     * @param minX current end of the generated world to the left
     */
    public void generateLandscapeToLeft(int minX){
        terrain.createInRange(minX - terrainSection, minX);
        trees.createInRange(minX - terrainSection, minX);
    }
    
    /**
     * generates world to the right side
     * @param maxX current end of the generated world to the right
     */
    public void generateLandscapeToRight(int maxX){
        terrain.createInRange(maxX, maxX + terrainSection);
        trees.createInRange(maxX, maxX + terrainSection);
    }
    
    /**
     * Creates the Hell Gate object
     * @param XPosition horizontal position to create the door
     */
    public void createHellDoor(float XPosition){
        GameObject hellDoor = new GameObject(
                new Vector2(XPosition, terrain.groundHeightAt(XPosition) - 2 * DOOR_SIZE.y()),
                DOOR_SIZE,
                this.doorImage);
        hellDoor.setTag(DOOR_TAG);
        gameObjects.addGameObject(hellDoor, avatarLayer);
    }
    
    /**
     * delete all game objects in the provided range
     * @param minX range's start
     * @param maxX ranges' end
     */
    public void deleteInRange(int minX, int maxX){
        for(GameObject object:gameObjects){
            if(object.getTopLeftCorner().x() >= minX && object.getTopLeftCorner().x() <= maxX){
                switch (object.getTag()){
                    case Terrain.TOP_TERRAIN_LAYER_TAG:
                        gameObjects.removeGameObject(object, topTerrainLayer);
                        break;
                    case Terrain.BOTTOM_TERRAIN_LAYER_TAG:
                        gameObjects.removeGameObject(object, bottomTerrainLayer);
                        break;
                    case Tree.STUMP_TAG:
                        gameObjects.removeGameObject(object, stumpLayer);
                        break;
                    case Leaf.LEAF_TAG:
                        gameObjects.removeGameObject(object, leafLayer);
                        break;
                    case Leaf.FALLING_LEAF_TAG:
                        gameObjects.removeGameObject(object, fallingLeafLayer);
                        break;
                }
            }
        }
    }
    
    /**
     * Crate Hell Monsters in the given range
     * @param minX range's start
     * @param maxX range's rnd
     */
    public void generateHellCreatures(int minX, int maxX){
        for(int position = minX; position < maxX; position += Block.SIZE){
            int randomValue = random.nextInt(RANDOM_BOUND);
            if(randomValue  < 1){
                float multFactor = (float)(random.nextInt(BAT_SCREEN_PARTIAL_BOUND) + 1)/10;
                HellBat.create(new Vector2(position, windowDimensions.y() * multFactor) , imageReader, stumpLayer,
                        gameObjects);
            }
        }
    }
}

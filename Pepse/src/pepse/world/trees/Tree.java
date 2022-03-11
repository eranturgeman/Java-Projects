package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.Block;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class Tree {
    public static final String STUMP_TAG = "stump";
    public final static int TREE_HEIGHT = 8;
    
    private final static Color TREE_COLOR = new Color(100, 50, 20);
    private final static Color LEAF_COLOR = new Color(50, 200, 30);
    private final static int TREE_TOP_SIDE_SIZE = 2;
    private final static int RANDOM_VALUE_BOUND = 10;
    private final static int RANDOM_BOUND = 1;
    private final static int TREE_FREE_BLOCKS_AMOUNT = 2;
    
    private final Function<Float, Float> groundHeightAt;
    private final GameObjectCollection gameObjects;
    private final int randomSeed;
    private final int stumpLayer;
    private final int leafLayer;
    private final Vector2 avatarStartingPosition;
    
    public Tree(GameObjectCollection gameObjects, Function<Float, Float> groundHeightAt, int randomSeed,
                int treesLayer, Vector2 avatarStartingPosition){
        this.gameObjects = gameObjects;
        this.groundHeightAt = groundHeightAt;
        this.randomSeed = randomSeed;
        this.stumpLayer = treesLayer;
        this.leafLayer = treesLayer + PepseGameManager.LEAF_ADDITION;
        this.avatarStartingPosition = avatarStartingPosition;
    }
    
    public void createInRange(int minX, int maxX){
        int roundedMinX = (int)Math.floor(minX / Block.SIZE) * Block.SIZE;
        int roundedMaxX = (int)Math.floor(maxX/ Block.SIZE) * Block.SIZE;
        float noTreesAreaLeft = this.avatarStartingPosition.x() - TREE_FREE_BLOCKS_AMOUNT * Block.SIZE;
        float noTreesAreaRight = this.avatarStartingPosition.x() + TREE_FREE_BLOCKS_AMOUNT * Block.SIZE;
        
        for(int i = roundedMinX; i <= roundedMaxX; i += Block.SIZE){
            int randomValue = new Random(Objects.hash(i, randomSeed)).nextInt(RANDOM_VALUE_BOUND);
            if(randomValue < RANDOM_BOUND && (i < noTreesAreaLeft || i > noTreesAreaRight)){
                createSingleTree(i, this.groundHeightAt.apply((float)(i)));
            }
        }
    }
    
    private void createSingleTree(int xCoordinate, Float baseHeight){
        RectangleRenderable stampBlockImage = new RectangleRenderable(TREE_COLOR);
        
        //creating tree stump
        for (int i = 1; i <= TREE_HEIGHT; i++){
            Block stumpBlock = new Block(new Vector2(xCoordinate, baseHeight - (i * Block.SIZE)),
                    stampBlockImage);
            stumpBlock.setTag(STUMP_TAG);
            gameObjects.addGameObject(stumpBlock, stumpLayer);
        }
    
        Vector2 topStumpTopLeftCorner = new Vector2(xCoordinate, baseHeight - (Block.SIZE * TREE_HEIGHT));
        //creating leafs
        Vector2 firstLeafTopLeftCorner =
                topStumpTopLeftCorner.subtract(new Vector2(TREE_TOP_SIDE_SIZE * Block.SIZE,
                        TREE_TOP_SIDE_SIZE * Block.SIZE));
        
        RectangleRenderable leafImage = new RectangleRenderable(LEAF_COLOR);
        
        for (int i = 0; i < 2 * TREE_TOP_SIDE_SIZE + 1; i++){
            for(int j = 0; j < 2 * TREE_TOP_SIDE_SIZE + 1; j++){
                Vector2 topLeftCorner = firstLeafTopLeftCorner.add(new Vector2(i * Block.SIZE,
                        j * Block.SIZE));
                Leaf.create(gameObjects, leafLayer, topLeftCorner, leafImage, randomSeed);
            }
        }
        
        
    }
    
}

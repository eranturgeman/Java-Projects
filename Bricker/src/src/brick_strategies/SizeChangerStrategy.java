package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.SizeBuffer;
import java.util.Random;

/**
 * Concrete brick strategy implementing CollisionStrategy interface.
 * changes the size of the paddle.
 * @author eran_turgeman
 */
public class SizeChangerStrategy extends RemoveBrickStrategyDecorator {
    //============================== private constants ==============================
    private static final String WIDEN_IMAGE_PATH = "assets/buffWiden.png";
    private static final String NARROW_IMAGE_PATH = "assets/buffNarrow.png";
    private static final int GOOD_DEFINER_DOWN_SPEED = 150;
    private static final int BAD_DEFINER_DOWN_SPEED = 200;
    private static final int DEFINER_WIDTH = 100;
    private static final int DEFINER_HEIGHT = 20;
    
    //============================== private static fields ==============================
    public static boolean isBigger = false;
    public static boolean isSmaller = false;
    
    //============================== private fields ==============================
    private final Vector2 definerDimensions;
    private final boolean isGood;
    private final ImageRenderable definerImage;
    private final GameObjectCollection gameObjects;
    private final int downSpeed;
    
    //============================== public methods ==============================
    /**
     * Constructor
     * @param toBeDecorated basic strategy implementing CollisionStrategy to be decorated
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     */
    public SizeChangerStrategy(CollisionStrategy toBeDecorated,
                               ImageReader imageReader) {
        super(toBeDecorated);
        this.isGood = new Random().nextBoolean();
        this.definerDimensions = new Vector2(DEFINER_WIDTH, DEFINER_HEIGHT);
        if(isGood){
            this.definerImage = imageReader.readImage(WIDEN_IMAGE_PATH, false);
            this.downSpeed = GOOD_DEFINER_DOWN_SPEED;
        }else{
            this.definerImage = imageReader.readImage(NARROW_IMAGE_PATH, false);
            this.downSpeed = BAD_DEFINER_DOWN_SPEED;
        }
        this.gameObjects = toBeDecorated.getGameObjectCollection();
    }
    
    /**
     * performs the necessary actions to this strategy upon collision
     * @param thisObj the object the strategy applied to
     * @param otherObj the object thisObj collided with
     * @param counter a counter objects counting the number of collisions
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        super.onCollision(thisObj, otherObj, counter);
        SizeBuffer sizeBuffer = new SizeBuffer(
                Vector2.ZERO,
                definerDimensions,
                definerImage,
                isGood,
                gameObjects);
        sizeBuffer.setTag("SizeBuffer");
        sizeBuffer.setCenter(otherObj.getCenter());
        sizeBuffer.setVelocity(new Vector2(0, downSpeed));
        gameObjects.addGameObject(sizeBuffer);
    }
}

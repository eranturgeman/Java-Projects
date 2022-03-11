package pepse.world.hell;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * Represents a Hell bat game object
 * @author eran_turgeman, elay_aharoni
 */
public class HellBat extends GameObject {
    //============================== public constants ==============================
    /**
     * A tag for this kind of game object
     */
    public static final String BAT_TAG = "bat";
    
    //============================== private constants ==============================
    
    private static final String IMAGE_FLY1 = "assets/bat1.png";
    private static final String IMAGE_FLY2 = "assets/bat2.png";
    private static final String IMAGE_FLY3 = "assets/bat3.png";
    private static final String IMAGE_FLY4 = "assets/bat4.png";
    private static final String[] FLY_IMAGES =
            new String[] {IMAGE_FLY1, IMAGE_FLY2, IMAGE_FLY3, IMAGE_FLY4};
    private static final float TIME_BETWEEN_CLIPS = 0.15f;
    private static final Vector2 BAT_DIMENSIONS = new Vector2(100, 100);
    
    private static final Counter killCounter = new Counter(0);
    
    //============================== public functions ==============================
    
    /**
     * creates a game object represents a Hell Bat
     * @param topLeftCorner position for the object
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param layer layer to insert object to
     * @param gameObjects collection of the objects in the game
     * @return a HellBat object
     */
    public static HellBat create(Vector2 topLeftCorner,
                                 ImageReader imageReader,
                                 int layer,
                                 GameObjectCollection gameObjects){
        Renderable batClip = new AnimationRenderable(FLY_IMAGES, imageReader, true, TIME_BETWEEN_CLIPS);
        HellBat bat = new HellBat(topLeftCorner, BAT_DIMENSIONS, batClip);
        bat.setTag(BAT_TAG);
        gameObjects.addGameObject(bat, layer);
        return bat;
    }
    
    /**
     * incrementor for the class counter
     */
    public static void increaseCounter(){
        killCounter.increment();
    }
    
    /**
     * getter for the counters current value
     * @return counter's value
     */
    public static int getCounterValue(){
        return killCounter.value();
    }
    
    //============================== private methods ==============================
    /*
     * Private constructor. all creations of this game object from outside this class should be done with
     * the create function
     */
    private HellBat(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }
}

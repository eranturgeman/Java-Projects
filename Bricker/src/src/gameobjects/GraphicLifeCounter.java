package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * Represents a Graphic counter, contains objects that presented on game window showing how many lives left.
 * An extension of danogl.GameObject
 * @author eran_turgeman
 */
public class GraphicLifeCounter extends GameObject {
    //================================== private constants ==================================
    private static final float SPACE_BETWEEN_WIDGETS_MULT_FACTOR = 1.5f;
    //================================== private fields ==================================
    private final Counter lifeCounter;
    private final GameObjectCollection gameObjects;
    private final GameObject[] counters;
    
    //================================== public methods ==================================
    /**
     * Constructor
     * @param widgetTopLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param widgetDimensions    Width and height in window coordinates.
     * @param livesCounter global counter that counts lives and passed among game objects
     * @param renderable The renderable representing the object. Can be null, in which case
     * @param gameObjectsCollection all game objects
     * @param numOfLives number of lives to present
     */
    public GraphicLifeCounter(Vector2 widgetTopLeftCorner,
                              Vector2 widgetDimensions,
                              Counter livesCounter,
                              Renderable renderable,
                              GameObjectCollection gameObjectsCollection,
                              int numOfLives) {
        super(widgetTopLeftCorner, widgetDimensions, renderable);
        this.gameObjects = gameObjectsCollection;
        this.lifeCounter = livesCounter;
        
        counters = new GameObject[numOfLives];
        
        for (int i = 0; i < numOfLives; i++) {
            GameObject counter = new GameObject(
                    widgetTopLeftCorner,
                    widgetDimensions,
                    renderable);
            counters[i] = counter;
            gameObjects.addGameObject(counter, Layer.BACKGROUND);
            widgetTopLeftCorner = widgetTopLeftCorner.add(
                    new Vector2(widgetDimensions.x() * SPACE_BETWEEN_WIDGETS_MULT_FACTOR,0));
        }
    }
    
    //================================== public methods ==================================
    /**
     * performing the necessary changes in the counter and removing presented graphic counters from the screen
     * @param deltaTime The time elapsed, in seconds, since the last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        lifeCounter.decrement();
        gameObjects.removeGameObject(counters[lifeCounter.value()], Layer.BACKGROUND);
    }
}

package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.util.Objects;

/**
 * Represents a Mock Paddle instance, disappearing after a given times of collisions with other objects.
 * Moves according to the main paddle moves.
 * An extension of Paddle
 * @see Paddle
 * @author eran_turgeman
 */
public class MockPaddle extends Paddle{
    //================================== private constants ==================================
    public static boolean isInstantiated = false;
    
    //================================== private fields ==================================
    private final GameObjectCollection gameObjects;
    private int numCollisionsAllowed;
    
    //================================== public methods ==================================
    /**
     * Constructor
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions Width and height in window coordinates.
     * @param renderable The renderable representing the object. Can be null, in which case
     * @param inputListener an InputListener instance for reading user input.
     * @param windowDimensions size of the game window in pixels
     * @param minDistanceFromEdge minimal distance allowed for thr paddle to be from the borders
     */
    public MockPaddle(Vector2 topLeftCorner,
                      Vector2 dimensions,
                      Renderable renderable,
                      UserInputListener inputListener,
                      Vector2 windowDimensions,
                      GameObjectCollection gameObjectCollection,
                      int minDistanceFromEdge,
                      int numCollisionsToDisappear) {
        super(topLeftCorner, dimensions, renderable, inputListener, windowDimensions,
                (int)(minDistanceFromEdge * 1.2));
        this.numCollisionsAllowed = numCollisionsToDisappear;
        this.gameObjects = gameObjectCollection;
    }
    
    /**
     * on collision with other game object (except border) decreases the counter, and removing the paddle
     * after numCollisionsAllowed collisions
     * @param other other objects in the collision event
     * @param collision stores information about the collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(!Objects.equals(other.getTag(),"Border")){
            numCollisionsAllowed--;
            if(numCollisionsAllowed <= 0){
                gameObjects.removeGameObject(this);
                MockPaddle.isInstantiated = false;
            }
        }
    }
}

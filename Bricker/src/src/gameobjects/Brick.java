package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.brick_strategies.CollisionStrategy;

/**
 * Represents a brick in the Bricker game. An extension of danogl.GameObject
 */
public class Brick  extends GameObject {
    //================================== private constants ==================================
    private final CollisionStrategy collisionStrategy;
    private final Counter bricksCounter;
    
    //================================== public methods ==================================
    /**
     * Constructor
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions Width and height in window coordinates.
     * @param renderable The renderable representing the object. Can be null, in which case
     * @param collisionStrategy an object describes what will happen upon collision with a brick
     * @param counter a counter objects that counting the existing bricks in the game
     */
    public Brick(Vector2 topLeftCorner,
                 Vector2 dimensions,
                 Renderable renderable,
                 CollisionStrategy collisionStrategy,
                 Counter counter){
        super(topLeftCorner, dimensions, renderable);
        this.collisionStrategy = collisionStrategy;
        this.bricksCounter = counter;
        bricksCounter.increment();
    }
    
    /**
     * performing the necessary actions and changes when a Ball instance collide with other object in the
     * Default Layer of the Static Layer
     * @param other the object the Ball collide with
     * @param collision Collision instance, providing details on the collision event
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        collisionStrategy.onCollision(this, other, bricksCounter);
    }
}

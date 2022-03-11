package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.brick_strategies.SizeChangerStrategy;
import java.util.Objects;

/**
 * A buffer drops from a brick when a brick with a SizeChangerStrategy has been hit.
 * upon collision with the paddle- changes its size.
 * An extension of danogl.GameObject
 * @author eran_turgeman
 */
public class SizeBuffer extends GameObject {
    //================================== private constants ==================================
    private static final float DOWNSIZE_FACTOR = 0.8f;
    
    //================================== private fields ==================================
    private final boolean isGood;
    private final GameObjectCollection gameObjects;
    
    //================================== public methods ==================================
    /**
     * Constructor
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions Width and height in window coordinates.
     * @param renderable The renderable representing the object. Can be null, in which case
     */
    public SizeBuffer(Vector2 topLeftCorner,
                      Vector2 dimensions,
                      Renderable renderable,
                      boolean isGood,
                      GameObjectCollection gameObjects) {
        super(topLeftCorner, dimensions, renderable);
        this.isGood = isGood;
        this.gameObjects = gameObjects;
    }
    
    /**
     * on collision with Paddle- increases/ decreases its size.
     * @param other other objects in the collision event
     * @param collision stores information about the collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        gameObjects.removeGameObject(this);
        Vector2 originalDimensions = other.getDimensions();
        if(isGood){
            if(SizeChangerStrategy.isBigger){
                return;
            }
            float growthFactor = 1/DOWNSIZE_FACTOR;
            other.setDimensions(new Vector2(originalDimensions.x() * growthFactor, originalDimensions.y()));
            if(SizeChangerStrategy.isSmaller) {
                SizeChangerStrategy.isSmaller = false;
            }else{
                SizeChangerStrategy.isBigger = true;
            }
        }else{
            if(SizeChangerStrategy.isSmaller){
                return;
            }
            other.setDimensions(new Vector2(originalDimensions.x() * DOWNSIZE_FACTOR,
                    originalDimensions.y()));
            if(SizeChangerStrategy.isBigger) {
                SizeChangerStrategy.isBigger = false;
            }else{
                SizeChangerStrategy.isSmaller = true;
            }
        }
    }
    
    /**
     * determines if this object should collide with other given object
     * @param other other object in the game
     * @return true or false
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        if(!Objects.equals(other.getTag(), "Paddle")){
            return false;
        }
        return super.shouldCollideWith(other);
    }
}

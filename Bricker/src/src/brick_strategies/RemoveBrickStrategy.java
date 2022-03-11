package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * Concrete brick strategy implementing CollisionStrategy interface. Removes holding brick on collision.
 * @author eran_turgeman
 */
public class RemoveBrickStrategy implements CollisionStrategy{
    //============================== private fields ==============================
    private final GameObjectCollection gameObjects;
    private boolean firstCollision;
    
    //============================== public methods ==============================
    /**
     * Constructor for CollisionStrategy object
     * @param gameObjects the objects that currently in the game
     */
    public RemoveBrickStrategy(GameObjectCollection gameObjects){
        this.gameObjects = gameObjects;
        this.firstCollision = true;
    }
    
    /**
     * performs the necessary actions to this strategy upon collision
     * @param thisObj the object the strategy applied to
     * @param otherObj the object thisObj collided with
     * @param counter a counter objects counting the number of collisions
     */
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter){
        if(firstCollision){
            counter.decrement();
            firstCollision = false;
        }
        gameObjects.removeGameObject(thisObj, Layer.STATIC_OBJECTS);
    }
    
    /**
     * return the current objects in the game
     * @return the game objects
     */
    @Override
    public GameObjectCollection getGameObjectCollection() {
        return gameObjects;
    }
}

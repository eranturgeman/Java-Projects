package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.util.Counter;

/**
 * Abstract decorator to add functionality to the remove brick strategy, following the decorator pattern.
 * All strategy decorators should inherit from this class.
 * @author eran_turgeman
 */
public abstract class RemoveBrickStrategyDecorator implements CollisionStrategy {
    //============================== private fields ==============================
    private final CollisionStrategy basicBrickStrategy;
    
    //============================== public methods ==============================
    /**
     * Constructor
     * @param toBeDecorated basic strategy implementing CollisionStrategy to be decorated
     */
    public RemoveBrickStrategyDecorator(CollisionStrategy toBeDecorated){
        this.basicBrickStrategy = toBeDecorated;
    }
    
    /**
     * performs the necessary actions to this strategy upon collision
     * @param thisObj the object the strategy applied to
     * @param otherObj the object thisObj collided with
     * @param counter a counter objects counting the number of collisions
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        basicBrickStrategy.onCollision(thisObj, otherObj, counter);
    }
    
    /**
     * return the current objects in the game
     * @return the game objects
     */
    @Override
    public GameObjectCollection getGameObjectCollection() {
        return basicBrickStrategy.getGameObjectCollection();
    }
}

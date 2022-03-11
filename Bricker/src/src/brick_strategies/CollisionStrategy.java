package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.util.Counter;

/**
 * General type for brick strategies, part of decorator pattern implementation.
 * All brick strategies implement this interface.
 * @author eran_turgeman
 */
public interface CollisionStrategy {
    void onCollision(GameObject thisObj, GameObject otherObj, Counter counter);
    GameObjectCollection getGameObjectCollection();
}

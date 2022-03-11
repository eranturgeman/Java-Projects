package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * class that represents a block object in the game
 * @author eran_turgeman, elay_aharoni
 */
public class Block extends GameObject {
    //================================== public constants ==================================
    /**
     * the block size
     */
    public static final int SIZE = 30;
    
    //================================== public methods ==================================
    /**
     * the constructor for a block instance
     * @param topLeftCorner - the place to creat the block in
     * @param renderable - a renderable object to represent the block
     */
    public Block(Vector2 topLeftCorner,
                 Renderable renderable){
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}

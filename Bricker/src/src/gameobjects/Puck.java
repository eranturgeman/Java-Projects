package src.gameobjects;

import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * One of the types of objects that can be set loose when a brick is hit.
 * An extension of Ball
 * @see Ball
 * @author eran_turgeman
 */
public class Puck extends Ball{
    //================================== public methods ==================================
    /**
     * Constructor
     * @param topLeftCorner  Position of the object, in window coordinates (pixels).
     *                       Note that (0,0) is the top-left corner of the window.
     * @param dimensions     Width and height in window coordinates.
     * @param renderable     The renderable representing the object. Can be null, in which case
     * @param collisionSound A sound to be heard upon collision
     */
    public Puck(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable, collisionSound);
    }
}

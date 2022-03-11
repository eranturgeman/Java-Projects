package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represent a Ball. An extension of danogl.GameObject.
 * @author Eran Turgeman
 */
public class Ball extends GameObject {
    //================================== private constants ==================================
    private final Sound collisionSound;
    private int numOfCollisions;
    
    //================================== public methods ==================================
    /**
     * Constructor
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     * @param collisionSound A sound to be heard upon collision
     */
    public Ball(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;
        this.numOfCollisions = 0;
    }
    
    /**
     * On collision, object velocity is reflected about the normal vector of the surface it collides with.
     * @param other the object the Ball collide with
     * @param collision Collision instance, providing details on the collision event
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        Vector2 newVel = getVelocity().flipped(collision.getNormal());
        setVelocity(newVel);
        numOfCollisions++;
        collisionSound.play();
    }
    
    /**
     * returns the number of collision of the ball with other objects in the game
     * @return number of collisions
     */
    public int getCollisionCount(){
        return numOfCollisions;
    }
}

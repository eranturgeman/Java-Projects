package src.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.awt.event.KeyEvent;

/**
 * One of the main game objects. Repels the ball against the bricks.
 * An extension of danogl.GameObject
 * @author eran_turgeman
 */
public class Paddle extends GameObject {
    //================================== private constants ==================================
    private static final float MOVEMENT_SPEED = 400;
    
    //================================== private fields ==================================
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;
    private final int minDistanceFromEdge;
    
    //================================== public methods ==================================
    /**
     * Constructor
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions Width and height in window coordinates.
     * @param renderable The renderable representing the object. Can be null, in which case
     * @param inputListener an InputListener instance for reading user input.
     * @param minDistanceFromEdge minimal distance allowed for thr paddle to be from the borders
     * @param windowDimensions size of the game window in pixels
     */
    public Paddle(Vector2 topLeftCorner,
                  Vector2 dimensions,
                  Renderable renderable,
                  UserInputListener inputListener,
                  Vector2 windowDimensions,
                  int minDistanceFromEdge) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
        this.minDistanceFromEdge = minDistanceFromEdge;
    }
    
    /**
     * updates the game frame. Checks if there are any pressed keys and sets the Paddle velocity accordingly.
     * @param deltaTime The time elapsed, in seconds, since the last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 movementDir = Vector2.ZERO;
        float halfSize = getDimensions().x() / 2;
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movementDir = movementDir.add(Vector2.RIGHT);
        }
        if(getCenter().x() - halfSize <= minDistanceFromEdge && inputListener.isKeyPressed(KeyEvent.VK_LEFT)){
            movementDir = Vector2.ZERO;
        }
        if((getCenter().x() + halfSize >= windowDimensions.x() - minDistanceFromEdge) &&
                inputListener.isKeyPressed(KeyEvent.VK_RIGHT)){
            movementDir = Vector2.ZERO;
        }
        setVelocity(movementDir.mult(MOVEMENT_SPEED));
    }
}

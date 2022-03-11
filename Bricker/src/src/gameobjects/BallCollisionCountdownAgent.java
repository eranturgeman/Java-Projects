package src.gameobjects;

import danogl.GameObject;
import src.brick_strategies.ChangeCameraStrategy;

/**
 * An object of this class is instantiated on collision of ball with a brick with a change camera strategy.
 * It checks ball's collision counter every frame, and once the ball has collided countDownValue
 * times since instantiation, it calls the strategy to reset the camera to normal.
 * An extension of danogl.GameObject
 * @see ChangeCameraStrategy
 * @author eran_turgeman
 */
public class BallCollisionCountdownAgent extends GameObject {
    //============================== private fields ==============================
    private final int valueToResetUpon;
    private final ChangeCameraStrategy owner;
    private final Ball ball;
    
    //============================== public methods ==============================
    /**
     * Constructor
     * @param ball ball object
     * @param owner the ChangeCameraStrategy related to this instance
     * @param countDownValue the number of hits which after them the effect is being revoked
     */
    public BallCollisionCountdownAgent(Ball ball,
                                       ChangeCameraStrategy owner,
                                       int countDownValue) {
        super(ball.getTopLeftCorner(), ball.getDimensions(), null);
        this.ball = ball;
        this.valueToResetUpon = ball.getCollisionCount() + countDownValue;
        this.owner = owner;
    }
    
    /**
     * Updates the game's frame
     * @param deltaTime The time elapsed, in seconds, since the last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(ball.getCollisionCount() == valueToResetUpon){
            owner.turnOffCameraChange();
            owner.getGameObjectCollection().removeGameObject(this);
        }
    }
}

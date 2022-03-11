package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.BrickerGameManager;
import src.gameobjects.Ball;
import src.gameobjects.BallCollisionCountdownAgent;
import java.util.Objects;

/**
 * Concrete class extending abstract RemoveBrickStrategyDecorator.
 * Changes camera focus from ground to ball until ball collides NUM_BALL_COLLISIONS_TO_TURN_OFF times.
 * @author eran_turgeman
 */
public class ChangeCameraStrategy extends RemoveBrickStrategyDecorator{
    //============================== private constants ==============================
    private static final int NUM_OF_COLLISIONS_UNTIL_RESET = 4;
    
    //============================== private fields ==============================
    private final BrickerGameManager gameManager;
    private final WindowController windowController;
    private final GameObjectCollection gameObjects;
    
    //============================== public methods ==============================
    /**
     * Constructor
     * @param toBeDecorated basic strategy implementing CollisionStrategy to be decorated
     * @param windowController controls visual rendering of the game window and object renderables.
     * @param gameManager the gameManager instance that running the current game
     */
    public ChangeCameraStrategy(CollisionStrategy toBeDecorated,
                                WindowController windowController,
                                BrickerGameManager gameManager) {
        super(toBeDecorated);
        this.windowController = windowController;
        this.gameManager = gameManager;
        this.gameObjects = getGameObjectCollection();
    }
    
    /**
     * performs the necessary actions to this strategy upon collision
     * @param thisObj the object the strategy applied to
     * @param otherObj the object thisObj collided with
     * @param counter a counter objects counting the number of collisions
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj, Counter counter) {
        super.onCollision(thisObj, otherObj, counter);
        if(gameManager.getCamera() == null){
           Ball ball = getBall();
            Camera camera = new Camera(
                    ball,
                    Vector2.ZERO,
                    windowController.getWindowDimensions().mult(1.2f),
                    windowController.getWindowDimensions());
            gameManager.setCamera(camera);
            assert ball != null;
            BallCollisionCountdownAgent countAgent = new BallCollisionCountdownAgent(ball, this,
                    NUM_OF_COLLISIONS_UNTIL_RESET);
            countAgent.setTag("countDownAgent");
            gameObjects.addGameObject(countAgent, Layer.BACKGROUND);
        }
    }
    
    /**
     * removes the following camera effect
     */
    public void turnOffCameraChange(){
        gameManager.setCamera(null);
    }
    
    //============================== private methods ==============================
    /*
     * returns the main ball of the game (this is a single instance of it at all times)
     */
    
    private Ball getBall() {
        for(GameObject gameObject: gameObjects){
            if(Objects.equals(gameObject.getTag(), "Ball")){
                return (Ball)gameObject;
            }
        }
        return null; // cannot happen
    }
}

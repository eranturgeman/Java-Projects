package src.brick_strategies;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.MockPaddle;
import java.util.Random;

/**
 * Concrete class extending abstract RemoveBrickStrategyDecorator.
 * Introduces extra paddle to game window which remains until colliding
 * NUM_COLLISIONS_FOR_MOCK_PADDLE_DISAPPEARANCE with other game objects but Borders.
 * @author eran_turgeman
 */
public class AddPaddleStrategy extends RemoveBrickStrategyDecorator{
    //============================== private constants ==============================
    private static final int NUM_COLLISIONS_FOR_MOCK_PADDLE_DISAPPEARANCE = 3;
    private static final String PADDLE_IMAGE_PATH = "assets/paddle.png";
    private static final int MOCK_PADDLE_HEIGHT = 20;
    private static final int MOCK_PADDLE_WIDTH = 100;
    private static final int BORDER_WIDTH = 20;
    
    //============================== private fields ==============================
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;
    private final Random random;
    private final Renderable paddleImage;
    
    //============================== public methods ==============================
    /**
     * Constructor
     * @param toBeDecorated basic strategy implementing CollisionStrategy to be decorated
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param inputListener an InputListener instance for reading user input.
     * @param windowDimensions size of the game window in pixels
     * @see CollisionStrategy
     */
    public AddPaddleStrategy(CollisionStrategy toBeDecorated,
                             ImageReader imageReader,
                             UserInputListener inputListener,
                             Vector2 windowDimensions) {
        super(toBeDecorated);
        this.paddleImage = imageReader.readImage(PADDLE_IMAGE_PATH, false);
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
        this.random = new Random();
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
        if(!MockPaddle.isInstantiated){
            float paddleY = windowDimensions.y()/2;
            float paddleX =
                    random.nextInt((int)(windowDimensions.x() - 2 * BORDER_WIDTH - MOCK_PADDLE_WIDTH)) + BORDER_WIDTH + MOCK_PADDLE_WIDTH;
    
            MockPaddle mockPaddle = new MockPaddle(
                    Vector2.ZERO,
                    new Vector2(MOCK_PADDLE_WIDTH, MOCK_PADDLE_HEIGHT),
                    paddleImage,
                    inputListener,
                    windowDimensions,
                    super.getGameObjectCollection(),
                    BORDER_WIDTH,
                    NUM_COLLISIONS_FOR_MOCK_PADDLE_DISAPPEARANCE);
            Vector2 paddleCenter = new Vector2(paddleX, paddleY);
            mockPaddle.setCenter(paddleCenter);
            mockPaddle.setTag("MockPaddle");
            getGameObjectCollection().addGameObject(mockPaddle);
            MockPaddle.isInstantiated = true;
        }
    }
}

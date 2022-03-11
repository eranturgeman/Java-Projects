package src.brick_strategies;

import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import src.BrickerGameManager;
import java.util.Random;

/**
 * Factory class for creating Collision strategies
 * @author eran_turgeman
 */
public class BrickStrategyFactory {
    //============================== private constants ==============================
    private static final int NUMBER_OF_STRATEGIES = 6;
    
    //============================== private fields ==============================
    private final GameObjectCollection gameObjects;
    private final Random random;
    private final BrickerGameManager gameManager;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;
    private final WindowController windowController;
    
    //============================== public methods ==============================
    /**
     * Constructor
     * @param gameObjectCollection the objects that in the game
     * @param gameManager the gameManager instance that running the current game
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param soundReader a SoundReader instance for reading sound clips from files for rendering event sounds
     * @param inputListener an InputListener instance for reading user input.
     * @param windowController controls visual rendering of the game window and object renderables.
     * @param windowDimensions size of the game window in pixels
     */
    public BrickStrategyFactory(GameObjectCollection gameObjectCollection,
                                BrickerGameManager gameManager,
                                ImageReader imageReader,
                                SoundReader soundReader,
                                UserInputListener inputListener,
                                WindowController windowController,
                                Vector2 windowDimensions){
        this.gameObjects = gameObjectCollection;
        this.gameManager = gameManager;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.inputListener = inputListener;
        this.windowController = windowController;
        this.windowDimensions = windowDimensions;
        this.random = new Random();
    }
    
    
    /**
     * randomly selects between the existing strategies implemented in brick_strategies.
     * can be the basic strategy, a decorated strategy with another effect or a strategy implementin
     * multiple strategies (up to 3)
     * @see RemoveBrickStrategyDecorator
     * @see RemoveBrickStrategy
     * @see MultipleStrategy
     * @return a strategy to be applied to a game object (brick)
     */
    public CollisionStrategy getStrategy(){
        int index = random.nextInt(NUMBER_OF_STRATEGIES);
        CollisionStrategy basicStrategy = new RemoveBrickStrategy(gameObjects);
        CollisionStrategy chosenStrategy;
        
        switch(index){
            case 0:
                chosenStrategy = basicStrategy;
                break;
            case 1:
                chosenStrategy = new MultipleStrategy(basicStrategy, gameManager, imageReader, soundReader,
                        inputListener, windowController, windowDimensions, false);
                break;
            case 2:
                chosenStrategy = new AddPaddleStrategy(basicStrategy,imageReader, inputListener, windowDimensions);
                break;
            case 3:
                chosenStrategy =  new PuckStrategy(basicStrategy, imageReader, soundReader);
                break;
            case 4:
                chosenStrategy = new SizeChangerStrategy(basicStrategy, imageReader);
                break;
            case 5:
                chosenStrategy = new ChangeCameraStrategy(basicStrategy, windowController, gameManager);
                break;
            default:
                chosenStrategy =  null;
        }
        return chosenStrategy;
    }
}

package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.BrickerGameManager;
import java.util.Random;

/**
 * Concrete class extending abstract RemoveBrickStrategyDecorator.
 * enables to apply several strategies (up to 3) to the same brick
 * @author eran_turgeman
 */
public class MultipleStrategy extends RemoveBrickStrategyDecorator {
    //============================== private constants ==============================
    private static final int MULTIPLE_INCLUDED = 5;
    private static final int MULTIPLE_EXCLUDED = MULTIPLE_INCLUDED - 1;
    
    //============================== private fields ==============================
    private final CollisionStrategy innerStrategy;
    private  final CollisionStrategy strategy1;
    private  final CollisionStrategy strategy2;
    private final BrickerGameManager gameManager;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final UserInputListener inputListener;
    private final WindowController windowController;
    private final Vector2 windowDimensions;
    private final Random random;
    
    //============================== public methods ==============================
    /**
     * Constructor
     * @param toBeDecorated basic strategy implementing CollisionStrategy to be decorated
     * @param gameManager the gameManager instance that running the current game
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param soundReader a SoundReader instance for reading sound clips from files for rendering event sounds
     * @param inputListener an InputListener instance for reading user input.
     * @param windowController controls visual rendering of the game window and object renderables.
     * @param windowDimensions size of the game window in pixels
     * @param maxStrategyLevel a boolean deciding whether a chosen strategy can be another multiple
     *                         strategy (in aim to limit to up to 3 strategies per brick)
     */
    public MultipleStrategy(CollisionStrategy toBeDecorated,
                            BrickerGameManager gameManager,
                            ImageReader imageReader,
                            SoundReader soundReader,
                            UserInputListener inputListener,
                            WindowController windowController,
                            Vector2 windowDimensions,
                            boolean maxStrategyLevel) {
        super(toBeDecorated);
        
        this.innerStrategy = toBeDecorated;
        this.gameManager = gameManager;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.inputListener = inputListener;
        this.windowController = windowController;
        this.windowDimensions = windowDimensions;
        this.random = new Random();
        
        if(!maxStrategyLevel){
            this.strategy1 = chooseAnotherStrategy(false);
            this.strategy2 = chooseAnotherStrategy(true);
        }else{
            this.strategy1 = chooseAnotherStrategy(false);
            this.strategy2 = chooseAnotherStrategy(false);
        }
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
        strategy1.onCollision(thisObj, otherObj, counter);
        strategy2.onCollision(thisObj, otherObj, counter);
    }
    
    /**
     * returns the current objects in the game
     * @return game objects
     */
    @Override
    public GameObjectCollection getGameObjectCollection() {
        return innerStrategy.getGameObjectCollection();
    }
    
    //============================== private methods ==============================
    /*
     * chooses another strategy from the strategies in brick_strategies.
     * includes a limitation whether another multiple strategy can be chosen
     */
    private CollisionStrategy chooseAnotherStrategy(boolean multipleStrategyPossible) {
        int randomLimit;
        if(multipleStrategyPossible){
            randomLimit = MULTIPLE_INCLUDED;
        }else{
            randomLimit = MULTIPLE_EXCLUDED;
        }
        int index = random.nextInt(randomLimit);
    
        CollisionStrategy chosenStrategy;
        switch(index){
            case 0:
                chosenStrategy = new SizeChangerStrategy(innerStrategy, imageReader);
                break;
            case 1:
                chosenStrategy = new AddPaddleStrategy(innerStrategy,imageReader, inputListener,
                        windowDimensions);
                break;
            case 2:
                chosenStrategy =  new PuckStrategy(innerStrategy, imageReader, soundReader);
                break;
            case 3:
                chosenStrategy = new ChangeCameraStrategy(innerStrategy, windowController, gameManager);
                break;
            case 4:
                chosenStrategy = new MultipleStrategy(innerStrategy, gameManager, imageReader, soundReader,
                        inputListener, windowController, windowDimensions, true);
                break;
            default:
                chosenStrategy =  null; //cannot happen
        }
        return chosenStrategy;
    }
}

package src;

import danogl.collisions.GameObjectCollection;
import src.brick_strategies.BrickStrategyFactory;
import src.gameobjects.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.util.Objects;
import java.util.Random;

/**
 * The game manager.
 * Responsible to initialize the game, keeping track on the game objects (and update them)
 * Includes the main function
 * @author eran_turgeman
 */
public class BrickerGameManager extends GameManager {
    //================================== private constants ==================================
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 500;
    
    private static final int BORDER_WIDTH = 20;
    
    private static final int PADDLE_HEIGHT = 20;
    private static final int PADDLE_WIDTH = 150; //original 100
    
    private static final int BALL_RADIUS = 25;
    private static final float BALL_SPEED = 300; //original 350

    private static final int MIN_DISTANCE_FROM_EDGE = BORDER_WIDTH;
    
    private static final int BRICK_ROWS = 5;
    private static final int BRICKS_IN_ROW = 8;
    
    private static final int INITIAL_LIVES = 4;
    private static final int GRAPHIC_COUNTER_HEIGHT = 20;
    private static final int GRAPHIC_COUNTER_WIDTH = 20;
    private static final int NUMERIC_COUNTER_HEIGHT = 20;
    private static final int NUMERIC_COUNTER_WIDTH = 60;
    
    private static final String BALL_IMAGE_PATH = "assets/ball.png";
    private static final String PADDLE_IMAGE_PATH = "assets/paddle.png";
    private static final String BRICK_IMAGE_PATH = "assets/brick.png";
    private static final String GRAPHIC_COUNTER_IMAGE_PATH = "assets/heart.png";
    private static final String BACKGROUND_IMAGE_PATH = "assets/DARK_BG2_small.jpeg";
    private static final String BALL_COLLISION_SOUND_PATH = "assets/blop_cut_silenced.wav";
    
    //================================== private fields ==================================
    private Ball ball;
    private Vector2 windowDimensions;
    private WindowController windowController;
    private Counter lifeCounter;
    private Counter bricksCounter;
    private GraphicLifeCounter graphicLifeCounter;
    private NumericLifeCounter numericLifeCounter;
    private BrickStrategyFactory brickStrategyFactory;
    
    //================================== public methods ==================================
    /**
     * Constructor for the Bricker game.
     * @param windowTitle Game's window title
     * @param windowDimensions window dimensions in pixels
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }
    
    /**
     * initializes a new game and all its objects (ball, paddle, bricks, borders, counters)
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param soundReader a SoundReader instance for reading sound clips from files for rendering event sounds
     * @param inputListener an InputListener instance for reading user input.
     * @param windowController controls visual rendering of the game window and object renderables.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        //initialization
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.windowDimensions = windowController.getWindowDimensions();
        this.windowController = windowController;
        this.lifeCounter = new Counter(INITIAL_LIVES);
        this.bricksCounter = new Counter(0);
        windowController.setTargetFramerate(80);
    
        //create ball
        createBall(imageReader, soundReader, windowController);
    
        //create paddles
        createPaddle(imageReader, inputListener);
    
        //create borders
        createBorders(windowDimensions);
        
        //create bricks
        this.brickStrategyFactory = new BrickStrategyFactory(gameObjects(), this, imageReader, soundReader,
                inputListener, windowController, windowDimensions);
        createBricks(imageReader);
        
        //create background
        createBackground(imageReader);
    
        //create counters
        createGraphicCounter(imageReader);
        createNumericCounters();
    }
    
    /**
     * Updates the game's frame
     * @param deltaTime The time elapsed, in seconds, since the last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        removeIrrelevantObjects();
        checkForGameEnd(deltaTime);
    }
    
    /**
     * repositions the ball after existed the game window
     * @param ball ball object
     */
    public void repositionBall(GameObject ball){
        ball.setCenter(windowDimensions.mult(0.5F));
        ball.setVelocity(getRandomUpwardsDirection());
    }
    
    /**
     * Program main function. Does not require any arguments.
     * @param args arguments from command line
     */
    public static void main(String[] args) {
        new BrickerGameManager("Bricker", new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT)).run();
    }
    
    //================================== private methods ==================================
    
    /*
     * Called in every game frame to check if the game has ended (lost case and win case)
     */
    private void checkForGameEnd(float deltaTime) {
        double ballHeight = ball.getCenter().y();
        
        //check for LOSE
        if(ballHeight >= windowDimensions.y()){
            graphicLifeCounter.update(deltaTime);
            numericLifeCounter.update(deltaTime);
            if(lifeCounter.value() <= 0){
                endGameDialog("YOU LOSE!");
            }
            repositionBall(ball);
        }
        //check for WIN
        if(bricksCounter.value() <= 0){
            endGameDialog("YOU WIN!");
        }
    }
    
    /*
     * Opens the pop up dialog at the end of the game and handling starting of a new game or ending the game
     */
    private void endGameDialog(String promptString){
        if(windowController.openYesNoDialog(promptString + "\nPlay again?")){
            windowController.resetGame();
        }else{
            windowController.closeWindow();
        }
    }
    
    /*
     * returns a random diagonal direction to the ball
     */
    private Vector2 getRandomUpwardsDirection(){
        float ballVelX = BALL_SPEED;
        Random rand = new Random();
        if (rand.nextBoolean())
            ballVelX *= -1;
        return new Vector2(ballVelX, BALL_SPEED * -1);
    }
    
    /*
     * goes through all game objects and remove from the game object's list all the objects that are not in
     * the game (exited the game window) except the ball
     */
    private void removeIrrelevantObjects() {
        GameObjectCollection gameObjects = gameObjects();
        for(GameObject gameObject: gameObjects){
            if(!Objects.equals(gameObject.getTag(), "Ball") &&
                    gameObject.getTopLeftCorner().y() >= windowDimensions.y()){
                gameObjects.removeGameObject(gameObject);
            }
        }
    }
    
    /*
     * Creating the ball object and add it to the game window
     */
    private void createBall(ImageReader imageReader, SoundReader soundReader, WindowController windowController) {
        Renderable ballImage = imageReader.readImage(BALL_IMAGE_PATH, true);
        Sound collisionSound = soundReader.readSound(BALL_COLLISION_SOUND_PATH);
        ball = new Ball(Vector2.ZERO, new Vector2(BALL_RADIUS, BALL_RADIUS), ballImage, collisionSound);
        
        Vector2 windowDimensions = windowController.getWindowDimensions();
        ball.setCenter(windowDimensions.mult(0.5f));
        ball.setTag("Ball");
        gameObjects().addGameObject(ball);
        ball.setVelocity(getRandomUpwardsDirection());
    }
    
    /*
     * Creating the paddle object and add it to the game window
     */
    private void createPaddle(ImageReader imageReader, UserInputListener inputListener) {
        Renderable paddleImage = imageReader.readImage(PADDLE_IMAGE_PATH, false);
        Paddle paddle = new Paddle(
                Vector2.ZERO,
                new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage,
                inputListener,
                windowDimensions,
                MIN_DISTANCE_FROM_EDGE);
        
        paddle.setCenter(
                new Vector2(windowDimensions.x() / 2,
                        (int) windowDimensions.y() - (float)(2.5 * GRAPHIC_COUNTER_HEIGHT)));
        
        paddle.setTag("Paddle");
        gameObjects().addGameObject(paddle);
    }
    
    /*
     * Creating the bricks objects and add them to the game window
     */
    private void createBricks(ImageReader imageReader){
        Renderable brickImage = imageReader.readImage(BRICK_IMAGE_PATH, false);
        int spaceBetweenBricks = 3;
        float spaceBetweenRows = 3f;
        // (window width - 2 borders width - space between bricks) / amount of bricks
        float brickWidth =
                (windowDimensions.x() - (2 * BORDER_WIDTH) - (BRICKS_IN_ROW + 1) * spaceBetweenBricks) / BRICKS_IN_ROW;
        
        // (1/4 (window height - border width) - space between rows) / amount of rows
        float brickHeight =
                (((windowDimensions.y() - BORDER_WIDTH) / 4) - ((BRICK_ROWS + 1) * spaceBetweenRows)) / BRICK_ROWS;
        
        Vector2 brickDimensions = new Vector2(brickWidth, brickHeight);
        Vector2 spaceBetweenVector = new Vector2(spaceBetweenBricks + brickWidth, 0);
        Vector2 currentRowTopLeftCorner = new Vector2(BORDER_WIDTH + spaceBetweenBricks, BORDER_WIDTH);
        for(int i = 1; i <= BRICK_ROWS; i++){
            for(int j = 0; j < BRICKS_IN_ROW; j++){
                
                Brick newBrick = new Brick(currentRowTopLeftCorner, brickDimensions, brickImage,
                        brickStrategyFactory.getStrategy(), bricksCounter);
                newBrick.setTag("Brick");
                gameObjects().addGameObject(newBrick, Layer.STATIC_OBJECTS);
                currentRowTopLeftCorner = currentRowTopLeftCorner.add(spaceBetweenVector);
            }
            currentRowTopLeftCorner = new Vector2(spaceBetweenBricks + BORDER_WIDTH,
                    BORDER_WIDTH + (spaceBetweenRows + brickHeight) * i);
        }
    }
    
    /*
     * Creating the graphic counter object and add it to the game window
     */
    private void createGraphicCounter(ImageReader imageReader) {
        Renderable graphicImage = imageReader.readImage(GRAPHIC_COUNTER_IMAGE_PATH, true);
        
        float topLeftCornerY = windowDimensions.y() - GRAPHIC_COUNTER_HEIGHT * 1.5f;
        Vector2 topLeftCorner = new Vector2(BORDER_WIDTH + 10, topLeftCornerY);
        graphicLifeCounter = new GraphicLifeCounter(
                topLeftCorner,
                new Vector2(GRAPHIC_COUNTER_WIDTH, GRAPHIC_COUNTER_HEIGHT),
                lifeCounter,
                graphicImage,
                gameObjects(),
                INITIAL_LIVES);
    }
    
    /*
     * Creating the numeric counter object and add it to the game window
     */
    private void createNumericCounters() {
        float topLeftCornerY = windowDimensions.y() - NUMERIC_COUNTER_HEIGHT * 1.5f;
        float topLeftCornerX = windowDimensions.x() - BORDER_WIDTH * 2 - NUMERIC_COUNTER_WIDTH;
         numericLifeCounter = new NumericLifeCounter(
                lifeCounter,
                new Vector2(topLeftCornerX, topLeftCornerY),
                new Vector2(NUMERIC_COUNTER_WIDTH, NUMERIC_COUNTER_HEIGHT),
                gameObjects());
    }
    
    /*
     * Creating the borders objects and add them to the game window
     */
    private void createBorders(Vector2 windowDimensions) {
        //left border
        GameObject border = new GameObject(Vector2.ZERO, new Vector2(BORDER_WIDTH, windowDimensions.y()),
                null);
        border.setTag("Border");
        gameObjects().addGameObject(border, Layer.STATIC_OBJECTS);
        
        //right border
        border = new GameObject(new Vector2(windowDimensions.x() - BORDER_WIDTH, 0),
                new Vector2(BORDER_WIDTH, windowDimensions.y()), null);
        border.setTag("Border");
        gameObjects().addGameObject(border, Layer.STATIC_OBJECTS);
        
        //upper border
        border = new GameObject(Vector2.ZERO, new Vector2(windowDimensions.x(), BORDER_WIDTH), null);
        border.setTag("Border");
        gameObjects().addGameObject(border, Layer.STATIC_OBJECTS);
    }
    
    /*
     * Creating the background object and add it to the game window
     */
    private void createBackground(ImageReader imageReader){
        Renderable backgroundImage = imageReader.readImage(BACKGROUND_IMAGE_PATH, false);
        GameObject background = new GameObject(Vector2.ZERO,windowDimensions, backgroundImage);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }
}

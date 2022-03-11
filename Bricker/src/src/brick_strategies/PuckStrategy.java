package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.Puck;
import java.util.Objects;
import java.util.Random;

/**
 * Concrete class extending abstract RemoveBrickStrategyDecorator.
 * Introduces several pucks instead of brick once removed.
 * @author eran_turgeman
 */
public class PuckStrategy extends RemoveBrickStrategyDecorator{
    //============================== private constants ==============================
    private static final String MOCK_BALL_IMAGE_PATH = "assets/mockBall.png";
    private static final String BALL_COLLISION_SOUND_PATH = "assets/Bubble5_4.wav";
    private static final int AMOUNT_OF_BALLS = 3;
    private static final int BALL_DEFAULT_RADIUS = 25;
    private static final float BALL_SPEED = 240; // -20% of original ball
    private static Renderable ballImage = null;
    private static Sound collisionSound = null;
    
    //============================== private fields ==============================
    private final GameObjectCollection gameObjects;
    
    //============================== public methods ==============================
    /**
     * Constructor
     * @param toBeDecorated basic strategy implementing CollisionStrategy to be decorated
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param soundReader a SoundReader instance for reading sound clips from files for rendering event sounds
     */
    public PuckStrategy(CollisionStrategy toBeDecorated, ImageReader imageReader, SoundReader soundReader){
        super(toBeDecorated);
        if(ballImage == null){
            ballImage = imageReader.readImage(MOCK_BALL_IMAGE_PATH, true);
        }
        if(collisionSound == null){
            collisionSound = soundReader.readSound(BALL_COLLISION_SOUND_PATH);
        }
        this.gameObjects = toBeDecorated.getGameObjectCollection();
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
        Vector2 puckCenter = new Vector2(thisObj.getDimensions().x()/2, thisObj.getDimensions().y() / 2).
                add(thisObj.getTopLeftCorner());
        Vector2 brickDimensions = getBrickDimensions();
        Vector2 additionVector = new Vector2(brickDimensions.x() / 3 * 0.5f, 0);
        Puck puck;
        for (int i = 0; i < AMOUNT_OF_BALLS; i++) {
           puck = new Puck(Vector2.ZERO, new Vector2(brickDimensions.x() / 3, brickDimensions.x() / 3),
                   ballImage, collisionSound);
           puck.setCenter(puckCenter);
           puck.setTag("Puck");
           super.getGameObjectCollection().addGameObject(puck);
           
            puck.setVelocity(getRandomDownwardsDirection());
            
            puckCenter = puckCenter.add(additionVector);
        }
    }
    
    //============================== private methods ==============================
    /*
     * returns the size of a brick
     */
    private Vector2 getBrickDimensions() {
        for(GameObject gameObject: gameObjects){
            if(Objects.equals(gameObject.getTag(), "Brick")){
                return gameObject.getDimensions();
            }
        }
        return new Vector2(BALL_DEFAULT_RADIUS, BALL_DEFAULT_RADIUS);
    }
    
    /*
     * returns a random diagonal direction to the ball
     */
    private Vector2 getRandomDownwardsDirection(){
        float ballVelX = BALL_SPEED;
        Random rand = new Random();
        if (rand.nextBoolean())
            ballVelX *= -1;
        return new Vector2(ballVelX, BALL_SPEED);
    }
}

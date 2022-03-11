package pepse.world.hell;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;
import java.util.Objects;

/**
 * Represents a FireBall game object
 * @author eran_turgeman, elay_aharoni
 */
public class FireBall extends GameObject {
    //================================== private constants ==================================
    private static final float LIFE_TIME = 3f;
    private static final int RIGHT_ANGLE = 90;
    private static final int LEFT_ANGLE = -90;
    private static final float EXPLOSION_TIME = 0.5f;
    private static final float EXPLOSION_MULT_FACTOR = 10f;
    private static final float TIME_BETWEEN_EXPLOSIONS = 0.07f;
    private static final int MAX_COLLISIONS = 5;
    private static final String FIREBALL_TAG = "fireBall";
    
    private static final Vector2 FIRE_DIMENSIONS = new Vector2(30,60);
    private static final Vector2 RIGHT_VELOCITY = new Vector2(250, 0);
    private static final Vector2 LEFT_VELOCITY = new Vector2(-250, 0);
    
    private static final String FIRE_IMAGE_PATH = "assets/fire.png";
    private static final String EXP2_IMAGE_PATH = "assets/exp2.png";
    private static final String EXP3_IMAGE_PATH = "assets/exp3.png";
    private static final String EXP4_IMAGE_PATH = "assets/exp4.png";
    private static final String EXP5_IMAGE_PATH = "assets/exp5.png";
    private static final String EXP6_IMAGE_PATH = "assets/exp6.png";
    private static final String EXP7_IMAGE_PATH = "assets/exp7.png";
    private static final String[] EXPLOSIONS = new String[] { EXP2_IMAGE_PATH, EXP3_IMAGE_PATH,
            EXP4_IMAGE_PATH, EXP5_IMAGE_PATH, EXP6_IMAGE_PATH, EXP7_IMAGE_PATH};
    
    //================================== private fields ==================================
    private final int layer;
    private int numberOfCollisionsAllowed;
    private float lifeTime;
    private final GameObjectCollection gameObjects;
    private final Renderable explosionClip;
    
    //================================== public methods ==================================
    /**
     * Creates a game object represents a fire-ball
     * @param position position to place the object
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @param gameObjects collection of the objects in the game
     * @param layer layer to insert object at
     * @param avatarXVelocity the horizontal velocity of the avatar "creating" the fire ball
     */
    public static void create(Vector2 position,
                                  ImageReader imageReader,
                                  GameObjectCollection gameObjects,
                                  int layer,
                                  float avatarXVelocity) {
        Renderable fireBallImage = imageReader.readImage(FIRE_IMAGE_PATH, true);

        Renderable explosionImage = new AnimationRenderable(EXPLOSIONS, imageReader, true,
                TIME_BETWEEN_EXPLOSIONS);
        
        if (avatarXVelocity != 0) {
            FireBall ball = new FireBall(position, FIRE_DIMENSIONS, fireBallImage,
                    gameObjects, layer, explosionImage);
            ball.setTag(FIREBALL_TAG);
            
            if (avatarXVelocity > 0) {
                ball.setVelocity(RIGHT_VELOCITY);
                ball.renderer().setRenderableAngle(RIGHT_ANGLE);
                gameObjects.addGameObject(ball, layer);
            } else {
                ball.setVelocity(LEFT_VELOCITY);
                ball.renderer().setRenderableAngle(LEFT_ANGLE);
                gameObjects.addGameObject(ball, layer);
            }
        }
    }
    
    /**
     * Updates the game's frame
     * @param deltaTime The time elapsed, in seconds, since the last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.lifeTime -= deltaTime;
        if(lifeTime <= 0){
            this.lifeTime = LIFE_TIME;
            gameObjects.removeGameObject(this, layer);
        }
    }
    
    /**
     * performing the necessary actions and checks required upon a collision
     * @param other the object it collides with
     * @param collision Collision instance, providing details on the collision event
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        
        if((Objects.equals(other.getTag(), Tree.STUMP_TAG) ||
                Objects.equals(other.getTag(), Leaf.LEAF_TAG) ||
                Objects.equals(other.getTag(), HellBat.BAT_TAG))
                && numberOfCollisionsAllowed > 0){
            numberOfCollisionsAllowed -= 1;
            this.renderer().setRenderable(explosionClip);
            this.setVelocity(Vector2.ZERO);
            new Transition<Vector2>(
                    this,
                    this::setDimensions,
                    this.getDimensions(),
                    this.getDimensions().mult(EXPLOSION_MULT_FACTOR),
                    Transition.LINEAR_INTERPOLATOR_VECTOR,
                    EXPLOSION_TIME,
                    Transition.TransitionType.TRANSITION_ONCE,
                    ()->gameObjects.removeGameObject(this, layer));
            gameObjects.removeGameObject(other, PepseGameManager.TREES_LAYER);
            
            if(Objects.equals(other.getTag(), HellBat.BAT_TAG)){
                HellBat.increaseCounter();
            }
        }
        

    }
    
    /*
     * Private constructor. all creations of this game object from outside this class should be done with
     * the create function
     */
    private FireBall(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                     GameObjectCollection gameObjects, int layer, Renderable explosion) {
        super(topLeftCorner, dimensions, renderable);
        this.lifeTime = LIFE_TIME;
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.explosionClip = explosion;
        this.numberOfCollisionsAllowed = MAX_COLLISIONS;
    }
    
}

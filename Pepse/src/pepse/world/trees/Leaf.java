package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.Block;
import pepse.world.Terrain;
import java.util.Random;

/**
 * this class represents a leaf object in the game.
 * @author eran_turgeman, elay_aharoni
 */
public class Leaf extends GameObject {
    public static final String LEAF_TAG = "leaf";
    public static final String FALLING_LEAF_TAG = "fallingLeaf";
    
    private static final Vector2 leafDimensions = new Vector2(Block.SIZE - 1, Block.SIZE - 1);
    private static final Vector2 smallLeafDimensions = new Vector2(Block.SIZE - 4, Block.SIZE - 4);
    private static final float INITIAL_DEGREE = 170;
    private static final float FINAL_DEGREE = 190;
    private static final float ANGLE_CHANGE_TRANSITION_TIME = 1.5f;
    private static final float SIZE_CHANGE_TRANSITION_TIME =2f;
    private static final int MAX_LIFE_TIME = 80;
    private static final int MAX_DEATH_TIME = 10;
    private static final float FALLING_SPEED = 20;
    private static final float FADEOUT_TIME = FALLING_SPEED / 3;
    private static final float INITIAL_HORIZONTAL_VELOCITY = -30f;
    private static final float FINAL_HORIZONTAL_VELOCITY = 30f;
    private static final float LEAF_STOP_MOVEMENT_WAIT_TIME = 0.02f;
    private static final float HORIZONTAL_MOVEMENT_TIME = 1f;
    private static final float HORIZONTAL_MOVEMENT_START_DELAY_TIME = 0.2f;
    private static Random random = null;
    
    private final int leafLayer;
    private final int fallingLeafLayer;
    
    private Vector2 leafStartingTopLeftCorner;
    private final float delayTime;
    private Transition<Float> horizontalTransition;
    private final GameObjectCollection gameObjects;
    
    public static void create(GameObjectCollection gameObjects,
                                    int layer,
                                    Vector2 topLeftCorner,
                                    RectangleRenderable leafImage,
                                    int randomSeed){
        if(Leaf.random == null){
            Leaf.random = new Random(randomSeed);
        }
        
        Leaf leaf = new Leaf(topLeftCorner, leafDimensions, leafImage, gameObjects, layer);
        
        leaf.setTag(LEAF_TAG);
        leaf.saveTopLeftCorner(topLeftCorner);
        gameObjects.addGameObject(leaf, layer);
        Leaf.setTransitionsStart(leaf);
    }
    
    /*
     * Construct a new GameObject instance.
     */
    private Leaf(Vector2 topLeftCorner,
                 Vector2 dimensions,
                 Renderable renderable,
                 GameObjectCollection gameObjects,
                 int layer) {
        super(topLeftCorner, dimensions, renderable);
        this.delayTime = random.nextFloat();
        this.gameObjects = gameObjects;
        this.leafLayer = layer;
        this.fallingLeafLayer = layer + PepseGameManager.FALLING_LEAF_ADDITION;
    }
    
    private void saveTopLeftCorner(Vector2 topLeftCorner){
        this.leafStartingTopLeftCorner = topLeftCorner;
    }
    
    private static void setTransitionsStart(Leaf leaf){
        // angle rotation
        new ScheduledTask(leaf,
                leaf.delayTime,
                false,
                leaf::setAngleTransition);
        
        // size change
        new ScheduledTask(leaf,
                leaf.delayTime,
                false,
                leaf::setSizeTransition);
        
        //fall
        float lifeTime = leaf.delayTime + random.nextInt(MAX_LIFE_TIME);
        new ScheduledTask(leaf,
                lifeTime,
                false,
                leaf::setFallTransition);
        
        new ScheduledTask(leaf,
                lifeTime + HORIZONTAL_MOVEMENT_START_DELAY_TIME,
                false,
                leaf::setHorizontalMovement);
    }
    
    private void setAngleTransition(){
        new Transition<Float>(
                this,
                this.renderer()::setRenderableAngle,
                INITIAL_DEGREE,
                FINAL_DEGREE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                ANGLE_CHANGE_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }
    
    private void setSizeTransition(){
        new Transition<Vector2>(
                this,
                this::setDimensions,
                leafDimensions,
                smallLeafDimensions,
                Transition.LINEAR_INTERPOLATOR_VECTOR,
                SIZE_CHANGE_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }
    
    private void setFallTransition(){
        this.gameObjects.removeGameObject(this, leafLayer);
        this.gameObjects.addGameObject(this, fallingLeafLayer);
        this.setTag(FALLING_LEAF_TAG);
        this.setVelocity(Vector2.DOWN.mult(FALLING_SPEED));
    
        new Transition<>(this,
                this.renderer()::fadeOut,
                FADEOUT_TIME,
                FADEOUT_TIME,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                FADEOUT_TIME,
                Transition.TransitionType.TRANSITION_ONCE,
                this::uponFadeoutEnd);
    }
    
    private void setHorizontalMovement(){
        this.horizontalTransition = new Transition<Float>(
                this,
                this.transform()::setVelocityX,
                INITIAL_HORIZONTAL_VELOCITY,
                FINAL_HORIZONTAL_VELOCITY,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                HORIZONTAL_MOVEMENT_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }
    
    private void uponFadeoutEnd(){
        float deathTime = FADEOUT_TIME + random.nextInt(MAX_DEATH_TIME);
         new ScheduledTask(this,
                 deathTime,
                 false,
                 this::restartLeaf);
    }
    
    private void restartLeaf(){
        this.gameObjects.removeGameObject(this, fallingLeafLayer);
        this.gameObjects.addGameObject(this, leafLayer);
        this.setTag(LEAF_TAG);
        this.setTopLeftCorner(this.leafStartingTopLeftCorner);
        this.renderer().fadeIn(0);
        float lifeTime = random.nextInt(MAX_LIFE_TIME);
        
        new ScheduledTask(this,
                lifeTime,
                false,
                this::setFallTransition);
        
        new ScheduledTask(this,
                lifeTime,
                false,
                this::setHorizontalMovement);
    }
    
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals(Terrain.TOP_TERRAIN_LAYER_TAG)){
            this.removeComponent(this.horizontalTransition);
            new ScheduledTask(this,
                    LEAF_STOP_MOVEMENT_WAIT_TIME,
                    false,
                    ()->this.setVelocity(Vector2.ZERO));
        }
    }
}

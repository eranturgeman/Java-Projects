package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.hell.FireBall;
import pepse.world.hell.HellBat;
import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * Represents the avatar in the game
 * @author eran_turgeman, elay_aharoni
 */
public class Avatar extends GameObject {
    //================================== public constants ==================================
    public static final float AVATAR_SIZE = 70f;
    public static final String AVATAR_TAG = "avatar";
    
    //================================== private constants..... ==================================
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -300;
    private static final float GRAVITY = 400;
    private static final float INITIAL_ENERGY = 100f;
    private static final float FLIGHT_ACCELERATION = 15;
    private static final float MAX_FALL_SPEED_ALLOWED = 1200f;
    private static final float TIME_BETWEEN_CLIPS = 0.15f;
    private static final float ENERGY_DIFFERENCE = 0.5f;
    private static final float FIREBALL_TIME_GAP = 3f;
    private static final Vector2 AVATAR_MOUTH_DECREASE = new Vector2(0, 30);
    private static final Vector2 ADDITION_AFTER_HIT = new Vector2(300,0);
    private static final String IMAGE_RIGHT1 = "assets/right1.png";
    private static final String IMAGE_RIGHT2 = "assets/right2.png";
    private static final String IMAGE_RIGHT3 = "assets/right3.png";
    private static final String IMAGE_RIGHT4 = "assets/right4.png";
    private static final String IMAGE_LEFT1 = "assets/left1.png";
    private static final String IMAGE_LEFT2 = "assets/left2.png";
    private static final String IMAGE_LEFT3 = "assets/left3.png";
    private static final String IMAGE_LEFT4 = "assets/left4.png";
    private static final String IMAGE_FLY1 = "assets/fly1.png";
    private static final String IMAGE_FLY2 = "assets/fly2.png";
    private static final String IMAGE_FLY3 = "assets/fly3.png";
    private static final String IMAGE_FLY4 = "assets/fly4.png";
    private static final String IMAGE_STAND = "assets/standing.png";
    private static final String[] RIGHT_IMAGES =
            new String[] {IMAGE_RIGHT1, IMAGE_RIGHT2, IMAGE_RIGHT3, IMAGE_RIGHT4};
    private static final String[] LEFT_IMAGES =
            new String[] {IMAGE_LEFT1, IMAGE_LEFT2, IMAGE_LEFT3, IMAGE_LEFT4};
    private static final String[] FLY_IMAGES =
            new String[] {IMAGE_FLY1, IMAGE_FLY2, IMAGE_FLY3, IMAGE_FLY4};
    private final UserInputListener inputListener;
    private final Renderable avatarStand;
    private final Renderable avatarFly;
    private final Renderable avatarLeft;
    private final Renderable avatarRight;
    private final ImageReader imageReader;
    private final GameObjectCollection gameObjects;
    private final int layer;
    private final Counter lifeCounter = new Counter(3);
    
    //================================== private fields ==================================
    private float fireBallTIme;
    private float energy;
    private boolean isHell;
    
    //================================== public functions ==================================
    
    /**
     * creates and initialize an Avatar object
     * @param gameObjects collection of the objects in the game
     * @param layer layer to insert object to
     * @param topLeftCorner position for the object
     * @param inputListener an InputListener instance for reading user input.
     * @param imageReader an ImageReader instance for reading images from files for rendering of objects.
     * @return Avatar instance
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer,
                                Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
        Renderable avatarRightImages =
                new AnimationRenderable(RIGHT_IMAGES, imageReader,true, TIME_BETWEEN_CLIPS);
        Renderable avatarLeftImages =
                new AnimationRenderable(LEFT_IMAGES, imageReader,true, TIME_BETWEEN_CLIPS);
        Renderable avatarFlyImages =
                new AnimationRenderable(FLY_IMAGES, imageReader,true, TIME_BETWEEN_CLIPS);
        Renderable avatarStand = imageReader.readImage(IMAGE_STAND, true);
        
        Avatar avatar = new Avatar(topLeftCorner, inputListener, avatarStand, avatarFlyImages,
                avatarLeftImages, avatarRightImages, imageReader, gameObjects, layer);
        
        avatar.setTag(AVATAR_TAG);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }
    
    /**
     * Updates the game's frame
     * @param deltaTime The time elapsed, in seconds, since the last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        //move actions
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)){
            xVel -= VELOCITY_X;
            this.renderer().setRenderable(avatarLeft);
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)){
            xVel += VELOCITY_X;
            this.renderer().setRenderable(avatarRight);
        }
        transform().setVelocityX(xVel);
        
        //stand position
        if(this.getVelocity().x() == 0){
            this.renderer().setRenderable(this.avatarStand);
            this.renderer().setRenderableAngle(0);
        }
        
        //jump
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0){
            transform().setVelocityY(VELOCITY_Y);
        }
        
        //fly
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && this.energy > 0){
            this.setAvatarFly(xVel);
        }
        
        // gain energy
        if(this.getVelocity().y() == 0){
            this.energy += ENERGY_DIFFERENCE;
        }
        
        //limit fall speed
        if(this.getVelocity().y() > MAX_FALL_SPEED_ALLOWED){
            this.transform().setVelocityY(MAX_FALL_SPEED_ALLOWED);
        }
        
        //fireBall
        this.fireBallTIme -= deltaTime;
        if(this.inputListener.isKeyPressed(KeyEvent.VK_ENTER) && fireBallTIme <= 0 && isHell){
            fireBallTIme = FIREBALL_TIME_GAP;
            FireBall.create(new Vector2(this.getCenter().subtract(AVATAR_MOUTH_DECREASE)),
                    this.imageReader ,gameObjects, layer, xVel);
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
        if(Objects.equals(other.getTag(), Terrain.TOP_TERRAIN_LAYER_TAG)){
            this.transform().setVelocityY(0);
        }
        if(Objects.equals(other.getTag(), PepseGameManager.DOOR_TAG)){
            gameObjects.removeGameObject(other);
            this.isHell = true;
        }
        if(Objects.equals(other.getTag(), Terrain.TOP_TERRAIN_LAYER_TAG) &&
                this.getCenter().y() > other.getTopLeftCorner().y()){
            this.setCenter(new Vector2(this.getCenter().x(),
                    other.getTopLeftCorner().y() - AVATAR_SIZE / 2));
        }
        if(Objects.equals(other.getTag(), HellBat.BAT_TAG)){
            if(this.getVelocity().x() > 0){
                this.setCenter(other.getCenter().subtract(ADDITION_AFTER_HIT));
            }else{
                this.setCenter(other.getCenter().add(ADDITION_AFTER_HIT));
            }
            this.decreaseCounter();
        }
    }
    
    /**
     * a 'flag' getter that notifies if the hell door in the game already touched
     * @return true/false
     */
    public boolean isDoorTouched(){
        return this.isHell;
    }
    
    /**
     * helper function to decrease the counter from outside this class
     */
    public void decreaseCounter(){
        lifeCounter.decrement();
    }
    
    /**
     * getter for the current counter's value
     * @return counter's value
     */
    public int getCounterValue(){
        return lifeCounter.value();
    }
    
    //================================== private methods ==================================
    /*
     * private Constructor. this constructor should not be called without the wrapping static method that
     * initialize things for the objects
     */
    private Avatar(Vector2 pos,
                  UserInputListener inputListener,
                  Renderable avatarStand,
                  Renderable avatarFly,
                  Renderable avatarLeft,
                  Renderable avatarRight,
                  ImageReader imageReader,
                  GameObjectCollection gameObjects,
                  int layer) {
        super(pos, Vector2.ONES.mult(AVATAR_SIZE), avatarStand);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        this.energy = INITIAL_ENERGY;
        this.avatarStand = avatarStand;
        this.avatarFly = avatarFly;
        this.avatarLeft = avatarLeft;
        this.avatarRight = avatarRight;
        this.fireBallTIme = 0;
        this.imageReader = imageReader;
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.isHell = false;
    }
    

    /*
     * Helper function that deal with the avatar flight
     */
    private void setAvatarFly(float xVel){
        this.energy -= ENERGY_DIFFERENCE;
        transform().setVelocityY(this.getVelocity().y() - FLIGHT_ACCELERATION);
        this.renderer().setRenderable(avatarFly);
        if(xVel > 0){
            this.renderer().setRenderableAngle(-30);
        }else if(xVel < 0){
            this.renderer().setRenderableAngle(30);
        }else{
            this.renderer().setRenderableAngle(0);
        }
    }
    

    
}

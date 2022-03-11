package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Represents a numeric counter. An extension of danogl.GameObject
 * @author eran_turgeman
 */
public class NumericLifeCounter extends GameObject {
    //================================== private fields ==================================
    private final Counter lifeCounter;
    private final TextRenderable textRenderable;
    
    //================================== public methods ==================================
    /**
     * Constructor
     * @param livesCounter Counter object to count the left lives in the game
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param gameObjectCollection    the objects the current game contains
     */
    public NumericLifeCounter(Counter livesCounter,
                              Vector2 topLeftCorner,
                              Vector2 dimensions,
                              GameObjectCollection gameObjectCollection) {
        super(topLeftCorner, dimensions, null);
        this.lifeCounter = livesCounter;
        this.textRenderable = new TextRenderable(String.format("Lives: %d", lifeCounter.value()));
        textRenderable.setColor(Color.RED);
        gameObjectCollection.addGameObject(
                new GameObject(topLeftCorner, dimensions, textRenderable), Layer.BACKGROUND);
    }
    
    /**
     * updates the presented numeric counter to present the current amount of lives
     * @param deltaTime The time elapsed, in seconds, since the last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        textRenderable.setString(String.format("Lives: %d", lifeCounter.value()));
    }
}

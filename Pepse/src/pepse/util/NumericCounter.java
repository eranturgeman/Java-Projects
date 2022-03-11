package pepse.util;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import java.awt.*;
import java.util.function.IntSupplier;

/**
 * Represents graphic numeric counters in the game
 * @author eran_turgeman, elay_aharoni
 */
public class NumericCounter extends GameObject {
    //================================== private fields ==================================
    private final TextRenderable textRenderable;
    private final String text;
    private final IntSupplier valueGetter;
    
    //================================== public functions ==================================
    
    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public NumericCounter(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                          GameObjectCollection gameObjects, String toPresent, IntSupplier valueGetter) {
        super(topLeftCorner, dimensions, renderable);
        this.textRenderable = new TextRenderable(String.format(toPresent + ": %d", valueGetter.getAsInt()));
        textRenderable.setColor(Color.WHITE);
        this.text = toPresent;
        this.valueGetter = valueGetter;
        GameObject counter = new GameObject(topLeftCorner, dimensions, textRenderable);
        counter.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(counter, Layer.FOREGROUND);
    }
    
    /**
     * updates the presented numeric counter to present the current amount of lives
     * @param deltaTime The time elapsed, in seconds, since the last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        textRenderable.setString(String.format(text + ": %d", valueGetter.getAsInt()));
    }
    
    /**
     * returns the counter's value
     * @return counter's value
     */
    public int getValue(){
        return this.valueGetter.getAsInt();
    }
}

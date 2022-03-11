package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Represents the sun in the game
 * @author eran_turgeman, elay_aharoni
 */
public class Sun {
    //================================== private constants ==================================
    
    private static final int SUN_SIZE = 120;
    private static final float SUN_INITIAL_DEGREE = -90f;
    private static final float SUN_END_DEGREE = 270f;
    private static final float OVAL_Y_STRETCH_FACTOR_DIVIDER = 2.5f;
    private static final float OVAL_X_STRETCH_FACTOR_DIVIDER = 2.3f;
    private static final double RADIANS_CONVERSION_FACTOR = Math.PI / 180;
    private static final float SUN_CENTER_Y_DIVISION_FACTOR = 1.8f;
    private static final String SUN_TAG = "sun";
    
    
    //================================== public methods ==================================
    /**
     * Creating a Sun object
     * @param gameObjects a collection of the game's current objects
     * @param layer layer to insert object at
     * @param windowDimensions size of the window
     * @param cycleLength length in seconds to make a full elliptic cycle
     * @return a game object represents the sun
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer,
                                    Vector2 windowDimensions,
                                    float cycleLength){
        OvalRenderable sunImage = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(Vector2.ZERO, new Vector2(SUN_SIZE, SUN_SIZE), sunImage);
        sun.setCenter(new Vector2(windowDimensions.x() / 2, SUN_SIZE / SUN_CENTER_Y_DIVISION_FACTOR));
        Vector2 roundAxisCenter = new Vector2(windowDimensions.x() / 2, windowDimensions.y() / 2);
        float xStretchFactor = windowDimensions.x() / OVAL_X_STRETCH_FACTOR_DIVIDER;
        float yStretchFactor = windowDimensions.y() / OVAL_Y_STRETCH_FACTOR_DIVIDER;
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);
        gameObjects.addGameObject(sun, layer);
    
        new Transition<>(
                sun,
                (deg)-> sun.setCenter(
                        new Vector2(
                                roundAxisCenter.x() + xStretchFactor * (float)Math.cos(deg * RADIANS_CONVERSION_FACTOR),
                        roundAxisCenter.y() + yStretchFactor * (float) Math.sin(deg * RADIANS_CONVERSION_FACTOR))),
                SUN_INITIAL_DEGREE,
                SUN_END_DEGREE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        return sun;
    }
}

package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Represents the night game object
 * @author eran_turgeman, elay_aharoni
 */
public class Night {
    //================================== private constants ==================================
    private static final float SUNRISE_OPACITY = 0f;
    private static final float MIDNIGHT_OPACITY = 0.5f;
    private static final String NIGHT_TAG = "night";
    
    //================================== public methods ==================================
    
    /**
     * Creating a Night object
     * @param gameObjects a collection of the game's current objects
     * @param layer layer to insert object at
     * @param windowDimensions size of the window
     * @param cycleLength length in seconds to last the day/night cycle
     * @return a game object represents the night
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer,
                                    Vector2 windowDimensions,
                                    float cycleLength){
        RectangleRenderable nightImage = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, nightImage);
        night.setTag(NIGHT_TAG);
        gameObjects.addGameObject(night, layer);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    
       new Transition<>(night,
               night.renderer()::setOpaqueness,
               SUNRISE_OPACITY,
               MIDNIGHT_OPACITY,
               Transition.CUBIC_INTERPOLATOR_FLOAT,
               cycleLength / 2,
               Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
               null);
        return night;
    }
}

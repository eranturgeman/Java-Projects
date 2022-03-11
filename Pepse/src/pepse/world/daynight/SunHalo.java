package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Represents the sun halo in the game
 * @author eran_turgeman, elay_aharoni
 */
public class SunHalo {
    //================================== private constants ==================================
    private static final float HALO_MULT_FACTOR = 3f;
    private static final String HALO_TAG = "sunHalo";
    
    //================================== public methods ==================================
    /**
     * Creating a SunHalo object
     * @param gameObjects a collection of the game's current objects
     * @param layer layer to insert object at
     * @param sun the sun the halo attached to
     * @param color color for the sun halo
     * @return a game object represents the sun halo
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer,
                                    GameObject sun,
                                    Color color){
        OvalRenderable sunHaloImage = new OvalRenderable(color);
        GameObject sunHalo = new GameObject(Vector2.ZERO,
                new Vector2(sun.getDimensions().mult(HALO_MULT_FACTOR)),
                sunHaloImage);
        sunHalo.setCenter(new Vector2(sun.getCenter()));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(HALO_TAG);
        sunHalo.addComponent((deltaTime)->sunHalo.setCenter(sun.getCenter()));
        gameObjects.addGameObject(sunHalo, layer);
        return sunHalo;
    }
}

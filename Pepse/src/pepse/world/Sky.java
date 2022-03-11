package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class that represents the sky of the game
 * @author eran_turgeman, elay_aharoni
 */
public class Sky {
    //================================== private constants ==================================
    private static final Color BASIC_SKY_COLOR = Color.decode("#80c6E5");
    private static final String SKY_TAG = "sky";
    
    //================================== public functions ==================================ccccccc
    /**
     * method to create the sky of the game
     * @param gameObjects - collection of the objects in the game
     * @param windowDimensions - the window dimensions
     * @param skyLayer - the layer to insert the sky to
     * @return - a new sky object
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    Vector2 windowDimensions,
                                    int skyLayer){
        GameObject sky = new GameObject(
                Vector2.ZERO,
                windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY_TAG);
        gameObjects.addGameObject(sky, skyLayer);
        
        return sky;
    }
    
    
}

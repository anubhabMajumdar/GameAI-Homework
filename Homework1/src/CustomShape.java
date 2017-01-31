import processing.core.PApplet;
import processing.core.PImage;

/**
 * Created by anubhabmajumdar on 1/30/17.
 */
public class CustomShape {

    PApplet pApplet;
    PImage pImage;
    float width, height;

    CustomShape(PApplet p, String imageName)
    {
        pApplet = p;
        pImage = pApplet.loadImage(imageName);
        width = 60;
        height = 40;
    }

    public void drawCustomShape(float x, float y)
    {
        pApplet.image(pImage, x, y, width, height);
    }

}

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by anubhabmajumdar on 1/30/17.
 */
public class CustomShape {

    PApplet pApplet;
    PImage pImage;
    float width, height;
    float orientation;
    ArrayList arrayList;

    CustomShape(PApplet p, String imageName)
    {
        pApplet = p;
        pImage = pApplet.loadImage(imageName);
        width = 60;
        height = 40;
        orientation = 0;
        arrayList = new ArrayList();
    }

    public void setOrientation(float o)
    {
        orientation = o;
    }

    public void drawCustomShape(float x, float y)
    {
        drawCircleAroundShape(x, y);

        pApplet.pushMatrix();
        pApplet.translate(x+width/2,y+height/2);
        pApplet.rotate(orientation);
        pApplet.image(pImage, -width/2, -height/2, width, height);
        pApplet.popMatrix();
    }

    public void drawCircleAroundShape(float x, float y)
    {
        arrayList.add(orientation);
        for (int i=0; i<arrayList.size(); i++)
        {
            pApplet.pushMatrix();
            pApplet.translate(x+width/2,y+height/2);
            pApplet.rotate((float)arrayList.get(i));
            pApplet.ellipse(2*width/3, 2*height/3, 3, 3);
            pApplet.popMatrix();
        }
    }

    public void getBB()
    {

    }

}

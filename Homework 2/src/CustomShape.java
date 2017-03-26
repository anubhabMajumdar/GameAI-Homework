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
    ArrayList<PVector> breadCrumbs;

    CustomShape(PApplet p, String imageName, float w, float h)
    {
        pApplet = p;
        pImage = pApplet.loadImage(imageName);
        width = w;
        height = h;
        arrayList = new ArrayList();
        breadCrumbs = new ArrayList<PVector>();
    }

    public void setOrientation(float o)
    {
        orientation = o;
    }

    public void drawCustomShape(float x, float y)
    {
        //drawCircleAroundShape(x, y);
        breadCrumbs.add(new PVector(x, y));

        pApplet.pushMatrix();
        pApplet.translate(x,y);
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
            pApplet.translate(x,y);
            pApplet.rotate((float)arrayList.get(i));
            pApplet.ellipse(2*width/3, 2*height/3, 3, 3);
            pApplet.popMatrix();
        }
    }

    public float getRadius()
    {
        return (float)((Math.sqrt(Math.pow(width,2) + Math.pow(height,2)))/2);
    }

    public PVector getVectorFromOrientation()
    {
        return (new PVector(pApplet.sin(orientation),  pApplet.cos(orientation)));
    }

    public void drawBreadcrumbs()
    {
        //System.out.println(breadCrumbs.size());
        for (int i=0; i<breadCrumbs.size(); i++)
        {
            if (i%20==0)
            {
                pApplet.fill(pApplet.random(0,255));
                pApplet.ellipse(breadCrumbs.get(i).x, breadCrumbs.get(i).y, 3, 3);
            }
        }
    }

}

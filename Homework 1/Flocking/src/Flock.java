import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by anubhabmajumdar on 2/8/17.
 */


public class Flock {
    PApplet pApplet;
    int shapes;
    //ArrayList<CustomShape> customShapes;
    CustomShape customShape;
    ArrayList<SteeringClass> steeringObjects;

    Flock(PApplet p, int n, int w, int h)
    {
        pApplet = p;
        shapes = n;
        customShape = new CustomShape(pApplet, "customShape.png", w, h);
        steeringObjects = new ArrayList<SteeringClass>();

        for (int i=0;i<shapes;i++)
        {
            steeringObjects.add(new SteeringClass(pApplet));
            steeringObjects.get(i).setPosition(new PVector(pApplet.random(20, 750),
                                                            pApplet.random(20, 450)));
            steeringObjects.get(i).setOrientation(pApplet.random(-pApplet.PI, pApplet.PI));
//            pApplet.println(pApplet.degrees(steeringObjects.get(i).getOrientation()));
        }
    }

    public void drawFlock()
    {
        for (int i=0;i<shapes;i++)
        {
            customShape.setOrientation(steeringObjects.get(i).getOrientation());
            customShape.drawCustomShape(steeringObjects.get(i).getPosition().x,steeringObjects.get(i).getPosition().y);
        }
    }

}

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by anubhabmajumdar on 2/8/17.
 */


public class Flock {
    PApplet pApplet;
    int shapes, width, height, w, h;
    //ArrayList<CustomShape> customShapes;
    CustomShape customShape;
    ArrayList<SteeringClass> steeringObjects;

    Flock(PApplet p, int n, int w, int h, int frame_w, int frame_h)
    {
        pApplet = p;
        shapes = n;
        width = frame_w;
        height = frame_h;
        this.w = w;
        this.h = h;

        customShape = new CustomShape(pApplet, "customShape.png", w, h);
        steeringObjects = new ArrayList<SteeringClass>();

        for (int i=0;i<shapes;i++)
        {
            steeringObjects.add(new SteeringClass(pApplet));
            steeringObjects.get(i).setPosition(new PVector(pApplet.random(20, frame_w),
                                                            pApplet.random(20, frame_h)));
            steeringObjects.get(i).setOrientation(pApplet.random(-pApplet.PI, pApplet.PI));
            steeringObjects.get(i).setAcceleration(new PVector(pApplet.random(-0.5f, 0.05f),
                                                    pApplet.random(-0.05f, 0.05f)));
            steeringObjects.get(i).setVelocity(new PVector(pApplet.random(-1, 1), pApplet.random(-1, 1)));

            //            pApplet.println(pApplet.degrees(steeringObjects.get(i).getOrientation()));
        }
    }

    public void drawFlock()
    {
        for (int i=0;i<shapes;i++)
        {
            customShape.setOrientation(steeringObjects.get(i).getOrientationFromVector());
            customShape.drawCustomShape(steeringObjects.get(i).getPosition().x,steeringObjects.get(i).getPosition().y);

            steeringObjects.get(i).update(1);
            handleCollision(steeringObjects.get(i));
        }
    }

    public void handleCollision(SteeringClass s)
    {
        float radius = (float) (Math.sqrt(Math.pow(w,2) + Math.pow(h,2)))/2;

        float x = s.getPosition().x;
        float y = s.getPosition().y;

        if (((x-radius)>width) && (s.getVelocity().x>0))
        {
            //s.setVelocity(convolution(s.getVelocity(), new PVector(-1, 1)));
            s.setPosition(new PVector(0, s.getPosition().y));
        }
        else if (((x+radius)<0) && (s.getVelocity().x<0))
        {
            s.setPosition(new PVector(width, s.getPosition().y));
        }

        else if (((y-radius)>height) && (s.getVelocity().y>0))
        {
            //s.setVelocity(convolution(s.getVelocity(), new PVector(1, -1)));
            s.setPosition(new PVector(s.getPosition().x, 0));
        }
        else if (((y+radius)<0) && (s.getVelocity().y<0))
        {
            s.setPosition(new PVector(s.getPosition().x, height));
        }
    }

}

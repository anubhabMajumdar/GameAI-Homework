import processing.core.*;

import java.util.ArrayList;

/**
 * Created by anubhabmajumdar on 1/30/17.
 */
public class KinematicMotion extends PApplet {

    CustomShape shape;
    SteeringClass s;
    float w, h;
    ArrayList<PVector> breadCrumbs;

    public void settings(){
        size(600,400);
    }

    public void setup(){
        w = 40;
        h = 30;
        breadCrumbs = new ArrayList<PVector>();

        shape = new CustomShape(this, "customShape.png", w, h);
        s = new SteeringClass(this);

        s.setPosition(new PVector(2*w/3,height-2*h/2));
        s.setVelocity(new PVector(3.2f,0.0f));
        //s.setAcceleration(new PVector(0.02f,0.03f));
        s.setOrientation(radians(0));
        //s.setRotation(0.01f);
    }

    public void draw()
    {
        background(255);
        breadCrumbs.add(new PVector(s.getPosition().x, s.getPosition().y));

        shape.setOrientation(s.getOrientation());
        shape.drawCustomShape(s.getPosition().x,s.getPosition().y);
        drawBreadcrumbs();

        specialHandleCollision();

        s.update(1);

    }

    public static void main(String[] args)
    {
        PApplet.main("KinematicMotion");
    }


    public void specialHandleCollision()
    {
        float radius = (float) (Math.sqrt(Math.pow(w,2) + Math.pow(h,2)))/2;

        float x = s.getPosition().x;
        float y = s.getPosition().y;

        if ((((x+radius)>width) && (s.getVelocity().x>0)) || ((x-radius)<0) && (s.getVelocity().x<0))
        {
            s.setOrientation(s.getOrientation() + radians(-90));
            s.setVelocity(new PVector(s.getVelocity().y, -s.getVelocity().x));
        }

        if ((((y+radius)>height) && (s.getVelocity().y>0)) || ((y-radius)<0) && (s.getVelocity().y<0))
        {
            s.setOrientation(s.getOrientation() + radians(-90));
            s.setVelocity(new PVector(s.getVelocity().y, -s.getVelocity().x));
        }
    }


    public void handleCollision()
    {
        float radius = (float) (Math.sqrt(Math.pow(w,2) + Math.pow(h,2)))/2;

        float x = s.getPosition().x;
        float y = s.getPosition().y;

        if ((((x+radius)>width) && (s.getVelocity().x>0)) || ((x-radius)<0) && (s.getVelocity().x<0))
            s.setVelocity(convolution(s.getVelocity(), new PVector(-1, 1)));

        if ((((y+radius)>height) && (s.getVelocity().y>0)) || ((y-radius)<0) && (s.getVelocity().y<0))
            s.setVelocity(convolution(s.getVelocity(), new PVector(1, -1)));
    }

    public PVector convolution(PVector a, PVector b)
    {
        return (new PVector(a.x * b.x, a.y * b.y));
    }

    public void drawBreadcrumbs()
    {
        //System.out.println(breadCrumbs.size());
        for (int i=0; i<breadCrumbs.size(); i++)
        {
            if (i%10==0)
            {
                fill(random(0,255));
                ellipse(breadCrumbs.get(i).x, breadCrumbs.get(i).y, 10, 10);
            }
        }
    }

}

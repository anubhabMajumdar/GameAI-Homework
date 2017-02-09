import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anubhabmajumdar on 2/1/17.
 */
public class DriverProgram extends PApplet {

    Flock flock;
    SteeringClass s;
    float w, h;
    Wander wander;
    PVector target;
    float targetOrientation;
    int startTime;
    MovementAlgorithms movementAlgorithms;

    public void settings(){
        size(800,500);
    }

    public void setup(){
        w = 30;
        h = 40;

        flock = new Flock(this, 20, 10, 20);

    }

    public void draw()
    {
        background(255);

        flock.drawFlock();

    }

    public void handleCollision()
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


    public PVector convolution(PVector a, PVector b)
    {
        return (new PVector(a.x * b.x, a.y * b.y));
    }

    public float getOrientationFromVector(PVector p)
    {
        return atan2(-1 * p.x , p.y);
    }

    public static void main(String[] args)
    {
        PApplet.main("DriverProgram");
    }
}

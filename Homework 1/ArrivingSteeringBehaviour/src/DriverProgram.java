import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anubhabmajumdar on 2/1/17.
 */
public class DriverProgram extends PApplet {

    CustomShape shape;
    SteeringClass s;
    float w, h;
    MovementAlgorithms movementAlgorithms;
    PVector target;
    float targetOrientation;

    public void settings(){
        size(600,400);
    }

    public void setup(){
        w = 30;
        h = 40;
        movementAlgorithms = new MovementAlgorithms(this);
        target = new PVector(0,0);
        targetOrientation = radians(0);

        shape = new CustomShape(this, "customShape.png", w, h);
        s = new SteeringClass(this);

        s.setPosition(new PVector(100,100));
        //s.setVelocity(new PVector(3.2f,0.0f));
        //s.setAcceleration(new PVector(0.02f,0.03f));
        s.setOrientation(radians(0));
        //s.setRotation(-0.01f);
    }

    public void draw()
    {
        background(255);
        if (mousePressed)
        {
            target = new PVector(mouseX,mouseY);
            targetOrientation = getOrientationFromVector(target.sub(s.getPosition()));
            println(degrees(getOrientationFromVector(target)));
        }
        movementAlgorithms.align(s, targetOrientation);
        //println(degrees(s.getOrientation()));
        shape.setOrientation(s.getOrientation());
        shape.drawCustomShape(s.getPosition().x,s.getPosition().y);
        //fill(255);
        //ellipse(s.getPosition().x, s.getPosition().y, 10, 10);
        //line(s.getPosition().x, s.getPosition().y, mouseX, mouseY);

        //shape.drawBreadcrumbs();

        s.update(1);

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

import processing.core.*;

/**
 * Created by anubhabmajumdar on 1/30/17.
 */
public class KinematicMotion extends PApplet {

    CustomShape shape;
    SteeringClass s;

    public void settings(){
        size(600,400);
    }

    public void setup(){
        shape = new CustomShape(this, "customShape.png");
        s = new SteeringClass(this);

        s.setPosition(new PVector(100,100));
        //s.setVelocity(new PVector(3.2f,2.1f));
        s.setAcceleration(new PVector(0.02f,0.03f));
        s.setOrientation(radians(0));
        s.setRotation(0.01f);

    }

    public void draw()
    {
        background(255);
        shape.setOrientation(s.getOrientation());
        shape.drawCustomShape(s.getPosition().x,s.getPosition().y);
        s.update(1);
    }

    public static void main(String[] args)
    {
        PApplet.main("KinematicMotion");
    }

}

import processing.core.PApplet;

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
        background(255);
    }

    public void draw()
    {
        shape.drawCustomShape(100, 100);
    }

    public static void main(String[] args)
    {
        PApplet.main("KinematicMotion");
    }

}

import processing.core.*;

/**
 * Created by anubhabmajumdar on 1/30/17.
 */
public class KinematicMotion extends PApplet {

    CustomShape shape;
    SteeringClass s;
    float w, h;

    public void settings(){
        size(600,400);
    }

    public void setup(){
        w = 40;
        h = 30;
        shape = new CustomShape(this, "customShape.png", w, h);
        s = new SteeringClass(this);

        s.setPosition(new PVector(100,100));
        s.setVelocity(new PVector(3.2f,2.1f));
        s.setAcceleration(new PVector(0.02f,0.03f));
        s.setOrientation(radians(0));
        s.setRotation(0.01f);

    }

    public void draw()
    {
        background(255);
        shape.setOrientation(s.getOrientation());
        shape.drawCustomShape(s.getPosition().x,s.getPosition().y);
        handleCollision();
        s.update(1);
    }

    public static void main(String[] args)
    {
        PApplet.main("KinematicMotion");
    }

    public void handleCollision()
    {
        float radius = (float) (Math.sqrt(Math.pow(w,2) + Math.pow(h,2)))/2;

        float x = s.getPosition().x;
        float y = s.getPosition().y;

        if (((x+radius)>=width) || (x-radius)<=0)
            s.setVelocity(convolution(s.getVelocity(), new PVector(-1, 1)));

        if (((y+radius)>=height) || (y-radius)<=0)
            s.setVelocity(convolution(s.getVelocity(), new PVector(1, -1)));
    }

    public PVector convolution(PVector a, PVector b)
    {
        return (new PVector(a.x * b.x, a.y * b.y));
    }

}

import processing.core.*;

/**
 * Created by anubhabmajumdar on 1/30/17.
 * Referenced the book "AI for games, 2nd edition" by Ian Millington and John Funge
 * Referenced the algorithms from class notes
 */

public class SteeringClass {

    private PApplet pApplet;
    private PVector position;
    private float orientation;
    private PVector velocity;
    private float rotation;
    private PVector acceleration;
    private float angular_acc;

    float maxRot, maxSpeed, maxAcc, maxAngAcc;
    PVector maxVel;

    SteeringClass(PApplet p)
    {
        pApplet = p;
        position = new PVector(0,0);
        orientation = 0;
        velocity = new PVector(0,0);
        rotation = 0;
        acceleration = new PVector(0,0);
        angular_acc = 0;

        maxRot = 0.05f;
        maxSpeed = 1;
        maxAcc = 0.001f;
        maxVel = new PVector(1,1);
        maxAngAcc = 0.1f;
    }

    public void setPosition(PVector pos)
    {
        position = pos;
    }

    public PVector getPosition()
    {
        return position;
    }

    public void setVelocity(PVector vel)
    {
        velocity = vel;
    }

    public PVector getVelocity()
    {
        return velocity;
    }

    public void setAcceleration(PVector acc)
    {
        acceleration = acc;
    }

    public PVector getAcceleration()
    {
        return acceleration;
    }

    public void setOrientation(float o)
    {
        orientation = o;
    }

    public float getOrientation()
    {
        return orientation;
    }

    public void setRotation(float r)
    {
        rotation = r;
    }

    public float getRotation()
    {
        return rotation;
    }

    public void setAngularAcc(float a)
    {
        angular_acc = a;
    }

    public float getAngularAcc()
    {
        return angular_acc;
    }

    public float getOrientationFromVector()
    {
        if (velocity.mag() > 0)
            return pApplet.atan2(-1 * velocity.x , velocity.y);
        else
            return orientation;
    }

    public PVector getVectorFromOrientation()
    {
        return (new PVector(pApplet.sin(orientation),  pApplet.cos(orientation)));
    }

    public void update(float time)
    {
        // Update position and orientation
        position.add(velocity.mult(time));
        orientation += rotation * time;
        orientation = orientation%(2*pApplet.PI);
        // Update velocity and rotation
        velocity.add(acceleration.mult(time));
        rotation += angular_acc * time;

        if (Math.abs(rotation)>maxRot)
            rotation = maxRot * (rotation/Math.abs(rotation));

        if (velocity.mag()>maxSpeed)
        {
            velocity = velocity.normalize().mult(maxSpeed);
        }

        if (Math.abs(angular_acc)>maxAngAcc)
            angular_acc = maxAngAcc*(angular_acc/Math.abs(angular_acc));

    }

}

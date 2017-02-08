import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anubhabmajumdar on 2/3/17.
 */
public class Wander {

    private PApplet pApplet;
    float wanderOffset, wanderRadius, wanderRate, wanderOrientation;
    MovementAlgorithms movementAlgorithms;

    Wander(PApplet p)
    {
        pApplet = p;

        wanderOffset = 100;
        wanderRadius = 100;
        wanderRate = pApplet.PI;

        movementAlgorithms = new MovementAlgorithms(pApplet);
    }


    public PVector wanderAlgo(SteeringClass character)
    {
//        pApplet.println("wanderAlgo");

        wanderOrientation = wanderOrientation + (pApplet.random(-1,1) * wanderRate);
        float targetOrientation = character.getOrientation() + wanderOrientation;

        PVector target = PVector.add(character.getPosition(), character.getVectorFromOrientation().mult(wanderOffset));
        target.add(getVectorFromOrientation(targetOrientation).mult(wanderRadius));

//        pApplet.println(getOrientationFromVector(target));
//        pApplet.println(character.getOrientation());
//        pApplet.println(target.mag());

//        movementAlgorithms.align(character, getOrientationFromVector(PVector.sub(target, character.getPosition())));
//        movementAlgorithms.arrive(character, target);

//        movementAlgorithms.seek(character, target);

        return target;
    }

    public PVector convolution(PVector a, PVector b)
    {
        return (new PVector(a.x * b.x, a.y * b.y));
    }

    public PVector getVectorFromOrientation(float orientation)
    {
        return (new PVector(pApplet.sin(orientation),  pApplet.cos(orientation)));
    }

    public float getOrientationFromVector(PVector velocity)
    {
        return pApplet.atan2(-1 * velocity.x , velocity.y);
    }

}

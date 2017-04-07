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

        wanderOffset = 50;
        wanderRadius = 50;
        wanderRate = pApplet.PI/3;

        movementAlgorithms = new MovementAlgorithms(pApplet);
    }


    public PVector wanderAlgo(SteeringClass character)
    {
        float randomNumber = pApplet.random(-1,1);
//        pApplet.println(randomNumber);
//        if (pApplet.random(0,1)>0.7)
//            randomNumber = randomNumber * -1;

        wanderOrientation = wanderOrientation + randomNumber * wanderRate;
        float targetOrientation = character.getOrientation() + wanderOrientation;

        PVector target = PVector.add(character.getPosition(), character.getVectorFromOrientation().mult(wanderOffset));
        target.add(getVectorFromOrientation(targetOrientation).mult(wanderRadius));


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

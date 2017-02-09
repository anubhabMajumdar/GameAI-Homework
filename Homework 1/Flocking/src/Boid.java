import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anubhabmajumdar on 2/8/17.
 */
public class Boid {

    PApplet pApplet;
    Flock flock;
    MovementAlgorithms movementAlgorithms;
    Boid(PApplet p, Flock f)
    {
        pApplet = p;
        flock = f;

        movementAlgorithms = new MovementAlgorithms(pApplet);
    }

    public void boidAlgo()
    {
        handleColision();
    }

    public void handleColision()
    {
        int len = flock.steeringObjects.size();
        float dist;

        for (int i=0;i<len;i++)
        {
            for (int j=0;j<len;j++)
            {
                dist = PVector.dist(flock.steeringObjects.get(i).getPosition(), flock.steeringObjects.get(j).getPosition());
//                if (dist<1000)
                if ((j!=i) && (dist<200))
                {
                    movementAlgorithms.collisionAvoidance(flock.steeringObjects.get(i), flock.steeringObjects.get(j),
                                                            flock.customShape.getRadius(), flock.customShape.getRadius());
                }

            }
        }
    }
}

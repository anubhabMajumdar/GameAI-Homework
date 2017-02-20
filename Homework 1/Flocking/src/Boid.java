import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anubhabmajumdar on 2/8/17.
 */
public class Boid {

    PApplet pApplet;
    Flock flock;
    MovementAlgorithms movementAlgorithms;
    float threshold;

    Boid(PApplet p, Flock f)
    {
        pApplet = p;
        flock = f;
        threshold = 300;
        movementAlgorithms = new MovementAlgorithms(pApplet);
    }

    public void boidAlgo()
    {
//        velocityMatching(0.05f);
//        cohesion(0.05f);
//        handleColision(1f);

        localVelocityMatching(0.05f);
        localCohesion(0.05f);
        handleColision(1f);

    }

    public void velocityMatching(float w)
    {
        int len = flock.steeringObjects.size();
        PVector avgV = new PVector(0,0);
        PVector reqV;
        float timeToTargetVel=10;

        for (int i=0;i<len;i++)
        {
            avgV = PVector.add(avgV, flock.steeringObjects.get(i).getVelocity());
        }

        avgV.div(flock.shapes);

        for (int i=0;i<len;i++)
        {
            reqV = PVector.sub(avgV, flock.steeringObjects.get(i).getVelocity());
            reqV.div(timeToTargetVel);
            flock.steeringObjects.get(i).setAcceleration(reqV.mult(w));
        }
    }

    public void localVelocityMatching(float w)
    {
        int len = flock.steeringObjects.size();
//        PVector avgV = new PVector(0,0);
        PVector[] avgV = new PVector[len];

        PVector reqV;
        float timeToTargetVel=10;
        float dist;
        int count;

        for (int i=0;i<len;i++)
        {
            count = 0;
            avgV[i] = flock.steeringObjects.get(i).getVelocity();
            for (int j=0;j<len;j++)
            {
                dist = PVector.dist(flock.steeringObjects.get(i).getPosition(), flock.steeringObjects.get(j).getPosition());
                if ((j!=i) && (dist<threshold))
                {
                    avgV[i] = PVector.add(avgV[i], flock.steeringObjects.get(j).getVelocity());
                    count++;
                }
            }
            if (count>0)
                avgV[i].div(count);
        }


        for (int i=0;i<len;i++)
        {
            reqV = PVector.sub(avgV[i], flock.steeringObjects.get(i).getVelocity());
            reqV.div(timeToTargetVel);
            flock.steeringObjects.get(i).setAcceleration(reqV.mult(w));
        }
    }


    public void cohesion(float w)
    {
        int len = flock.steeringObjects.size();
        PVector avgP = new PVector(0,0);

        for (int i=0;i<len;i++)
        {
            avgP = PVector.add(avgP, flock.steeringObjects.get(i).getPosition());
        }

        avgP.div(flock.shapes);

        for (int i=0;i<len;i++)
        {
            movementAlgorithms.align(flock.steeringObjects.get(i),
                                        getOrientationFromVector(PVector.sub(avgP, flock.steeringObjects.get(i).getPosition())));
            movementAlgorithms.arrive(flock.steeringObjects.get(i), avgP);
            flock.steeringObjects.get(i).setAcceleration(flock.steeringObjects.get(i).getAcceleration().mult(w));
        }
    }

    public void localCohesion(float w)
    {
        int len = flock.steeringObjects.size();
//        PVector avgP = new PVector(0,0);
        PVector[] avgP = new PVector[len];
        float dist;
        int count;

        for (int i=0;i<len;i++)
        {
            count = 0;
            avgP[i] = flock.steeringObjects.get(i).getPosition();
            for (int j=0;j<len;j++)
            {
                dist = PVector.dist(flock.steeringObjects.get(i).getPosition(), flock.steeringObjects.get(j).getPosition());
                if ((j!=i) && (dist<threshold))
                {
                    avgP[i] = PVector.add(avgP[i], flock.steeringObjects.get(j).getPosition());
                    count++;
                }
            }
            if (count>0)
                avgP[i].div(count);
        }

        for (int i=0;i<len;i++)
        {
            movementAlgorithms.align(flock.steeringObjects.get(i),
                    getOrientationFromVector(PVector.sub(avgP[i], flock.steeringObjects.get(i).getPosition())));
            movementAlgorithms.arrive(flock.steeringObjects.get(i), avgP[i]);
            flock.steeringObjects.get(i).setAcceleration(flock.steeringObjects.get(i).getAcceleration().mult(w));
        }
    }

    public void handleColision(float w)
    {
        int len = flock.steeringObjects.size();
        float dist;

        for (int i=0;i<len;i++)
        {
            for (int j=0;j<len;j++)
            {
                dist = PVector.dist(flock.steeringObjects.get(i).getPosition(), flock.steeringObjects.get(j).getPosition());
                if ((j!=i) && (dist<70))
                {
                    movementAlgorithms.collisionAvoidance(flock.steeringObjects.get(i), flock.steeringObjects.get(j),
                                                            flock.customShape.getRadius(), flock.customShape.getRadius());
                    flock.steeringObjects.get(i).setAcceleration(flock.steeringObjects.get(i).getAcceleration().mult(w));
                }

            }
        }
    }

    public float getOrientationFromVector(PVector p)
    {
        return pApplet.atan2(-1 * p.x , p.y);
    }
}

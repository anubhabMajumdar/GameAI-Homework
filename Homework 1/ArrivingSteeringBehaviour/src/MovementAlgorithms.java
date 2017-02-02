import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anubhabmajumdar on 2/1/17.
 */

public class MovementAlgorithms {

    PApplet pApplet;
    float align_ROS, align_ROD, align_timeToTargetAcc, arrive_ROS, arrive_ROD, arrive_timeToTargetAcc;

    MovementAlgorithms(PApplet p)
    {
        pApplet = p;

        align_ROS = pApplet.radians(3);
        align_ROD = pApplet.radians(20);
        align_timeToTargetAcc = 10;

        arrive_ROS = 3;
        arrive_ROD = 30;
        arrive_timeToTargetAcc = 10;

    }

    public void arrive(SteeringClass character, PVector targetPos)
    {
        PVector deltaPos = PVector.sub(targetPos, character.getPosition());
        PVector goalSpeed = new PVector(0,0);

        if (deltaPos.mag() < arrive_ROS)
        {
            character.setVelocity(new PVector(0,0));
            character.setAcceleration(new PVector(0,0));
            return;
        }
        else if(deltaPos.mag()>arrive_ROD)
        {
            goalSpeed = character.maxVel;
        }
        else
        {
            goalSpeed = convolution(character.maxVel, new PVector(deltaPos.mag()/arrive_ROD, deltaPos.mag()/arrive_ROD));
        }

        PVector goalVel = convolution(goalSpeed, deltaPos.normalize());
        character.setAcceleration(goalVel.sub(character.getVelocity()).div(arrive_timeToTargetAcc));
    }




    public void align(SteeringClass character, float targetOrientation)
    {
        float deltaOrientation = mapToRange(targetOrientation - character.getOrientation());
        float rotSize = Math.abs(deltaOrientation);
        float goalRot;

        if (rotSize<align_ROS)
        {
            character.setAngularAcc(0);
            character.setRotation(0);
            return;
        }
        else if (rotSize>align_ROD)
        {
            goalRot = character.maxRot;
        }
        else
        {
            goalRot = character.maxRot * (rotSize/align_ROD);
        }

        if (Math.abs(deltaOrientation)>0)
            goalRot = goalRot * (deltaOrientation/Math.abs(deltaOrientation));

        character.setAngularAcc((goalRot-character.getRotation())/align_timeToTargetAcc);

    }

    public float mapToRange(float deltaOrientation)
    {
        deltaOrientation = deltaOrientation % (2*pApplet.PI);
        if (Math.abs(deltaOrientation) <= pApplet.PI)
            return deltaOrientation;
        else
        {
            if (deltaOrientation>pApplet.PI)
                return (deltaOrientation-2*pApplet.PI);
            else
                return (deltaOrientation+2*pApplet.PI);
        }
    }

    public PVector convolution(PVector a, PVector b)
    {
        return (new PVector(a.x * b.x, a.y * b.y));
    }
}

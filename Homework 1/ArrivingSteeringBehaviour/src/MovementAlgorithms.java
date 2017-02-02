import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anubhabmajumdar on 2/1/17.
 */

public class MovementAlgorithms {

    PApplet pApplet;
    float align_ROS, align_ROD, align_timeToTargetAcc;

    MovementAlgorithms(PApplet p)
    {
        pApplet = p;

        align_ROS = pApplet.radians(1);
        align_ROD = pApplet.radians(10);
        align_timeToTargetAcc = 10;

    }

    public void align(SteeringClass character, float targetOrientation)
    {
        float deltaOrientation = mapToRange(targetOrientation - character.getOrientation());
        //pApplet.println(deltaOrientation);
        float rotSize = Math.abs(deltaOrientation);
        float goalRot;

        if (rotSize<align_ROS)
        {
            //goalRot = 0;
            //pApplet.println("in ROS");
            character.setAngularAcc(0);
            character.setRotation(0);
            return;
        }
        else if (rotSize>align_ROD)
        {
            goalRot = character.maxRot;
            //pApplet.println("outside ROD");
        }
        else
        {
            goalRot = character.maxRot * (rotSize/align_ROD);
            //pApplet.println("in ROD");
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
}

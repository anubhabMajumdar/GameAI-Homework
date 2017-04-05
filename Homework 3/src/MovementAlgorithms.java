import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by anubhabmajumdar on 2/1/17.
 */

public class MovementAlgorithms {

    PApplet pApplet;
    float align_ROS, align_ROD, align_timeToTargetAcc, arrive_ROS, arrive_ROD, arrive_timeToTargetAcc;

    MovementAlgorithms(PApplet p)
    {
        pApplet = p;

        align_ROS = pApplet.radians(1);
        align_ROD = pApplet.radians(20);
        align_timeToTargetAcc = 10;

        arrive_ROS = 10;
        arrive_ROD = 50;
        arrive_timeToTargetAcc = 10;



    }

    public void seek(SteeringClass character, PVector targetPos)
    {
        PVector deltaPos = PVector.sub(targetPos, character.getPosition());

        character.setAcceleration(deltaPos.normalize().mult(character.maxAcc));
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

        //pApplet.println(PApplet.degrees(deltaOrientation));

        if (rotSize<align_ROS)
        {
//            pApplet.println("in ros");
            character.setAngularAcc(0);
            character.setRotation(0);
            return;
        }
        else if (rotSize>align_ROD)
        {
//            pApplet.println("outside rod");
            goalRot = character.maxRot;
        }
        else
        {
//            pApplet.println("between rod and ros");
            goalRot = character.maxRot * (rotSize/align_ROD);
        }

        if (Math.abs(deltaOrientation)>0)
            goalRot = goalRot * (deltaOrientation/Math.abs(deltaOrientation));

        character.setAngularAcc((goalRot-character.getRotation())/align_timeToTargetAcc);

    }


    public void collisionAvoidance(SteeringClass character, SteeringClass target, float charRad, float targetRad)
    {
        PVector dp = PVector.sub(character.getPosition(), target.getPosition());
        PVector dv = PVector.sub(character.getVelocity(), target.getVelocity());

        float t = 0;

        if (dv.mag()>0)
        {
            t = -1*PVector.dot(dp, dv)/(dv.magSq());
        }


        boolean colisionFlag = isCollision(character, target, charRad, targetRad, t);

        PVector relativePos;

        if (t>0 && colisionFlag)
        {
            relativePos = PVector.add(dp, PVector.mult(dv, t)).normalize();
            character.setAcceleration(PVector.mult(relativePos, character.maxAcc));
        }
        else if (t==0 && colisionFlag)
        {
            relativePos = PVector.sub(target.getPosition(), character.getPosition()).normalize();
            character.setAcceleration(PVector.mult(relativePos, character.maxAcc));
        }
    }

    public boolean isCollision(SteeringClass character, SteeringClass target, float charRad, float targetRad, float t)
    {
        PVector pc = PVector.add(character.getPosition(), PVector.mult(character.getVelocity(), t));
        PVector pt = PVector.add(target.getPosition(), PVector.mult(target.getVelocity(), t));

        PVector dp_ = PVector.sub(pc, pt);

        return  (dp_.mag()<=(charRad+targetRad));

    }

    public int pathFollowing(SteeringClass character, ArrayList<PVector> path, int lastIndex)
    {
        if (path.size()>0)
        {
            int offset = 3;
            int curindex = nearestPoint(character.getPosition(), path, -1, path.size());
            //int nextPoint = nearestPoint(character.getPosition(), path, curindex+1, offset);
            int nextPoint = curindex + offset;
            if (nextPoint>=path.size())
                nextPoint = path.size()-1;
            align(character, getOrientationFromVector(PVector.sub(path.get(nextPoint), character.getPosition())));
            arrive(character, path.get(nextPoint));
            return nextPoint;

        }
        return lastIndex;

    }

    public int nearestPoint(PVector charPos, ArrayList<PVector> path, int lastIndex, int offset)
    {
        float dist = Float.MAX_VALUE;

        int curIndex=lastIndex;

        int last = lastIndex+offset;
        if (last>path.size())
            last = path.size();

        for (int i=lastIndex+1;i<last;i++)
        {
            float temp = (PVector.sub(path.get(i), charPos)).mag();
            if (temp<dist)
            {
                dist = temp;
                curIndex = i;
            }
        }

        return curIndex;

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

    public PVector getVectorFromOrientation(float orientation)
    {
        return (new PVector(pApplet.sin(orientation),  pApplet.cos(orientation)));
    }

    public float getOrientationFromVector(PVector velocity)
    {
        return pApplet.atan2(-1 * velocity.x , velocity.y);
    }
}

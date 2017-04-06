import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anubhabmajumdar on 2/1/17.
 */
public class DriverClassDT extends PApplet {

    CustomShape shape;
    SteeringClass s;
    float w, h;
    Wander wander;
    PVector target;
    float targetOrientation;
    int startTime;
    MovementAlgorithms movementAlgorithms;
    DecisionTree decisionTree;
    NodeInterface root;

    public void settings(){
        size(600,400);
    }

    public void setup(){
        w = 60;
        h = 80;
        startTime = millis();
//        wander = new Wander(this);

        shape = new CustomShape(this, "legoBatman.png", w, h);
        s = new SteeringClass(this);

        s.setPosition(new PVector(width/2,height/2));
        s.setOrientation(radians(0));
        //s.setAcceleration(new PVector(0.2f, 0.1f));

        target = s.getPosition();

        movementAlgorithms = new MovementAlgorithms(this);

        decisionTree = new DecisionTree(this, shape);
        root = decisionTree.makeTree();

    }

    public void draw()
    {
        background(255);

        if (millis()>startTime+1000)
        {
            startTime = millis();
//            target = wander.wanderAlgo(s);
            decisionTree.traverseDT(root, s);
        }

//        movementAlgorithms.align(s, getOrientationFromVector(PVector.sub(target, s.getPosition())));
//        movementAlgorithms.arrive(s, target);
//        movementAlgorithms.seek(s, target);

//        line(s.getPosition().x, s.getPosition().y, target.x, target.y);

        shape.setOrientation(s.getOrientation());
        shape.drawCustomShape(s.getPosition().x,s.getPosition().y);

//        shape.drawBreadcrumbs();

        handleCollision();
        s.update(1);

//        println(s.getPosition());

    }

    public void handleCollision()
    {
        float radius = (float) (Math.sqrt(Math.pow(w,2) + Math.pow(h,2)))/2;

        float x = s.getPosition().x;
        float y = s.getPosition().y;

        if (((x-radius)>width) && (s.getVelocity().x>0))
        {
            //s.setVelocity(convolution(s.getVelocity(), new PVector(-1, 1)));
            s.setPosition(new PVector(0, s.getPosition().y));
        }
        else if (((x+radius)<0) && (s.getVelocity().x<0))
        {
            s.setPosition(new PVector(width, s.getPosition().y));
        }

        else if (((y-radius)>height) && (s.getVelocity().y>0))
        {
            //s.setVelocity(convolution(s.getVelocity(), new PVector(1, -1)));
            s.setPosition(new PVector(s.getPosition().x, 0));
        }
        else if (((y+radius)<0) && (s.getVelocity().y<0))
        {
            s.setPosition(new PVector(s.getPosition().x, height));
        }
    }


    public PVector convolution(PVector a, PVector b)
    {
        return (new PVector(a.x * b.x, a.y * b.y));
    }

    public float getOrientationFromVector(PVector p)
    {
        return atan2(-1 * p.x , p.y);
    }

    public static void main(String[] args)
    {
        PApplet.main("DriverClassDT");
    }
}

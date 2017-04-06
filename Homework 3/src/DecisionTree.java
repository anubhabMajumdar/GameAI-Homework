import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anubhabmajumdar on 4/4/17.
 */
public class DecisionTree  {

    PApplet pApplet;
    MovementAlgorithms movementAlgorithms;
    CustomShape customShape;

    public DecisionTree(PApplet pApplet, CustomShape customShape) {
       this.pApplet = pApplet;
        movementAlgorithms = new MovementAlgorithms(pApplet);
        this.customShape = customShape;
    }

    public NodeInterface makeTree()
    {
        // get leaves
        NodeInterface leaf1 = new ArriveCenterLeaf(new InternalNode(), null, null);
        NodeInterface leaf2 = new setEverythingToZeroLeaf(new InternalNode(), null, null);
        NodeInterface leaf3 = new highSpeedRotLeaf(new InternalNode(), null, null);
        NodeInterface leaf4 = new wanderLeaf(new InternalNode(), null, null);

        // get internal nodes
        NodeInterface internalNode1 = new MaxRotationCheckNode(new InternalNode(), leaf2, leaf3);
        NodeInterface internalNode2 = new SpeedCheckNode(new InternalNode(), internalNode1, leaf4);
        NodeInterface root = new NearWallCheckNode(new InternalNode(), leaf1, internalNode2);

        return root;
    }

    public void traverseDT(NodeInterface root, SteeringClass steeringClass)
    {
        boolean flag;
        NodeInterface temp = root;
        while (temp != null)
        {
            flag = temp.evaluate(steeringClass);
            if (flag)
                temp = ((InternalNodeInterface)temp).getLeft();
            else
                temp = ((InternalNodeInterface)temp).getRight();
        }
    }


    public class InternalNode implements NodeInterface
    {

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            return false;
        }
    }

    public abstract class InternalNodeInterface implements NodeInterface
    {
        NodeInterface nodeInterface;
        NodeInterface left;
        NodeInterface right;

        public void setLeft(NodeInterface left) {
            this.left = left;
        }

        public void setRight(NodeInterface right) {
            this.right = right;
        }

        public NodeInterface getLeft() {
            return left;
        }

        public NodeInterface getRight() {
            return right;
        }

        public InternalNodeInterface(NodeInterface nodeInterface, NodeInterface left, NodeInterface right)
        {
            this.nodeInterface = nodeInterface;
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass)
        {
            return nodeInterface.evaluate(steeringClass);
        }
    }

    public class SpeedCheckNode extends InternalNodeInterface
    {
        public SpeedCheckNode(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            if (steeringClass.getVelocity().mag() >= steeringClass.maxSpeed)
                return true;    // Accelaration > threshold
            else
                return false;
        }
    }

    public class MaxRotationCheckNode extends InternalNodeInterface
    {
        public MaxRotationCheckNode(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
//            pApplet.println(steeringClass.getRotation());
            if (Math.abs(steeringClass.getRotation()) >= steeringClass.maxRot)
                return true;    // Accelaration > threshold
            else
                return false;
        }
    }

    public class NearWallCheckNode extends InternalNodeInterface
    {
        int thresh;

        public NearWallCheckNode(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
            thresh = 100;
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            //pApplet.println(steeringClass.getAngularAcc());
            float x = steeringClass.getPosition().x;
            float y = steeringClass.getPosition().y;

            if ((x<thresh) || (x>pApplet.width-thresh) || (y<thresh) || (y>pApplet.height-thresh))
            {
                movementAlgorithms.arrive(steeringClass, new PVector(pApplet.width/2, pApplet.height/2));
                return true;
            }

            return false;
        }
    }

    public class ArriveCenterLeaf extends InternalNodeInterface
    {
        public ArriveCenterLeaf(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            pApplet.println("In ArriveCenter\n");
            //pApplet.println(steeringClass.getAngularAcc());
            steeringClass.setVelocity(new PVector(0,0));
            steeringClass.setAcceleration(new PVector(0,0));
            steeringClass.setRotation(0);
            steeringClass.setAngularAcc(0);
            movementAlgorithms.arrive(steeringClass, new PVector(pApplet.width/2, pApplet.height/2));
            return true;
        }
    }

    public class highSpeedRotLeaf extends InternalNodeInterface
    {
        public highSpeedRotLeaf(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            pApplet.println("In rotation\n");

            try
            {
                //steeringClass.setVelocity(new PVector(0,0));
                //steeringClass.setAcceleration(new PVector(0,0));
                steeringClass.setAngularAcc(0.001f);
                //steeringClass.getAcceleration()
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }
    }

    public class setEverythingToZeroLeaf extends InternalNodeInterface
    {
        public setEverythingToZeroLeaf(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {


            try
            {
                pApplet.println("In setEverythingToZeroLeaf\n");
//                steeringClass.setPosition(new PVector(pApplet.width/2, pApplet.height/2));
                if (customShape.getImageName().equals("legoBatman.png"))
                {
                    customShape.setImageName("legoSuperman.png");
                }
                else
                {
                    customShape.setImageName("legoBatman.png");
                }
                customShape.reloadPImage();
                steeringClass.setVelocity(new PVector(0,0));
                steeringClass.setAcceleration(new PVector(0,0));
                steeringClass.setRotation(0);
                steeringClass.setAngularAcc(0);
//                pApplet.println(steeringClass.getAngularAcc());
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }
    }

    public class wanderLeaf extends InternalNodeInterface
    {
        public wanderLeaf(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            pApplet.println("In wander\n");
            try
            {
                //steeringClass.setAcceleration(new PVector(0.01f, 0.01f));
                Wander wander = new Wander(pApplet);
                PVector target = wander.wanderAlgo(steeringClass);

                movementAlgorithms.align(steeringClass, getOrientationFromVector(PVector.sub(target, steeringClass.getPosition())));
                movementAlgorithms.arrive(steeringClass, target);

                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }

        public float getOrientationFromVector(PVector p)
        {
            return pApplet.atan2(-1 * p.x , p.y);
        }
    }




}

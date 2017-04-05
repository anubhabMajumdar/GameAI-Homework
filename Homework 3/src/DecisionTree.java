import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by anubhabmajumdar on 4/4/17.
 */
public class DecisionTree  {

    PApplet pApplet;

    public DecisionTree(PApplet pApplet) {
       this.pApplet = pApplet;
    }

    public NodeInterface makeTree()
    {
        //get left leaf 1
        NodeInterface leftLeaf1 = new highSpeedRotLeaf(new InternalNode(), null, null);

        // get right leaf 1
        NodeInterface rightLeaf1 = new setEverythingToZeroLeaf(new InternalNode(), null, null);

        NodeInterface internalNode1 = new AngularAccCheckNode(new InternalNode(), leftLeaf1, rightLeaf1);

        // get right leaf 2
        NodeInterface rightLeaf2 = new wanderLeaf(new InternalNode(), null, null);

        // get root node
        NodeInterface root = new AccCheckNode(new InternalNode(), internalNode1, rightLeaf2);

        return root;

    }

    public void traverseDT(NodeInterface root, SteeringClass steeringClass)
    {
        boolean flag;

        while (root != null)
        {
            flag = root.evaluate(steeringClass);
            if (flag)
                root = ((InternalNodeInterface)root).getLeft();
            else
                root = ((InternalNodeInterface)root).getRight();
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

    public class AccCheckNode extends InternalNodeInterface
    {
        public AccCheckNode(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            if (steeringClass.getAcceleration().mag() > 0.15)
                return true;    // Accelaration > threshold
            else
                return false;
        }
    }

    public class AngularAccCheckNode extends InternalNodeInterface
    {
        public AngularAccCheckNode(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            //pApplet.println(steeringClass.getAngularAcc());
            if (Math.abs(steeringClass.getRotation()) <= 0.3)
                return true;    // Accelaration > threshold
            else
                return false;
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
            pApplet.println("In setEverythingToZeroLeaf\n");

            try
            {
                steeringClass.setVelocity(new PVector(0,0));
                steeringClass.setAcceleration(new PVector(0,0));
                steeringClass.setAngularAcc(0);
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
                Wander wander = new Wander(pApplet);
                PVector target = wander.wanderAlgo(steeringClass);

                MovementAlgorithms movementAlgorithms = new MovementAlgorithms(pApplet);

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

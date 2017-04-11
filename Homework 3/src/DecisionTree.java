import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by anubhabmajumdar on 4/4/17.
 */
public class DecisionTree  {

    PApplet pApplet;
    MovementAlgorithms movementAlgorithms;
    CustomShape customShape;
    int tileSize, tileCountWidth, tileCountHeight;
    ArrayList<Tile> allTiles;
    Graph roomGraph;
    ArrayList<PVector> path;

    int S1_x, S1_y;
    int R1_x, R1_y;

//    public DecisionTree(PApplet pApplet, CustomShape customShape) {
//       this.pApplet = pApplet;
//        movementAlgorithms = new MovementAlgorithms(pApplet);
//        this.customShape = customShape;
//    }

    public DecisionTree(PApplet pApplet, CustomShape customShape, int tileSize, int tileCountWidth, int tileCountHeight, ArrayList<Tile> allTiles, Graph roomGraph) {
        this.pApplet = pApplet;
        this.movementAlgorithms = movementAlgorithms;
        this.customShape = customShape;
        this.tileSize = tileSize;
        this.tileCountWidth = tileCountWidth;
        this.tileCountHeight = tileCountHeight;
        this.allTiles = allTiles;
        this.roomGraph = roomGraph;
        movementAlgorithms = new MovementAlgorithms(pApplet);

        S1_x = pApplet.width/2;
        S1_y = pApplet.height/2;

        R1_x = 50;
        R1_y = 50;

        path = new ArrayList<PVector>();


    }

    public NodeInterface makeTree()
    {
        // get leaves
        NodeInterface leaf1 = new RandomPathFollowingLeaf(new InternalNode(), null, null);

        NodeInterface leaf2 = new PathFollowingLeaf(new InternalNode(), null, null);
        ((PathFollowingLeaf)leaf2).setX(S1_x);
        ((PathFollowingLeaf)leaf2).setY(S1_y);

        NodeInterface leaf3 = new PathFollowingLeaf(new InternalNode(), null, null);
        ((PathFollowingLeaf)leaf3).setX(R1_x);
        ((PathFollowingLeaf)leaf3).setY(R1_y);

        NodeInterface leaf4 = new highSpeedRotLeaf(new InternalNode(), null, null);

        NodeInterface leaf5 = new PathFollowingLeaf(new InternalNode(), null, null);
        ((PathFollowingLeaf)leaf5).setX(500);
        ((PathFollowingLeaf)leaf5).setY(300);

        NodeInterface leaf6 = new ChangeCharacterLeaf(new InternalNode(), null, null);

        // get internal nodes
        NodeInterface internalNode1 = new NearWallCheckNode(new InternalNode(), leaf2, leaf1);
        NodeInterface internalNode2 = new OutsideRoomCheck(new InternalNode(), leaf3, internalNode1);
        NodeInterface internalNode3 = new MaxRotationCheckNode(new InternalNode(), leaf5, leaf4);
        NodeInterface root = new InsideRoomCheck(new InternalNode(), internalNode3, internalNode2);
        //NodeInterface root = new SpeedCheckNode(new InternalNode(), leaf6, internalNode4);

        return root;
    }

    public ArrayList<PVector> traverseDT(NodeInterface root, SteeringClass steeringClass)
    {
        boolean flag;
        path = new ArrayList<PVector>();

        NodeInterface temp = root;
        while (temp != null)
        {
            flag = temp.evaluate(steeringClass);
            if (flag)
                temp = ((InternalNodeInterface)temp).getLeft();
            else
                temp = ((InternalNodeInterface)temp).getRight();
        }
        return path;
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

    public class OutsideRoomCheck extends InternalNodeInterface
    {
        public OutsideRoomCheck(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {

            float x = steeringClass.getPosition().x;
            float y = steeringClass.getPosition().y;

            if (x<pApplet.width/3)
                return true;
            else
                return false;
        }
    }

    public class InsideRoomCheck extends InternalNodeInterface
    {
        public InsideRoomCheck(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {

            float x = steeringClass.getPosition().x;
            float y = steeringClass.getPosition().y;

            int room_width = 190;
            int room_height = 190;


            if ((x<room_width) && (y<room_height))
                return true;
            else
                return false;
        }
    }

    public class NearWallCheckNode extends InternalNodeInterface
    {
        int thresh;

        public NearWallCheckNode(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
            thresh = 50;
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            //pApplet.println("In NearWallCheckNode");
            //pApplet.println(steeringClass.getAngularAcc());
            float x = steeringClass.getPosition().x;
            float y = steeringClass.getPosition().y;
            if ((x<thresh) || (x>pApplet.width-thresh) || (y<thresh) || (y>pApplet.height-thresh))
            {
                //movementAlgorithms.arrive(steeringClass, new PVector(pApplet.width/2, pApplet.height/2));
                return true;
            }

            return false;
        }
    }

    public class PathFollowingLeaf extends InternalNodeInterface {

        int X, Y;

        public PathFollowingLeaf(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
            //pApplet.registerMethod("Draw", this);
        }

        public void setX(int x) {
            X = x;
        }

        public void setY(int y) {
            Y = y;
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            pApplet.println("In PathFollowingLeaf\n");
            path = pathFindingAlgo(X, Y, steeringClass);

            return true;
        }

        public ArrayList<PVector> pathFindingAlgo(int mouseX, int mouseY, SteeringClass character)
        {
            ArrayList<Edge> edges;
            ArrayList<PVector> path;

            edges = new ArrayList<Edge>();
            path = new ArrayList<PVector>();

            PathFinding pathFinding = new PathFinding(pApplet);

            Tile target = new Tile(mouseX, mouseY, tileSize, pApplet);
            Tile charPos = new Tile((int) character.getPosition().x, (int) character.getPosition().y, tileSize, pApplet);

            //println(charPos.tileNumber + "\t" + target.tileNumber);


//            edges = pathFinding.dijkstra(roomGraph, charPos.tileNumber, target.tileNumber);
            edges = pathFinding.aStar(roomGraph, charPos.tileNumber, target.tileNumber, "distanceHeuristic");
            if (edges!=null) {
                for (int i = 0; i < edges.size(); i++)
                    path.add(getPosFromTile(getTileFromTileNum(edges.get(i).toNode), tileSize));
            }
            else
            {
                //pApplet.println("No Path");
                edges = new ArrayList<Edge>();
            }
            return path;
        }


        public Tile getTileFromTileNum(int tileNum)
        {
            int i;
            for (i=0;i<allTiles.size();i++)
            {
                if (allTiles.get(i).tileNumber==tileNum)
                    break;

            }
            return allTiles.get(i);
        }

        public PVector getPosFromTile(Tile tile, int tileSize)
        {
            float x = (tile.tileX*tileSize) + 0.5f * tileSize;
            float y = (tile.tileY*tileSize) + 0.5f * tileSize;

            return new PVector(x, y);


        }

        public void drawPath(ArrayList<PVector> path)
        {
            for (int i=0;i<path.size()-1;i++)
            {
                pApplet.line(path.get(i).x, path.get(i).y, path.get(i+1).x, path.get(i+1).y);
            }
        }
    }

    public class RandomPathFollowingLeaf extends InternalNodeInterface {

        int X, Y;

        public RandomPathFollowingLeaf(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
            //pApplet.registerMethod("Draw", this);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            pApplet.println("In RandomPathFollowingLeaf\n");

            int thresh = 20;
            X = (int) pApplet.random(thresh, pApplet.width-thresh);
            Y = (int) pApplet.random(thresh, pApplet.height-thresh);

            path = pathFindingAlgo(X, Y, steeringClass);

            return true;
        }

        public ArrayList<PVector> pathFindingAlgo(int mouseX, int mouseY, SteeringClass character)
        {
            ArrayList<Edge> edges;
            ArrayList<PVector> path;

            edges = new ArrayList<Edge>();
            path = new ArrayList<PVector>();

            PathFinding pathFinding = new PathFinding(pApplet);

            Tile target = new Tile(mouseX, mouseY, tileSize, pApplet);
            Tile charPos = new Tile((int) character.getPosition().x, (int) character.getPosition().y, tileSize, pApplet);

            //println(charPos.tileNumber + "\t" + target.tileNumber);


//            edges = pathFinding.dijkstra(roomGraph, charPos.tileNumber, target.tileNumber);
            edges = pathFinding.aStar(roomGraph, charPos.tileNumber, target.tileNumber, "distanceHeuristic");
            if (edges!=null) {
                for (int i = 0; i < edges.size(); i++)
                    path.add(getPosFromTile(getTileFromTileNum(edges.get(i).toNode), tileSize));
            }
            else
            {
                //pApplet.println("No Path");
                edges = new ArrayList<Edge>();
            }
            return path;
        }


        public Tile getTileFromTileNum(int tileNum)
        {
            int i;
            for (i=0;i<allTiles.size();i++)
            {
                if (allTiles.get(i).tileNumber==tileNum)
                    break;

            }
            return allTiles.get(i);
        }

        public PVector getPosFromTile(Tile tile, int tileSize)
        {
            float x = (tile.tileX*tileSize) + 0.5f * tileSize;
            float y = (tile.tileY*tileSize) + 0.5f * tileSize;

            return new PVector(x, y);


        }

        public void drawPath(ArrayList<PVector> path)
        {
            for (int i=0;i<path.size()-1;i++)
            {
                pApplet.line(path.get(i).x, path.get(i).y, path.get(i+1).x, path.get(i+1).y);
            }
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
                steeringClass.setVelocity(new PVector(0,0));
                steeringClass.setAcceleration(new PVector(0,0));
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

    public class ChangeCharacterLeaf extends InternalNodeInterface
    {
        public ChangeCharacterLeaf(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
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
                steeringClass.setAcceleration(new PVector(0.01f, 0.01f));
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

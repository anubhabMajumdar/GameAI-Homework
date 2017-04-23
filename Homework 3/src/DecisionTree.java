import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

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
    SteeringClass monster;

    int S1_x, S1_y;
    int R1_x, R1_y;

//    public DecisionTree(PApplet pApplet, CustomShape customShape) {
//       this.pApplet = pApplet;
//        movementAlgorithms = new MovementAlgorithms(pApplet);
//        this.customShape = customShape;
//    }

    public DecisionTree(PApplet pApplet, CustomShape customShape, int tileSize, int tileCountWidth, int tileCountHeight, ArrayList<Tile> allTiles, Graph roomGraph, SteeringClass monster) {
        this.pApplet = pApplet;
        this.movementAlgorithms = movementAlgorithms;
        this.customShape = customShape;
        this.tileSize = tileSize;
        this.tileCountWidth = tileCountWidth;
        this.tileCountHeight = tileCountHeight;
        this.allTiles = allTiles;
        this.roomGraph = roomGraph;
        this.monster = monster;
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


        // get internal nodes
        NodeInterface internalNode1 = new NearWallCheckNode(new InternalNode(), leaf2, leaf1);
        NodeInterface internalNode2 = new OutsideRoomCheck(new InternalNode(), leaf3, internalNode1);
        NodeInterface internalNode3 = new MaxRotationCheckNode(new InternalNode(), leaf5, leaf4);
        NodeInterface root = new InsideRoomCheck(new InternalNode(), internalNode3, internalNode2);

        return root;
    }

    public NodeInterface makeLearnedTree()
    {
        /* DTLearning output on features2.csv
        Parent Node-->N/A	Parent Value-->N/A	Current Node-->In Room	Left-->false	Right-->true
        Parent Node-->In Room	Parent Value-->false	Current Node-->Distance Between Objects	Left-->far	Right-->close
        Parent Node-->Distance Between Objects	Parent Value-->far	Current Node-->Leaf	ChangeMonster	Probability=0.00684403752889453	PathFollow	Probability=0.9886914744141776	Rotation	Probability=0.004464488056927889
        Parent Node-->Distance Between Objects	Parent Value-->close	Current Node-->Leaf	reset
        Parent Node-->In Room	Parent Value-->true	Current Node-->Distance Between Objects	Left-->far	Right-->close
        Parent Node-->Distance Between Objects	Parent Value-->far	Current Node-->Leaf	ChangeMonster	Probability=0.44172094617757973	PathFollow	Probability=0.07319163524168666	Rotation	Probability=0.4850874185807336
        Parent Node-->Distance Between Objects	Parent Value-->close	Current Node-->Leaf	reset
        */

        NodeInterface randomPathFollowingLeaf = new SupermanFollowingLeaf(new InternalNode(), null, null);
        NodeInterface doResetLeaf = new doReset(new InternalNode(), null, null);
        NodeInterface highSpeedRot = new highSpeedRotLeaf(new InternalNode(), null, null);
        NodeInterface changeMonster = new ChangeMonster(new InternalNode(), null, null);

        NodeInterface randomDecision = new chooseBetweenRotAndChangeMonster(new InternalNode(), highSpeedRot, changeMonster);
        ((chooseBetweenRotAndChangeMonster)randomDecision).setSplitPercentage(0.44172094617757973);

        NodeInterface distRight = new VicinityCheck(new InternalNode(), randomDecision, doResetLeaf);

        NodeInterface distLeft = new VicinityCheck(new InternalNode(), randomPathFollowingLeaf, doResetLeaf);

        NodeInterface root = new SupermanInsideRoomCheck(new InternalNode(), distRight, distLeft);

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

    public class SupermanInsideRoomCheck extends InternalNodeInterface
    {
        public SupermanInsideRoomCheck(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {

            float x = monster.getPosition().x;
            float y = monster.getPosition().y;

            int room_width = 190;
            int room_height = 190;


            if ((x<room_width) && (y<room_height))
                return true;
            else
                return false;
        }
    }

    public class doReset extends InternalNodeInterface
    {
        public doReset(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {

            pApplet.println("In doReset\n");

            path = new ArrayList<PVector>();
            steeringClass.setPosition(new PVector(pApplet.width-100, pApplet.height-100));
            steeringClass.setOrientation(0);
            return true;
        }
    }

    public class chooseBetweenRotAndChangeMonster extends InternalNodeInterface {
        double splitPercentage;
        public chooseBetweenRotAndChangeMonster(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        public void setSplitPercentage(double splitPercentage) {
            this.splitPercentage = splitPercentage;
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            return (Math.random()>splitPercentage);
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

            //action = "Rotation";

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

    public class ChangeMonster extends InternalNodeInterface
    {
        public ChangeMonster(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {

            pApplet.println("In ChnageMonster\n");

            //action = "ChangeMonster";

            if (customShape.getImageName().equals("cuteMonster_red.jpeg"))
                customShape.setImageName("cuteMonster_blue.jpeg");
            else
                customShape.setImageName("cuteMonster_red.jpeg");
            customShape.reloadPImage();
            return true;
        }
    }

    public class SupermanFollowingLeaf extends InternalNodeInterface {

        int X, Y;

        public SupermanFollowingLeaf(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
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
            pApplet.println("In SupermanFollowingLeaf\n");

            //action = "PathFollow";

            X = (int) monster.getPosition().x;
            Y = (int) monster.getPosition().y;

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

    public class VicinityCheck extends InternalNodeInterface
    {
        public VicinityCheck(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {

            pApplet.println("In VicinityCheck\n");

            float dist = PVector.dist(steeringClass.getPosition(), monster.getPosition());
            return (dist>30);
        }
    }

    public class Materialize extends InternalNodeInterface
    {
        public Materialize(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {

            PVector p1 = new PVector(100, pApplet.height-100);
            PVector p2 = new PVector(pApplet.width-100, pApplet.height/3);
            if ((PVector.dist(steeringClass.getPosition(), p1)) < (PVector.dist(steeringClass.getPosition(), p2)))
                steeringClass.setPosition(new PVector(p1.x, p1.y));
            else
                steeringClass.setPosition(new PVector(p2.x, p2.y));
            return true;
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
            //pApplet.println("In SupermanFollowingLeaf\n");
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
            //pApplet.println("In RandomPathFollowingLeaf\n");

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

    public class GetAwayFromMonsterPathFollowingLeaf extends InternalNodeInterface {

        int X, Y;

        public GetAwayFromMonsterPathFollowingLeaf(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
            //pApplet.registerMethod("Draw", this);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            pApplet.println("In GetAwayFromMonsterPathFollowingLeaf\n");

            int thresh = 20;
            int distThresh = 200;
            X = (int) pApplet.random(thresh, pApplet.width-thresh);
            Y = (int) pApplet.random(thresh, pApplet.height-thresh);
            PVector curPos = new PVector(X, Y);
            while (PVector.dist(curPos, monster.getPosition())<distThresh)
            {
                X = (int) pApplet.random(thresh, pApplet.width-thresh);
                Y = (int) pApplet.random(thresh, pApplet.height-thresh);
                curPos = new PVector(X, Y);
            }

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

    public class EvadeLeaf extends InternalNodeInterface
    {
        public EvadeLeaf(NodeInterface nodeInterface, NodeInterface left, NodeInterface right) {
            super(nodeInterface, left, right);
        }

        @Override
        public boolean evaluate(SteeringClass steeringClass) {
            pApplet.println("In wander\n");
            try
            {
                MovementAlgorithms movementAlgorithms = new MovementAlgorithms(pApplet);
                movementAlgorithms.evade(steeringClass, monster.getPosition());

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

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.io.*;
import java.util.*;

/**
 * Created by anubhabmajumdar on 3/18/17.
 * Followed the tutorial for learning and reference - https://py.processing.org/tutorials/pixels/
 */
public class DriverClassDT_BT extends PApplet {

    PImage pImage;
    int tileSize, tileCountWidth, tileCountHeight;
    ArrayList<Tile> allTiles;
    Graph roomGraph;
    CustomShape customShape;
    int w, h;
    PathFinding pathFinding;
    SteeringClass character;
    int target;
    MovementAlgorithms movementAlgorithms;
    ArrayList<Edge> edges;
    ArrayList<PVector> characterPath;
    int lastIndex, lastIndexMonster;
    int startTime, startTimeMonster;
    DecisionTree decisionTree;
    NodeInterface dtRoot;
    SteeringClass monster_steeringClass;
    CustomShape monster_customShape;
    BehaviourTree behaviourTree;
    NodeInterface btRoot;
    ArrayList<PVector> monsterPath;
    int resetCount = 0, drawCount=0;

    public void settings()
    {
        size(600, 400);

        tileSize = 20;
        tileCountWidth = width/tileSize;
        tileCountHeight = height/tileSize;

        allTiles = new ArrayList<Tile>();

        pImage = loadImage("room.jpg");

        w = 20;
        h = 20;

        float monsterVel = 0.9f;

        pathFinding = new PathFinding(this);
        character = new SteeringClass(this);
        character.setPosition(new PVector(width-100, height-100));
        character.setOrientation(0);

        monster_steeringClass = new SteeringClass(this);
        monster_steeringClass.setPosition(new PVector(100, height-100));
        monster_steeringClass.setOrientation(0);
        monster_steeringClass.setMaxVel(new PVector(monsterVel,monsterVel));
        monster_steeringClass.setMaxSpeed(monsterVel);

        movementAlgorithms = new MovementAlgorithms(this);

        edges = new ArrayList<Edge>();
        characterPath = new ArrayList<PVector>();

        lastIndex = 0;
        lastIndexMonster = 0;

        startTime = millis();
        startTimeMonster = millis();

    }

    public void setup()
    {
        image(pImage, -0, 0, width, height);
        filter(THRESHOLD,0.5f);
        try {
            roomGraph = getRoomGraph(tileSize);
        }
        catch (IOException e)
        {
            println("Cannot generate room graph");
        }

        customShape = new CustomShape(this, "legoSuperman.png", w, h);
        customShape.drawCustomShape(character.getPosition().x, character.getPosition().y);

        monster_customShape = new CustomShape(this, "cuteMonster_red.jpeg", w, h);
        monster_customShape.drawCustomShape(monster_steeringClass.getPosition().x, monster_steeringClass.getPosition().y);

        decisionTree = new DecisionTree(this, customShape, tileSize, tileCountWidth, tileCountHeight, allTiles, roomGraph, monster_steeringClass);
        dtRoot = decisionTree.makeTree();

        behaviourTree = new BehaviourTree(this, monster_customShape, tileSize, tileCountWidth, tileCountHeight, allTiles, roomGraph, character);
        btRoot = behaviourTree.makeTree();
    }

    public void draw()
    {
        image(pImage, -0, 0, width, height);
        line(width/3, 0, width/3, height);
        filter(THRESHOLD,0.5f);


        if ((millis()>startTime+300) && ((lastIndex==0) || (lastIndex==(characterPath.size()-1))))
        {
            characterPath = decisionTree.traverseDT(dtRoot, character);
            startTime = millis();
            lastIndex = 0;
        }

        if ((millis()>startTimeMonster+700))
        {
            monsterPath = behaviourTree.traverseBT(btRoot, monster_steeringClass);
            startTimeMonster = millis();
        }

        lastIndex = movementAlgorithms.pathFollowing(character, characterPath, lastIndex);
        lastIndexMonster = movementAlgorithms.pathFollowing(monster_steeringClass, monsterPath, lastIndexMonster);

        customShape.setOrientation(character.getOrientation());
        customShape.drawCustomShape(character.getPosition().x, character.getPosition().y);
        handleBoundary(character);
        //customShape.drawBreadcrumbs();

        monster_customShape.setOrientation(monster_steeringClass.getOrientation());
        monster_customShape.drawCustomShape(monster_steeringClass.getPosition().x, monster_steeringClass.getPosition().y);
        handleBoundary(monster_steeringClass);

        reset();

        character.update(1);
        monster_steeringClass.update(1);

        //drawPath(monsterPath);
        //drawPath(characterPath);

        drawCount++;
        if (drawCount%1000 == 0)
            println("Draw Count = "+drawCount+"\t Reset Count = "+resetCount);

    }

    public void handleBoundary(SteeringClass steeringClass)
    {
        steeringClass.setPosition(new PVector(steeringClass.getPosition().x%width, steeringClass.getPosition().y%height));
    }

    public void reset()
    {
        if (PVector.dist(monster_steeringClass.getPosition(), character.getPosition())<30)
        {
            characterPath = new ArrayList<PVector>();
            lastIndex = 0;
            character.setPosition(new PVector(width-100, height-100));
            character.setOrientation(0);

            monsterPath = new ArrayList<PVector>();
            lastIndexMonster = 0;
            monster_steeringClass.setPosition(new PVector(100, height-100));
            monster_steeringClass.setOrientation(0);
            resetCount++;
        }
    }

    public void prettyPrintGrid(Graph graph)
    {
/* Followed the example provided here - http://stackoverflow.com/questions/1066589/iterate-through-a-hashmap */
/* Followed the example provided here - https://www.tutorialspoint.com/java/java_using_iterator.htm */

        HashMap g = graph.g;
        // Get a set of the entries
        Set set = g.entrySet();

        // Get an iterator
        Iterator i = set.iterator();

        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            //System.out.print(me.getKey() + ": [ ");
            ArrayList<Edge> edges = (ArrayList<Edge>) me.getValue();

            for (int j=0; j<edges.size(); j++)
            {
                int to = edges.get(j).toNode;
                int from = edges.get(j).fromNode;

                PVector toNode = getPosFromTile(getTileFromTileNum(to), tileSize);
                PVector fromNode = getPosFromTile(getTileFromTileNum(from), tileSize);

                line(toNode.x, toNode.y, fromNode.x, fromNode.y);


            }
        }
    }
/* -------------------------------------------------------------------------------------------------------------------- */

    public void drawPath(ArrayList<PVector> path)
    {
        for (int i=0;i<path.size()-1;i++)
        {
            line(path.get(i).x, path.get(i).y, path.get(i+1).x, path.get(i+1).y);
        }
    }

    public ArrayList<Edge> guideCharacter(ArrayList<Edge> edges)
    {
        Tile tile;
        //for (int i=0;i<edges.size();i++)
        if (edges.size()>0)
        {
            //println(edges.get(i).toNode);
            tile = getTileFromTileNum(edges.get(edges.size()-1).toNode);
            PVector target = getPosFromTile(tile, tileSize);

            //movementAlgorithms.align(character, targetOrientation);
            movementAlgorithms.arrive(character, target);
            customShape.setOrientation(character.getOrientation());
            customShape.drawCustomShape(character.getPosition().x,character.getPosition().y);

            //customShape.drawBreadcrumbs();

            character.update(1);


        }
        //edges.clear();
        return edges;

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

    public void printPath(ArrayList<Edge> edges)
    {
        if (edges != null)
        {
            String str = "Path represented as list of edges. Format of edge is (fromNode, toNode, weight) " +
                    "\n Path --> ";
            println(str);
            for (int j=0; j<edges.size(); j++)
            {
                str = str + "(" + edges.get(j).fromNode + ", " + edges.get(j).toNode + ", " + edges.get(j).weight + ")  ";
                print(str);
            }

        }
        else
            println("No Path exists");
    }

    public Graph getRoomGraph(int tileSize) throws IOException
    {
        /* Followed documentation here - https://processing.org/reference/loadPixels_.html */
        loadPixels();

        for (int i=0;i<width;i+=tileSize){
            for (int j=0;j<height;j+=tileSize)
            {
                //println(i+" "+j);
                allTiles.add(new Tile(i, j, tileSize, this));
            }
        }
        updatePixels();
/* -------------------------------------------------------------------------------------------------------------------- */

        // create graph file
        String fileName = "roomGraph.txt";
        File file = new File(fileName);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        bufferedWriter.write("fromNode\ttoNode\tweight\tcluster\n");

        for (int i=0;i<allTiles.size();i++)
        {
            int curTileNum = allTiles.get(i).tileNumber;
            if (!allTiles.get(i).obstacle) {
                int neighbourTop = -1, neighbourLeft = -1, neighbourRight = -1, neighbourBottom = -1;
                if (curTileNum > tileCountWidth)
                    neighbourTop = curTileNum - tileCountWidth;
                if (curTileNum < (tileCountHeight - 1) * tileCountWidth)
                    neighbourBottom = curTileNum + tileCountWidth;
                if (((curTileNum + 1) % tileCountWidth) != 0)
                    neighbourRight = curTileNum + 1;
                if ((curTileNum % tileCountWidth) != 0)
                    neighbourLeft = curTileNum - 1;

                if ((neighbourTop != -1) && (!(getTileFromTileNum(neighbourTop).obstacle))) {
                    bufferedWriter.write(curTileNum + "\t" + neighbourTop + "\t" + "1\t1\n");
                }
                if ((neighbourBottom != -1) && (!(getTileFromTileNum(neighbourBottom).obstacle))) {
                    bufferedWriter.write(curTileNum + "\t" + neighbourBottom + "\t" + "1\t1\n");
                }
                if ((neighbourRight != -1) && (!(getTileFromTileNum(neighbourRight).obstacle))) {
                    bufferedWriter.write(curTileNum + "\t" + neighbourRight + "\t" + "1\t1\n");
                }
                if ((neighbourLeft != -1) && (!(getTileFromTileNum(neighbourLeft).obstacle))) {
                    bufferedWriter.write(curTileNum + "\t" + neighbourLeft + "\t" + "1\t1\n");
                }
            }
        }

        bufferedWriter.close();

        // get graph
        Graph graph = new Graph(this);
        graph.makeGraph(fileName);
        //graph.prettyPrint();

        return graph;

    }


    public static void main(String[] args)
    {
        PApplet.main("DriverClassDT_BT");
    }





}

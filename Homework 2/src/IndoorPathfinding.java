import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.*;
import java.io.*;

/**
 * Created by anubhabmajumdar on 3/18/17.
 * Followed the tutorial for learning and reference - https://py.processing.org/tutorials/pixels/
 */
public class IndoorPathfinding extends PApplet {

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
    ArrayList<PVector> path;
    int lastIndex;
    int startTime;


    public void settings()
    {
        size(500, 300);

        tileSize = 10;
        tileCountWidth = width/tileSize;
        tileCountHeight = height/tileSize;

        allTiles = new ArrayList<Tile>();

        pImage = loadImage("room.jpg");

        w = 10;
        h = 10;

        pathFinding = new PathFinding(this);
        character = new SteeringClass(this);
        character.setPosition(new PVector(10, 10));
        character.setOrientation(0);
        //character.setAcceleration(new PVector(0.1f, 0.1f));

        movementAlgorithms = new MovementAlgorithms(this);

        edges = new ArrayList<Edge>();
        path = new ArrayList<PVector>();

        lastIndex = 0;

        startTime = millis();

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

        customShape = new CustomShape(this, "customShape.png", w, h);
        customShape.drawCustomShape(character.getPosition().x, character.getPosition().y);

    }

    public void draw()
    {
        image(pImage, -0, 0, width, height);
        filter(THRESHOLD,0.5f);

//        for (int i=0;i<allTiles.size();i++)
//        {
//            PVector p = getPosFromTile(allTiles.get(i), tileSize);
//            ellipse(p.x, p.y, 5, 5);
//        }

//        prettyPrintGrid(roomGraph);


        if (mousePressed)
        {
            edges.clear();
            path.clear();
            lastIndex = 0;

            Tile target = new Tile(mouseX, mouseY, tileSize);
            Tile charPos = new Tile((int) character.getPosition().x, (int) character.getPosition().y, tileSize);

            //println(charPos.tileNumber + "\t" + target.tileNumber);


//            edges = pathFinding.dijkstra(roomGraph, charPos.tileNumber, target.tileNumber);
            edges = pathFinding.aStar(roomGraph, charPos.tileNumber, target.tileNumber, "distanceHeuristic", "");
            if (edges!=null) {
                for (int i = 0; i < edges.size(); i++)
                    path.add(getPosFromTile(getTileFromTileNum(edges.get(i).toNode), tileSize));
            }
            else
            {
                println("No Path");
                edges = new ArrayList<Edge>();
            }

            //printPath(edges);
        }

        startTime = millis();
        lastIndex = movementAlgorithms.pathFollowing(character, path, lastIndex);

        customShape.setOrientation(character.getOrientation());
        customShape.drawCustomShape(character.getPosition().x, character.getPosition().y);
        customShape.drawBreadcrumbs();

        character.update(1);

        drawPath(path);

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
                allTiles.add(new Tile(i, j, tileSize));
            }
        }
        updatePixels();

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
        PApplet.main("IndoorPathfinding");
    }

    public class Tile
    {
        /* Followed the tutorial for learning and reference - https://py.processing.org/tutorials/pixels/ */
        int tileX, tileY, tileNumber;
        Boolean obstacle; // true means the tile is an obstacle

        public Tile(int x, int y, int tileSize)
        {
            this.tileNumber = computeTileNumber(x, y, tileSize);

            obstacle = false;
            for (int i=x; i<x+tileSize;i++)
            {
                for (int j=y;j<y+tileSize;j++)
                {
                    int curPos = i + j*width;
                    if (brightness(pixels[curPos])==0)
                    {
                        obstacle = true;
                        break;
                    }
                }
            }
        }

        public int computeTileNumber(int x, int y, int tileSize)
        {
            /* Followed the tutorial for learning and reference - https://py.processing.org/tutorials/pixels/ */

            this.tileX = (int) Math.floor(x/tileSize);
            this.tileY = (int) Math.floor(y/tileSize);

            int tileNumber = tileX + ((int) width/tileSize)*tileY;
            return  tileNumber;
        }
    }



}

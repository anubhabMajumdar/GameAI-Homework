import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.io.*;

/**
 * Created by anubhabmajumdar on 3/18/17.
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


    public void settings()
    {
        size(500, 260);

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
        //background(0);
        if (mousePressed)
        {
            Tile target = new Tile(mouseX, mouseY, tileSize);
            Tile charPos = new Tile((int) character.getPosition().x, (int) character.getPosition().y, tileSize);
            //println(tile.tileNumber + "\t" + tile.obstacle);
            println(target.tileNumber + "\t" + charPos.tileNumber);


            edges = pathFinding.dijkstra(roomGraph, charPos.tileNumber, target.tileNumber);
            //printPath(edges);
        }
        edges = guideCharacter(edges);




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
                //edges.get(j).prettyPrint();
//                            System.out.print("  ");
                str = str + "(" + edges.get(j).fromNode + ", " + edges.get(j).toNode + ", " + edges.get(j).weight + ")  ";
                print(str);
            }

        }
        else
            println("No Path exists");
    }

    public Graph getRoomGraph(int tileSize) throws IOException
    {
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
            int neighbourTop=-1, neighbourLeft=-1, neighbourRight=-1, neighbourBottom=-1;
            if (i>tileCountWidth)
                neighbourTop = i-tileCountWidth;
            if (i<(tileCountHeight-1)*tileCountWidth)
                neighbourBottom = i+tileCountWidth;
            if (((i+1)%tileCountWidth)!=0)
                neighbourRight = i+1;
            if ((i%tileCountWidth)!=0)
                neighbourLeft = i-1;

            if ((neighbourTop!=-1) && (!(allTiles.get(neighbourTop).obstacle)))
            {
                bufferedWriter.write(i+"\t"+neighbourTop+"\t"+"1\t1\n");
            }
            if ((neighbourBottom!=-1) && (!(allTiles.get(neighbourBottom).obstacle)))
            {
                bufferedWriter.write(i+"\t"+neighbourBottom+"\t"+"1\t1\n");
            }
            if ((neighbourRight!=-1) && (!(allTiles.get(neighbourRight).obstacle)))
            {
                bufferedWriter.write(i+"\t"+neighbourRight+"\t"+"1\t1\n");
            }
            if ((neighbourLeft!=-1) && (!(allTiles.get(neighbourLeft).obstacle)))
            {
                bufferedWriter.write(i+"\t"+neighbourLeft+"\t"+"1\t1\n");
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
        int tileX, tileY, tileNumber;
        Boolean obstacle; // true means the tile is an obstacle

        public Tile(int x, int y, int tileSize)
        {
//            int tileX = (int) Math.floor(x/tileSize);
//            int tileY = (int) Math.floor(y/tileSize);
//
//            this.tileNumber = tileX + ((int) width/tileSize)*tileY;
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
            this.tileX = (int) Math.floor(x/tileSize);
            this.tileY = (int) Math.floor(y/tileSize);

            int tileNumber = tileX + ((int) width/tileSize)*tileY;
            return  tileNumber;
        }
    }



}

import processing.core.PApplet;
import java.util.ArrayList;

/**
 * Created by anubhabmajumdar on 3/4/17.
 */
public class DriverClass extends PApplet {

//    public void settings(){
//        size(1200,800);
//    }

    public void setup()
    {
        Graph newG = new Graph(this);
        newG.makeGraph("world_graph.txt");
//        newG.makeGraph("cit-HepPh.txt");
        PathFinding pathFinding = new PathFinding(this);
        ArrayList<Edge> edges = pathFinding.dijkstra(newG.g, 13,12);
//        ArrayList<Edge> edges = pathFinding.dijkstra(newG.g, 112008,9406205);
        if (edges == null)
            System.out.println("No path exists");
        else
            pathFinding.prettyPrintPath(edges);

    }

    public void draw()
    {

    }



    public static void main(String[] args)
    {
        PApplet.main("DriverClass");
    }
}

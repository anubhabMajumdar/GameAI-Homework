import controlP5.ControlEvent;
import controlP5.ControlP5;
import processing.core.PApplet;
import java.util.ArrayList;

/**
 * Created by anubhabmajumdar on 3/4/17.
 */
public class DriverClass extends PApplet {

    public void settings(){
        size(330,260);
    }

    ControlP5 controlP5;

    public void setup()
    {
        smooth();

        controlP5 = new ControlP5(this);

//        controlP5.addButton("button1",1,70,10,60,20);
        controlP5.addButton("button1")
                .setPosition(1,1)
                .setSize(70,10)
                .setValue(0)
        ;

//        Graph newG = new Graph(this);
//        newG.makeGraph("world_graph.txt");
////        newG.makeGraph("cit-HepPh.txt");
//        PathFinding pathFinding = new PathFinding(this);
//        ArrayList<Edge> edges = pathFinding.dijkstra(newG.g, 13,12);
////        ArrayList<Edge> edges = pathFinding.dijkstra(newG.g, 112008,9406205);
//        if (edges == null)
//            System.out.println("No path exists");
//        else
//            pathFinding.prettyPrintPath(edges);

    }

    public void draw()
    {
        background(0);
    }

    public void controlEvent(ControlEvent theEvent) {
  /* events triggered by controllers are automatically forwarded to
     the controlEvent method. by checking the name of a controller one can
     distinguish which of the controllers has been changed.
  */

  /* check if the event is from a controller otherwise you'll get an error
     when clicking other interface elements like Radiobutton that don't support
     the controller() methods
  */

        if(theEvent.isController()) {

            print("control event from : "+theEvent.getController().getName());
            println(", value : "+theEvent.getController().getValue());

            if(theEvent.getController().getName().equals("button1")) {
                System.out.println("button 1 clicked");
            }
            else
                System.out.println("button 1 not clicked");


        }
        else
            System.out.println("else");
    }


    public static void main(String[] args)
    {
        PApplet.main("DriverClass");
    }
}

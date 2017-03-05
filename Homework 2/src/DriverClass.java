import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.*;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by anubhabmajumdar on 3/4/17.
 */
public class DriverClass extends PApplet {

    ControlP5 controlP5;
    DropdownList algorithm, graphName, startNode, targetNode;
    String algo, graph;
    int start, stop;
    Graph newG;
    PathFinding pathFinding;
    Textfield cost, fill;

    public void settings(){
        size(1000,800);

        newG = null;
        pathFinding = new PathFinding(this);

        algo = "Dijkstra";
        graph = "world_graph.txt";

        start = 1;
        stop = 1;
    }


    public void setup()
    {
        smooth();

        controlP5 = new ControlP5(this);

        algorithm = controlP5.addDropdownList("algorithm")
                .setPosition(10, 50).setSize(200,200);
        algorithm.setCaptionLabel("Algorithm");
        algorithm.addItem("Dijkstra", "Dijkstra");
        algorithm.addItem("A*", "A*");
        algorithm.setDefaultValue(0.0f);
        algorithm.close();
        customize(algorithm);

        graphName = controlP5.addDropdownList("graphName")
                .setPosition(250, 50)
                .setSize(200,200);
        graphName.setCaptionLabel("Graph");
        graphName.addItem("Hand-crafted Graph", "world");
        graphName.addItem("Huge Graph", "random");
        graphName.setDefaultValue(0.0f);
        graphName.close();
        customize(graphName);


        startNode = controlP5.addDropdownList("startNode")
                .setPosition(500, 50).setSize(200,200);
        startNode.setCaptionLabel("Start Node");
        startNode.close();
        customize(startNode);


        targetNode = controlP5.addDropdownList("targetNode")
                .setPosition(750, 50).setSize(200,200);
        targetNode.setCaptionLabel("Target node");
        targetNode.close();
        customize(targetNode);


        controlP5.addButton("Go")
                .setPosition(400,150)
                .setSize(100,50)
                .setValue(0)
        ;

//        myTextfield = controlP5.addTextfield("Number of nodes expanded",100,160,200,20);
//        myTextfield.setFocus(true);
        cost = controlP5.addTextfield("Total cost",250,350,200,20);
        fill = controlP5.addTextfield("Number of nodes expanded",250,400,200,20);


        //newG = new Graph(this);
//        newG.makeGraph("world_graph.txt");
////        newG.makeGraph("cit-HepPh.txt");
        PathFinding pathFinding = new PathFinding(this);
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

        if(theEvent.isController()) {

//            print("control event from : "+theEvent.getController().getName());
//            println(", value : "+theEvent.getController().getValue());

            if(theEvent.getController().getName().equals("Go")) {
                if (newG!=null)
                {
                    if (algo.equals("Dijkstra"))
                        pathFinding.dijkstra(newG.g, start, stop);
                    else if (algo.equals("A*"))
                        System.out.println("A*");//pathFinding.dijkstra(newG.g, start, stop);
                }

                cost.setValue()


            }
            else if (theEvent.getController().getName().equals("algorithm"))
            {
                if (theEvent.getController().getValue() == 0.0f)
                    algo = "Dijkstra";
                else
                    algo = "A*";
            }
            else if (theEvent.getController().getName().equals("graphName"))
            {
                if (theEvent.getController().getValue() == 0.0f)
                    graph = "world_graph.txt";
                else
                    graph = "cit-HepPh.txt";

                //System.out.println(graph);
                List<Integer> l;

                startNode.remove();

                startNode = controlP5.addDropdownList("startNode")
                        .setPosition(500, 50).setSize(200,200);
                startNode.setCaptionLabel("Start Node");
                startNode.close();
                customize(startNode);


                targetNode.remove();

                targetNode = controlP5.addDropdownList("targetNode")
                        .setPosition(750, 50).setSize(200,200);
                targetNode.setCaptionLabel("Target node");
                targetNode.close();
                customize(targetNode);



                newG = new Graph(this);
                newG.makeGraph(graph);
                l = new ArrayList<Integer>(newG.g.keySet());
                for (int i=0;i<l.size();i++)
                {
                    startNode.addItem(l.get(i).toString(), l.get(i));
                    targetNode.addItem(l.get(i).toString(), l.get(i));
                }
                startNode.close();
                targetNode.close();
            }
            else if (theEvent.getController().getName().equals("startNode"))
            {
                List<Integer> l = new ArrayList<Integer>(newG.g.keySet());
                start = l.get((int)theEvent.getController().getValue());
                //System.out.println(l.get((int)theEvent.getController().getValue()));
            }
            else if (theEvent.getController().getName().equals("targetNode"))
            {
                List<Integer> l = new ArrayList<Integer>(newG.g.keySet());
                stop = l.get((int)theEvent.getController().getValue());
                //System.out.println(l.get((int)theEvent.getController().getValue()));
            }

            //System.out.println(algo + "   " + graph);

        }
        else if(theEvent.isGroup()) {
            // check if the Event was triggered from a ControlGroup
            System.out.println("event from group : "+theEvent.getGroup().getValue()+" from "+theEvent.getGroup());
        }

    }



    public void customize(DropdownList ddl) {
        // a convenience function to customize a DropdownList
        ddl.setBackgroundColor(color(190));
        ddl.setItemHeight(20);
        ddl.setBarHeight(15);
//        ddl.setCaptionLabel("dropdown");
        ddl.getCaptionLabel().getStyle().marginTop = 3;
        ddl.getCaptionLabel().getStyle().marginLeft = 3;
        ddl.getValueLabel().getStyle().marginTop = 3;
        //ddl.scroll(0);
        ddl.setColorBackground(color(60));
        ddl.setColorActive(color(255, 128));
    }


    public static void main(String[] args)
    {
        PApplet.main("DriverClass");
    }
}

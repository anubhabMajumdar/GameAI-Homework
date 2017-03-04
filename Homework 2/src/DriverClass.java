import processing.core.PApplet;

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
        newG.makeGraph("world_graph");
//        newG.makeGraph("cit-HepPh.txt");

    }

    public void draw()
    {

    }



    public static void main(String[] args)
    {
        PApplet.main("DriverClass");
    }
}

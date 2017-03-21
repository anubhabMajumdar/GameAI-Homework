import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by anubhabmajumdar on 3/16/17.
 * For measuring time, I referenced the blog post here - http://stackoverflow.com/questions/11785200/how-can-i-get-the-current-milliseconds-from-the-current-time
 */
public class Performance {

    PathFinding pathFinding = new PathFinding(new PApplet());
    Random random = new Random();

    int n = 100;

    public void setN(int num)
    {
        n = num;
    }

    private double avgList_long(ArrayList list)
    {
        long sum = 0;
        for (int i=0;i<list.size();i++)
        {
            sum += (long) (list.get(i));
        }
        return (double) (sum/list.size());
    }

    private double avgList_int(ArrayList list)
    {
        int sum = 0;
        for (int i=0;i<list.size();i++)
        {
            sum += (int)(list.get(i));
        }
        return (double) (sum/list.size());
    }

    public double[] measureTime(Graph graph, ArrayList list)
    {
/* Followed the example provided here - http://stackoverflow.com/questions/11785200/how-can-i-get-the-current-milliseconds-from-the-current-time */
        long millis_start;
        long millis_stop;
        ArrayList time_dijkstra = new ArrayList();
        ArrayList time_astar = new ArrayList();

        for (int i=1;i<=n;i++)
        {
            int start = random.nextInt(list.size());
            int stop = random.nextInt(list.size());

            millis_start = System.currentTimeMillis();
            pathFinding.dijkstra(graph, start,stop);
            millis_stop = System.currentTimeMillis();
            time_dijkstra.add(millis_stop-millis_start);

            millis_start = System.currentTimeMillis();
            pathFinding.aStar(graph, start, stop, "distanceHeuristic", "" );
            millis_stop = System.currentTimeMillis();
            time_astar.add(millis_stop-millis_start);
        }
        double avg_dijkstra = avgList_long(time_dijkstra);
        double avg_astar = avgList_long(time_astar);

        double[] results = {avg_dijkstra, avg_astar};
        return results;
    }

    public double[] measureFill(Graph graph, ArrayList list)
    {
        ArrayList fill_dijkstra = new ArrayList();
        ArrayList fill_astar = new ArrayList();

        for (int i = 1; i <= n; i++) {
            int start = random.nextInt(list.size());
            int stop = random.nextInt(list.size());

            pathFinding.dijkstra(graph, start, stop);
            fill_dijkstra.add(pathFinding.fill);

            pathFinding.aStar(graph, start, stop, "distanceHeuristic", "" );
            fill_astar.add(pathFinding.fill);
        }
        double avg_dijkstra = avgList_int(fill_dijkstra);
        double avg_astar = avgList_int(fill_astar);

        double[] results = {avg_dijkstra, avg_astar};
        return results;
    }

    public double[] measureMemory(Graph graph, ArrayList list)
    {
        ArrayList fill_dijkstra = new ArrayList();
        ArrayList fill_astar = new ArrayList();

        for (int i = 1; i <= n; i++) {
            int start = random.nextInt(list.size());
            int stop = random.nextInt(list.size());

            pathFinding.dijkstra(graph, start, stop);
            fill_dijkstra.add(pathFinding.maxUnvisitedNodeList);

            pathFinding.aStar(graph, start, stop, "distanceHeuristic", "" );
            fill_astar.add(pathFinding.maxUnvisitedNodeList);
        }

        double avg_dijkstra = avgList_int(fill_dijkstra);
        double avg_astar = avgList_int(fill_astar);

        double[] results = {avg_dijkstra, avg_astar};
        return results;

    }

    public void measurePerformance()
    {

        String [] files = {"world_graph2.txt", "cit-HepPh.txt"};
        Graph newG = new Graph(new PApplet());
        ArrayList l;
        double[] results_time, results_fill, results_mem;
        for (int i=0;i<2;i++)
        {
            newG.makeGraph(files[i]);
            l = new ArrayList<Integer>(newG.g.keySet());

            results_time = measureTime(newG,l);
            System.out.println("Time --> " + files[i] + " " + results_time[0] + " " + results_time[1]);

            results_fill = measureFill(newG, l);
            System.out.println("Fill --> " + files[i] + " " + results_fill[0] + " " + results_fill[1]);

            results_mem = measureMemory(newG, l);
            System.out.println("Memory --> " + files[i] + " " + results_mem[0] + " " + results_mem[1]);

            System.out.println();

        }
    }

    public static void main(String args[])
    {
        Performance performance = new Performance();

        for (int i=100;i<=1000;i+=100)
        {
            performance.setN(i);
            System.out.println("N = " + i);
            performance.measurePerformance();
        }

    }

}

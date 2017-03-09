import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.*;

/**
 * Created by anubhabmajumdar on 3/4/17.
 */
public class Graph {


    HashMap g;
    HashMap clusterInfo;
    PApplet pApplet;

    public Graph(PApplet p)
    {
        pApplet = p;
    }

    public StringBuffer readFile(String fileName)
    {
        StringBuffer stringBuffer = new StringBuffer();

        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            fileReader.close();
//            System.out.println("Contents of file:");
//            System.out.println(stringBuffer.toString());

        } catch (IOException e) {
            System.out.println("Wrong filename");
            e.printStackTrace();
        }

        return stringBuffer;
    }

    public void makeGraph(String fileName)
    {
        g = new HashMap();
        clusterInfo = new HashMap();

        StringBuffer stringBuffer = readFile(fileName);
        String toString = stringBuffer.toString();
        String edges[] = toString.split("\\r?\\n");

        for (int i=1; i<edges.length; i++)
        {
            String cur = edges[i];
            String vals[] = cur.split("\\t");


            int fromNode = Integer.parseInt(vals[0]);
            int toNode = Integer.parseInt(vals[1]);
            float weight;

            if (vals.length == 4) {
                weight = Float.parseFloat(vals[2]);
                if (!clusterInfo.containsKey(fromNode))
                    clusterInfo.put(fromNode, Integer.parseInt(vals[3]));

            }
            else
            {
                weight = pApplet.random(1,100);
                if (!clusterInfo.containsKey(fromNode))
                    clusterInfo.put(fromNode, fromNode%10);
            }

                ArrayList<Edge> outEdges;

                if (g.containsKey(fromNode))
                {
                   outEdges  = (ArrayList<Edge>) g.get(fromNode);
                }
                else
                {
                    outEdges = new ArrayList<Edge>();
                }
                    outEdges.add(new Edge(fromNode, toNode, weight));
                    g.put(fromNode, outEdges);

        }
        System.out.println("Number of vertices = " + g.keySet().size());
        System.out.println("Number of edges = " + (edges.length-1));

        prettyPrint();

    }

    public void prettyPrint()
    {
        // Get a set of the entries
        Set set = g.entrySet();

        // Get an iterator
        Iterator i = set.iterator();

        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            System.out.print(me.getKey() + ": [ ");
            ArrayList<Edge> edges = (ArrayList<Edge>) me.getValue();

            for (int j=0; j<edges.size(); j++)
            {
                edges.get(j).prettyPrint();
                if (j!=edges.size()-1)
                    System.out.print(", ");
            }

            System.out.println(" ]");

        }
        System.out.println();

    }


}

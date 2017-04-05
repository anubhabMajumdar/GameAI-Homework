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
/* Followed the example provided here - http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html */
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

        } catch (IOException e) {
            System.out.println("Wrong filename");
            e.printStackTrace();
        }

        return stringBuffer;
    }
/* -------------------------------------------------------------------------------------------------------------------- */

    public void makeGraph(String fileName)
    {
        g = new HashMap();
        clusterInfo = new HashMap();
/* Followed the example provided here - http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html */
        StringBuffer stringBuffer = readFile(fileName);
        String toString = stringBuffer.toString();
/* -------------------------------------------------------------------------------------------------------------------- */

/* Followed the example provided here - http://stackoverflow.com/questions/454908/split-java-string-by-new-line */
        String edges[] = toString.split("\\r?\\n");
/* -------------------------------------------------------------------------------------------------------------------- */

        for (int i=1; i<edges.length; i++)
        {
            String cur = edges[i];
/* Followed the example provided here - http://stackoverflow.com/questions/1635764/string-parsing-in-java-with-delimeter-tab-t-using-split */
            String vals[] = cur.split("\\t");

/* -------------------------------------------------------------------------------------------------------------------- */

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
                {
                    String temp = Integer.toString(fromNode);
                    clusterInfo.put(fromNode, Integer.parseInt(temp.substring(0,2)));
                }

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
    }

    public void prettyPrint()
    {
/* Followed the example provided here - http://stackoverflow.com/questions/1066589/iterate-through-a-hashmap */
/* Followed the example provided here - https://www.tutorialspoint.com/java/java_using_iterator.htm */

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
/* -------------------------------------------------------------------------------------------------------------------- */


}

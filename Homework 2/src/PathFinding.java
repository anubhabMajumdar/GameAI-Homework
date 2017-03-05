import processing.core.PApplet;

import javax.xml.soap.Node;
import java.util.*;

/**
 * Created by anubhabmajumdar on 3/4/17.
 */

public class PathFinding {

    PApplet pApplet;

    public static Comparator<node> idComparator = new Comparator<node>(){

        @Override
        public int compare(node c1, node c2) {
            return (int) (c1.getCsf() - c2.getCsf());
        }

    };

    public PathFinding(PApplet p)
    {
        pApplet = p;
    }

    public ArrayList<Edge> dijkstra(HashMap graph, int start, int target)
    {

        PriorityQueue <node>  unvisitedNodes = new PriorityQueue <node> (idComparator);
        HashMap visitedNodes = new HashMap();

        int nodeName = start;
        float csf = 0;
        ArrayList<Edge> edges = new ArrayList<Edge>();

        unvisitedNodes.add(new node(nodeName, edges, 0));

        node curNode = null;

        while (!unvisitedNodes.isEmpty())
        {

//            System.out.print("Unvisited nodes = ");
//            Object n[] =  unvisitedNodes.toArray();
//            for (int i=0;i<unvisitedNodes.size();i++)
//                System.out.print(((node)n[i]).getNodeName() + ", ");
//            System.out.println();

            curNode = unvisitedNodes.poll();

            if (curNode.getNodeName() == target)
                break;

            System.out.println("Expanding node " + curNode.getNodeName());

            if (graph.containsKey(curNode.getNodeName()))
            {
                ArrayList<Edge> curEdges = (ArrayList<Edge>) graph.get(curNode.getNodeName());

                for (int j=0; j<curEdges.size(); j++)
                {
                    nodeName = curEdges.get(j).toNode;
                    if (!visitedNodes.containsKey(nodeName))
                    {
                        csf = curNode.csf + curEdges.get(j).weight;
                        edges = (ArrayList<Edge>) curNode.getEdges().clone();
                        edges.add(curEdges.get(j));

                        Iterator<node> itr = unvisitedNodes.iterator();
                        Boolean flag = true;

                        while (itr.hasNext())
                        {
                            node temp = itr.next();
                            if (temp.getNodeName() == nodeName)
                            {
                                if (temp.getCsf() > csf)
                                {
                                    unvisitedNodes.remove(temp);
                                }
                                else
                                {
                                    flag = false;
                                }
                                break;
                            }
                        }
                        if (flag)
                            unvisitedNodes.add(new node(nodeName, edges, csf));
                    }


                }

                visitedNodes.put(curNode.getNodeName(), 1);

//                System.out.println("Visited nodes = " + visitedNodes.keySet());
//                System.out.println();
//                System.out.println();

            }
        }


        if ((curNode == null) || (curNode.getEdges().get(curNode.getEdges().size()-1)).toNode!=target)
        {
            //printStat(visitedNodes.keySet().size(), -1);
            return null;
        }
        else
        {
            int cost = 0;
            for (int i=0;i<curNode.getEdges().size();i++)
            {
                cost += curNode.getEdges().get(i).getWeight();
            }
            //printStat(visitedNodes.keySet().size(), cost);
            return curNode.getEdges();
        }

    }

    public void prettyPrintPath(ArrayList<Edge> edges)
    {
        System.out.print("Path --> ");
        for (int j=0; j<edges.size(); j++)
        {
            edges.get(j).prettyPrint();
            System.out.print("  ");

        }
        System.out.println();
    }

    public void printStat(int expandedNodes, int cost)
    {
        System.out.println("Number of nodes expanded = " + expandedNodes);
        if (cost == -1)
            System.out.println("Total cost = infinity");
        else
            System.out.println("Total cost = " + cost);

    }

    public class node
    {
        int nodeName;
        float csf;
        ArrayList<Edge> edges;

        public node(int n, ArrayList<Edge> e, float c)
        {
            nodeName = n;
            edges = e;
            csf = c;
        }

        public float getCsf() {
            return csf;
        }

        public void setCsf(float csf) {
            this.csf = csf;
        }

        public int getNodeName() {
            return nodeName;
        }

        public ArrayList<Edge> getEdges() {
            return edges;
        }
    }



}

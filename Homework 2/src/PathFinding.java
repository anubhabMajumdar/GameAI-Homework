import processing.core.PApplet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by anubhabmajumdar on 3/4/17.
 * Read a lot of blog post to use priorityQueue in the program. They are referenced below :
 * http://www.journaldev.com/1642/java-priority-queue-priorityqueue-example
 * http://www.programcreek.com/2009/02/using-the-priorityqueue-class-example/
 * http://www.javatpoint.com/java-priorityqueue
 * https://www.tutorialspoint.com/java/util/java_util_priorityqueue.htm
 * Referenced the pathfinding algorithms from class notes
 */

public class PathFinding {

    PApplet pApplet;
    float cost;
    int fill;
    int maxUnvisitedNodeList;

/* Followed the example provided here - http://www.programcreek.com/2009/02/using-the-priorityqueue-class-example/ */
    public static Comparator<Node> idComparatorDijkstra = new Comparator<Node>(){

        @Override
        public int compare(Node c1, Node c2) {
            return (int) (c1.getCsf() - c2.getCsf());
        }

    };
/* -------------------------------------------------------------------------------------------------------------------- */

/* Followed the example provided here - http://www.programcreek.com/2009/02/using-the-priorityqueue-class-example/ */
    public static Comparator<Node> idComparatorAStar = new Comparator<Node>(){

        @Override
        public int compare(Node c1, Node c2) {
            return (int) (c1.getEtc() - c2.getEtc());
        }

    };
/* -------------------------------------------------------------------------------------------------------------------- */

    public PathFinding(PApplet p)
    {
        pApplet = p;
        ArrayList<Edge> edges = new ArrayList<Edge>();

    }

    public ArrayList<Edge> dijkstra(Graph worldGraph, int start, int target)
    {
        maxUnvisitedNodeList = 0;
        fill = 0;
        HashMap graph = worldGraph.g;
/* Followed the example provided here - http://www.programcreek.com/2009/02/using-the-priorityqueue-class-example/ */
        PriorityQueue <Node>  unvisitedNodes = new PriorityQueue <Node> (idComparatorDijkstra);
/* -------------------------------------------------------------------------------------------------------------------- */

        HashMap visitedNodes = new HashMap();

        int nodeName = start;
        float csf = 0;
        ArrayList<Edge> edges = new ArrayList<Edge>();

        unvisitedNodes.add(new Node(nodeName, edges, 0));

        Node curNode = null;

        while (!unvisitedNodes.isEmpty())
        {

            curNode = unvisitedNodes.poll();

            if (curNode.getNodeName() == target)
                break;

            //System.out.println("Expanding Node " + curNode.getNodeName());

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

                        Iterator<Node> itr = unvisitedNodes.iterator();
                        Boolean flag = true;

                        while (itr.hasNext())
                        {
                            Node temp = itr.next();
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
                            unvisitedNodes.add(new Node(nodeName, edges, csf));
                    }


                }

                visitedNodes.put(curNode.getNodeName(), 1);

//                System.out.println("Visited nodes = " + visitedNodes.keySet());
//                System.out.println();
//                System.out.println();

            }
            if (unvisitedNodes.size()>maxUnvisitedNodeList)
                maxUnvisitedNodeList = unvisitedNodes.size();
        }


        if ((curNode == null) || (curNode.getNodeName()!=target))
        {
            cost = -1;
            fill = visitedNodes.keySet().size();
            return null;
        }
        else
        {
            cost = 0;
            for (int i=0;i<curNode.getEdges().size();i++)
            {
                cost += curNode.getEdges().get(i).getWeight();
            }
            fill = visitedNodes.keySet().size();
            return curNode.getEdges();
        }

    }   // End of dijkstra's algorithm


    public ArrayList<Edge> aStar(Graph worldGraph, int start, int target, String heuristicName, String graphName)
    {
        maxUnvisitedNodeList = 0;
        fill = 0;
        HashMap graph = worldGraph.g;
        PriorityQueue <Node>  unvisitedNodes = new PriorityQueue <Node> (idComparatorAStar);
        HashMap visitedNodes = new HashMap();
        Heuristic h = new Heuristic(heuristicName, worldGraph, target, graphName);

        int nodeName = start;
        float csf = 0;
        ArrayList<Edge> edges = new ArrayList<Edge>();

        Node curNode = new Node(nodeName, edges, csf);

        float heuristic = h.heuristicValue(curNode);
        float etc = csf + heuristic;
        curNode.setEtc(etc);

        unvisitedNodes.add(curNode);


        while (!unvisitedNodes.isEmpty())
        {
            curNode = unvisitedNodes.poll();

            if (curNode.getNodeName() == target)
                break;

            //System.out.println("Expanding Node " + curNode.getNodeName());

            if (graph.containsKey(curNode.getNodeName()))
            {
                ArrayList<Edge> curEdges = (ArrayList<Edge>) graph.get(curNode.getNodeName());

                for (int j=0; j<curEdges.size(); j++)
                {
                    nodeName = curEdges.get(j).toNode;
                    csf = curNode.getCsf() + curEdges.get(j).getWeight();
                    edges = (ArrayList<Edge>) curNode.getEdges().clone();
                    edges.add(curEdges.get(j));
                    etc = csf + h.heuristicValue(new Node(nodeName, edges, csf));

                    if (!visitedNodes.containsKey(nodeName))
                    {
                        Iterator<Node> itr = unvisitedNodes.iterator();
                        Boolean flag = true;

                        while (itr.hasNext())
                        {
                            Node temp = itr.next();
                            if (temp.getNodeName() == nodeName)
                            {
                                if (temp.getEtc() > etc)
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
                        {
                            Node tempNode = new Node(nodeName, edges, csf);
                            tempNode.setEtc(etc);
                            unvisitedNodes.add(tempNode);
                        }
                    }
                    else
                    {
                        Node tempNode = (Node) visitedNodes.get(nodeName);
                        //if (csf < tempNode.getCsf())
                        if (etc < tempNode.getEtc())
                        {
                            visitedNodes.remove(curNode.getNodeName());
                            tempNode = new Node(nodeName, edges, csf);
                            //etc = csf + h.heuristicValue(tempNode);
                            tempNode.setEtc(etc);
                            unvisitedNodes.add(tempNode);
                        }
                    }
                }

                visitedNodes.put(curNode.getNodeName(), curNode);

//                System.out.println("Visited nodes = " + visitedNodes.keySet());
//                System.out.println();
//                System.out.println();

            }
            if (unvisitedNodes.size()>maxUnvisitedNodeList)
                maxUnvisitedNodeList = unvisitedNodes.size();
        }


        if ((curNode == null) || (curNode.getNodeName()!=target))
        {
            //printStat(visitedNodes.keySet().size(), -1);
            cost = -1;
            fill = visitedNodes.keySet().size();
            return null;
        }
        else
        {
            cost = 0;
            for (int i=0;i<curNode.getEdges().size();i++)
            {
                cost += curNode.getEdges().get(i).getWeight();
            }
            fill = visitedNodes.keySet().size();
            //printStat(visitedNodes.keySet().size(), cost);
            return curNode.getEdges();
        }
    } // End of aStar algorithm

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

    public class Heuristic
    {
        String heuristicName;
        int target;
        Graph worldGraph;
        HashMap graph;
        String graphName;
        int[][] cd;

        public Heuristic(String heuristicName, Graph worldGraph, int end, String graphName)
        {
            this.heuristicName = heuristicName;
            this.target = end;
            this.worldGraph = worldGraph;
            this.graph = worldGraph.g;
            this.graphName = graphName;
            this.cd = getClusterLookUp("clusterLookUpTable.txt");
        }

        public void setHeuristicName(String heuristicName)
        {
            this.heuristicName = heuristicName;
        }

        public float heuristicValue(Node n)
        {
            if (heuristicName.equals("distanceHeuristic"))
                return distanceHeuristic(n);
            else if ((heuristicName.equals("clusterHeuristic")))
                return clusterHeuristic(n);

            return distanceHeuristic(n);

        }

        private float distanceHeuristic(Node n)
        {
            return (float) Math.abs(Math.pow((n.getNodeName()-target), 2));

        }

        private float clusterHeuristic(Node n)
        {
            if (graphName.equals("cit-HepPh.txt"))
            {
                return cluterHeuristicLargeGraph(n);
            }
            else
                return cluterHeuristicWorldGraph(n, cd);
        }

        private float cluterHeuristicLargeGraph(Node n)
        {
            //System.out.println("Large Graph");
            if (worldGraph.clusterInfo.get(n.getNodeName()) == worldGraph.clusterInfo.get(target))
                return 0;
            else
                if ((worldGraph.clusterInfo.containsKey(n.getNodeName())) && (worldGraph.clusterInfo.containsKey(target)))
                    return Math.abs(((int)worldGraph.clusterInfo.get(n.getNodeName()) - (int)worldGraph.clusterInfo.get(target)) * 100);
                else
                    return 100;

        }

        private float cluterHeuristicWorldGraph(Node n, int[][] clusterDist)
        {
//            System.out.println(n.getNodeName());
            int nodeCluster = (int) worldGraph.clusterInfo.get(n.getNodeName());
            int targetCluster = (int) worldGraph.clusterInfo.get(target);

            return (float) Math.pow(clusterDist[nodeCluster-1][targetCluster-1],2);
        }

        private int[][] getClusterLookUp(String fileName)
        {
            /* Followed the example provided here - http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html */

            StringBuffer stringBuffer = readFile(fileName);
            String toString = stringBuffer.toString();
            String edges[] = toString.split("\\r?\\n");

            int clusterDist[][] = new int[edges.length-1][edges.length-1];

            for (int i=1;i<edges.length;i++)
            {
                String cur = edges[i];
                String vals[] = cur.split("\\t");

                for (int j=1;j<vals.length;j++)
                {
                    clusterDist[i-1][j-1] = Integer.parseInt(vals[j]);
                }
            }

            return clusterDist;

        }
/* -------------------------------------------------------------------------------------------------------------------- */

        public StringBuffer readFile(String fileName)
        {
            /* Followed the example provided here - http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html */
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

            } catch (IOException e) {
                System.out.println("Wrong filename");
                e.printStackTrace();
            }

            return stringBuffer;
        }
    }
/* -------------------------------------------------------------------------------------------------------------------- */

    public class Node
    {
        int nodeName;
        float csf, etc;
        ArrayList<Edge> edges;

        public Node(int n, ArrayList<Edge> e, float c)
        {
            nodeName = n;
            edges = e;
            csf = c;
            etc = csf;
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

        public float getEtc() {
            return etc;
        }

        public void setEtc(float etc) {
            this.etc = etc;
        }
    }



}

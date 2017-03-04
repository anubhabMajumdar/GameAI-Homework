/**
 * Created by anubhabmajumdar on 3/4/17.
 */
public class Edge {
    int fromNode, toNode;
    float weight;

    public Edge(int f, int t, float w)
    {
        fromNode = f;
        toNode = t;
        weight = w;
    }

    public int getToNode() {
        return toNode;
    }

    public float getWeight() {
        return weight;
    }

    public void prettyPrint()
    {
        System.out.print("(" + toNode + ", " + weight + ")");
    }
}

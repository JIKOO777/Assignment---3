package mst.assign3;
import java.util.*;
public class Graph {
    public final int n;
    public final List<Edge> edges;
    public final List<List<Edge>> adj;
    public Graph(int n, List<Edge> edges){
        this.n=n; this.edges=List.copyOf(edges);
        adj=new ArrayList<>(n);
        for(int i=0;i<n;i++) adj.add(new ArrayList<>());
        for(Edge e:edges){
            adj.get(e.u).add(e);
            adj.get(e.v).add(new Edge(e.v,e.u,e.w));
        }
    }
    public int edgeCount(){ return edges.size(); }
}

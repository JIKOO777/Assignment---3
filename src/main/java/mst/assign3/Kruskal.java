package mst.assign3;

import java.util.*;

public class Kruskal {
    public static Models.RunMetrics run(Graph graph) {
        Models.RunMetrics res = new Models.RunMetrics();
        res.algo = "Kruskal";
        res.vertices = graph.n;
        res.edges = graph.edgeCount();

        long t0 = System.nanoTime();

        List<Edge> edges = new ArrayList<>(graph.edges);
        edges.sort(Comparator.comparingDouble(e -> e.w));
        UnionFind uf = new UnionFind(graph.n);

        List<Edge> mstEdges = new ArrayList<>();
        double totalCost = 0.0;

        for (Edge e : edges) {
            int ru = uf.find(e.u);
            int rv = uf.find(e.v);
            res.finds += 2;
            res.comparisons++;
            if (ru != rv) {
                uf.union(ru, rv);
                res.unions++;
                mstEdges.add(e);
                totalCost += e.w;
            }
        }

        long t1 = System.nanoTime();
        long dt = t1 - t0;

        res.timeUs = dt / 1_000.0;
        res.timeMs = dt / 1_000_000.0;

        res.mstEdges = mstEdges;
        res.mstCost = totalCost;
        res.connected = mstEdges.size() == graph.n - 1;
        return res;
    }
}

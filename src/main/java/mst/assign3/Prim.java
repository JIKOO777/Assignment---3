package mst.assign3;

import java.util.*;

public class Prim {

    public static Models.RunMetrics run(Graph g, int start) {
        var res = new Models.RunMetrics();
        res.algo = "Prim";
        res.vertices = g.n;
        res.edges = g.edgeCount();
        res.mstEdges = new ArrayList<>();

        boolean[] used = new boolean[g.n];
        double[] key = new double[g.n];
        int[] parent = new int[g.n];
        Arrays.fill(key, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);

        record PQItem(int v, double w) {}
        PriorityQueue<PQItem> pq = new PriorityQueue<>(Comparator.comparingDouble(i -> i.w));
        long comparisons = 0;

        long t0 = System.nanoTime();
        key[start] = 0;
        pq.add(new PQItem(start, 0));

        while (!pq.isEmpty()) {
            var cur = pq.poll();
            int u = cur.v();
            if (used[u]) continue;
            used[u] = true;

            if (parent[u] != -1) {
                res.mstEdges.add(new Edge(parent[u], u, key[u]));
            }

            for (Edge e : g.adj.get(u)) {
                comparisons++;
                if (!used[e.v] && e.w < key[e.v]) {
                    key[e.v] = e.w;
                    parent[e.v] = u;
                    pq.add(new PQItem(e.v, key[e.v]));
                }
            }
        }

        long t1 = System.nanoTime();
        boolean connected = true;
        for (boolean b : used) connected &= b;

        res.connected = connected;
        res.mstCost = connected ? res.mstEdges.stream().mapToDouble(ed -> ed.w).sum() : Double.NaN;
        res.timeMs = (t1 - t0) / 1_000_000;
        res.comparisons = comparisons;
        res.unions = 0;
        res.finds = 0;

        return res;
    }
}

package mst.assign3;

import java.util.*;

public class Prim {
    public static Models.RunMetrics run(Graph graph, int start) {
        Models.RunMetrics res = new Models.RunMetrics();
        res.algo = "Prim";
        res.vertices = graph.n;
        res.edges = graph.edgeCount();

        long t0 = System.nanoTime();

        boolean[] visited = new boolean[graph.n];
        List<Edge> mstEdges = new ArrayList<>();
        double totalCost = 0.0;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.w));
        visited[start] = true;
        pq.addAll(graph.adj.get(start));
        res.comparisons += graph.adj.get(start).size();

        while (!pq.isEmpty()) {
            Edge e = pq.poll();
            int u = e.u, v = e.v;
            if (visited[u] && visited[v]) continue;

            mstEdges.add(e);
            totalCost += e.w;
            int next = visited[u] ? v : u;
            visited[next] = true;

            for (Edge ne : graph.adj.get(next)) {
                if (!visited[ne.v]) {
                    pq.add(ne);
                    res.comparisons++;
                }
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

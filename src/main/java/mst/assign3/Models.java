package mst.assign3;

import java.util.ArrayList;
import java.util.List;

public class Models {

    public static class InputEdge { public int u, v; public double w; }
    public static class InputGraph { public String name; public Integer id; public int n; public List<InputEdge> edges; }
    public static class InputRoot { public List<InputGraph> datasets; }

    public static class RunMetrics {
        public String algo;
        public int vertices;
        public int edges;
        public double mstCost;
        public List<Edge> mstEdges;
        public boolean connected;
        public long timeMs;
        public long comparisons;
        public long unions;
        public long finds;
        public String dataset;
    }

    public static class InputStats { public int vertices; public int edges; }
    public static class AlgoBlock {
        public List<Edge> mst_edges;
        public double total_cost;
        public long operations_count;
        public double execution_time_ms;
    }
    public static class OutputEntry {
        public int graph_id;
        public InputStats input_stats;
        public AlgoBlock prim;
        public AlgoBlock kruskal;
    }
    public static class OutputRootV2 {
        public List<OutputEntry> results = new ArrayList<>();
    }
}

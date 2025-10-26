package mst.assign3;

import java.util.ArrayList;
import java.util.List;

public class Models {
    // ---- Вход (старый формат datasets)
    public static class InputEdge { public int u, v; public double w; }
    public static class InputGraph { public String name; public Integer id; public int n; public List<InputEdge> edges; }
    public static class InputRoot { public List<InputGraph> datasets; }

    // ---- Вход (новый формат graphs)
    public static class NamedEdge { public String from; public String to; public double weight; }
    public static class NamedGraph { public Integer id; public List<String> nodes; public List<NamedEdge> edges; public String name; }
    public static class GraphsRoot { public List<NamedGraph> graphs; }

    // ---- Метрики одного запуска
    public static class RunMetrics {
        public String algo;
        public int vertices;
        public int edges;
        public double mstCost;
        public List<Edge> mstEdges;
        public boolean connected;
        public long comparisons;
        public long unions;
        public long finds;
        public double timeUs;   // микросекунды
        public double timeMs;   // миллисекунды (дробные)
        public String dataset;
    }

    // ---- Выходной формат
    public static class InputStats { public int vertices; public int edges; }
    public static class AlgoBlock {
        public List<Edge> mst_edges;
        public double total_cost;
        public long operations_count;
        public double execution_time_us;   // время в микросекундах
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

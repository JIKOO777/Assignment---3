package mst.assign3;

import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Standalone visualizer for SMALL dataset.
 * Reads either { "datasets": [...] } with (n, edges[{u,v,w}])
 * or { "graphs": [...] } with (nodes[], edges[{from,to,weight}]) and shows a Swing window.
 */
public class GraphVisualizer {

    public static void main(String[] args) throws Exception {
        // путь к small-файлу (можешь поменять при запуске через аргумент)
        String inputPath = (args.length > 0) ? args[0] : "data/assign_3_input_small_v2.json";

        String json = Files.readString(Path.of(inputPath));
        Gson gson = new Gson();

        // Попытка №1: формат datasets
        Graph g = tryBuildFromDatasets(gson, json);
        String title = null;

        if (g != null) {
            title = "Graph Visualizer — datasets[0]";
        } else {
            // Попытка №2: формат graphs (именованные вершины)
            g = tryBuildFromGraphs(gson, json);
            title = "Graph Visualizer — graphs[0]";
        }

        if (g == null) {
            System.err.println("❌ Unsupported input format: neither 'datasets' nor 'graphs' found.");
            System.exit(2);
            return;
        }

        System.out.println("Loaded graph: " + g.n + " vertices, " + g.edgeCount() + " edges");
        g.visualizeGUI(title);
    }

    private static Graph tryBuildFromDatasets(Gson gson, String json) {
        try {
            Models.InputRoot root = gson.fromJson(json, Models.InputRoot.class);
            if (root == null || root.datasets == null || root.datasets.isEmpty()) return null;

            Models.InputGraph ig = root.datasets.get(0); // берём первый граф
            var edges = new ArrayList<Edge>();
            if (ig.edges != null) {
                for (Models.InputEdge e : ig.edges) edges.add(new Edge(e.u, e.v, e.w));
            }
            return new Graph(ig.n, edges);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Graph tryBuildFromGraphs(Gson gson, String json) {
        try {
            Models.GraphsRoot root = gson.fromJson(json, Models.GraphsRoot.class);
            if (root == null || root.graphs == null || root.graphs.isEmpty()) return null;

            Models.NamedGraph ng = root.graphs.get(0); // берём первый граф

            // map имён вершин -> индексы 0..n-1
            var idx = new HashMap<String, Integer>();
            if (ng.nodes != null && !ng.nodes.isEmpty()) {
                for (int i = 0; i < ng.nodes.size(); i++) idx.put(ng.nodes.get(i), i);
            } else if (ng.edges != null) {
                for (Models.NamedEdge e : ng.edges) {
                    idx.computeIfAbsent(e.from, k -> idx.size());
                    idx.computeIfAbsent(e.to,   k -> idx.size());
                }
            }
            int n = idx.size();
            var edges = new ArrayList<Edge>();
            if (ng.edges != null) {
                for (Models.NamedEdge e : ng.edges) {
                    Integer u = idx.get(e.from), v = idx.get(e.to);
                    if (u != null && v != null) edges.add(new Edge(u, v, e.weight));
                }
            }
            return new Graph(n, edges);
        } catch (Exception ignored) {
            return null;
        }
    }
}

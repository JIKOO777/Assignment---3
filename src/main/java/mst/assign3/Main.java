package mst.assign3;

import com.google.gson.*;
import java.nio.file.*;
import java.util.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java -jar assignment3-mst-1.0.0-shaded.jar <input.json> <output_dir>");
            System.exit(1);
        }

        Path input = Paths.get(args[0]);
        Path outDir = Paths.get(args[1]);
        Files.createDirectories(outDir);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = Files.readString(input, UTF_8);

        Models.InputRoot rootDatasets = null;
        Models.GraphsRoot rootGraphs = null;
        try { rootDatasets = gson.fromJson(json, Models.InputRoot.class); } catch (Exception ignored) {}
        try { rootGraphs = gson.fromJson(json, Models.GraphsRoot.class); } catch (Exception ignored) {}

        boolean useDatasets = (rootDatasets != null && rootDatasets.datasets != null && !rootDatasets.datasets.isEmpty());
        boolean useGraphs = (rootGraphs != null && rootGraphs.graphs != null && !rootGraphs.graphs.isEmpty());

        if (!useDatasets && !useGraphs) {
            System.err.println("❌ Input JSON doesn't contain 'datasets' or 'graphs'.");
            System.exit(2);
        }

        Models.OutputRootV2 out = new Models.OutputRootV2();
        List<String> csv = new ArrayList<>();
        csv.add("graph_id,function_name,total_cost,time_us,operations_count,vertices,edges,connected,dataset_name");

        int autoId = 1;

        if (useDatasets) {
            for (Models.InputGraph ig : rootDatasets.datasets) {
                int gid = (ig.id != null ? ig.id : autoId++);
                Graph g = toGraphFromDatasets(ig);
                runBoth(out, csv, g, gid, ig.name);
            }
        } else {
            for (Models.NamedGraph ng : rootGraphs.graphs) {
                int gid = (ng.id != null ? ng.id : autoId++);
                Map<String,Integer> idx = new HashMap<>();
                if (ng.nodes != null && !ng.nodes.isEmpty()) {
                    for (int i = 0; i < ng.nodes.size(); i++) idx.put(ng.nodes.get(i), i);
                } else {
                    for (Models.NamedEdge e : ng.edges) {
                        idx.computeIfAbsent(e.from, k -> idx.size());
                        idx.computeIfAbsent(e.to, k -> idx.size());
                    }
                }

                List<Edge> edges = new ArrayList<>();
                for (Models.NamedEdge e : ng.edges) {
                    Integer u = idx.get(e.from), v = idx.get(e.to);
                    if (u == null || v == null) continue;
                    edges.add(new Edge(u, v, e.weight));
                }

                Graph g = new Graph(idx.size(), edges);
                runBoth(out, csv, g, gid, ng.name != null ? ng.name : "graph_" + gid);
            }
        }

        String type = outDir.getFileName().toString().toLowerCase();
        Files.writeString(outDir.resolve("output.json"), gson.toJson(out), UTF_8);
        Files.writeString(outDir.resolve(type + ".csv"), String.join("\n", csv), UTF_8);

        System.out.println("Done → " + outDir.toAbsolutePath());
    }

    private static Graph toGraphFromDatasets(Models.InputGraph ig){
        List<Edge> edges = new ArrayList<>();
        for (Models.InputEdge e : ig.edges) edges.add(new Edge(e.u, e.v, e.w));
        return new Graph(ig.n, edges);
    }

    private static void runBoth(Models.OutputRootV2 out, List<String> csv, Graph g, int gid, String name) {
        var prim = Prim.run(g, 0);
        var kruskal = Kruskal.run(g);

        Models.OutputEntry e = new Models.OutputEntry();
        e.graph_id = gid;
        e.input_stats = new Models.InputStats();
        e.input_stats.vertices = g.n;
        e.input_stats.edges = g.edgeCount();

        e.prim = new Models.AlgoBlock();
        e.prim.mst_edges = prim.mstEdges;
        e.prim.total_cost = prim.mstCost;
        e.prim.operations_count = prim.comparisons;
        e.prim.execution_time_us = prim.timeUs;

        e.kruskal = new Models.AlgoBlock();
        e.kruskal.mst_edges = kruskal.mstEdges;
        e.kruskal.total_cost = kruskal.mstCost;
        e.kruskal.operations_count = kruskal.comparisons + kruskal.unions + kruskal.finds;
        e.kruskal.execution_time_us = kruskal.timeUs;

        out.results.add(e);

        csv.add(String.join(",",
                String.valueOf(gid), "Prim",
                String.valueOf(e.prim.total_cost),
                String.valueOf(prim.timeUs),
                String.valueOf(e.prim.operations_count),
                String.valueOf(g.n),
                String.valueOf(g.edgeCount()),
                String.valueOf(prim.connected),
                name == null ? "" : name.replace(',', ';')
        ));
        csv.add(String.join(",",
                String.valueOf(gid), "Kruskal",
                String.valueOf(e.kruskal.total_cost),
                String.valueOf(kruskal.timeUs),
                String.valueOf(e.kruskal.operations_count),
                String.valueOf(g.n),
                String.valueOf(g.edgeCount()),
                String.valueOf(kruskal.connected),
                name == null ? "" : name.replace(',', ';')
        ));
    }
}

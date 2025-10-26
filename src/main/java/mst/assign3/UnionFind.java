package mst.assign3;

public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    public int unions = 0;
    public int finds = 0;

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    // Finds the root of a set (with path compression)
    private int find(int x) {
        finds++;
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    // Combines two quantities
    public boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) return false; // уже соединены → цикл

        if (rank[rootA] < rank[rootB]) {
            parent[rootA] = rootB;
        } else if (rank[rootA] > rank[rootB]) {
            parent[rootB] = rootA;
        } else {
            parent[rootB] = rootA;
            rank[rootA]++;
        }
        unions++;
        return true;
    }

    public boolean same(int a, int b) {
        return find(a) == find(b);
    }
}

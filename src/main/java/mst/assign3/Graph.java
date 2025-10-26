package mst.assign3;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

/** Undirected weighted graph with adjacency list + simple Swing visualization. */
public class Graph {
    public final int n;
    public final List<Edge> edges;
    public final List<List<Edge>> adj;

    public Graph(int n, List<Edge> edges) {
        this.n = n;
        this.edges = List.copyOf(edges);
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.u).add(e);
            adj.get(e.v).add(new Edge(e.v, e.u, e.w)); // undirected mirror
        }
    }

    public int edgeCount() { return edges.size(); }

    /** Show a simple GUI window with the graph drawn on a circle. */
    public void visualizeGUI(String title) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title == null ? "Graph Visualization" : title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);

            JPanel panel = new JPanel() {
                @Override protected void paintComponent(Graphics g2) {
                    super.paintComponent(g2);
                    Graphics2D g = (Graphics2D) g2;
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setStroke(new BasicStroke(2f));

                    int W = getWidth(), H = getHeight();
                    int R = Math.min(W, H) / 3;
                    int CX = W / 2, CY = H / 2;

                    // positions on a circle
                    Point[] pos = new Point[n];
                    for (int i = 0; i < n; i++) {
                        double ang = 2 * Math.PI * i / Math.max(1, n);
                        pos[i] = new Point(
                                CX + (int)(R * Math.cos(ang)),
                                CY + (int)(R * Math.sin(ang))
                        );
                    }

                    // draw edges (use original list to avoid duplicates)
                    g.setColor(Color.GRAY);
                    for (Edge e : edges) {
                        Point p1 = pos[e.u], p2 = pos[e.v];
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);

                        // weight label at midpoint
                        int mx = (p1.x + p2.x) / 2;
                        int my = (p1.y + p2.y) / 2;
                        String wtxt = (e.w == Math.rint(e.w)) ? String.valueOf((int)e.w) : String.format("%.1f", e.w);
                        g.setColor(new Color(0, 90, 200));
                        g.drawString(wtxt, mx, my);
                        g.setColor(Color.GRAY);
                    }

                    // draw nodes
                    int r = 18;
                    for (int i = 0; i < n; i++) {
                        Point p = pos[i];
                        g.setColor(new Color(220, 60, 60));
                        g.fillOval(p.x - r, p.y - r, 2*r, 2*r);
                        g.setColor(Color.WHITE);
                        String label = String.valueOf(i);
                        int tx = p.x - g.getFontMetrics().stringWidth(label)/2;
                        int ty = p.y + 4;
                        g.drawString(label, tx, ty);
                    }
                }
            };

            frame.add(panel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

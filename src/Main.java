
import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.ws.container.SumoPosition2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    public static void main(String[] args) {

        System.out.println("Launching SUMO map WITH SUMO (vehicle positions)...");

        // IntelliJ-safe hardcoded config
        String sumoBinary = "sumo-gui";
        String sumoConfig = "SumoConfig/hello.sumocfg";

        // Optional override: args[0]=binary, args[1]=sumocfg
        if (args.length > 0) sumoBinary = args[0];
        if (args.length > 1) sumoConfig = args[1];

        // ✅ IMPORTANT: use the (binary, configFile) constructor
        final SumoTraciConnection conn = new SumoTraciConnection(sumoBinary, sumoConfig);

        // Start immediately
        conn.addOption("start", "true");

        try {
            System.out.println("Starting SUMO with -c " + sumoConfig);
            conn.runServer();
            System.out.println("Connected to SUMO via TraCI!");

            final SumoVehicleMapPanel panel = new SumoVehicleMapPanel();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JFrame frame = new JFrame("SUMO Vehicle Map (TraCI)");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.add(panel);
                    frame.setSize(900, 700);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });

            Thread simThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int step = 0; step < 1000000; step++) {
                            conn.do_timestep();
                            panel.updateFromSimulation(conn);

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    panel.repaint();
                                }
                            });

                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try { conn.close(); } catch (Exception ignore) {}
                    }
                }
            });

            simThread.setDaemon(true);
            simThread.start();

        } catch (Exception e) {
            System.err.println("Failed to run SUMO: " + e.getMessage());
            e.printStackTrace();
            try { conn.close(); } catch (Exception ignore) {}
        }
    }
}

class SumoVehicleMapPanel extends JPanel {

    private final Map<String, Point2D.Double> vehiclePositions =
            new ConcurrentHashMap<String, Point2D.Double>();

    private double minX = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;

    public SumoVehicleMapPanel() {
        setBackground(Color.WHITE);
    }

    @SuppressWarnings("unchecked")
    public void updateFromSimulation(SumoTraciConnection conn) {
        try {
            List<String> vehIds = (List<String>) conn.do_job_get(Vehicle.getIDList());
			//System.out.println(vehIds);

            if (vehIds.isEmpty()) {
                vehiclePositions.clear();
                return;
            }

            minX = Double.POSITIVE_INFINITY;
            maxX = Double.NEGATIVE_INFINITY;
            minY = Double.POSITIVE_INFINITY;
            maxY = Double.NEGATIVE_INFINITY;

            vehiclePositions.clear();

            for (String id : vehIds) {
                Object posObj = conn.do_job_get(Vehicle.getPosition(id));

                double x;
                double y;

                if (posObj instanceof SumoPosition2D) {
                    SumoPosition2D p = (SumoPosition2D) posObj;
                    x = p.x;
                    y = p.y;
                } else if (posObj instanceof double[]) {
                    double[] arr = (double[]) posObj;
                    if (arr.length < 2) continue;
                    x = arr[0];
                    y = arr[1];
                } else {
                    continue;
                }

                vehiclePositions.put(id, new Point2D.Double(x, y));

                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (vehiclePositions.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("No vehicles yet. (Flows may start later)", 20, 20);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int margin = 40;

        double netW = maxX - minX;
        double netH = maxY - minY;
        if (netW <= 0 || netH <= 0) return;

        double scaleX = (w - 2.0 * margin) / netW;
        double scaleY = (h - 2.0 * margin) / netH;
        double scale = Math.min(scaleX, scaleY);

        g2.setColor(new Color(240, 240, 240));
        g2.fillRect(margin, margin, w - 2 * margin, h - 2 * margin);

        int radius = 6;

        for (Map.Entry<String, Point2D.Double> e : vehiclePositions.entrySet()) {
            String id = e.getKey();
            Point2D.Double p = e.getValue();

            double sx = (p.x - minX) * scale + margin;
            double sy = h - ((p.y - minY) * scale + margin);

            int x = (int) Math.round(sx);
            int y = (int) Math.round(sy);

            g2.setColor(Color.RED);
            g2.fillOval(x - radius, y - radius, radius * 2, radius * 2);

            g2.setColor(Color.BLACK);
            g2.setFont(g2.getFont().deriveFont(10f));
            g2.drawString(id, x + radius + 2, y + 2);
        }
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JFrame f = new JFrame("Map Visualization - 10 Cars (RED stop / GREEN go)");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setContentPane(new TrafficSimPanel());
                f.setSize(1040, 560);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}

class TrafficSimPanel extends JPanel {

    private double cx, cy;
    private final double gap = 22;
    private final double roadW = 18;
    private final double boxHalf = 150;

    private final List<VisualCar> cars = new ArrayList<VisualCar>(); // exactly 10 cars
    private final TrafficLights lights = new TrafficLights();
    private long lastNanos = System.nanoTime();

    private final Timer timer;

    TrafficSimPanel() {
        setBackground(Color.WHITE);

        // EXACTLY 10 cars
        // 3 EAST, 3 WEST, 2 SOUTH, 2 NORTH
        cars.add(new VisualCar(Direction.EAST,  -900, +gap, 150, new Color(220, 50, 50)));
        cars.add(new VisualCar(Direction.EAST,  -600, +gap, 140, new Color(30, 140, 255)));
        cars.add(new VisualCar(Direction.EAST,  -300, +gap, 160, new Color(255, 140, 0)));

        cars.add(new VisualCar(Direction.WEST,   900, -gap, 150, new Color(50, 180, 80)));
        cars.add(new VisualCar(Direction.WEST,   600, -gap, 145, new Color(140, 80, 255)));
        cars.add(new VisualCar(Direction.WEST,   300, -gap, 155, new Color(0, 160, 160)));

        cars.add(new VisualCar(Direction.SOUTH, +gap, -900, 160, new Color(180, 90, 230)));
        cars.add(new VisualCar(Direction.SOUTH, +gap, -450, 145, new Color(120, 120, 120)));

        cars.add(new VisualCar(Direction.NORTH, -gap,  900, 170, new Color(255, 80, 120)));
        cars.add(new VisualCar(Direction.NORTH, -gap,  450, 155, new Color(220, 50, 50)));

        timer = new Timer(16, e -> tick());
        timer.start();
    }

    private void tick() {
        long now = System.nanoTime();
        double dt = (now - lastNanos) / 1_000_000_000.0;
        lastNanos = now;

        lights.update(dt);

        for (VisualCar c : cars) {
            double stopLine = stopLineFor(c.dir);      //  RELATIVE stop line (center = 0)
            boolean red = lights.isRedFor(c.dir);      //  RED = stop
            c.update(dt, stopLine, red);
            wrapIfOutOfScreen(c);
        }

        repaint();
    }

    private void wrapIfOutOfScreen(VisualCar c) {
        double limit = 1000;
        switch (c.dir) {
            case EAST:  if (c.x > limit)  c.x = -limit; break;
            case WEST:  if (c.x < -limit) c.x =  limit; break;
            case SOUTH: if (c.y > limit)  c.y = -limit; break;
            case NORTH: if (c.y < -limit) c.y =  limit; break;
        }
    }

    //  stop lines are relative to center (0)
    private double stopLineFor(Direction d) {
        double stopMargin = 18;
        switch (d) {
            case EAST:  return (-boxHalf - stopMargin);
            case WEST:  return ( boxHalf + stopMargin);
            case SOUTH: return (-boxHalf - stopMargin);
            case NORTH: return ( boxHalf + stopMargin);
            default:    return 0;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        cx = w * 0.62;
        cy = h * 0.50;

        drawMap(g2, w, h);
        drawTrafficLights(g2);
        drawCars(g2);

        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.drawString("Vehicles: " + cars.size() + " (fixed)", 12, 18);
        g2.drawString("Rule: RED = stop, GREEN = go", 12, 36);
    }

    private void drawMap(Graphics2D g2, int w, int h) {
        Color roadFill = new Color(235, 235, 235);
        Color roadEdge = new Color(150, 150, 150);

        Shape h1 = thickLine((float) roadW, 20, cy - gap, w - 20, cy - gap);
        Shape h2 = thickLine((float) roadW, 20, cy + gap, w - 20, cy + gap);
        Shape v1 = thickLine((float) roadW, cx - gap, 20, cx - gap, h - 20);
        Shape v2 = thickLine((float) roadW, cx + gap, 20, cx + gap, h - 20);

        g2.setColor(roadFill);
        g2.fill(h1); g2.fill(h2); g2.fill(v1); g2.fill(v2);

        g2.setColor(roadEdge);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(h1); g2.draw(h2); g2.draw(v1); g2.draw(v2);

        Rectangle2D box = new Rectangle2D.Double(cx - boxHalf, cy - boxHalf, boxHalf * 2, boxHalf * 2);
        g2.setColor(new Color(210, 210, 210, 170));
        g2.fill(box);
        g2.setColor(new Color(120, 120, 120));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(box);
    }

    private void drawTrafficLights(Graphics2D g2) {
        drawTrafficLight(g2, cx, cy - 210, lights.nsColor());
        drawTrafficLight(g2, cx, cy + 210, lights.nsColor());
        drawTrafficLight(g2, cx - 220, cy, lights.ewColor());
        drawTrafficLight(g2, cx + 220, cy, lights.ewColor());
    }

    private void drawCars(Graphics2D g2) {
        for (VisualCar c : cars) c.draw(g2, cx, cy);
    }

    private static Shape thickLine(float width, double x1, double y1, double x2, double y2) {
        return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
                .createStrokedShape(new Line2D.Double(x1, y1, x2, y2));
    }

    private static void drawTrafficLight(Graphics2D g2, double x, double y, Color lens) {
        g2.setColor(new Color(60, 60, 60));
        g2.fill(new Rectangle2D.Double(x - 2, y + 10, 4, 26));

        RoundRectangle2D head = new RoundRectangle2D.Double(x - 12, y - 24, 24, 34, 8, 8);
        g2.setColor(new Color(35, 35, 35));
        g2.fill(head);

        g2.setColor(lens);
        g2.fill(new Ellipse2D.Double(x - 7, y - 15, 14, 14));

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(head);
    }
}

enum Direction { EAST, WEST, NORTH, SOUTH }

class TrafficLights {
    private double t = 0.0;

    // 2-phase: EW green then NS green
    private final double green = 7.0;
    private final double cycle = green + green;

    void update(double dt) {
        t += dt;
        while (t >= cycle) t -= cycle;
    }

    boolean ewGreen() { return t < green; }
    boolean nsGreen() { return !ewGreen(); }

    boolean isRedFor(Direction d) {
        if (d == Direction.EAST || d == Direction.WEST) return !ewGreen();
        return !nsGreen();
    }

    Color ewColor() { return ewGreen() ? new Color(0, 180, 0) : new Color(220, 0, 0); }
    Color nsColor() { return nsGreen() ? new Color(0, 180, 0) : new Color(220, 0, 0); }
}

class VisualCar {
    final Direction dir;
    final Color color;
    double x;
    double y;
    double speed;

    double length = 18;
    double width = 10;

    VisualCar(Direction dir, double x, double y, double speed, Color color) {
        this.dir = dir;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.color = color;
    }

    // stopLine is RELATIVE to center
    void update(double dt, double stopLine, boolean red) {
        double v = speed * dt;

        if (red) {
            //stop only if we have NOT reached the stop line yet
            switch (dir) {
                case EAST:
                    if (x + length < stopLine) x += v;
                    break;
                case WEST:
                    if (x - length > stopLine) x -= v;
                    break;
                case SOUTH:
                    if (y + length < stopLine) y += v;
                    break;
                case NORTH:
                    if (y - length > stopLine) y -= v;
                    break;
            }
        } else {
            // GREEN => go
            switch (dir) {
                case EAST:  x += v; break;
                case WEST:  x -= v; break;
                case SOUTH: y += v; break;
                case NORTH: y -= v; break;
            }
        }
    }

    void draw(Graphics2D g2, double cx, double cy) {
        double sx = cx + x;
        double sy = cy + y;

        AffineTransform old = g2.getTransform();

        double angle;
        if (dir == Direction.EAST) angle = 0;
        else if (dir == Direction.WEST) angle = Math.PI;
        else if (dir == Direction.SOUTH) angle = Math.PI / 2.0;
        else angle = -Math.PI / 2.0;

        g2.translate(sx, sy);
        g2.rotate(angle);

        RoundRectangle2D body = new RoundRectangle2D.Double(-length / 2.0, -width / 2.0, length, width, 6, 6);

        g2.setColor(color);
        g2.fill(body);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(body);

        g2.setColor(new Color(255, 255, 255, 200));
        g2.fill(new Rectangle2D.Double(length / 2.0 - 4, -width / 2.0 + 2, 3, width - 4));

        g2.setTransform(old);
    }
}

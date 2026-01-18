import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javafx.embed.swing.SwingFXUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GuiController {

    private GuiMain guiMain;

    // Textures
    private Image carImage;
    private Image trafficLightGrey;
    private Image trafficLightGreen;
    private Image trafficLightRed;
    private Image trafficLightYellow;

    // Camera
    private final Camera2D camera = new Camera2D(960, 540);

    // Simulation loop
    private AnimationTimer simulationTimer;
    private boolean simulationRunning = false;

    // Live chart
    @FXML private LineChart<Number, Number> speedChart;
    private final XYChart.Series<Number, Number> speedSeries = new XYChart.Series<>();
    private int timeStep = 0;

    // Export buttons
    @FXML private Button buttonExportCsv;
    @FXML private Button buttonExportPdf;

    // Stats labels
    @FXML private Label labelAvgSpeed;
    @FXML private Label labelVehicleDesiny;
    @FXML private Label labelCongHotspots;
    @FXML private Label labelTravelTIme;

    // Create vehicle tab
    @FXML private ComboBox<String> comboBoxColors;
    @FXML private ComboBox<String> comboBoxRoutes;
    @FXML private TextField textFieldStartSpeed;
    @FXML private Button buttonAddToSim;

    // Edit vehicle tab
    @FXML private ComboBox<String> comboBoxSelectVehicle;
    @FXML private ComboBox<String> comboBoxSetColor;
    @FXML private ComboBox<String> comboBoxChangeRoute;
    @FXML private TextField textFieldChangeSpeed;
    @FXML private Button buttonChangeColor;
    @FXML private Button buttonChangeRoute;
    @FXML private Button buttonChangeSpeed;
    @FXML private Label labelVehicleColor;
    @FXML private Label labelVehicleSpeed;
    @FXML private Label labelVehicleRoute;

    // Traffic lights tab
    @FXML private ComboBox<String> comboBoxSelectLight;
    @FXML private Label labelCurrentLightPhase;
    @FXML private Label labelNextLightPhase;
    @FXML private Label labelDurationRed;
    @FXML private Button buttonChangePhase;
    @FXML private Button buttonLightDuration;
    @FXML private TextField textFieldLightDuration;
    @FXML private Button buttonStartStopSimulation;

    // Canvas + controls
    @FXML private Canvas canvasMap;
    @FXML private Button buttonZoomIn;
    @FXML private Button buttonZoomOut;
    @FXML private Button buttonRotateLeft;
    @FXML private Button buttonRotateRight;
    @FXML private Button buttonLeft;
    @FXML private Button buttonRight;
    @FXML private Button buttonUp;
    @FXML private Button buttonDown;

    // Config buttons
    @FXML private Button chooseFileButton;
    @FXML private Button activeFileButton;
    @FXML private Button chooseNetButton;

    // Constants
    private static final double TEXTURERADIUS = 8;

    // Config state
    private String selectedConfigPath;
    private Runnable restartCallback;

    // Simulation
    private Simulation sim;
    private boolean isConfigStarted = false;

    public void setOnRestart(Runnable r) {
        this.restartCallback = r;
    }

    public void loadConfig() {
        isConfigStarted = true;
    }

    public void setSimulation(Simulation sim) {
        this.sim = sim;
        comboBoxFill();
    }

    public void loadImagesFromDisk() {
        carImage = new Image(new File("textures/car_icon.png").toURI().toString());
        trafficLightYellow = new Image(new File("textures/yellow_light.png").toURI().toString());
        trafficLightGreen = new Image(new File("textures/green_light.png").toURI().toString());
        trafficLightRed = new Image(new File("textures/red_light.png").toURI().toString());
        trafficLightGrey = new Image(new File("textures/grey_light.png").toURI().toString());
    }

    public void comboBoxFill() {
        if (sim == null) return;
        comboBoxRoutes.setItems(FXCollections.observableArrayList(sim.getRouteIDs()));
        comboBoxSelectVehicle.setItems(FXCollections.observableArrayList(sim.getCarIDs()));
        comboBoxSelectLight.setItems(FXCollections.observableArrayList(sim.getTrafficLightIDs()));
    }

    public void newCar() {
        if (sim == null) return;
        sim.createNewCar("0", textFieldStartSpeed.getText(), comboBoxColors.getValue(), comboBoxRoutes.getValue());
    }

    public void currentCar() {
        if (sim == null) return;
        String curCar = comboBoxSelectVehicle.getValue();
        labelVehicleColor.setText(sim.getCarsColorFromID(curCar));
        labelVehicleSpeed.setText(sim.getCarsSpeedFromID(curCar));
        labelVehicleRoute.setText(sim.getCarsRouteFromID(curCar));
    }

    public void readLight() {
        if (sim == null) return;
        String curTrafficLight = comboBoxSelectLight.getValue();
        labelCurrentLightPhase.setText(sim.getTrafficLightColorFromID(curTrafficLight));
        labelDurationRed.setText(sim.getTrafficLightCycleLengthFromID(curTrafficLight));
    }

    public void setLightDurationBtn() {
        if (sim == null) return;
        String curTrafficLight = comboBoxSelectLight.getValue();
        Float dur = Float.valueOf(textFieldLightDuration.getText());
        sim.setTrafficLightCycleLengthFromID(curTrafficLight, String.valueOf(dur));
        labelDurationRed.setText(sim.getTrafficLightCycleLengthFromID(curTrafficLight));
    }

    public void togglePause() {
        if (sim == null) return;
        sim.togglePause();
    }

    public void draw() {
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        double w = canvasMap.getWidth();
        double h = canvasMap.getHeight();
        double halfW = w / 2;
        double halfH = h / 2;

        drawMapFX(gc, w, h, halfW, halfH);

        updateAverageSpeedLabel();
        updateVehicleDensityLabel();
        updateCongestionHotspotsLabel();
        updateTravelTimeLabel();
        updateSpeedChart();
    }

    private void drawMapFX(GraphicsContext gc, double w, double h, double halfW, double halfH) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, w, h);
        if (sim == null) return;

        // Draw lanes
        for (Lane lane : sim.getLanes()) {
            for (int i = 0; i < lane.getLine().size() - 1; i++) {
                Vector2D p1 = camera.getLocalPositionFromGLobal(lane.getLine().get(i));
                Vector2D p2 = camera.getLocalPositionFromGLobal(lane.getLine().get(i + 1));
                gc.strokeLine(p1.x + halfW, p1.y + halfH, p2.x + halfW, p2.y + halfH);
            }
        }

        // Draw traffic lights
        for (trafficLight tl : sim.getTrafficLights()) {
            if (tl == null) continue;

            if (tl.getTrafficLight().equals("")) {
                for (int i = 0; i < tl.getStopLinePositions().size(); i++) {
                    Vector2D p = camera.getLocalPositionFromGLobal(tl.getStopLinePositions().get(i));
                    gc.drawImage(trafficLightGrey, p.x - TEXTURERADIUS + halfW, p.y - TEXTURERADIUS + halfH);
                }
            } else {
                for (int i = 0; i < tl.getTrafficLight().length(); i++) {
                    Vector2D p = camera.getLocalPositionFromGLobal(tl.getStopLinePositions().get(i));
                    char c = tl.getTrafficLight().charAt(i);

                    if (c == 'G' || c == 'g') {
                        gc.drawImage(trafficLightGreen, p.x - TEXTURERADIUS + halfW, p.y - TEXTURERADIUS + halfH);
                    } else if (c == 'Y' || c == 'y') {
                        gc.drawImage(trafficLightYellow, p.x - TEXTURERADIUS + halfW, p.y - TEXTURERADIUS + halfH);
                    } else if (c == 'R' || c == 'r') {
                        gc.drawImage(trafficLightRed, p.x - TEXTURERADIUS + halfW, p.y - TEXTURERADIUS + halfH);
                    }
                }
            }
        }

        // Draw cars
        for (Car car : sim.getCars()) {
            Transform2D local = camera.getLocalTransformFromGlobal(car.getTransform());

            double x = local.getPosition().x + halfW;
            double y = local.getPosition().y + halfH;

            gc.save();
            gc.translate(x, y);
            gc.scale(local.getScale(), local.getScale());

            // car Rotation
            gc.rotate(Math.toDegrees(local.getRotation()) - 90);
			gc.drawImage(
				carImage,
				-TEXTURERADIUS,
				-TEXTURERADIUS
			);

            gc.restore();
        }

    }

    @FXML
    public void initialize() {
        comboBoxColors.setItems(FXCollections.observableArrayList("black", "white"));

        if (speedChart != null) {
            speedSeries.setName("Average Speed");
            speedChart.getData().add(speedSeries);
        }

        draw();
    }

    // Chart update
    private void updateSpeedChart() {
        if (sim == null || speedChart == null) return;
        float avg = sim.getStats().getAverageSpeed();
        speedSeries.getData().add(new XYChart.Data<>(timeStep++, avg));
        if (speedSeries.getData().size() > 100) speedSeries.getData().remove(0);
    }

    // Label updates
    private void updateAverageSpeedLabel() {
        if (sim == null) return;
        float avg = sim.getStats().getAverageSpeed();
        labelAvgSpeed.setText(String.format("%.2f m/s", avg));
    }

    private void updateVehicleDensityLabel() {
        if (sim == null) return;

        Map<String, Double> dens = sim.getStats().getDensityPerEdge();
        if (dens == null || dens.isEmpty()) {
            labelVehicleDesiny.setText("-");
            return;
        }

        String bestEdge = null;
        double max = -1;
        for (Map.Entry<String, Double> e : dens.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                bestEdge = e.getKey();
            }
        }

        labelVehicleDesiny.setText(String.format("%.4f veh/m (%s)", max, bestEdge));
    }

    private void updateCongestionHotspotsLabel() {
        if (sim == null) return;
        int hotspots = sim.getStats().getCongestionHotspots();
        labelCongHotspots.setText(String.valueOf(hotspots));
    }

    private void updateTravelTimeLabel() {
        if (sim == null) return;
        double avgTT = sim.getStats().getAverageTravelTime();
        labelTravelTIme.setText(avgTT < 0 ? "-" : String.format("%.1f s", avgTT));
    }

    // Timer setup
    private void createSimulationTimer() {
        simulationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (sim == null || sim.paused) return;
                sim.step();
                draw();
            }
        };
    }

    @FXML
    private void handleStartStopSimulation() {
        if (sim == null) return;

        if (!simulationRunning) {
            if (simulationTimer == null) createSimulationTimer();
            simulationTimer.start();
            simulationRunning = true;
            buttonStartStopSimulation.setText("Stop Simulation");
        } else {
            simulationTimer.stop();
            simulationRunning = false;
            buttonStartStopSimulation.setText("Start Simulation");
        }
    }

    // Choose sumocfg
    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SUMO Config (*.sumocfg)", "*.sumocfg")
        );
        fileChooser.setTitle("Select Config");
        File selectedFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
        if (selectedFile != null) selectedConfigPath = selectedFile.getAbsolutePath();
    }

    // Choose net.xml
    @FXML
    private void handleChooseNet() {
        if (sim == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SUMO Network (*.net.xml)", "*.net.xml")
        );
        fileChooser.setTitle("Select Network");
        File selectedNetFile = fileChooser.showOpenDialog(chooseNetButton.getScene().getWindow());
        if (selectedNetFile != null) sim.setCurrentNetFile(selectedNetFile.getAbsolutePath());
    }

    // Activate config
    @FXML
    private void handleActivedFile() {
        if (selectedConfigPath == null) return;
        if (sim == null) return;

        sim.setSumocfgPath(selectedConfigPath);
        if (restartCallback != null) restartCallback.run();
    }

    // CSV export
    @FXML
    private void handleExportCsv() {
        if (sim == null) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Export Statistics");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("stats.csv");

        File file = fc.showSaveDialog(buttonExportCsv.getScene().getWindow());
        if (file == null) return;

        try (PrintWriter out = new PrintWriter(file, StandardCharsets.UTF_8)) {
            float avg = sim.getStats().getAverageSpeed();
            out.println("SIMULATION STATISTICS");
            out.println("=====================");
            out.println("AverageSpeed," + avg);

            Map<String, Double> dens = sim.getStats().getDensityPerEdge();
            out.println();
            out.println("EdgeId,DensityVehPerM");

            if (dens != null) {
                for (Map.Entry<String, Double> e : dens.entrySet()) {
                    out.println(e.getKey() + "," + e.getValue());
                }
            }

            int hotspots = sim.getStats().getCongestionHotspots();
            out.println("CongestionHotspots," + hotspots);

            double avgTT = sim.getStats().getAverageTravelTime();
            out.println("AverageTravelTimeSec," + avgTT);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // PDF export
    @FXML
    private void handleExportPdf() {
        if (sim == null) return;

        FileChooser fc = new FileChooser();
        fc.setTitle("Export PDF Report");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        fc.setInitialFileName("simulation_report.pdf");

        File file = fc.showSaveDialog(buttonExportPdf.getScene().getWindow());
        if (file == null) return;

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float y = 780;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                y = writeLine(cs, y, 18, true, "SIMULATION REPORT");

                String ts = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                y = writeLine(cs, y - 8, 11, false, "Generated: " + ts);

                y -= 10;

                float avgSpeed = sim.getStats().getAverageSpeed();
                int hotspots = sim.getStats().getCongestionHotspots();
                double avgTT = sim.getStats().getAverageTravelTime();
                Map<String, Double> dens = sim.getStats().getDensityPerEdge();

                y = writeLine(cs, y, 12, true, "Metrics");
                y = writeLine(cs, y, 12, false, "Average speed: " + String.format("%.2f m/s", avgSpeed));
                y = writeLine(cs, y, 12, false, "Congestion hotspots: " + hotspots);
                y = writeLine(cs, y, 12, false, "Average travel time: " + (avgTT < 0 ? "-" : String.format("%.1f s", avgTT)));

                y -= 8;

                if (dens != null && !dens.isEmpty()) {
                    int count = 0;
                    for (Map.Entry<String, Double> e : dens.entrySet().stream()
                            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                            .toList()) {

                        y = writeLine(cs, y, 12, false,
                                "Density [" + e.getKey() + "]: " + String.format("%.4f veh/m", e.getValue()));
                        if (++count >= 5) break;
                    }
                } else {
                    y = writeLine(cs, y, 12, false, "Density: -");
                }
            }

            addChartToPdf(doc, page, y);

            PDPage histPage = new PDPage(PDRectangle.A4);
            doc.addPage(histPage);

            try (PDPageContentStream cs = new PDPageContentStream(doc, histPage)) {
                drawTravelTimeHistogram(
                        cs, 60, 120, 480, 200,
                        sim.getStats().getAllTravelTimes()
                );
            }

            doc.save(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addChartToPdf(PDDocument doc, PDPage page, float currentY) throws Exception {
        if (speedChart == null) return;

        speedChart.setAnimated(false);
        speedChart.setLegendVisible(false);
        speedChart.setCreateSymbols(false);

        speedChart.applyCss();
        speedChart.layout();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.WHITE);
        params.setTransform(new javafx.scene.transform.Scale(2.0, 2.0));

        WritableImage fxImg = speedChart.snapshot(params, null);
        BufferedImage bImg = SwingFXUtils.fromFXImage(fxImg, null);

        PDImageXObject pdImg = LosslessFactory.createFromImage(doc, bImg);

        float marginX = 50;
        float marginBottom = 60;

        float maxWidth = page.getMediaBox().getWidth() - 2 * marginX;
        float targetWidth = maxWidth;
        float targetHeight = 240;

        float gap = 20;
        float x = marginX;
        float y = currentY - gap - targetHeight;
        if (y < marginBottom) y = marginBottom;

        try (PDPageContentStream cs = new PDPageContentStream(
                doc, page, PDPageContentStream.AppendMode.APPEND, true)) {
            cs.drawImage(pdImg, x, y, targetWidth, targetHeight);
        }
    }

    private void drawTravelTimeHistogram(
            PDPageContentStream cs,
            float x,
            float y,
            float width,
            float height,
            List<Double> travelTimes
    ) throws IOException {

        if (travelTimes == null || travelTimes.isEmpty()) {
            drawNoDataMessage(cs, x, y, width, height);
            return;
        }

        cs.setNonStrokingColor(255, 255, 255);
        cs.addRect(0, 0, 595, 842);
        cs.fill();

        cs.setNonStrokingColor(60, 60, 60);

        int bins = 6;
        double max = travelTimes.stream().mapToDouble(v -> v).max().orElse(1);
        double binSize = max / bins;

        int[] counts = new int[bins];
        for (double t : travelTimes) {
            int b = (int) (t / binSize);
            if (b >= bins) b = bins - 1;
            counts[b]++;
        }

        int maxCount = Arrays.stream(counts).max().orElse(1);
        float barWidth = width / bins;

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
        cs.newLineAtOffset(x, y + height + 20);
        cs.showText("Travel Time Distribution");
        cs.endText();

        cs.setStrokingColor(0, 0, 0);
        cs.setLineWidth(1);

        cs.moveTo(x, y);
        cs.lineTo(x, y + height);
        cs.moveTo(x, y);
        cs.lineTo(x + width, y);
        cs.stroke();

        cs.setFont(PDType1Font.HELVETICA, 9);

        int yTicks = Math.max(1, maxCount);
        for (int i = 0; i <= yTicks; i++) {
            float yPos = y + (i / (float) yTicks) * height;
            cs.moveTo(x - 4, yPos);
            cs.lineTo(x, yPos);
            cs.stroke();

            cs.beginText();
            cs.newLineAtOffset(x - 25, yPos - 3);
            cs.showText(String.valueOf(i));
            cs.endText();
        }

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 10);
        cs.newLineAtOffset(x + width / 2 - 35, y - 35);
        cs.showText("Travel Time (s)");
        cs.endText();

        cs.setFont(PDType1Font.HELVETICA, 9);
        for (int i = 0; i < bins; i++) {
            String label = String.format("%.0f–%.0f", i * binSize, (i + 1) * binSize);
            cs.beginText();
            cs.newLineAtOffset(x + i * barWidth + 5, y - 15);
            cs.showText(label);
            cs.endText();
        }

        cs.setNonStrokingColor(180, 40, 40);
        for (int i = 0; i < bins; i++) {
            float barHeight = (counts[i] / (float) maxCount) * height;
            cs.addRect(x + i * barWidth, y, barWidth - 10, barHeight);
            cs.fill();
        }

        cs.setNonStrokingColor(180, 40, 40);
        cs.addRect(x + width - 140, y + height - 10, 10, 10);
        cs.fill();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 9);
        cs.newLineAtOffset(x + width - 120, y + height - 2);
        cs.showText("All vehicles");
        cs.endText();
    }

    private void drawNoDataMessage(PDPageContentStream cs, float x, float y, float width, float height) throws IOException {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 12);
        cs.newLineAtOffset(x, y + height / 2);
        cs.showText("No data available.");
        cs.endText();
    }

    private float writeLine(PDPageContentStream cs, float y, int fontSize, boolean bold, String text) throws IOException {
        cs.beginText();
        cs.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, fontSize);
        cs.newLineAtOffset(50, y);
        cs.showText(text);
        cs.endText();
        return y - (fontSize + 6);
    }

    public void zoomIn(){
        camera.changeZoom(0.1F);
    }
    public void zoomOut(){
        camera.changeZoom(-0.1F);
    }
    public void moveUp(){
        System.out.println("has moved");
        camera.moveCameraByAmount(new Vector2D(0,-10));
    }
    public void moveDown(){
        System.out.println("has moved");
        camera.moveCameraByAmount(new Vector2D(0,10));
    }
    public void moveLeft(){
        System.out.println("has moved");
        camera.moveCameraByAmount(new Vector2D(-10,0));
    }
    public void moveRight(){
        System.out.println("has moved");
        camera.moveCameraByAmount(new Vector2D(10,0));
    }
    public void rotateClockwise(){
        camera.rotateClockwise();
    }
    public void rotateCounterclockwise(){
        camera.rotateCounterclockwise();
    }

}

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.application.Platform;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.print.PrinterJob;
import javafx.stage.FileChooser;

import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.swing.*;
//import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.Arrays;

public class GuiController {
    private GuiMain guiMain;
    private Image carImage;
    private Image trafficLightGrey;
    private Image trafficLightGreen;
    private Image trafficLightRed;
    private Image trafficLightYellow;

    private AnimationTimer simulationTimer; // Simulation loop
    private boolean simulationRunning = false;

    // realtime average speedtime
    @FXML
    private LineChart<Number, Number> speedChart;


    private XYChart.Series<Number, Number> speedSeries
            = new XYChart.Series<>();

    private int timeStep = 0;


    //Label for statistic
    @FXML
    private Button buttonExportCsv;
    @FXML
    private Button buttonExportPdf;
    @FXML
    private Label labelAvgSpeed;
    @FXML
    private Label labelVehicleDesiny;
    @FXML
    private Label labelCongHotspots;
    @FXML
    private Label labelTravelTIme;

    //Tab Create Vehicle
    @FXML
    private ComboBox<String> comboBoxEdges;
    @FXML
    private ComboBox<String> comboBoxColors;
    @FXML
    private ComboBox<String> comboBoxRoutes;
    @FXML
    private TextField textFieldStartSpeed;
    @FXML
    private Button buttonAddToSim;


    //Tab Edit Vehicle
    @FXML
    private ComboBox<String> comboBoxSelectVehicle;
    @FXML
    private ComboBox<String> comboBoxSetColor;
    @FXML
    private ComboBox<String> comboBoxChangeRoute;
    @FXML
    private TextField textFieldChangeSpeed;
    @FXML
    private Button buttonChangeColor;
    @FXML
    private Button buttonChangeRoute;
    @FXML
    private Button buttonChangeSpeed;
    @FXML
    private Label labelVehicleColor;
    @FXML
    private Label labelVehicleSpeed;
    @FXML
    private Label labelVehicleRoute;

    //Tab Traffic Lights
    @FXML
    private ComboBox<String> comboBoxSelectLight;
    @FXML
    private Label labelCurrentLightPhase;
    @FXML
    private Label labelNextLightPhase;
    @FXML
    private Label labelDurationRed;
    @FXML
    private Button buttonChangePhase;
    @FXML
    private Button buttonLightDuration;
    @FXML
    private TextField textFieldLightDuration;
    @FXML
    private Button buttonStartStopSimulation;
    //Map Canvas + Control


    @FXML
    private Canvas canvasMap;
    @FXML
    private Button buttonZoomIn;
    @FXML
    private Button buttonZoomOut;
    @FXML
    private Button buttonRotateLeft;
    @FXML
    private Button buttonRotateRight;
    @FXML
    private Button buttonLeft;
    @FXML
    private Button buttonRight;
    @FXML
    private Button buttonUp;
    @FXML
    private Button buttonDown;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button activeFileButton;

    @FXML
    private Button chooseNetButton;


    private double roadW = 20;
    private double gap = 40;
    private double boxHalf = 35;
    final double TEXTURERADIUS = 8;

    private String selectedConfigPath;
    private Runnable restartCallback;

    public void setOnRestart(Runnable r) {
        this.restartCallback = r;
    }

    //Simulation for statistic
    private Simulation sim;
    private boolean isConfigStarted = false;


    public void loadConfig() {
        isConfigStarted = true;
    }

    public void setSimulation(Simulation sim) {
        System.out.println("sim started");
        this.sim = sim;
        comboBoxFill();
    }

    public void loadImagesFromDisk() {
        carImage = new Image(new File("textures/car_icon.png").toURI().toString());//.getImage();
        trafficLightYellow = new Image(new File("textures/yellow_light.png").toURI().toString());
        trafficLightGreen = new Image(new File("textures/green_light.png").toURI().toString());
        trafficLightRed = new Image(new File("textures/red_light.png").toURI().toString());
        trafficLightGrey = new Image(new File("textures/grey_light.png").toURI().toString());
    }

    public void comboBoxFill() {
        if (sim != null) {
            comboBoxEdges.setItems(FXCollections.observableArrayList());
            comboBoxRoutes.setItems(FXCollections.observableArrayList(sim.getRouteIDs()));
            comboBoxSelectVehicle.setItems(FXCollections.observableArrayList(sim.getCarIDs()));
            comboBoxSelectLight.setItems(FXCollections.observableArrayList(sim.getTrafficLightIDs()));
        }
    }

    public void newCar() {
        sim.createNewCar("0", textFieldStartSpeed.getText(), comboBoxColors.getValue(), comboBoxRoutes.getValue());
    }

    public void currentCar() {
        String curCar = comboBoxSelectVehicle.getValue();
        labelVehicleColor.setText(sim.getCarsColorFromID(curCar));
        labelVehicleSpeed.setText(sim.getCarsSpeedFromID(curCar));
        labelVehicleRoute.setText(sim.getCarsRouteFromID(curCar));
    }

    public void readLight() {
        String curTrafficLight = comboBoxSelectLight.getValue();
        labelCurrentLightPhase.setText(sim.getTrafficLightColorFromID(curTrafficLight));
        labelDurationRed.setText(sim.getTrafficLightCycleLengthFromID(curTrafficLight));
    }

    public void setLightDurationBtn() {
        String curTrafficLight = comboBoxSelectLight.getValue();
        Float dur = Float.valueOf(textFieldLightDuration.getText());
        sim.setTrafficLightCycleLengthFromID(curTrafficLight, String.valueOf(dur));
        labelDurationRed.setText(sim.getTrafficLightCycleLengthFromID(curTrafficLight));
    }

    public void togglePause() {
        sim.togglePause();
    }

    public void printMap() {
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        //Build Map here
    }

    public void draw() {
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        double w = canvasMap.getWidth();
        double h = canvasMap.getHeight();

        double halfwidth = w / 2;
        double halfheight = h / 2;

        drawMapFX(gc, w, h, halfwidth, halfheight);
        updateAverageSpeedLabel(); // Update average speed
        updateVehicleDensityLabel(); // Update vehicle density
        updateCongestionHotspotsLabel(); // Update hotspot
        updateTravelTimeLabel(); // Update time
        updateSpeedChart(); // Update chart


    }


    private void drawMapFX(GraphicsContext gc, double w, double h,
                           double halfwidth, double halfheight) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, w, h);
        if (sim == null) {
            System.out.println("is null");
            //gc.drawImage(carImage,300,200);
            //System.out.println("is null");
            return;
        }
        for (Lane lane : sim.getLanes()) {
            for (int i = 0; i < lane.getLine().size() - 1; i++) {
                gc.strokeLine(lane.getLine().get(i).x + halfwidth, -lane.getLine().get(i).y + halfheight, lane.getLine().get(i + 1).x + halfwidth, -lane.getLine().get(i + 1).y + halfheight);
            }
        }
        System.out.println("not null");


        for (Car car : sim.getCars()) {

            double x = car.getPosition().x + halfwidth;
            double y = -car.getPosition().y + halfheight;

            double angle = car.getAngle(); // in degree

            gc.save();

            //beginning
            gc.translate(x, y);

            // rotate
            gc.rotate(angle + 90); //+90 orientation for rotation

            // create image
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
        draw();
        // speed chart
        speedSeries.setName("Average Speed");
        speedChart.getData().add(speedSeries);
    }

    //Speed chart
    private void updateSpeedChart() {
        if (sim == null) return;

        float avg = sim.getStats().getAverageSpeed();
        speedSeries.getData().add(
                new XYChart.Data<>(timeStep++, avg)
        );

        //keep chart small
        if (speedSeries.getData().size() > 100) {
            speedSeries.getData().remove(0);
        }
    }


    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("SUMO Config Dateien (*.sumocfg)", "*.sumocfg");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Select Cfg");
        File selectedFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());

        if (selectedFile != null) {
            selectedConfigPath = selectedFile.getAbsolutePath();
        } else {
            System.out.println("That is Null");
        }
    }

    @FXML
    private void handleChooseNet() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Net Xml Dateien (*.net.xml)", "*.net.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Select Network");
        File selectedNetFile = fileChooser.showOpenDialog(chooseNetButton.getScene().getWindow());

        if (selectedNetFile != null) {
            sim.setCurrentNetFile(selectedNetFile.getAbsolutePath());
        } else {
            System.out.println("No net,xml selected");
        }
    }

    private void createSimulationTimer() {
        simulationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (sim == null || sim.paused) return;

                sim.step();     // Simulation step

                draw();         // draw + updateAverageSpeedLabel() call
            }
        };
    }

    @FXML
    private void handleStartStopSimulation() {
        if (sim == null) return;

        if (!simulationRunning) {
            if (simulationTimer == null) {
                createSimulationTimer();
            }
            simulationTimer.start();
            simulationRunning = true;
            buttonStartStopSimulation.setText("Stop Simulation");
        } else {
            simulationTimer.stop();
            simulationRunning = false;
            buttonStartStopSimulation.setText("Start Simulation");
        }
    }

    private void updateAverageSpeedLabel() {
        if (sim == null) return;

        //last value
        if (sim.getCars() == null || sim.getCars().length == 0) {
            return;
        }

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

        // Show max density edge
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

        if (avgTT < 0) {
            labelTravelTIme.setText("-");
        } else {
            labelTravelTIme.setText(String.format("%.1f s", avgTT));
        }
    }


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
            out.println("Edge Id, density vehicle per m");

            for (Map.Entry<String, Double> e : dens.entrySet()) {
                out.println(e.getKey() + ", " + e.getValue());
            }

            int hotspots = sim.getStats().getCongestionHotspots();
            out.println("CongestionHotspots: " + hotspots);

            double avgTT = sim.getStats().getAverageTravelTime();
            out.println("Average Travel time(s): " + avgTT);


        } catch (Exception e) {
            e.printStackTrace();
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

        // if no data nothing to do
        if (travelTimes == null || travelTimes.isEmpty()) {
            drawNoDataMessage(cs, x, y, width, height);
            return;
        }

        //white background
        cs.setNonStrokingColor(255, 255, 255); // fill color = white
        cs.addRect(0, 0, 595, 842);            //full page rectangle
        cs.fill();                             // paint it

        cs.setNonStrokingColor(60, 60, 60);

        // chart setup
        int bins = 6; // number of bars
        double max = travelTimes.stream().mapToDouble(v -> v).max().orElse(1); //max travel time
        double binSize = max / bins; //width of one bin in seconds

        //count values per bin
        int[] counts = new int[bins];
        for (double t : travelTimes) {
            int b = (int) (t / binSize);
            if (b >= bins) b = bins - 1;
            counts[b]++;
        }

        int maxCount = Arrays.stream(counts).max().orElse(1); // tallest bar count
        float barWidth = width / bins; // bar width in PDF units

        //title
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
        cs.newLineAtOffset(x, y + height + 20);
        cs.showText("Travel Time Distribution");
        cs.endText();


        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 10);
        cs.newLineAtOffset(x, y + height + 5);
        cs.showText("The histogram shows the distribution of vehicle travel times across the simulation.");
        cs.newLineAtOffset(0, -12);
        cs.endText();

        // draw axes
        cs.setStrokingColor(0, 0, 0); //line color = black
        cs.setLineWidth(1);

        // y-axis
        cs.moveTo(x, y);
        cs.lineTo(x, y + height);

        // x-axis
        cs.moveTo(x, y);
        cs.lineTo(x + width, y);

        cs.stroke(); // paint the axis lines

        cs.setFont(PDType1Font.HELVETICA, 9); //small font for labels

        //y axis
        int yTicks = maxCount;
        if (yTicks < 1) yTicks = 1;

        for (int i = 0; i <= yTicks; i++) {
            float yPos = y + (i / (float) yTicks) * height;

            //tick line
            cs.moveTo(x - 4, yPos);
            cs.lineTo(x, yPos);
            cs.stroke();

            //tick label
            cs.beginText();
            cs.newLineAtOffset(x - 25, yPos - 3);
            cs.showText(String.valueOf(i));
            cs.endText();
        }

        //x axis label
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 10);
        cs.newLineAtOffset(x + width / 2 - 30, y - 35); //centered under axis
        cs.showText("Travel Time (s)");
        cs.endText();
        cs.setFont(PDType1Font.HELVETICA, 9);

        // x axis
        for (int i = 0; i < bins; i++) {
            String label = String.format(
                    "%.0f–%.0f",
                    i * binSize,
                    (i + 1) * binSize
            );

            cs.beginText();
            cs.newLineAtOffset(
                    x + i * barWidth + 5, // inside each bin
                    y - 15                 // below axis
            );
            cs.showText(label);
            cs.endText();
        }

        //bars for all vehicles
        cs.setNonStrokingColor(180, 40, 40); //fill color = red

        for (int i = 0; i < bins; i++) {
            float barHeight = (counts[i] / (float) maxCount) * height; //scale to chart height

            cs.addRect(
                    x + i * barWidth,  //bar x
                    y,                 //bar y
                    barWidth - 10,     //bar width
                    barHeight          //bar height
            );
            cs.fill(); // paint the bar
        }

        // all vehicle
        cs.setNonStrokingColor(180, 40, 40); //legend color box
        cs.addRect(x + width - 140, y + height - 10, 10, 10); //legend square
        cs.fill();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 9);
        cs.newLineAtOffset(x + width - 120, y + height - 2); //legend text position
        cs.showText("All vehicles");
        cs.endText();
    }

    private void drawNoDataMessage(PDPageContentStream cs, float x, float y, float width, float height) {
    }


    @FXML
    private void handleExportPdf() {
        if (sim == null) return; //no simulation, nothing to do

        // File chooser for PDF
        FileChooser fc = new FileChooser();
        fc.setTitle("Export PDF Report"); // title
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")  //only PDF files
        );
        fc.setInitialFileName("simulation_report.pdf"); //file name

        File file = fc.showSaveDialog(buttonExportPdf.getScene().getWindow()); //open
        if (file == null) return; //user canceled

        try (PDDocument doc = new PDDocument()) { //create PDF

            // Create one A4 page
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float y = 780; // vertical cursor (top to bottom)

            try (PDPageContentStream cs =
                         new PDPageContentStream(doc, page)) {

                //title
                y = writeLine(cs, y, 18, true, "SIMULATION REPORT");

                // Timestape
                String ts = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd ; HH:mm:ss"));
                y = writeLine(cs, y - 8, 11, false,
                        "Generated time: " + ts);

                y -= 10;

                // Metrics data
                float avgSpeed = sim.getStats().getAverageSpeed();
                int hotspots = sim.getStats().getCongestionHotspots();
                double avgTT = sim.getStats().getAverageTravelTime();
                Map<String, Double> dens =
                        sim.getStats().getDensityPerEdge();

                y = writeLine(cs, y, 12, true, "Metrics");

                y = writeLine(cs, y, 12, false,
                        "Average speed: " +
                                String.format("%.2f m/s", avgSpeed));

                y = writeLine(cs, y, 12, false,
                        "Congestion hotspots: " + hotspots);

                y = writeLine(cs, y, 12, false,
                        "Average travel time: " +
                                (avgTT < 0
                                        ? "-"
                                        : String.format("%.1f s", avgTT)));

                y -= 8;


                if (dens != null && !dens.isEmpty()) {
                    int count = 0;
                    for (Map.Entry<String, Double> e : dens.entrySet()
                            .stream()
                            .sorted((a, b) ->
                                    Double.compare(b.getValue(), a.getValue()))
                            .toList()) {

                        y = writeLine(cs, y, 12, false,
                                "Vehicle density [" + e.getKey() + "]: " +
                                        String.format("%.4f veh/m", e.getValue()));

                        count++;
                        if (count >= 5) break;
                    }


                } else {
                    y = writeLine(cs, y, 12, false, "-");
                }
            }


            // Add chart image below text
            addChartToPdf(doc, page, y);

            // New Page for the diagramm
            PDPage histPage = new PDPage(PDRectangle.A4);
            doc.addPage(histPage);

            try (PDPageContentStream cs =
                         new PDPageContentStream(doc, histPage)) {

                drawTravelTimeHistogram(
                        cs,
                        60,
                        120,
                        480,
                        200,
                        sim.getStats().getAllTravelTimes()
                );
            }

            doc.save(file); //write PDF file

            System.out.println(
                    "The PDF file was saved in the selected " +
                            "directory at the specified location: " + file.getAbsolutePath()
            );

        } catch (Exception e) {
            e.printStackTrace(); // print error details
        }
    }

    private void addChartToPdf(PDDocument doc, PDPage page, float currentY) throws Exception {
        if (speedChart == null) return; // no chart, nothing to do

        // make chart look better
        speedChart.setAnimated(false); //disable animation
        speedChart.setLegendVisible(false); // no legend
        speedChart.setCreateSymbols(false);     // no dots on every point
        speedChart.setHorizontalGridLinesVisible(true); // show horizontal grid
        speedChart.setVerticalGridLinesVisible(true);
        speedChart.setAlternativeRowFillVisible(false);  // no stripes
        speedChart.setAlternativeColumnFillVisible(false);

        // background
        speedChart.setStyle("""
        -fx-background-color: white;
        -fx-padding: 10;
    """);


        // needed for snapshot
        speedChart.applyCss();
        speedChart.layout();

        if (!speedChart.getData().isEmpty()
                && speedChart.getData().get(0).getNode() != null) {
            speedChart.getData().get(0).getNode().lookup(".chart-series-line")
                    .setStyle("-fx-stroke-width: 2.5px;"); // Thicker line
        }

        // snapshot settings white background and higher resolution
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.WHITE);
        params.setTransform(new javafx.scene.transform.Scale(2.0, 2.0));

        //chart to image
        WritableImage fxImg = speedChart.snapshot(params, null);
        BufferedImage bImg = SwingFXUtils.fromFXImage(fxImg, null);

        // convert image for PDFBox
        PDImageXObject pdImg = LosslessFactory.createFromImage(doc, bImg);

        // Placement in PDF
        float marginX = 50;
        float marginBottom = 60;

        float maxWidth = page.getMediaBox().getWidth() - 2 * marginX;
        float targetWidth = maxWidth;
        float targetHeight = 240; // slightly bigger for readability

        float gap = 20;
        float x = marginX;

        float y = currentY - gap - targetHeight; // place below currentY
        if (y < marginBottom) y = marginBottom;

        // draw image into the PDF page
        try (PDPageContentStream cs = new PDPageContentStream(
                doc, page, PDPageContentStream.AppendMode.APPEND, true)) {
            cs.drawImage(pdImg, x, y, targetWidth, targetHeight);
        }
    }




    private float writeLine(PDPageContentStream cs, float y, int fontSize, boolean bold, String text) throws Exception {
        cs.beginText();
        cs.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, fontSize);
        cs.newLineAtOffset(50, y); //set text position
        cs.showText(text);
        cs.endText();
        return y - (fontSize + 6); //move down for next line
    }

}
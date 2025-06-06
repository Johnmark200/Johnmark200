package Eclinic;



import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class DailyPatientChart extends Application {
    // Update these for your database connection
    private static final String DB_URL = "jdbc:mysql://localhost:3306/eclinic";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    public void start(Stage primaryStage) {
        Map<String, Integer> dailyPatientCounts;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            dailyPatientCounts = loadDailyPatientCounts(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            dailyPatientCounts = new HashMap<>();
        }

        BarChart<String, Number> dailyChart = createDailyPatientChart(dailyPatientCounts);

        VBox root = new VBox(dailyChart);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Daily Patient Visits - Last 7 Days");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Map<String, Integer> loadDailyPatientCounts(Connection conn) throws SQLException {
        Map<String, Integer> dailyPatientCounts = new LinkedHashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6); // last 7 days including today

        // Initialize all 7 days with 0 counts, in order Mon-Sun
        List<String> daysOrder = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        for (String day : daysOrder) {
            dailyPatientCounts.put(day, 0);
        }

        // Query counts by visit_date
        String sql = "SELECT admission_date, COUNT(*) AS id" +
                     "FROM patients " +
                     "WHERE admission_date BETWEEN ? AND ? " +
                     "GROUP BY admission_date";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(weekAgo));
            stmt.setDate(2, Date.valueOf(today));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date visitDate = rs.getDate("visit_date");
                int count = rs.getInt("id");

                LocalDate localDate = visitDate.toLocalDate();
                String dayName = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                // Make sure day is in map (some DB locales might differ)
                if (dailyPatientCounts.containsKey(dayName)) {
                    dailyPatientCounts.put(dayName, count);
                }
            }
        }

        return dailyPatientCounts;
    }

    private BarChart<String, Number> createDailyPatientChart(Map<String, Integer> dailyPatientCounts) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Day");
        yAxis.setLabel("Number of Patients");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Daily Patient Visits");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Past 7 Days");

        // Ensure days show in Mon-Sun order
        List<String> daysOrder = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

        for (String day : daysOrder) {
            int count = dailyPatientCounts.getOrDefault(day, 0);
            series.getData().add(new XYChart.Data<>(day, count));
        }

        barChart.getData().add(series);
        return barChart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String URL = "jdbc:postgresql://localhost:5432/Hospital";
    private static final String USER = "postgres";
    private static final String PASSWORD = "dimitris";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to the database!", e);
        }
    }
}

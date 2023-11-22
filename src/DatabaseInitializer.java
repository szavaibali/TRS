import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseInitializer {

    private static final String DATABASE_URL = "jdbc:sqlite:kerdesek.db";

    public static void main(String[] args) {
        createDatabase();
        createQuestionsTable();
    }

    private static void createDatabase() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            System.out.println("Adatbázis létrehozva: " + DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();  // Hozzáadott sor a hibakereséshez
        }
    }
    //Próbálok javaból adatbazist létrehozni, még nem működik
    private static void createQuestionsTable() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS kerdesek (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "kerdes TEXT NOT NULL)";
            connection.createStatement().executeUpdate(createTableQuery);

            System.out.println("Kérdés tábla létrehozva.");
        } catch (SQLException e) {
            e.printStackTrace();  // Hozzáadott sor a hibakereséshez
        }
    }
}

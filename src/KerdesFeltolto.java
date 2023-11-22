import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KerdesFeltolto {

    private static final String DATABASE_URL = "jdbc:sqlite:kerdesek.db";

    public static void main(String[] args) {
        // SQLite JDBC driver regisztrálása
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Adatok beszúrása a kerdesek táblába
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            // Példa: beszúrás egy kérdéssel, itt rendezni kell a választ, ami értékelés lesz......
            insertQuestion(connection, 
            "Az oktató mindig pontosan kezdi és fejezi be a tanórát.", "Válasz1", "Válasz2", "Válasz3", "Válasz4", 2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertQuestion(Connection connection, String kerdes, String valasz1, String valasz2, String valasz3, String valasz4, int helyesValasz) throws SQLException {
        String insertQuery = "INSERT INTO kerdesek (kerdes, valasz1, valasz2, valasz3, valasz4, helyes_valasz) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setString(1, kerdes);
            insertStatement.setString(2, valasz1);
            insertStatement.setString(3, valasz2);
            insertStatement.setString(4, valasz3);
            insertStatement.setString(5, valasz4);
            insertStatement.setInt(6, helyesValasz);

            insertStatement.executeUpdate();
        }
    }
}

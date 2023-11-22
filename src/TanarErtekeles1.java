
// File: index.html
// Author: Zsiga Gergely
// Copyright: 2023, Zsiga Gergely,Szávai Balázs
// Group: Szoft II-1-E
// Date: 2023-11-01
// Github: https://github.com/zsi-ga/TRS.git
// Licenc: GNU GPL


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TanarErtekeles1 extends JFrame {

    private Map<Integer, JSlider> sliders = new HashMap<>();
    private JComboBox<String> tanarComboBox;
    private JTextField osztalyField;
    private JTextField userField;
    private JCheckBox korlatozottCheckBox;
    private JPanel panel;  // Deklaráljuk a panelt itt

    
    private Map<Integer, Integer> pontok = new HashMap<>();
    private JComboBox osztalyComboBox;
    private Object osztaly;

    private static final String DATABASE_URL = "jdbc:sqlite:tanarertekeles.db";

    public TanarErtekeles1() {
        setTitle("Tanár Értékelés");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);

        panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        int kerdesekSzama = 18;

        for (int i = 1; i <= kerdesekSzama; i++) {
            JLabel label = new JLabel(i + ". kérdés:");
            panel.add(label);

            JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
            slider.setMajorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            sliders.put(i, slider);
            panel.add(slider);
        }

        JLabel osztalyLabel = new JLabel("Osztály:");
        panel.add(osztalyLabel);

        String[] osztaly = {"A", "B", "C", "D"};//valódi osztályok felsorolása a kiválasztáshoz
        osztalyComboBox = new JComboBox<>(osztaly);
        panel.add(osztalyComboBox);

        JLabel tanarLabel = new JLabel("Tanár:");
        panel.add(tanarLabel);

        String[] tanarok = {"Jani", "Pali", "Joco", "Irma"};//ide majd a valódi nevek kellenek
        tanarComboBox = new JComboBox<>(tanarok);
        panel.add(tanarComboBox);

        JLabel userLabel = new JLabel("Felhasználó kódja:");
        panel.add(userLabel);

        userField = new JTextField();
        panel.add(userField);

        korlatozottCheckBox = new JCheckBox("Korlátozott válaszadás (1 válasz/felhasználó)");
        panel.add(korlatozottCheckBox);

        JButton submitButton = new JButton("Értékelés beküldése");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bekuldesSzervernek();
            }
        });
        panel.add(submitButton);

        add(panel);
        setVisible(true);
    }

    private void bekuldesSzervernek() {
        String tanar = (String) tanarComboBox.getSelectedItem();
        String osztaly = osztalyField.getText();
        String felhasznalo = userField.getText();
        boolean korlatozott = korlatozottCheckBox.isSelected();

        if (korlatozott && felhasznaloMarValaszolt(felhasznalo)) {
            JOptionPane.showMessageDialog(this, "A felhasználó már válaszolt a kérdésekre!", "Hiba", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        pontok.clear();

      
        for (Map.Entry<Integer, JSlider> entry : sliders.entrySet()) {
            pontok.put(entry.getKey(), entry.getValue().getValue());
        }

        try {
            // Adatok mentése az adatbázisba
            mentesAdatbazisba(tanar, osztaly, felhasznalo, pontok);

            System.out.println("Értékelések sikeresen rögzítve.");

            if (korlatozott) {
                // Felhasználó válaszának jelölése
                jelolesFelhasznalonak(felhasznalo);

                System.out.println("A felhasználó válaszát elmentettük.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Adatbázis hiba!", "Hiba", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jelolesFelhasznalonak(String felhasznalo) throws SQLException {
        String updateQuery = "UPDATE ertekelesek SET valasz_jelolt = 1 WHERE felhasznalo = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

            updateStatement.setString(1, felhasznalo);
            updateStatement.executeUpdate();
        }
    }

    private boolean felhasznaloMarValaszolt(String felhasznalo) {
        //  Implementálnia, hogy ellenőrizze, hogy a felhasználó már válaszolt-e
        return false;
    }

    private void mentesAdatbazisba(String tanar, String osztaly, String felhasznalo, Map<Integer, Integer> pontok) throws SQLException {
        String insertQuery = "INSERT INTO ertekelesek (tanar, osztaly, felhasznalo";

        for (int i = 1; i <= pontok.size(); i++) {
            insertQuery += ", kerdes" + i;
        }

        insertQuery += ") VALUES (" + "?,".repeat(pontok.size() + 2).replaceAll(",$", ")");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            insertStatement.setString(1, tanar);
            insertStatement.setString(2, osztaly);
            insertStatement.setString(3, felhasznalo);

            for (int i = 1; i <= pontok.size(); i++) {
                insertStatement.setInt(i + 3, pontok.get(i));  // +3, mert az első 3 hely már foglalt
            }

            insertStatement.executeUpdate();
        }
    }

    public static void main(String[] args) {
        // SQLite JDBC driver regisztrálása
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TanarErtekeles1();
            }
        });
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.DataStorage;

/**
 * Deze methode is verantwoordelijk voor het verbinden met de database, het
 * uitvoeren van queries op deze database en het sluiten van de connectie.
 *
 * @author projectgroep12
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class ConnectionHandler {

    //instantie variabele
    private static String database_url;
    private static String username;
    private static String password;
    private static Statement stm;


    /**
     * Deze methode ontvangt de databas settings die gebruikt moeten worden om
     * verbinding te maken.
     *
     * @param url
     * @param user
     * @param pass
     */
    public void setSettings(String url, String user, String pass) {
        database_url = url;
        username = user;
        password = pass;
    }

    /**
     * Deze methode opent de connectie met de database en opent een Statemenent
     * waarmee de database bevraagd kan worden.
     */
    public void openConnection() {
        try {
            Connection conn = DriverManager.getConnection(database_url, username, password);
            stm = conn.createStatement();
        } catch (SQLException ex) {
            showError("Kan niet verbinden met de database");
        }
    }

    /**
     * Deze methode ontvangt een insert query en retouneert het ID uit de
     * AUTO_INCREMENT kolom in de database.
     *
     * @param query Een String object met de insert query.
     * @return retouneert de AUTO_INCREMENT waarde als deze aanwezig is in de
     * tabel.
     * @throws SQLException Gooit een exception als de query niet uitgevoerd kan
     * worden.
     */
    public int insert(String query) throws SQLException {
        stm.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stm.getGeneratedKeys();
        while (rs.next()) {
            return (rs.getInt(1));
        }
        return 0; //retouneer 0 als er geen AUTO_INCREMENT kolom in de tabel aanwezig is.
    }

    /**
     * Deze methode ontvangt een retrieve query en retouneert de data die deze
     * query opleverd.
     *
     * @param query Een String boject met de retrieve query.
     * @return retouneert een ResultSet met daarin de resultaten van de query.
     * @throws SQLException gooit een exception als de data niet opgehaald kan
     * worden.
     */
    public ResultSet retrieve(String query) throws SQLException {
        return (stm.executeQuery(query));
    }

    /**
     * Deze methode sluit de connectie als deze niet meer nodig is
     *
     * @param conn een Connection object waarvan de connectie gesloten moet
     * worden.
     * @throws SQLException Gooit een Exceptie als de verbinding niet gesloten
     * kan worden.
     */
    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    /**
     * Deze methode laat een Error pop-up zien met daarin het meegegeven
     * bericht.
     *
     * @param mssg Het bericht dat weergegeven moet worden in de pop-up.
     */
    private void showError(String mssg) {
        JOptionPane.showMessageDialog(null, mssg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

}

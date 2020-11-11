package tw.momocraft.playerdataplus.utils;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.bukkit.entity.Player;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

import java.sql.*;

public class MySQLAPI {
    //DataBase vars.
    final String hostname = ConfigHandler.getConfig("config.yml").getString("MyCommand.MySQL-Convertor.Settings.MySQL.hostname");
    final int port = ConfigHandler.getConfig("config.yml").getInt("MyCommand.MySQL-Convertor.Settings.MySQL.port");
    final String username = ConfigHandler.getConfig("config.yml").getString("MyCommand.MySQL-Convertor.Settings.MySQL.username");
    final String password = ConfigHandler.getConfig("config.yml").getString("MyCommand.MySQL-Convertor.Settings.MySQL.password");
    final String database = ConfigHandler.getConfig("config.yml").getString("MyCommand.MySQL-Convertor.Settings.MySQL.database");
    final String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database; //Enter URL w/db name

    //Connection vars
    static Connection connection; //This is the variable we will use to connect to database

    public MySQLAPI() {
        setUp();
    }

    // Connect the database.
    private void setUp() {
        try { //We use a try catch to avoid errors, hopefully we don't get any.
            Class.forName("com.mysql.jdbc.Driver"); //this accesses Driver in jdbc.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            return;
        }
        try { //Another try catch to get any SQL errors (for example connections errors)
            connection = DriverManager.getConnection(url, username, password);
            //with the method getConnection() from DriverManager, we're trying to set
            //the connection's url, username, password to the variables we made earlier and
            //trying to get a connection at the same time. JDBC allows us to do this.
            createTab("");
        } catch (SQLException e) { //catching errors)
            e.printStackTrace(); //prints out SQLException errors to the console (if any)
        }
    }

    public void disabledConnect() {
        // invoke on disable.
        try { //using a try catch to catch connection errors (like wrong sql password...)
            if (connection != null && !connection.isClosed()) { //checking if connection isn't null to
                //avoid receiving a nullpointer
                connection.close(); //closing the connection field variable.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTab(String tab) {
        String sql = "CREATE TABLE IF NOT EXISTS playerdata(PLAYER varchar(255) NOT NULL, VARIABLE varchar(255) NOT NULL, CONTENT varchar(255) NULL) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;";
        // prepare the statement to be executed
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            // I use executeUpdate() to update the databases table.
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void search() throws SQLException {
        String sql = "SELECT * FROM myTable WHERE Something='Something'";
        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet results = stmt.executeQuery();
        if (!results.next()) {
            System.out.println("Failed");
        } else {
            System.out.println("Success");
        }
    }


    public void addScore(Player player, int score) throws SQLException {
        PreparedStatement stat = connection.prepareStatement("INSERT INTO PlayerScore(Player_Name,Score) VALUES (?,?)");
        stat.setString(1, player.getName());
        stat.setInt(2, score);
        stat.executeQuery();
    }

    public void setScore(Player player, int score) throws SQLException {
        PreparedStatement stat = connection.prepareStatement("UPDATE PlayerScore SET Score = ? WHERE Player_Name = ?");
        stat.setString(2, player.getName());
        stat.setInt(1, score);
        stat.executeQuery();
    }

    public int getScore(Player player) throws SQLException {
        int score = 0;
        PreparedStatement stat = connection.prepareStatement("SELECT Score FROM PlayerScores WHERE Player_Name = ?");
        stat.setString(1, player.getName());
        ResultSet result = stat.executeQuery();
        while (result.next()) {
            score = result.getInt("Score");
        }
        return score;
    }
}

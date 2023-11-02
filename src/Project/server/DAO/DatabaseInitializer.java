package server.DAO;

import java.sql.DriverManager;
import java.sql.Connection;

public class DatabaseInitializer {

    // TODO: SET THIS TO THE DEFAULT GAME STATE
    public static String defaultGameState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    private static final String[] sqlCommands = {
            // create chess database
            "CREATE DATABASE IF NOT EXISTS `chess`;",
            // if chess.gameInfo exists, drop it
            "DROP TABLE IF EXISTS `chess`.`gameinfo`;",
            // create gameInfo table
            "CREATE TABLE `chess`.`gameinfo` (\n" +
                    "  `gameID` int NOT NULL AUTO_INCREMENT,\n" +
                    "  `whiteUser` VARCHAR(45),\n" +
                    "  `blackUser` VARCHAR(45),\n" +
                    "  `gameName` VARCHAR(45) NOT NULL,\n" +
                    "  `turn` VARCHAR(5) NOT NULL DEFAULT \"WHITE\",\n" +
                    "  `gameState` VARCHAR(100) NOT NULL DEFAULT \"" + defaultGameState +  "\",\n" +
                    "  PRIMARY KEY (`gameID`));",
            // if chess.gameHistory table exists, drop it
            "DROP TABLE IF EXISTS `chess`.`gamehistory`;",
            // create gameHistory table
            "CREATE TABLE `chess`.`gamehistory` (\n" +
                    "  `gameID` int NOT NULL,\n" +
                    "  `moveNumber` int NOT NULL,\n" +
                    "  `gameState` VARCHAR(100) NOT NULL,\n" +
                    "  PRIMARY KEY (`gameID`))\n;",
            // if chess.auth table exists, drop it
            "DROP TABLE IF EXISTS `chess`.`auth`;",
            // create auth table
            "CREATE TABLE `chess`.`auth` (\n" +
                    "  `authToken` VARCHAR(45) NOT NULL,\n" +
                    "  `username` VARCHAR(45) NOT NULL,\n" +
                    "  PRIMARY KEY (`authToken`));",
            // if chess.userInfo table exists, drop it
            "DROP TABLE IF EXISTS `chess`.`userinfo`;",
            // create userInfo table
            "CREATE TABLE `chess`.`userinfo` (\n" +
                    "  `username` VARCHAR(45) NOT NULL,\n" +
                    "  `password` VARCHAR(45) NOT NULL,\n" +
                    "  `email` VARCHAR(45) NOT NULL,\n" +
                    "  PRIMARY KEY (`username`));",
            // if gameSpectators table exists, drop it
            "DROP TABLE IF EXISTS `chess`.`gamespectators`;",
            // create game spectator table
            "CREATE TABLE `chess`.`gamespectators` (\n" +
                    "  `gameID` VARCHAR(45) NOT NULL,\n" +
                    "  `username` VARCHAR(45) NOT NULL,\n" +
                    "  PRIMARY KEY (`username`));",
    };

    public static void main(String[] args) throws Exception {
        try {
            var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "laseredface", "2a79bb26qz9");
//            conn.setCatalog("chess");
            // execute each command from sqlCommand in order
            for (String sqlCommand : sqlCommands) {
                var preparedStatement = conn.prepareStatement(sqlCommand);
                preparedStatement.execute();
            }
        } catch (Exception e) {
            System.out.println("Error connecting to database. Please make sure that the database is running.");
            System.out.println(e.getMessage());
            throw e;
        }
    }
}

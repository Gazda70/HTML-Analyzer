package database;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class responsible for dealing with database connection
 * and CRUD operations.
 * @author Piotr Gazda
 * @version 1.0
 * @since 5.0
 */
public class DatabaseManager {

    /**
     * Field for database connection object.
     */
    private Connection connection;

    /**
     * String constant for SQL query responsible for creating
     * table for persisting file names.
     */
    private final String createFilesTable = "CREATE TABLE Files " +
                                            "(ID int NOT NULL GENERATED ALWAYS AS IDENTITY, " +
                                            "Name varchar(255), " +
                                            "PRIMARY KEY (ID))";

    /**
     * String constant for SQL query responsible for creating
     * table for persisting operations names.
     */
    private final String createOperationsTable = "CREATE TABLE Operations " +
            "(ID int NOT NULL GENERATED ALWAYS AS IDENTITY, " +
            "Name varchar(255)," +
            "FileID int, " +
            "PRIMARY KEY (ID))";

    /**
     * Field for prepared statement object.
     */
    private PreparedStatement preparedStatement;

    /**
     * Method for setting up database driver.
     */
    public void setupDriver(){
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for setting up database connection.
     */
    public void setupConnection(){
        if(connection == null){
            try {
                connection = DriverManager.getConnection(
                        "jdbc:derby:target/tmp/derby/hpjp");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * Method for creating database tables.
     */
    public void createDatabase(){
       // dropTables();
        Statement stmt = null;
        try {
            connection.setAutoCommit( false );
            stmt = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            stmt.executeUpdate(createFilesTable);
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            stmt.executeUpdate(createOperationsTable);
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Method for executing insert statement on the database.
     * @param statement String containing SQL insert statement to execute.
     * @return Id number of inserted row.
     */
    public long executeInsertStatement(String statement){

        long generatedKey = 0;

        try {
            connection.setAutoCommit( false );
            preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try{
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                generatedKey = resultSet.getLong(1);
            }
            connection.commit();
            preparedStatement.close();
        }catch(SQLException se){
            se.printStackTrace();
        }

        return generatedKey;
    }

    /**
     * Method for executing select statement on the database.
     * @param statement String containing SQL select statement to execute.
     * @return List of strings returned by the query.
     */
    public ArrayList<String> executeSelectStatement(String statement){
        ResultSet resultSet = null;
        ArrayList<String> toReturn = new ArrayList<String>();
        try {
            connection.setAutoCommit( false );
            preparedStatement = connection.prepareStatement(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try{
            resultSet = preparedStatement.executeQuery();
            try{
                while(resultSet.next()){
                    toReturn.add(resultSet.getString("Name"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }catch(NullPointerException exception){
                toReturn.add("There weren't any operations performed !");
            }
            connection.commit();
            preparedStatement.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }

        return toReturn;
    }

    /**
     * Method for executing both update and delete statements.
     * * @param statement String containing SQL delete or update statement to execute.
     */
    public void executeDeleteOrUpdateStatement(String statement){
        try {
            connection.setAutoCommit( false );
            preparedStatement = connection.prepareStatement(statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try{
            preparedStatement.executeUpdate();
            connection.commit();
            preparedStatement.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
}

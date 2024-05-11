package connection;

interface ConnectionDB {
    public void openConnection(String databaseName);
    public void closeConnection();
}

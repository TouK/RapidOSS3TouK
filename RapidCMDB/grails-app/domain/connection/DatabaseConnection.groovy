package connection;
class DatabaseConnection extends Connection{

    String driver;
    String url;
    String username;
    String password;
    String connectionClass = "connection.DatabaseConnectionImpl";

    static constraints = {
            driver(blank:false, nullable:false);
            url(blank:false, nullable:false);
            username(blank:false, nullable:false);
            password(nullable:false);
     };
     static mapping = {
	      tablePerHierarchy false
	 }
}

package connection;
class SmartsConnection extends Connection{
    String broker;
    String domain;
    String username;
    String password;
    String connectionClass = "com.ifountain.smarts.connection.SmartsConnectionImpl";

     static constraints = {
            broker(blank:false, nullable:false);
            domain(blank:false, nullable:false);
            username(blank:false, nullable:false);
            password(nullable:false);
     };
}

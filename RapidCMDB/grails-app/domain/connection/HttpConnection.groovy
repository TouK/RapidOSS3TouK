package connection;
class HttpConnection extends Connection{
     String baseUrl;
     String connectionClass = "connection.HttpConnectionImpl";

     static constraints = {
        baseUrl(blank:false, nullable:false);
     };

}

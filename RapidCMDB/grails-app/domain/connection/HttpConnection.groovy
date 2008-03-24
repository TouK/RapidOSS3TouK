package connection;
class HttpConnection extends Connection{
     String baseUrl;
     String connectionClass = "connections.HttpConnectionImpl";

     static constraints = {
        baseUrl(blank:false, nullable:false);
     };

}

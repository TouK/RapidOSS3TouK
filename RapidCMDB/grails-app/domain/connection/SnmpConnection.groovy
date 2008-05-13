package connection;
class SnmpConnection extends Connection{
    String host;
    int port;
    String connectionClass = "noConnectionClass";

    static constraints = {
        host(blank: false);
        port(blank: false);
    };
}
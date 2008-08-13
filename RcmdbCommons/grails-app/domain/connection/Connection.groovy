package connection;
class Connection {
    def connectionService
    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB": ["master": true, "keys": ["name": ["nameInDs": "name"]]]]

    String name = "";

    String connectionClass = "";
    int maxNumberOfConnections = 10;


    static hasMany = [:]

    static constraints = {
        name(blank: false, nullable: false, key: [])
        connectionClass(blank: true, nullable: true)
    };

    static mappedBy = [:]
    static belongsTo = []
    static transients = ["connectionService"];

    String toString() {
        return "$name";
    }

    def beforeDelete = {
          connectionService.removeConnection(this.name);
    }
}

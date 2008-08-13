package connection

import com.ifountain.core.connection.ConnectionManager;
class Connection {
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
    static transients = [];

    String toString() {
        return "$name";
    }

    def beforeDelete = {
          ConnectionManager.removeConnection(this.name);
    }
}

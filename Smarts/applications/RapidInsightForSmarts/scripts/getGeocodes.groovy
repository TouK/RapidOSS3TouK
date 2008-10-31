import connection.HttpConnection
import datasource.HttpDatasource

KEY = "ABQIAAAA7ipkp7ZXyXVH2UHyOgqZhxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxRnNbZP5arP3T53Mzg-yLZcEMRBew";

def httpConn = HttpConnection.add(name: "geoConn", baseUrl: "http://maps.google.com");
def httpDatasource = HttpDatasource.add(name: "geoDs", connection: httpConn);
RsComputerSystem.list().each {RsComputerSystem device ->
    def location = device.location;
    if (location != "") {
        def params = ["q": location, "output": "csv", "key": KEY];
        def result = httpDatasource.doRequest("/maps/geo", params);
        def codes = result.split(",");
        if (codes[0] == "200") {
            def latitude = codes[2];
            def longitude = codes[3];
            device.update(geocodes: "${latitude}::${longitude}");
        }
    }
}
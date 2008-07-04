import datasource.NetcoolDatasource
import connector.NetcoolConnectorFactory

def netcoolDatasource = NetcoolDatasource.get(${datasourceName});
def connector = NetcoolConnectorFactory.createConnector(netcoolDatasource);
connector.run();
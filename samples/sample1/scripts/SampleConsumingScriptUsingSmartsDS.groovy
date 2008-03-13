import datasources.TopologyAdapter;
import org.apache.log4j.Logger;

def className = "Router";
def instanceName = "route1";
def props = ["Location":"London","DisplayName":"DisplayNameForRoute1"];
TopologyAdapter topoAdapter = TopologyAdapter.getInstance("smartsDs");
topoAdapter.addObject(className, instanceName, props);
def topoObj = topoAdapter.getObject(className, instanceName);
instanceName = "route2";
props = ["ClassName":className,"InstanceName":instanceName, "Location":"Kizlan","DisplayName":"DisplayNameForRoute2"];
topoAdapter.addObject(props);
topoObj = topoAdapter.getObject(className, instanceName);
return topoObj.Location;
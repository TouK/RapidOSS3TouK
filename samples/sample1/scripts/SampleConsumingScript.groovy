// A Device instance has its "ClassName", and "InstanceName" attributes in object space. 
// It also has dsName attribute in object space to keep the datasource from which this data come.
// "description" property is in its original datasource (either datasource 1 or datasource 2)
// "location" property is in datasource3

import models.Device;

def myDevice = Device.get(["InstanceName":"instance9"]);
myDevice.ClassName = "new classname"; //ClassName attribute is part of core data, it will be updated locally
myDevice.Location = "new location"; //Location attribute is NOT part of core data, it will keep its original value

// description attribute is NOT part of core data, it will be retrieved from the external system (original datasource) 
// location attribute is NOT part of core data, it will be retrieved from the external system (datasource3)
return "Description for (" + myDevice.InstanceName + ", " + myDevice.ClassName + ") is \"" + myDevice.Description + "\" @ " + 
                    myDevice.Location + " @ " + myDevice.dsName;


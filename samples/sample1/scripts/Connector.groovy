import datasources.DatabaseAdapter;
import org.apache.log4j.Logger;
import models.Device;
import api.RS;


def appSpace = RS.getAppSpace();
def devices = appSpace.getObjects("RsType==\"Device\"");
for(device in devices){
    appSpace.removeObject("Device", device.getRsName());
}

def ds1 = "dsForDevices1";
def ds2 = "dsForDevices2";
def adapter =  RS.getAdapter(ds1);
def records = adapter.getRecords(); 
records.each(){
	Device.add(["ClassName":it.CLASSNAME,"InstanceName":it.INSTANCENAME, "dsName":ds1]);
};

adapter =  RS.getAdapter(ds2);
records = adapter.getRecords(); 
records.each(){
	Device.add(["ClassName":it.CLASSNAME,"InstanceName":it.INSTANCENAME, "dsName":ds2]);
};
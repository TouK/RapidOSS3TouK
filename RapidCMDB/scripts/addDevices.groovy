SmartsObject.list().each{
    it.remove();
}
for(i in 0..999){
    Device.add(name: "myDevice" + i, creationClassName: "Device", smartsDs: "smartsDs" + i, ipAddress: "192.168.1."+ i,
                location: "myLocation" + i, model: "myModel" + i, snmpReadCommunity: "mysnmpReadCommunity" + i, vendor: "myVendor" + (1000-i))
}
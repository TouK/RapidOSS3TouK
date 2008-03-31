import datasource.DatabaseAdapter;
def adapter = DatabaseAdapter.getInstance("ds1", "devices1", "InstanceName");
try{
	adapter.executeUpdate("drop TABLE devices1");
}
catch(Exception e){
}
adapter.executeUpdate("CREATE TABLE devices1(instanceName VARCHAR(100), className VARCHAR(100), descr VARCHAR(100))");
adapter.addRecord(["InstanceName":"instance1", "ClassName":"class 1", "descr":"description 1"]);       
adapter.addRecord(["InstanceName":"instance2", "ClassName":"class 1","descr":"description 2"]); 
adapter.addRecord(["InstanceName":"instance3", "ClassName":"class 1","descr":"description 3"]); 
adapter.addRecord(["InstanceName":"instance4", "ClassName":"class 1","descr":"description 4"]); 
adapter.addRecord(["InstanceName":"instance5", "ClassName":"class 1","descr":"description 5"]); 
adapter.addRecord(["InstanceName":"instance6", "ClassName":"class 1","descr":"description 6"]); 

adapter = DatabaseAdapter.getInstance("ds2", "devices2", "InstanceName");
try{
	adapter.executeUpdate("drop TABLE devices2");
}
catch(Exception e){
}
adapter.executeUpdate("CREATE TABLE devices2(instanceName VARCHAR(100), className VARCHAR(100), descr VARCHAR(100))");
adapter.addRecord(["InstanceName":"instance7", "ClassName":"class 1","descr":"description 7"]);       
adapter.addRecord(["InstanceName":"instance8", "ClassName":"class 1","descr":"description 8"]); 
adapter.addRecord(["InstanceName":"instance9", "ClassName":"class 1","descr":"description 9"]);  

adapter = DatabaseAdapter.getInstance("ds3", "devices3", "InstanceName");
try{
	adapter.executeUpdate("drop TABLE devices3");
}
catch(Exception e){
}
adapter.executeUpdate("CREATE TABLE devices3(instanceName VARCHAR(100), location VARCHAR(100))");
adapter.addRecord(["InstanceName":"instance1", "location":"location1"]);       
adapter.addRecord(["InstanceName":"instance2", "location":"location2"]); 
adapter.addRecord(["InstanceName":"instance3", "location":"location3"]);  
adapter.addRecord(["InstanceName":"instance4", "location":"location4"]);  
adapter.addRecord(["InstanceName":"instance5", "location":"location3"]);  
adapter.addRecord(["InstanceName":"instance6", "location":"location1"]);  
adapter.addRecord(["InstanceName":"instance7", "location":"location4"]);  
adapter.addRecord(["InstanceName":"instance8", "location":"location3"]);  
adapter.addRecord(["InstanceName":"instance9", "location":"location1"]);  
adapter.addRecord(["InstanceName":"instance9", "location":"location9_1"]); // to show that addRecord updates if record exists

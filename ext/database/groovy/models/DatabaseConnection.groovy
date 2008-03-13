package models;

class DatabaseConnection extends Connection
{
	static final String TYPE = "DatabaseConnection";

//	 Remove after xml use for constraints is implemented
	public void createConstraints()
	{
		addConstraint("Class").mandatory().defaultValue("connections.DatabaseConnectionImpl");
		addConstraint("Class").equal("connections.DatabaseConnectionImpl");
	}

	def static create(String name, String driver, String url, String username, String password){
		Connection.create(["Type":TYPE, "Name":name, "Driver":driver, "Url":url, "Username":username, "Password":password]);
	}
	
	def static update(Map params){
		def tempParams =[:];
		tempParams.putAll(params);
		tempParams.put("Type", TYPE);
		Connection.update(tempParams);
	}
	
	def static list(){
		return Connection.list(TYPE);
	}
}



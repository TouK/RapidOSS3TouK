package models;

class SmartsConnection extends Connection
{
	public static final String TYPE = "SmartsConnection";

//	 Remove after xml use for constraints is implemented
	public void createConstraints()
	{
		addConstraint("Class").mandatory().defaultValue("com.ifountain.smarts.connection.SmartsConnectionImpl");
		addConstraint("Class").equal("com.ifountain.smarts.connection.SmartsConnectionImpl");
	}

	def static create(String name, String broker, String domain, String username, String password){
		Connection.create(["Type":TYPE, "Name":name, "Broker":broker, "Domain":domain, "Username":username, "Password":password]);
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
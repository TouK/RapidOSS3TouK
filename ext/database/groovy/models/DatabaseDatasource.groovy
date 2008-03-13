package models;

class DatabaseDatasource extends Datasource
{
	static final String TYPE = "DatabaseDatasource";

	// Remove after xml use for constraints is implemented
	public void createConstraints()
	{
		addConstraint("Adapter").mandatory().defaultValue("datasources.DatabaseAdapter");
		addConstraint("Adapter").equal("datasources.DatabaseAdapter");
	}
	
	def static create(String name, String connection){
		Datasource.create(["Type":TYPE, "Name":name, "Connection":connection]);
	}
	
	def static update(Map params){
		def tempParams =[:];
		tempParams.putAll(params);
		tempParams.put("Type", TYPE);
		Datasource.update(tempParams);
	}
	
	def static list(){
		return Datasource.list(TYPE);
	}
}
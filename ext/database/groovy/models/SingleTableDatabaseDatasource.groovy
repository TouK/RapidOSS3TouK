package models;

class SingleTableDatabaseDatasource extends Datasource
{
	static final String TYPE = "SingleTableDatabaseDatasource";
	// Remove after xml use for constraints is implemented
	public void createConstraints()
	{
		addConstraint("Adapter").mandatory().defaultValue("datasources.SingleTableDatabaseAdapter");
		addConstraint("Adapter").equal("datasources.SingleTableDatabaseAdapter");
	}
	
	def static create(String name, String connection, String table, String keys){
		Datasource.create(["Type":TYPE, "Name":name, "Connection":connection, "Table":table, "Keys":keys]);
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
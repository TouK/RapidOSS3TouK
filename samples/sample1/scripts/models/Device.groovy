package models;

import api.RS;
 
class Device extends BaseModel{
 	
	public static def get(Map params){
		return BaseModel.getObject(params, Device.class.getName());
	}
	public static def add(Map params){
		return BaseModel.add(params, Device.class.getName());
	}
}
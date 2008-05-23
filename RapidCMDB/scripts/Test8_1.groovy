/*
Model remove
	
	Verify: Model is removed regardless of the hierarchy
*/	

import datasource.*
import model.*

def myModel = Model.findByName("Link");
myModel.delete(flush:true);

myModel = Model.findByName("SmartsObject");
myModel.delete(flush:true);

return "Model is modified. Generate SmartsObject and reload application!   RUN SAMPLE3_SETUP AGAIN TO CHECK THE RESULTS!!!!";

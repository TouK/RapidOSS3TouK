/*
Model remove
	
	Verify: Model is removed regardless of the hierarchy
*/	

import datasource.*
import model.*
import com.ifountain.rcmdb.domain.generation.ModelGenerator

def myModel = Model.findByName("Link");
myModel.delete(flush:true);

// This test fails
myModel = Model.findByName("SmartsObject");
myModel.delete(flush:true);

return "Model is modified. Generate SmartsObject and reload application!   CHECK LINK MODEL TO VERIFY THE RESULTS!!!!";

/*
Model remove
	
	Verify: Model is removed regardless of the hierarchy
*/	

def myModel = Model.findByName("Link");
myModel.delete(flush:true);

// This test fails
myModel = Model.findByName("SmartsObject");
myModel.delete(flush:true);

return "Model is modified. Generate SmartsObject and reload application!   CHECK LINK MODEL TO VERIFY THE RESULTS!!!!";

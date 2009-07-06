/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
checkPropertyChangeWithNoProb()
checkPropertyChangeWithProb()
checkParentPropertyChangeWithProb()

checkRelationChangesNoProblems()
checkRelationChangesWithProblems()
checkParentRelationChangeWithProb()

return "Successfully tested the model changes"

def checkPropertyChangeWithNoProb(){
	data = Model1.search("prop1:*");
	assert data.total == 2;
	data.results.each{
		assert (it.prop1 == "Model1 Prop1 Instance1" || it.prop1 == "Model1 Prop1 Instance2")
		assert it.prop2 == ""
	}
	
	data = Model3.search("prop1:*");
	assert data.total == 2;
	data.results.each{
		assert (it.prop3 == "")
	}
	
	data = Model4.search("prop2:*");
	assert data.total == 2;
	data.results.each{
		assert (it.prop2 == "Model4 Prop2")
	}
	
	data = Model5.search("prop2:*");
	assert data.total == 2;
	data.results.each{
		assert (it.prop2 == "Model5 Prop2")
	}
	
	data = Model2.search("prop2:*");
	assert data.total == 2;
	data.results.each{
		assert (it.prop2 == "Model2 KeyToBeProp2 Instance1" || it.prop2 == "Model2 KeyToBeProp2 Instance2")
	}
}

def checkPropertyChangeWithProb(){
	data = Model24.getPropertiesList().name;
	assert (!data.contains("prop1"))
	data = Model24.list();
	assert (data.size() == 0);
	
	data = Model25.list();
	assert (data.size() == 0);
	
	data = Model26.search("prop2:*");
	data.results.each{
		assert (it.prop1 == 0)
	}
	
	data = Model27.search("prop2:*");
	data.results.each{
		assert (it.newprop1 == "")
	}
	data = Model27.getPropertiesList().name;
	assert (!data.contains("prop1"))
	
}
	
def checkParentPropertyChangeWithProb(){
	data = Model29.list();
	assert (data.size() == 0);
	data = Model30.list();
	assert (data.size() == 3);
	data.each{
		assert (it.reverse_from29To30 == null); 
	}
	
	data = Model32.list();
	assert (data.size() == 0);
	data = Model33.list();
	assert (data.size() == 1);
	data.each{
		assert (it.reverse_from32To33 == null);
	}
	
	m1 = Model35.search("prop22:*");
	assert (m1.total == 1);
	m1.results.each{
		assert (it.prop2 == 0)
	}
	data = Model36.search("prop2:\"Model36 Prop2 Instance2\"");
	assert (data.total == 1);
	assert (data.results[0].reverse_from35To36 == m1.results[0]);
	
	m1 = Model38.search("prop2:*");
	assert (m1.total == 1);
	m1.results.each{
		assert (it.newprop1 == "")
	}
	props = Model38.getPropertiesList().name;
	assert (!props.contains("prop1"))
	
	data = Model39.search("prop2:\"Model39 Prop2 Instance2\"");
	assert (data.total == 1);
	assert (data.results[0].reverse_from38To39 == m1.results[0]);
}

def checkRelationChangesNoProblems(){
	dataFroM6 = Model6.search("prop1:*");
	assert dataFroM6.total == 2;	
	dataFroM7 = Model7.search("prop1:*");
	assert dataFroM7.total == 2;	
	dataFroM6.results[0].addRelation(from6To7:dataFroM7.results[0]);
	m6 = Model6.search("prop1:Model6 Prop1 Instance1");
 	assert m6.results[0].from6To7 == dataFroM7.results[0] ;
	
 	dataFroM8 = Model8.search("prop1:*");
	assert dataFroM8.total == 2;	
	dataFroM9 = Model9.search("prop1:*");
	assert dataFroM9.total == 2;	
	dataFroM8.results[0].addRelation(from8To9:dataFroM9.results);
	m8 = Model8.search("prop1:Model8 Prop1 Instance1").results[0].from8To9;
	assert m8.size() == 2;
	m8.each{
		assert (it == dataFroM9.results[0] || it == dataFroM9.results[1]);
	}
	
	dataFroM10 = Model10.search("prop1:*");
	assert dataFroM10.total == 2;	
	dataFroM11 = Model11.search("prop1:*");
	assert dataFroM11.total == 2;	
	dataFroM10.results[0].addRelation(from10To11:[dataFroM11.results[0],dataFroM11.results[1]] );
	dataFroM10.results[1].addRelation(from10To11:dataFroM11.results[1]);
	m10 = Model10.search("prop1:Model10 Prop1 Instance1").results[0].from10To11;
	assert m10.size() == 2;
	m10.each{
		assert (it == dataFroM11.results[0] || it == dataFroM11.results[1]);
	}
	
	m11 = Model11.search("prop1:Model11 Prop1 Instance2").results[0].reverse_from10To11;
	assert m11.size() == 2;
	m11.each{
		assert (it == dataFroM10.results[0] || it == dataFroM10.results[1]);
	}
		
	for (i in 12..17){
		modelname = "Model"+i;
		def model = web.grailsApplication.getDomainClass(modelname).clazz;
 		def res = model.search("prop1:*");
 		assert (res.total == 2 || (i==15 && res.total == 3));
	}
}

def checkRelationChangesWithProblems(){
	data = Model40.list();
	assert (data.size() == 1);
	data.each{
		assert (it.newfrom40To41== null);
	}
	
	data = Model41.list();
	assert (data.size() == 1);
	data.each{
		assert (it.reverse_from40To41 == null);
	}
	
	data = Model42.list();
	assert (data.size() == 1);
	data.each{
		assert (it.newfrom42To43.size() == 0);
	}
	
	data = Model43.list();
	assert (data.size() == 2);
	data.each{
		assert (it.reverse_from42To43 == null);
	}
	
	data = Model44.list();
	assert (data.size() == 2);
	data.each{
		assert (it.newfrom44To45.size() == 0);
	}
	
	data = Model45.list();
	assert (data.size() == 2);
	data.each{
		assert (it.reverse_from44To45.size() == 0);
	}
	
	data = Model46.list();
	assert (data.size() == 1);
	data.each{
		assert (it.from46To47 == null);
	}
	
	data = Model47.list();
	assert (data.size() == 2);
	data.each{
		assert (it.reverse_from46To47 == null);	
	}
	
	data = Model48.list();
	assert (data.size() == 2);
	data.each{
		assert (it.from48To49 == null);
	}
	
	data = Model49.list();
	assert (data.size() == 2);
	data.each{
		assert (it.reverse_from48To49 == null);
	}
	
	data = Model50.list();
	assert (data.size() == 2);
	data.each{
		assert (it.from50To51.size() == 0);
	}
	
	data = Model51.list();
	assert (data.size() == 2);
	data.each{
		assert (it.reverse_from50To51 == null);
	}
}

def checkParentRelationChangeWithProb(){
	data = Model53.list();
	assert (data.size() == 1);
	data.each{
		assert (it.newfrom52To54== null);
	}
	
	data = Model54.list();
	assert (data.size() == 1);
	data.each{
		assert (it.reverse_from52To54 == null);
	}
	
	data = Model56.list();
	assert (data.size() == 1);
	data.each{
		assert (it.newfrom55To57.size() == 0);
	}
	
	data = Model57.list();
	assert (data.size() == 2);
	data.each{
		assert (it.reverse_from55To57 == null);
	}
	
	data = Model59.list();
	assert (data.size() == 2);
	data.each{
		assert (it.newfrom58To60.size() == 0);
	}
	
	data = Model60.list();
	assert (data.size() == 2);
	data.each{
		assert (it.reverse_from58To60.size() == 0);
	}
	
	data = Model62.list();
	assert (data.size() == 1);
	data.each{
		assert (it.from61To63 == null);
	}
	
	data = Model63.list();
	assert (data.size() == 2);
	data.each{
		assert (it.reverse_from61To63 == null);	
	}
	
	data = Model65.list();
	assert (data.size() == 2);
	data.each{
		assert (it.from64To66 == null);
	}
	
	data = Model66.list();
	assert (data.size() == 2);
	data.each{
		assert (it.reverse_from64To66 == null);
	}
	
	data = Model68.list();
	assert (data.size() == 2);
	data.each{
		assert (it.from67To69.size() == 0);
	}
	
	data = Model69.list();
	assert (data.size() == 2);
	data.each{
		assert (it.reverse_from67To69 == null);
	}
}
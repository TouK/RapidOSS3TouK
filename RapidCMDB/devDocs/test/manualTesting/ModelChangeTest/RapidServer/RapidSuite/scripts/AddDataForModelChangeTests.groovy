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
addDataForPropertyTestsWithNoProb()
addDataForPropertyTestsWithProb()
addDataForParentPropertyTestsWithProb()

addDataForRelationChangeTestsWithNoProb()
addRelationChangesWithProblems()
addDataForParentRelationChangeWithProb()

return "Successfully populated the models"


def addDataForPropertyTestsWithNoProb(){
	for (i in 1..5){
		def modelname = "Model"+i;
		def model = web.grailsApplication.getDomainClass(modelname).clazz;
 		model.removeAll();
	}
	// Add a non-key property to an existing modeled class
	Model1.add(prop1:"Model1 Prop1 Instance1");
	Model1.add(prop1:"Model1 Prop1 Instance2");
	
	// Add a key property to an existing modeled class when there are already key props
	Model3.add(prop1:"Model3 KeyProp1 Instance1", prop2:"Model3 Prop2");
	Model3.add(prop1:"Model3 KeyProp1 Instance2", prop2:"Model3 Prop2");
	
	// Delete  a non-key property for a model with key
	Model4.add(prop1:"Model4 ToBeDeletedProp1", prop2:"Model4 Prop2", prop3:"Model4 KeyProp3 Instance1");
	Model4.add(prop1:"Model4 ToBeDeletedProp1", prop2:"Model4 Prop2", prop3:"Model4 KeyProp3 Instance2");
	
	// Delete  a non-key property for a model without key
	Model5.add(prop1:"Model5 ToBeDeletedProp1", prop2:"Model5 Prop2");
	Model5.add(prop1:"Model5 ToBeDeletedProp1", prop2:"Model5 Prop2");
	
	// Make a non-key property, key
	Model2.add(prop1:"Model2 Prop1 Instance1", prop2:"Model2 KeyToBeProp2 Instance1");
	Model2.add(prop1:"Model2 Prop1 Instance2", prop2:"Model2 KeyToBeProp2 Instance2");
}

def addDataForPropertyTestsWithProb(){
	for (i in 24..27){
		def modelname = "Model"+i;
		def model = web.grailsApplication.getDomainClass(modelname).clazz;
 		model.removeAll();
	}
	// Delete key property
	Model24.add(prop1:"Model24 ToBeDeletedKeyProp1", prop2:"Model24 Prop2 Instance1");
	Model24.add(prop1:"Model24 ToBeDeletedKeyProp1", prop2:"Model24 Prop2 Instance2");
	
	// Make a key property non-key
	Model25.add(prop1:"Model25 ToBeNonKeyProp1", prop2:"Model25 Prop2 Instance1");
	Model25.add(prop1:"Model25 ToBeNonKeyProp1", prop2:"Model25 Prop2 Instance2");
	
	// Change type of the property
	Model26.add(prop1:"Model26 ToBeTypeChangedProp1", prop2:"Model26 Prop2 Instance1");
	Model26.add(prop1:"Model26 ToBeTypeChangedProp1", prop2:"Model26 Prop2 Instance2");
	
	// Rename property
	Model27.add(prop1:"Model27 ToBeRenamedProp1", prop2:"Model27 Prop2 Instance1");
	Model27.add(prop1:"Model27 ToBeRenamedProp1", prop2:"Model27 Prop2 Instance2");
}

def addDataForParentPropertyTestsWithProb(){
	for (i in 28..39){
		def modelname = "Model"+i;
		def model = web.grailsApplication.getDomainClass(modelname).clazz;
 		model.removeAll();
	}
	// Delete key property
	m1 = Model29.add(prop1:"Model29 ToBeDeletedKeyProp1", prop2:"Model29 Prop2 Instance1", prop29_1:"Model29 Prop29_1 Instance1");
	m2 = Model30.add(prop1:"Model30 Prop1", prop2:"Model30 Prop2 Instance1");
	m3 = Model30.add(prop1:"Model30 Prop1", prop2:"Model30 Prop2 Instance2");
	Model30.add(prop1:"Model30 Prop1", prop2:"Model30 Prop2 Instance3");
	m1.addRelation(from29To30:[m2,m3]);
	
	// Make a key property non-key
	m1 = Model32.add(prop1:"Model32 ToBeNonKeyProp1", prop2:"Model32 Prop2 Instance1", prop32_1:"Model32 Prop32_1 Instance1");
	m2 = Model33.add(prop1:"Model33 Prop1", prop2:"Model33 Prop2 Instance1");
	m1.addRelation(from32To33:m2);
	
	// Change type of the property
	m1 = Model35.add(prop1:"Model35 ToBeTypeChangedProp1", prop22:"Model35 Prop2 Instance1", prop35_1:"Model35 Prop35_1 Instance1");
	m2 = Model36.add(prop1:"Model36 Prop1", prop2:"Model36 Prop2 Instance1");
	m3 = Model36.add(prop1:"Model36 Prop1", prop2:"Model36 Prop2 Instance2");
	m1.addRelation(from35To36:[m2,m3]);
	
	// Rename property
	m1 = Model38.add(prop1:"Model38 ToBeRenamedProp1", prop2:"Model38 Prop2 Instance1", prop38_1:"Model38 Prop38_1 Instance1");
	m2 = Model39.add(prop1:"Model39 Prop1", prop2:"Model39 Prop2 Instance1");
	m3 = Model39.add(prop1:"Model39 Prop1", prop2:"Model39 Prop2 Instance2");
	m1.addRelation(from38To39:[m2,m3]);
}

def addDataForRelationChangeTestsWithNoProb(){
	for (i in 6..23){
		def modelname = "Model"+i;
		def model = web.grailsApplication.getDomainClass(modelname).clazz;
 		model.removeAll();
	}
	// Add One-to-One Relation between 2 modeled classes
	Model6.add(prop1:"Model6 Prop1 Instance1");
	Model6.add(prop1:"Model6 Prop1 Instance2");
	Model7.add(prop1:"Model7 Prop1 Instance1");
	Model7.add(prop1:"Model7 Prop1 Instance2");
	
	// Add One-to-Many Relation between 2 modeled classes
	Model8.add(prop1:"Model8 Prop1 Instance1");
	Model8.add(prop1:"Model8 Prop1 Instance2");
	Model9.add(prop1:"Model9 Prop1 Instance1");
	Model9.add(prop1:"Model9 Prop1 Instance2");
	
    // Add Many-to-Many Relation between 2 modeled classes
	Model10.add(prop1:"Model10 Prop1 Instance1");
	Model10.add(prop1:"Model10 Prop1 Instance2");
	Model11.add(prop1:"Model11 Prop1 Instance1");
	Model11.add(prop1:"Model11 Prop1 Instance2");
	
	// Delete One-to-One Relation between 2 modeled classes
	m12_1 = Model12.add(prop1:"Model12 Prop1 Instance1");
	m12_2 = Model12.add(prop1:"Model12 Prop1 Instance2");
	m13_1 = Model13.add(prop1:"Model13 Prop1 Instance1");
	m13_2 = Model13.add(prop1:"Model13 Prop1 Instance2");
	m12_1.addRelation(from12To13:m13_1);
	m12_2.addRelation(from12To13:m13_2);
	
    // Delete One-to-Many Relation between 2 modeled classes
    m14_1 = Model14.add(prop1:"Model14 Prop1 Instance1");
	m14_2 = Model14.add(prop1:"Model14 Prop1 Instance2");
	m15_1 = Model15.add(prop1:"Model15 Prop1 Instance1");
	m15_2 = Model15.add(prop1:"Model15 Prop1 Instance2");
	m15_3 = Model15.add(prop1:"Model15 Prop1 Instance3");
	m14_1.addRelation(from14To15:[m15_1,m15_2]);
	m14_2.addRelation(from14To15:m15_3);
	
    // Delete Many-to-Many Relation between 2 modeled classes
    m16_1 = Model16.add(prop1:"Model16 Prop1 Instance1");
	m16_2 = Model16.add(prop1:"Model16 Prop1 Instance2");
	m17_1 = Model17.add(prop1:"Model17 Prop1 Instance1");
	m17_2 = Model17.add(prop1:"Model17 Prop1 Instance2");
	m16_1.addRelation(from16To17:[m17_1,m17_2]);
	m16_2.addRelation(from16To17:m17_2);
	
	// Change cardinality from One-to-One to One-to-Many
	m18_1 = Model18.add(prop1:"Model18 Prop1 Instance1");
	m18_2 = Model18.add(prop1:"Model18 Prop1 Instance2");
	m19_1 = Model19.add(prop1:"Model19 Prop1 Instance1");
	m19_2 = Model19.add(prop1:"Model19 Prop1 Instance2");
	m18_1.addRelation(from18To19:m19_1);
	m18_2.addRelation(from18To19:m19_2);
	
    // Change cardinality from One-to-One to Many-to-Many
    m20_1 = Model20.add(prop1:"Model20 Prop1 Instance1");
	m20_2 = Model20.add(prop1:"Model20 Prop1 Instance2");
	m21_1 = Model21.add(prop1:"Model21 Prop1 Instance1");
	m21_2 = Model21.add(prop1:"Model21 Prop1 Instance2");
	m20_1.addRelation(from20To21:m21_1);
	m20_2.addRelation(from20To21:m21_2);
	
    // Change cardinality from One-to-Many to Many-to-Many
    m22_1 = Model22.add(prop1:"Model22 Prop1 Instance1");
	m22_2 = Model22.add(prop1:"Model22 Prop1 Instance2");
	m23_1 = Model23.add(prop1:"Model23 Prop1 Instance1");
	m23_2 = Model23.add(prop1:"Model23 Prop1 Instance2");
	m23_3 = Model23.add(prop1:"Model23 Prop1 Instance3");
	m22_1.addRelation(from22To23:[m23_1,m23_2]);
	m22_2.addRelation(from22To23:m23_3);
	
}

def addRelationChangesWithProblems(){
	for (i in 40..51){
		def modelname = "Model"+i;
		def model = web.grailsApplication.getDomainClass(modelname).clazz;
 		model.removeAll();
	}
	// Rename One-to-One Relation between 2 modeled classes
	m1 = Model40.add(prop1:"Model40 Prop1 Instance1");
	m2 = Model41.add(prop1:"Model41 Prop1 Instance1");
	m1.addRelation(from40To41:m2);
	
	// Rename One-to-Many Relation between 2 modeled classes
	m1 = Model42.add(prop1:"Model42 Prop1 Instance1");
	m2 = Model43.add(prop1:"Model43 Prop1 Instance1");
	m3 = Model43.add(prop1:"Model43 Prop1 Instance2");
	m1.addRelation(from42To43:[m2,m3]);
	
	// Rename Many-to-Many Relation between 2 modeled classes
	m1 = Model44.add(prop1:"Model44 Prop1 Instance1");
	m2 = Model44.add(prop1:"Model44 Prop1 Instance2");
	m3 = Model45.add(prop1:"Model45 Prop1 Instance1");
	m4 = Model45.add(prop1:"Model45 Prop1 Instance2");
	m1.addRelation(from44To45:[m3,m4]);
	m4.addRelation(reverse_from44To45:[m2]);
	
	// Change cardinality from One-to-Many to One-to-One
	m1 = Model46.add(prop1:"Model46 Prop1 Instance1");
	m2 = Model47.add(prop1:"Model47 Prop1 Instance1");
	m3 = Model47.add(prop1:"Model47 Prop1 Instance2");
	m1.addRelation(from46To47:[m2,m3]);
	
	// Change cardinality from Many-to-Many to One-to-One
	m1 = Model48.add(prop1:"Model48 Prop1 Instance1");
	m2 = Model48.add(prop1:"Model48 Prop1 Instance2");
	m3 = Model49.add(prop1:"Model49 Prop1 Instance1");
	m4 = Model49.add(prop1:"Model49 Prop1 Instance2");
	m1.addRelation(from48To49:[m3,m4]);
	m4.addRelation(reverse_from48To49:[m2]);
	
	// Change cardinality from Many-to-Many to One-to-Many
	m1 = Model50.add(prop1:"Model50 Prop1 Instance1");
	m2 = Model50.add(prop1:"Model50 Prop1 Instance2");
	m3 = Model51.add(prop1:"Model51 Prop1 Instance1");
	m4 = Model51.add(prop1:"Model51 Prop1 Instance2");
	m1.addRelation(from50To51:[m3,m4]);
	m4.addRelation(reverse_from50To51:[m2]);
}

def addDataForParentRelationChangeWithProb(){
	for (i in 52..69){
		def modelname = "Model"+i;
		def model = web.grailsApplication.getDomainClass(modelname).clazz;
 		model.removeAll();
	}
	
	// Rename One-to-One Relation between 2 modeled classes
	m1 = Model53.add(prop1:"Model53 Prop1 Instance1");
	m2 = Model54.add(prop1:"Model54 Prop1 Instance1");
	m1.addRelation(from52To54:m2);
	
	// Rename One-to-Many Relation between 2 modeled classes
	m1 = Model56.add(prop1:"Model56 Prop1 Instance1");
	m2 = Model57.add(prop1:"Model57 Prop1 Instance1");
	m3 = Model57.add(prop1:"Model57 Prop1 Instance2");
	m1.addRelation(from55To57:[m2,m3]);
	
	// Rename Many-to-Many Relation between 2 modeled classes
	m1 = Model59.add(prop1:"Model59 Prop1 Instance1");
	m2 = Model59.add(prop1:"Model59 Prop1 Instance2");
	m3 = Model60.add(prop1:"Model60 Prop1 Instance1");
	m4 = Model60.add(prop1:"Model60 Prop1 Instance2");
	m1.addRelation(from58To60:[m3,m4]);
	m4.addRelation(reverse_from58To60:[m2]);
	
	// Change cardinality from One-to-Many to One-to-One
	m1 = Model62.add(prop1:"Model62 Prop1 Instance1");
	m2 = Model63.add(prop1:"Model63 Prop1 Instance1");
	m3 = Model63.add(prop1:"Model63 Prop1 Instance2");
	m1.addRelation(from61To63:[m2,m3]);
	
	// Change cardinality from Many-to-Many to One-to-One
	m1 = Model65.add(prop1:"Model65 Prop1 Instance1");
	m2 = Model65.add(prop1:"Model65 Prop1 Instance2");
	m3 = Model66.add(prop1:"Model66 Prop1 Instance1");
	m4 = Model66.add(prop1:"Model66 Prop1 Instance2");
	m1.addRelation(from64To66:[m3,m4]);
	m4.addRelation(reverse_from64To66:[m2]);
	
	// Change cardinality from Many-to-Many to One-to-Many
	m1 = Model68.add(prop1:"Model68 Prop1 Instance1");
	m2 = Model68.add(prop1:"Model68 Prop1 Instance2");
	m3 = Model69.add(prop1:"Model69 Prop1 Instance1");
	m4 = Model69.add(prop1:"Model69 Prop1 Instance2");
	m1.addRelation(from67To69:[m3,m4]);
	m4.addRelation(reverse_from67To69:[m2]);
	
}

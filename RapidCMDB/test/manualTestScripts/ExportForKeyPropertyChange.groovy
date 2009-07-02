createInstancesAndRelations()

def expUtility = new ExportUtility()
def modelName = "Author"
def fname = "export.xml"
expUtility.exportBothPropertiesAndRelationsForAModelAndItsChildren(web, modelName,fname)
return "Done"

def createInstancesAndRelations(){
	Author.removeAll()
	Book.removeAll()
	Publisher.removeAll()
	def author1 = Author.add(name:"John", lastname:"Steinbeck");
	def author2 = Author.add(name:"Orhan", lastname:"Pamuk");
	def author3 = Author.add(name:"Carl", lastname:"Sagan");
	def author4 = Author.add(name:"Jared", lastname:"Diamond");

	def book1 = Fiction.add(isbn:"isbn_GOW",title:"grapes of wrath",fictionProp:"fiction prop for grapes of wrath", prop2:10);
	def book2 = Fiction.add(isbn:"isbn_BAK",title:"benim adim kirmizi", fictionProp:"fiction prop for benim adim kirmizi", prop2:9);
	def book3 = Fiction.add(isbn:"isbn_Kar",title:"Kar", fictionProp:"fiction prop for Kar", prop2:8);
	def book4 = Nonfiction.add(isbn:"isbn_Csms",title:"Cosmos", nonfictionProp:"nonfiction prop for Cosmos");
	def book5 = Nonfiction.add(isbn:"isbn_TC",title:"Third Chimpanzee", nonfictionProp:"nonfiction prop for Third Chimpanzee");
	def book6 = Nonfiction.add(isbn:"isbn_GGS",title:"Guns, Germs, and Steel", nonfictionProp:"nonfiction prop for Guns, Germs, and Steel");
	
	def publisher1 = Publisher.add(name:"Penguen", address:"Address Line")
	def publisher2 = Publisher.add(name:"Prentice Hall", address:"Another Address Line")
	
	book1.addRelation(fictionPublisher:publisher1);
	book4.addRelation(nonfictionPublisher:publisher2);
	
	author1.addRelation(myBooks:book1);
	author2.addRelation(myBooks:book2);
	book3.addRelation(myAuthor:author2);
	
	book4.addRelation(myAuthor:author3);
	author4.addRelation(myBooks:[book5,book6]);
}

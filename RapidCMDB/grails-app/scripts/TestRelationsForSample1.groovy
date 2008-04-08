// clean model instances
/*
def emp1= Employee.get(name:"ayse");
def emp2= Employee.get(name:"ali");
def emp3= Employee.get(name:"veli");
def dev1= Developer.get(name:"gonca");
def dev2= Developer.get(name:"bilal");
def dev3= Developer.get(name:"musa");
def team1= Team.get(name:"lions");
def team2= Team.get(name:"zebras");
def team3= Team.get(name:"stars",maskot:"sun");
def task1= Task.get(name:"testing v1");
def task2= Task.get(name:"testing v2");
def task3= Task.get(name:"development v3");
def task4= Task.get(name:"magellan alpha");
def task5= Task.get(name:"SP 4");
*/
def all = Person.list();
all.each{
	it.remove();
}
all = Team.list();
all.each{
	it.remove();
}
all = Task.list();
all.each{
	it.remove();
}
println "size after delete:" + Person.list().size();

// recreate them
def emp1= Employee.add(name:"ayse",bday:"1/1/11",dept:"QA");
def emp2= Employee.add(name:"ali",bday:"2/2/22",dept:"QA");
def emp3= Employee.add(name:"veli",bday:"3/3/33",dept:"QA");
def dev1= Developer.add(name:"gonca",bday:"4/4/44",dept:"Dev",language:"java");
def dev2= Developer.add(name:"bilal",bday:"5/5/55",dept:"Dev",language:"c++");
def dev3= Developer.add(name:"musa",bday:"6/6/66",dept:"Dev",language:"lisp");
def team1= Team.add(name:"lions",maskot:"lion");
def team2= Team.add(name:"zebras",maskot:"zebra");
def team3= Team.add(name:"stars",maskot:"sun");
def task1= Task.add(name:"testing v1");
def task2= Task.add(name:"testing v2");
def task3= Task.add(name:"development v3");
def task4= Task.add(name:"magellan alpha");
def task5= Task.add(name:"SP 4");

// add reverse relation to dev1: emp1->dev1
emp1.addRelation(prevEmp:dev1);
emp1 = Employee.get(name:"ayse");
assert emp1.name == "ayse"
assert emp1.bday == "1/1/11";
def temp = emp1.prevEmp;
assert temp.name == dev1.name; 
temp = temp.nextEmp;
assert temp.name == emp1.name; 

// add relation to dev1: dev1->emp2
dev1.addRelation(prevEmp:emp2);
dev1 = Employee.get(name:"gonca");
assert dev1.name == "gonca";
assert dev1.bday == "4/4/44";
temp = dev1.prevEmp;
assert temp.name == emp2.name; 

// remove reverse relation from emp2
emp2.removeRelation(nextEmp:dev1);
dev1 = Employee.get(name:"gonca");
assert dev1.prevEmp == null; 

// remove instance with a relation. instance is at the end of the relation chain. emp1->dev1->emp2. remove emp2
dev1.addRelation(prevEmp:emp2);
dev1 = Employee.get(name:"gonca");
assert dev1.bday == "4/4/44";
temp = dev1.prevEmp;
assert temp.name == emp2.name; 
temp.remove();
dev1 = Employee.get(name:"gonca");
assert dev1.prevEmp == null; 
emp2 = Employee.get(name:"ali");
assert emp2 == null;

// remove instance with a relation. instance is in the middle of the relation chain. emp1->dev1->emp2. remove dev1 
emp2= Employee.add(name:"ali",bday:"2/2/22",dept:"QA");
dev1.addRelation(prevEmp:emp2);
dev1.remove();
emp1= Employee.get(name:"ayse");
assert emp1.prevEmp == null;
emp2= Employee.get(name:"ali");
assert emp1.nextEmp == null;

dev1= Developer.add(name:"gonca",bday:"4/4/44",dept:"Dev",language:"java");
// add relation: emp1 manages emp3, dev2. Add relation in both directions (employees and manager)
emp1.addRelation(employees:emp3);
dev2.addRelation(manager:emp1);
println "dev2 errors: " + dev2.errors;
println "emp1 errors: " + emp1.errors;
emp1= Employee.get(name:"ayse");
def cntr = 0;
emp1.employees.each{
	assert (it.name == emp3.name || it.name == dev2.name );
	println it.name
	cntr++;
}
assert cntr == 2;


return "success"

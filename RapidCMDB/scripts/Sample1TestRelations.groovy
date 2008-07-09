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

// T1: add relation to dev1: dev1->emp2
dev1.addRelation(prevEmp:emp2);
dev1 = Employee.get(name:"gonca");
assert dev1.name == "gonca";
assert dev1.bday == "4/4/44";
temp = dev1.prevEmp;
assert temp.name == emp2.name;

// T2: remove reverse relation from emp2
emp2.removeRelation(nextEmp:dev1);
dev1 = Employee.get(name:"gonca");
assert dev1.prevEmp == null;

// T3: remove instance with a relation. instance is at the end of the relation chain. emp1->dev1->emp2. remove emp2
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

// T4: remove instance with a relation. instance is in the middle of the relation chain. emp1->dev1->emp2. remove dev1
emp2= Employee.add(name:"ali",bday:"2/2/22",dept:"QA");
dev1.addRelation(prevEmp:emp2);
dev1.remove();
emp1= Employee.get(name:"ayse");
assert emp1.prevEmp == null;
emp2= Employee.get(name:"ali");
assert emp1.nextEmp == null;

dev1= Developer.add(name:"gonca",bday:"4/4/44",dept:"Dev",language:"java");
/* UNCOMMENT WHEN CMDB-283 IS FIXED 

// T5: add relation: emp1 manages emp3, dev2. Add relation in both directions (employees and manager)
emp1.addRelation(employees:emp3);
dev2.addRelation(manager:emp1);
emp1= Employee.get(name:"ayse");
def cntr = 0;
emp1.employees.each{
	assert (it.name == emp3.name || it.name == dev2.name );
	cntr++;
}
assert cntr == 2;

// T6: add relation: dev1 manages emp2, emp1, dev3. Add relation in both directions (employees and manager)
// dev1.addRelation(employees:emp2);
dev3.addRelation(manager:dev1);
emp1.addRelation(manager:dev1);
dev1= Employee.get(name:"gonca");
cntr = 0;
dev1.employees.each{
	assert (it.name == emp2.name || it.name == dev3.name || it.name == emp1.name);
	cntr++;
}
assert cntr == 3;

// T7: change the manager of dev3 and confirm it is reflected on both dev1, dev3, and new manager emp1
dev3.addRelation(manager:emp1);
if (emp1.employees.contains(dev3)) assert true;
if (!dev1.employees.contains(dev3)) assert true;
// delete emp1 (ayse)
emp1.remove();
println dev1.employees;
println emp3.manager;
println dev2.manager;
println dev3.manager;
emp1= Employee.add(name:"ayse",bday:"1/1/11",dept:"QA");
*/

// T8: add and change 1-m relation between employee/developer and team
emp2.addRelation(manages:team1);
dev1.addRelation(manages:team2);
dev1.addRelation(manages:team3);

assert (team1.managedBy).name == emp2.name;
cntr = 0;
dev1.manages.each{
	assert (it.name == team2.name || it.name == team3.name);
	cntr++;
}
assert cntr == 2;

//team3.managedBy = emp1; DOES NOT WORK NOW
team3.addRelation(managedBy:emp2);
cntr = 0;
emp2.manages.each{
	assert (it.name == team1.name || it.name == team3.name);
	cntr++;
}
assert cntr == 2;

dev1 = Developer.get(name:"gonca");
assert dev1.language == "java";
assert dev1.manages.size() == 1;
// assert dev1.manages[0].name == team2.name;
dev1.manages.each{
	assert it.name == team2.name;
}

// T9: Remove a relation bw Employee and Team
team1.removeRelation(managedBy:emp2);
assert team1.managedBy == null;
assert emp2.manages.size() == 1;
// assert emp2.manages[0].name == team3.name;
emp2.manages.each{
	assert it.name == team3.name;
}

// T10: Remove instance with a relation bw Employee and Team
emp2.remove()
emp2 = Employee.add(name:"ali",bday:"2/2/22",dept:"QA");

// T11: add and change m-m relation between employee/developer and team
dev2.addRelation(worksOn:task1);
dev1.addRelation(worksOn:task1);
dev1.addRelation(worksOn:task2);

assert task2.workedOnBy.size() == 1;
task2.workedOnBy.each{
	assert it.name == dev1.name;
}
cntr = 0;
task1.workedOnBy.each{
	assert (it.name == dev1.name || it.name == dev2.name);
	cntr++;
}
assert cntr == 2;

task2.addRelation(workedOnBy:dev3);
cntr = 0;
dev3.worksOn.each{
	assert (it.name == task2.name);
	cntr++;
}
assert cntr == 1;

cntr = 0;
task2.workedOnBy.each{
	assert (it.name == dev1.name || it.name == dev3.name);
	cntr++;
}
assert cntr == 2;
// T12: Remove a relation bw Developer and Task
task1.removeRelation(workedOnBy:dev2);
assert task1.workedOnBy.size() == 1;
assert dev2.worksOn.size() == 0;
dev1.worksOn.each{
	assert it.name == task2.name || it.name == task1.name ;
}

// T13: Remove instance with a relation bw Developer and Task
task1.remove()
dev3.remove()

return "success"

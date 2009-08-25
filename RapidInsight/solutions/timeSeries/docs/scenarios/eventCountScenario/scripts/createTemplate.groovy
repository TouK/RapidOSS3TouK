def rddVar = RrdGraphTemplate.add(name:"template1", title:"All Events");
assert rrdVar.hasErrors() == false
return "Template created successfully"

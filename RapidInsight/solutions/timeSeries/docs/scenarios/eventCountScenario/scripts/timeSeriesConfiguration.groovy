//Step 1:creating variables
def allEvents = RrdVariable.add(name:"allEvents", frequency:60)
def criticalEvents = RrdVariable.add(name:"criticalEvents", frequency:60)
def majorEvents = RrdVariable.add(name:"majorEvents", frequency:60)

//Step 2:creating time series database from variables
allEvents.createDB();
criticalEvents.createDB();
majorEvents.createDB();

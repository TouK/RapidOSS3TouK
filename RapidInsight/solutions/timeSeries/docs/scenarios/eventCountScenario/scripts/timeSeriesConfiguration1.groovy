//Step 1:creating archives (Some default archives are already added to system by default)
def oneH = RrdArchive.add(name:"oneH", step:60, row:60)
def sixH = RrdArchive.add(name:"sixH", step:240, row:90)
def twelveH = RrdArchive.add(name:"twelveH", step:6, row:120)
def twentyFourH = RrdArchive.add(name:"twentyFourH", step:9, row:160)

//Step 2:creating variables
def allEvents = RrdVariable.add(name:"allEvents", archives: [oneH, sixH, twelveH, twentyfourH])
def criticalEvents = RrdVariable.add(name:"criticalEvents", archives: [oneH, sixH, twelveH, twentyfourH])
def majorEvents = RrdVariable.add(name:"majorEvents", archives: [oneH, sixH, twelveH, twentyfourH])

//Step 3:creating time series database from variables
allEvents.createDB();
criticalEvents.createDB();
majorEvents.createDB();

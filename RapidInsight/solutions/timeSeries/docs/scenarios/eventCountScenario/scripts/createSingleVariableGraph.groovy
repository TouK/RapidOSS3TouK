def criticalEventsRrd = RrdVariable.get(name:"criticalEvents")
criticalEventsRrd.graph()

/*
Users can also pass configuration data for graph such as title, width, height, vlabel, startTime, endTime and etc.
    criticalEventsRrd.graph(title:"Number of Critical Events")
*/
def allEvents = RrdVariable.get(name:"allEvents");
def graphConfiguration =[template:"template1"]
allEvents.graph(graphConfiguration )
/*
Title specified in template can be overwritten if it is specified in graphConfiguration
    def graphConfiguration =[template:"template1", title:"Overwritten Template Title for All Events"]
*/
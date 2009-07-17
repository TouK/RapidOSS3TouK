def variableConfiguration = [
    allEvents:[color:"0000FF"],
    criticalEvents:[color:"FF0000"],
    majorEvents:[color:"00FF00"],
]
RrdVariable.graphMultiple(variableConfiguration)
/*
-Note that graph configuration can be passed to this function optionally. If it is not specified graph will be
constructed with default properties.
    RrdVariable.graphMultiple(variableConfiguration, [title:"Event Count Graph"])
-Users can specify type, thickness, description for variable configuration. If they are not specified, following default
values will be assigned to these configuration items
       type         :line
       thickness    :2
       description  : variable name
*/
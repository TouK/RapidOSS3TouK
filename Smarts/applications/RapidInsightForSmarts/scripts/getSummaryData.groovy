def nodeType = params.nodeType;
def name = params.name;

def severityMap = ["1":0, "2":0, "3":0, "4":0, "5":0]
def severitySummary = null
if(nodeType == "Container"){
    severitySummary = RsSmartsNotification.propertySummary("className:\"${name}\"", ["severity"]);
}
else{
   severitySummary = RsSmartsNotification.propertySummary("instanceName:\"${name}\"", ["severity"]);
}
 severitySummary.severity.each{propValue, numberOfObjects ->
     severityMap.put("" + propValue, numberOfObjects);
 }
web.render(contentType: 'text/xml'){
   Items(){
      Item(severity:"Critical", count:severityMap.get("1"))
      Item(severity:"Major", count:severityMap.get("2"))
      Item(severity:"Minor", count:severityMap.get("3"))
      Item(severity:"Unknown", count:severityMap.get("4"))
      Item(severity:"Normal", count:severityMap.get("5"))
   }
}
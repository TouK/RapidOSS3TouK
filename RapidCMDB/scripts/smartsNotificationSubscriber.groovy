
def getParameters(){
   return [
           "Attributes":["ClassName", "InstanceName", "EventName", "Severity"],
           "NotificationList":"ALL_NOTIFICATIONS",
           "TransientInterval":300,
           "TailMode":false
   ]
}

def init(){

}

def cleanUp(){

}

def update(event){
    println "Notification object: ${event}";
}
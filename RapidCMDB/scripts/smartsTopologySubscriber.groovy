
def getParameters(){
   return [
           "subscribeParameters":[
               ["CreationClassName":"Router", "Name":".*", "Attributes":["Model", "Location"]],
               ["CreationClassName":"Switch", "Name":".*", "Attributes":["Model", "Location"]],
               ["CreationClassName":"Host", "Name":".*", "Attributes":[]]
           ]
   ]
}

def init(){

}

def cleanUp(){

}

def update(object){
    println "Topology object: ${object}";
}
import application.RapidApplication

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 14, 2009
* Time: 9:56:35 AM
*/

def getParameters() {

    return [
            Classes: [
                    RsEvent: [],
                    RsTopologyObject: ["afterDelete"]
            ]
    ]
}
def init() {}
def cleanUp() {}

def update(changeEvent) {    
    def domainObject = changeEvent.domainObject;
    def eventName = changeEvent.eventName;
    if (domainObject instanceof RsEvent) {
        if (eventName == "afterInsert") {
            getStateCalculatorUtility().eventIsAdded(domainObject)

        }
        else if (eventName == "afterUpdate") {
            getStateCalculatorUtility().eventIsUpdated(domainObject, changeEvent.updatedProps)
        }
        else {
            getStateCalculatorUtility().eventIsDeleted(domainObject)
        }

    }
    else {
        getStateCalculatorUtility().objectIsDeleted(domainObject)
    }
}

def getStateCalculatorUtility() {
    RapidApplication.getUtility("StateCalculator");
}
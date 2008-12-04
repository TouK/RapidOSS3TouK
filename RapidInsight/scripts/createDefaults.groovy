import script.CmdbScript

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 12, 2008
* Time: 10:50:03 AM
*/
CmdbScript.addScript(name: "modelCreator");
CmdbScript.addScript(name: "removeAll");
CmdbScript.addScript(name: "acknowledge");
CmdbScript.addScript(name: "setOwnership");
CmdbScript.addScript(name: "queryList");
CmdbScript.addScript(name: "createQuery");
CmdbScript.addScript(name: "editQuery");
CmdbScript.addScript(name: "reloadOperations");
CmdbScript.addScript(name: "getViewFields");

CmdbScript.addScript(name: "importSampleRiData");

// topology scripts
CmdbScript.addScript(name: "createMap");
CmdbScript.addScript(name: "editMap");
CmdbScript.addScript(name: "expandMap");
CmdbScript.addScript(name: "getMap");
CmdbScript.addScript(name: "mapList");
CmdbScript.addScript(name: "saveMap");
CmdbScript.addScript(name: "getMapData");
CmdbScript.addScript(name: "createDefaultQueries");
CmdbScript.runScript("createDefaultQueries");
/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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
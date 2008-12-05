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
def expUtility = new ExportUtility();

expUtility.exportIdsAndSelectedPropertiesForAModelAndItsChildren(web, "RsNetworkAdapter",["creationClassName","deviceID"], "exportRsNetworkAdaptorSelectedProps.xml")
expUtility.exportPropertiesForAModelAndItsChildren(web, "RsNetworkAdapter","exportRsNetworkAdaptorProperties.xml")
expUtility.exportRelationsForAModelAndItsChildren(web, "RsNetworkAdapter","exportRsNetworkAdaptorRelations.xml")
expUtility.exportBothPropertiesAndRelationsForAModelAndItsChildren(web, "RsNetworkAdapter","exportRsNetworkAdaptor.xml")
expUtility.exportAllData(web,"all.xml")

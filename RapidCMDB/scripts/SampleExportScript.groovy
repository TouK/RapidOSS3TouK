def expUtility = new ExportUtility();

expUtility.exportIdsAndSelectedPropertiesForAModelAndItsChildren(web, "RsNetworkAdapter",["creationClassName","deviceID"], "exportRsNetworkAdaptorSelectedProps.xml")
expUtility.exportPropertiesForAModelAndItsChildren(web, "RsNetworkAdapter","exportRsNetworkAdaptorProperties.xml")
expUtility.exportRelationsForAModelAndItsChildren(web, "RsNetworkAdapter","exportRsNetworkAdaptorRelations.xml")
expUtility.exportBothPropertiesAndRelationsForAModelAndItsChildren(web, "RsNetworkAdapter","exportRsNetworkAdaptor.xml")
expUtility.exportAllData(web,"all.xml")

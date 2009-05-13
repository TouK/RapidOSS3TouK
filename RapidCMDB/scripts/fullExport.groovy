import com.ifountain.rcmdb.domain.FullExportImportUtility

CONFIG=[:];
CONFIG.backupDir="backupData";
CONFIG.exportDir="exportFiles";
CONFIG.objectsPerFile=1000;

CONFIG.MODELS=[];
CONFIG.MODELS.add([model:"all"]);
//CONFIG.MODELS.add([model:"conf"]);
//CONFIG.MODELS.add([model:"RsTopologyObject"]);
//CONFIG.MODELS.add([model:"RsGroup",childModels:false]);
//CONFIG.MODELS.add([model:"auth.RsUser"]);

def fullExportUtility=new FullExportImportUtility(logger);
try{
    fullExportUtility.fullExport(CONFIG);
}
catch(e)
{
    logger.warn("Error during fullExport, Reason : ${e}",e);
    throw e;
}

return "success"
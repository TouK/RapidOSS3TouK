CONFIG=[:];
CONFIG.backupDir="exportTempData";
CONFIG.exportDir="exportFiles";
CONFIG.objectsPerFile=10000;

CONFIG.MODELS=[];
CONFIG.MODELS.add([model:"all"]);

//conf models export
//CONFIG.MODELS.add([model:"conf"]);

//exporting selected models
//CONFIG.MODELS.add([model:"RsTopologyObject"]);
//CONFIG.MODELS.add([model:"RsEvent"]);


try{
    application.RapidApplication.fullExport(CONFIG);
}
catch(e)
{
    logger.warn("Error during fullExport, Reason : ${e}",e);
    throw e;
}

return "success"
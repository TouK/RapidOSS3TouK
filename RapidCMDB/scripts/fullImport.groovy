CONFIG=[:];
CONFIG.importDir="importData";
CONFIG.exportDir="exportFiles";

try{
    application.RsApplication.fullImport(CONFIG);
}
catch(e)
{
    logger.warn("Error during fullImport, Reason : ${e}",e);
    throw e;
}

return "success"
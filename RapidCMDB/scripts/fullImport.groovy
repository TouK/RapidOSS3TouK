import com.ifountain.rcmdb.domain.FullExportImportUtility

CONFIG=[:];
CONFIG.importDir="importData";
CONFIG.exportDir="export";

def fullExportUtility=new FullExportImportUtility(logger);
try{
    fullExportUtility.fullImport(CONFIG);
}
catch(e)
{
    logger.warn("Error during fullExport, Reason : ${e}",e);
    throw e;
}

return "success"
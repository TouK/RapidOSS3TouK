import com.ifountain.rcmdb.domain.FullExportImportUtility

CONFIG.backupDir="backup";
CONFIG.exportDir="export";
CONFIG.objectPerFile=1000;

CONFIG.MODELS=[];
CONFIG.MODELS.add([model:"all"]);
//CONFIG.MODELS.add([model:"conf"]);
//CONFIG.MODELS.add([model:"RsTopologyObject"]);
//CONFIG.MODELS.add([model:"RsGroup",childModels:false]);
//CONFIG.MODELS.add([model:"auth.RsUser"]);

def fullExportUtility=new FullExportImportUtility(logger);
fullExportUtility.fullExpor(CONFIG);

return "success"

def getExportConfiguration()
{
    def CONFIG=[:];
    CONFIG.backupDir="backup";
    CONFIG.exportDir="export";
    CONFIG.objectPerFile=1000;

    CONFIG.MODELS=[];
    //CONFIG.MODELS.add([model:"all"]);
    //CONFIG.MODELS.add([model:"conf"]);
    CONFIG.MODELS.add([model:"RsTopologyObject"]);
    //CONFIG.MODELS.add([model:"RsGroup",childModels:false]);
    CONFIG.MODELS.add([model:"auth.RsUser"]);

    return CONFIG;
}

def getImportConfiguration()
{
   def CONFIG=[:];

   return CONFIG;
}
package remoteModification

import datasource.HttpDatasource
import groovy.util.slurpersupport.GPathResult;
class RemoteApplicationModificationOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    def static getActiveModification(String filePath)
    {
        return RemoteApplicationModification.searchEvery("filePath:${filePath.exactQuery()} AND isActive:true")[0]
    }

    def static getActiveModifications(Map searchOptions)
    {
        return RemoteApplicationModification.searchEvery("isActive:true", searchOptions)
    }

    def commit(HttpDatasource ds, String comment)
    {
        if(!isActive)
        {
            throw new Exception("You can not commit an inactive modification");
        }
        def file = new File("${targetUploadDir}/${filePath}");
        def targetFile = new File("${rsDirectory}/${filePath}");

        ds.uploadFile("/uploader/upload", "file", completeFilePath, file.name, [dir:file.parentFile.path]);
        def res = ds.doRequest("script/run/fileOperation", [from:file.getPath(), operation:operation, to:targetFile.path, login:"rsadmin", password:"changeme"]);
        GPathResult xmlRespose = new XmlSlurper().parseText(res)
        def errors = xmlRespose.Error
        if(errors.size() != 0)
        {
            throw new Exception(errors[0].error.text());            
        }
        update(commited:true, commitedAt:new Date(), comment:comment, isActive:false);
    }

    def ignore(String comment)
    {
        update(ignored:true, isActive:false)
    }
}
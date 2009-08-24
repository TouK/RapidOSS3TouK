package remoteModification

import datasource.HttpDatasource
import groovy.util.slurpersupport.GPathResult;
class RemoteApplicationModificationOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    def static getActiveModification(String relativeFilePath)
    {
        return RemoteApplicationModification.searchEvery("relativeFilePath:${relativeFilePath.exactQuery()} AND isActive:true")[0]
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
        def file = new File(relativeFilePath);
        def targetUploadFile = new File(targetUploadFilePath);
        def targetRsFile = new File(targetRsFilePath);
        if(operation == RemoteApplicationModification.COPY)
        {
            ds.uploadFile("/uploader/upload", "file", completeFilePath, file.name, [dir:targetUploadFile.parentFile.path]);
        }
        def res = ds.doRequest("script/run/fileOperation", [from:targetUploadFile.path, operation:operation, to:targetRsFile.path, login:"rsadmin", password:"changeme"]);
        GPathResult xmlRespose = new XmlSlurper().parseText(res)
        def errors = xmlRespose.Error
        if(errors.size() != 0)
        {
            throw new Exception(errors[0].error.text());            
        }
        update(commited:true, commitedAt:new Date(), comment:comment, isActive:false);
    }

    def static ignoreAllChanges(String comment)
    {
        RemoteApplicationModification.list().each{
            it.ignore();
        }
    }

    def ignore(String comment)
    {
        update(ignored:true, isActive:false)
    }
}
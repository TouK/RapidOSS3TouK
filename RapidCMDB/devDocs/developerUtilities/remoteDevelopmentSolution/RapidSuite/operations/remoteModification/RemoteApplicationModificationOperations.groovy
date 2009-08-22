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
        ds.uploadFile("uploader/upload", "file", filePath, "uploadedCustomerFiles/${filePath}");
        def res = ds.doRequest("script/run/fileOperation", [file:filePath, operation:operation]);
        GPathResult xmlRespose = new XmlSlurper().parseText(res)
        def errors = xmlRespose.Error
        if(errors.size() != 0)
        {
            throw new Exception(errors[0].error.text());            
        }
        update(commited:true, commitedAt:new Date(), comment:comment, isActive:false);
    }

    def ignoreLocalChanges(String comment)
    {
        RemoteApplicationModification lastModification = RemoteApplicationModification.search("filePath:${filePath.exactQuery()}", [sort:"id", order:"desc"]);
        if(lastModification != null)
        {
            new File(completeFilePath).setText(lastModification.content);
        }
        update(ignored:true, isActive:false)
    }
}
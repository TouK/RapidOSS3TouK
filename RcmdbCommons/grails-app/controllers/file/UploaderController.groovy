package file

import org.codehaus.groovy.runtime.StackTraceUtils


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 21, 2009
* Time: 2:55:04 PM
* To change this template use File | Settings | File Templates.
*/
class UploaderController {
    def index = {redirect(action: show, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [upload: 'POST']
    def upload = {
        def downloadedfile = request.getFile('file');
        if(downloadedfile != null)
        {
            def fileName = downloadedfile.getOriginalFilename()
            try{
                def uploadDir = getUploadDir();
                uploadDir.mkdirs()
                downloadedfile.transferTo(new File(uploadDir, fileName))
                flash.message = "Successfully uploaded to uploadedFiles/${fileName}"
            }catch(Throwable t)
            {
                log.warn("Exception occurred while uploading ${fileName}", StackTraceUtils.deepSanitize(t));
                addError("file.upload.exception", [fileName, t.toString()]);
            }
        }
        else
        {
            addError("file.upload.file.not.specified", []);
        }
        flash.errors = this.errors;
        redirect(action: 'show', controller:"uploader")
    }

    public getUploadDir()
    {
        def baseDir = System.getProperty("base.dir")
        return new File("${baseDir}/uploadedFiles")
    }

    def show = {
        render(view: 'show', model: [:])
    }
}
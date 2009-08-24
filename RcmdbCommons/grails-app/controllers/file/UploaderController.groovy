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
            def directory = params.dir;
            def fileName = downloadedfile.getOriginalFilename()
            if(directory != null)
            {
                fileName = "${directory}/${fileName}"
            }
            try{
                def uploadDir = getUploadDir();
                def targetFile = new File(uploadDir, fileName);
                if(!targetFile.getParentFile().getCanonicalPath().startsWith(uploadDir.getCanonicalPath()))
                {
                    addError("file.upload.invalid.location", [fileName]);
                }
                else
                {
                    targetFile.parentFile.mkdirs();
                    downloadedfile.transferTo(new File(uploadDir, fileName))
                    flash.message = "Successfully uploaded to uploadedFiles/${fileName}"
                }
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
package file

import org.springframework.web.multipart.commons.CommonsMultipartFile

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
        def baseDir = System.getProperty("base.dir")
        def downloadedfile = request.getFile('file');
        def uploadDir = new File("${baseDir}/uploadedFiles")
        uploadDir.mkdirs()
        def fileName = downloadedfile.getOriginalFilename()
        downloadedfile.transferTo(new File(uploadDir, fileName))
        flash.message = "Successfully uploaded to uploadedFiles/${fileName}"
        redirect(action: 'show')
    }

    def show = {
        render(view: 'show', model: [:])
    }
}
package uploader

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import file.UploaderController
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.mock.web.MockServletContext
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.web.context.request.RequestContextHolder
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 21, 2009
* Time: 5:08:38 PM
* To change this template use File | Settings | File Templates.
*/
class UploaderControllerIntegrationTest extends RapidCmdbIntegrationTestCase {
    public void testUpload()
    {
        def prevAttributes = RequestContextHolder.getRequestAttributes()
        try{
            def originalFileName = "uploadedFile.txt"
            bindMockMultipartWebRequest();
            def uploaderController = new UploaderController();
            def uploadDir = uploaderController.getUploadDir();
            def expectedFileToBeCreated = new File(uploadDir, originalFileName)
            expectedFileToBeCreated.delete();

            MockMultipartHttpServletRequest request = uploaderController.request
            def fileContent = "this is the file content"
            def byteData = fileContent.getBytes();
            def file = new MockMultipartFile("file", originalFileName, null, byteData)
            request.addFile(file)
            uploaderController.upload();

            assertEquals (fileContent, expectedFileToBeCreated.getText());
            assertEquals ("/uploader/show", uploaderController.response.redirectedUrl);

            IntegrationTestUtils.resetController (uploaderController);
            request = uploaderController.request
            def updatedFileContent = "updated file content"
            byteData = updatedFileContent.getBytes();
            file = new MockMultipartFile("file", originalFileName, null, byteData)
            request.addFile(file)
            uploaderController.upload();

            assertEquals (updatedFileContent, expectedFileToBeCreated.getText());
            assertEquals ("/uploader/show", uploaderController.response.redirectedUrl);
        }finally{
            RequestContextHolder.setRequestAttributes (prevAttributes);
        }
    }

    public void testUploadWithDirParameter()
    {
        def prevAttributes = RequestContextHolder.getRequestAttributes()
        try{
            def originalFileName = "uploadedFile.txt"
            def dir = "directory"
            bindMockMultipartWebRequest();
            def uploaderController = new UploaderController();
            def uploadDir = uploaderController.getUploadDir();
            def expectedFileToBeCreated = new File(uploadDir, "${dir}/${originalFileName}")
            expectedFileToBeCreated.delete();

            MockMultipartHttpServletRequest request = uploaderController.request
            def fileContent = "this is the file content"
            def byteData = fileContent.getBytes();
            def file = new MockMultipartFile("file", originalFileName, null, byteData)
            uploaderController.params.dir = dir;
            request.addFile(file)
            uploaderController.upload();

            assertEquals (fileContent, expectedFileToBeCreated.getText());
            assertEquals ("/uploader/show", uploaderController.response.redirectedUrl);
        }finally{
            RequestContextHolder.setRequestAttributes (prevAttributes);
        }
    }

    public void testUploadThrowsExceptionIfOriginalFileIsOutOfuploadDir()
    {
        def prevAttributes = RequestContextHolder.getRequestAttributes()
        try{
            def originalFileName = "../uploadedFile.txt"
            bindMockMultipartWebRequest();
            def uploaderController = new UploaderController();
            def uploadDir = uploaderController.getUploadDir();
            def expectedFileToBeCreated = new File(uploadDir, originalFileName)
            expectedFileToBeCreated.delete();

            MockMultipartHttpServletRequest request = uploaderController.request
            def fileContent = "this is the file content"
            def byteData = fileContent.getBytes();
            def file = new MockMultipartFile("file", originalFileName, null, byteData)
            request.addFile(file)
            uploaderController.upload();

            assertEquals ("/uploader/show", uploaderController.response.redirectedUrl);
            assertTrue(uploaderController.flash.errors.hasErrors());
            assertFalse(expectedFileToBeCreated.exists());

        }finally{
            RequestContextHolder.setRequestAttributes (prevAttributes);
        }
    }

    public void testUploadWithDirThrowsExceptionIfOriginalFileIsOutOfuploadDir()
    {
        def prevAttributes = RequestContextHolder.getRequestAttributes()
        try{
            def originalFileName = "uploadedFile.txt"
            def dir  = "../directory"
            bindMockMultipartWebRequest();
            def uploaderController = new UploaderController();
            def uploadDir = uploaderController.getUploadDir();
            def expectedFileToBeCreated = new File(uploadDir, "${dir}/${originalFileName}")
            expectedFileToBeCreated.delete();

            MockMultipartHttpServletRequest request = uploaderController.request
            def fileContent = "this is the file content"
            def byteData = fileContent.getBytes();
            def file = new MockMultipartFile("file", originalFileName, null, byteData)
            request.addFile(file)
            uploaderController.params.dir = dir;
            uploaderController.upload();

            assertEquals ("/uploader/show", uploaderController.response.redirectedUrl);
            assertTrue(uploaderController.flash.errors.hasErrors());
            assertFalse(expectedFileToBeCreated.exists());

        }finally{
            RequestContextHolder.setRequestAttributes (prevAttributes);
        }
    }

    public void testUploadReturnsErrorIfFileCouldNotBeCreated()
    {
        def prevAttributes = RequestContextHolder.getRequestAttributes()
        def originalFileName = "uploadedFile.txt"
        bindMockMultipartWebRequest();
        def uploaderController = new UploaderController();
        def uploadDir = uploaderController.getUploadDir();
        def expectedFileToBeCreated = new File(uploadDir, originalFileName)
        try{

            FileUtils.deleteDirectory(expectedFileToBeCreated.parentFile);
            expectedFileToBeCreated.parentFile.setText ("uploadedFile dir exist as file");
            
            MockMultipartHttpServletRequest request = uploaderController.request
            def fileContent = "this is the file content"
            def byteData = fileContent.getBytes();
            def file = new MockMultipartFile("file", originalFileName, null, byteData)
            request.addFile(file)
            uploaderController.upload();

            assertEquals ("/uploader/show", uploaderController.response.redirectedUrl);
            assertTrue(uploaderController.flash.errors.hasErrors());
            
        }finally{
            expectedFileToBeCreated.parentFile.delete();
            RequestContextHolder.setRequestAttributes (prevAttributes);
        }
    }

    public void testUploadReturnsErrorIfFileParameterDoesNotExist()
    {
        def prevAttributes = RequestContextHolder.getRequestAttributes()
        bindMockMultipartWebRequest();
        def uploaderController = new UploaderController();
        def uploadDir = uploaderController.getUploadDir();
        try{

            MockMultipartHttpServletRequest request = uploaderController.request
            uploaderController.upload();

            assertEquals ("/uploader/show", uploaderController.response.redirectedUrl);
            assertTrue(uploaderController.flash.errors.hasErrors());

        }finally{
            RequestContextHolder.setRequestAttributes (prevAttributes);
        }
    }

    GrailsWebRequest bindMockMultipartWebRequest() {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        GrailsWebRequest webRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), new MockServletContext());
        request.setAttribute(GrailsApplicationAttributes.WEB_REQUEST, webRequest);
        RequestContextHolder.setRequestAttributes(webRequest);
        return webRequest;
    }
}
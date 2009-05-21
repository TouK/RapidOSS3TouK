package datasource

import com.ifountain.core.test.util.RapidCoreTestCase
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine

/**
* Created by IntelliJ IDEA.
* User: iFountain
* Date: Dec 26, 2008
* Time: 10:27:52 AM
* To change this template use File | Settings | File Templates.
*/
class EmailDatasourceOperationsIntegrationTests extends RapidCoreTestCase{
    static transactional = false;
    public void testRenderTemplate(){

        GroovyPagesTemplateEngine.pageCache.clear();
        File folder=new File(System.getProperty("base.dir")+"/testOutput");
        folder.mkdirs()

        File file=new File(folder.getPath()+"/emailtest.gsp");
        println file.getPath();
        println file.getAbsolutePath();

        def gspContent="""
        <g:each in="\${aList}">\${it},</g:each>
        """;
        file.setText(gspContent);


        def templatePath=file.getPath();
        def parameters=["aList":[1,2,3]];

        String result=EmailDatasource.renderTemplate(templatePath,parameters);
        assertEquals("1,2,3,",result.trim())
    }
}

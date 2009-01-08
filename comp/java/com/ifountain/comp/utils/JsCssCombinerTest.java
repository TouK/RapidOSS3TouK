package com.ifountain.comp.utils;

import junit.framework.TestCase;
import com.ifountain.comp.test.util.file.TestFile;
import com.ifountain.comp.test.util.file.FileTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;


/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 1, 2008
 * Time: 2:53:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsCssCombinerTest extends TestCase {

    JsCssCombiner combiner = new JsCssCombiner();
    String[] args;
    String filePath = "index.html";
    String appPath = "RapidInsight/RapidInsight/clients/js";
    String targetPath = TestFile.TESTOUTPUT_DIR;
    String rootPath = "RapidApplicationServer/RapidApplicationServer/webapps/root";

    protected void setUp() throws Exception {
        super.setUp();
    }

    public static void main(String[] args) {

    }

    public void testGetFileContent_asString() throws Exception {
        String fileName = "test.html";
        FileTestUtils.deleteFile(fileName);
        File file = new TestFile(fileName);
        StringBuffer wholeFileAsString = new StringBuffer();
        try{
            combiner.getFileContentAsString(file, wholeFileAsString);
            fail("should throw exception");
        }
        catch(Exception e){
        }
        String expectedString = "A <head>ABC";
        ArrayList lines = new ArrayList();
        lines.add(expectedString);
        FileTestUtils.generateFile(fileName, lines);
        combiner.getFileContentAsString(file, wholeFileAsString);
        assertEquals(expectedString + "\n", wholeFileAsString.toString());
    }


    public void testGetJSPathsAsList() throws Exception {
        args = new String[]{"-" + JsCssCombiner.FILE_PATH_OPTION, filePath,
                "-" + JsCssCombiner.TARGET_PATH_OPTION, targetPath,
                "-" + JsCssCombiner.ROOT_PATH_OPTION, rootPath,
                "-" + JsCssCombiner.APP_PATH_OPTION, appPath};
        combiner.parseArgs(args);
        StringBuffer wholeHTML = new StringBuffer();
        wholeHTML.append("abc\n");
        wholeHTML.append("<script language=\"javascript\" src=\"/jslib/rapidjs/data/xml1json.js\" >AAAAAAAAA</ script>\n");
        wholeHTML.append("<script src=\"/jslib/rapidjs/data/xml2json.js\">AAAAAAAAA</wrongTag>\n");
        wholeHTML.append("<script src=\"rijslib/component/layout/FilterTool.js\">BBBBBBBBBBB</script>\n");
        wholeHTML.append("<script language=\"javascript\" src=\"/jslib/rapidjs/data/xml5json.js\" />\n");
        wholeHTML.append("xyz\n");
        List JSPaths = new ArrayList();
        combiner.setPathsAndRemoveFromWholeHTML(wholeHTML.toString(), JsCssCombiner.JSPATTERN, JSPaths);

        assertEquals(rootPath + "/jslib/rapidjs/data/xml1json.js", JSPaths.get(0).toString());
        assertEquals(appPath + "/rijslib/component/layout/FilterTool.js", JSPaths.get(1).toString());
        assertEquals(rootPath + "/jslib/rapidjs/data/xml5json.js", JSPaths.get(2).toString());
        assertEquals(3, JSPaths.size());

    }

    public void testGetCssPaths() throws Exception {
        args = new String[]{"-" + JsCssCombiner.FILE_PATH_OPTION, filePath,
                "-" + JsCssCombiner.TARGET_PATH_OPTION, targetPath,
                "-" + JsCssCombiner.ROOT_PATH_OPTION, rootPath,
                "-" + JsCssCombiner.APP_PATH_OPTION, appPath};
        combiner.parseArgs(args);
        StringBuffer wholeHTML = new StringBuffer();
        wholeHTML.append("abc\n");
        wholeHTML.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"ricomp/css/allCSS1.css\"/>");
        wholeHTML.append("<lin k rel=\"stylesheet\" type=\"text/css\" href=\"ricomp/css/allCSS2.css\"/>");
        wholeHTML.append("<link rel=\"stylesheet\" href=\"ricomp/css/allCSS3.css\" type=\"text/css\" />");
        wholeHTML.append("<link rel=\"stylesheet\" ref=\"ricomp/css/allCSS4.css\" type=\"text/css\" />");
        wholeHTML.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"ricomp/css/allCSS5.css\" ></ link>");
        wholeHTML.append("xyz\n");
        List CSSPaths = new ArrayList();
        combiner.setPathsAndRemoveFromWholeHTML(wholeHTML.toString(), JsCssCombiner.CSSPATTERN, CSSPaths);
        assertEquals(appPath + "/ricomp/css/allCSS1.css", CSSPaths.get(0).toString());
        assertEquals(appPath + "/ricomp/css/allCSS3.css", CSSPaths.get(1).toString());
        assertEquals(appPath + "/ricomp/css/allCSS5.css", CSSPaths.get(2).toString());
        assertEquals(3, CSSPaths.size());
    }

    public void testCreateFile() throws Exception {
        String content = "A <head>ABC\n";
        File file = new TestFile("test.html");
        FileTestUtils.deleteFile(file);
        combiner.createFile(content, file.getPath());
        StringBuffer actualFileContent = new StringBuffer();
        combiner.getFileContentAsString(new TestFile(file), actualFileContent);
        assertEquals(content, actualFileContent.toString());
    }

    public void testwriteModifiedHTML() throws Exception {
        args = new String[]{"-" + JsCssCombiner.FILE_PATH_OPTION, filePath,
                "-" + JsCssCombiner.TARGET_PATH_OPTION, targetPath,
                "-" + JsCssCombiner.ROOT_PATH_OPTION, rootPath,
                "-" + JsCssCombiner.APP_PATH_OPTION, appPath};
        combiner.parseArgs(args);
        String htmlWithNoScripts = "A <head>ABC\n";
        File file = new TestFile("test.html");

        FileTestUtils.deleteFile(file);
        combiner.createFile(htmlWithNoScripts, file.getPath());
        combiner.writeModifiedHTML(htmlWithNoScripts, file);
        assertTrue(file.exists());

        String expectedFileContent = "A <head>\n\t<script src=\"test.js\"></script>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"test.css\"/>\nABC\n";
        StringBuffer actualContent = new StringBuffer();
        combiner.getFileContentAsString(file, actualContent);
        assertEquals(expectedFileContent, actualContent.toString());

    }

    public void testwriteMergedCSS() throws Exception {
        args = new String[]{"-" + JsCssCombiner.FILE_PATH_OPTION, filePath,
                "-" + JsCssCombiner.TARGET_PATH_OPTION, targetPath,
                "-" + JsCssCombiner.ROOT_PATH_OPTION, rootPath,
                "-" + JsCssCombiner.APP_PATH_OPTION, appPath};
        combiner.parseArgs(args);
        File file = new TestFile("test.html");
        FileTestUtils.deleteFile(file);
        List cssPaths = new ArrayList();
        cssPaths.add(rootPath + "/jslib/css/treegrid.css");
        String htmlWithNoScripts = "A <head>ABC\n";
        combiner.createFile(htmlWithNoScripts, file.getPath());
        combiner.writeModifiedHTML(htmlWithNoScripts, file);
        assertTrue(file.exists());
        String expectedFileContent = "A <head>\n\t<script src=\"test.js\"></script>\n	<link rel=\"stylesheet\" type=\"text/css\" href=\"test.css\"/>\nABC\n";
        StringBuffer actualContent = new StringBuffer();
        combiner.getFileContentAsString(file, actualContent);
        assertEquals(expectedFileContent, actualContent.toString());

    }
    public void testwriteMergedJS() throws Exception {
        args = new String[]{"-" + JsCssCombiner.FILE_PATH_OPTION, filePath,
                "-" + JsCssCombiner.TARGET_PATH_OPTION, targetPath,
                "-" + JsCssCombiner.ROOT_PATH_OPTION, rootPath,
                "-" + JsCssCombiner.APP_PATH_OPTION, appPath};
        combiner.parseArgs(args);
        File file = new TestFile("test.html");
        FileTestUtils.deleteFile(file);
        List jsPaths = new ArrayList();
        jsPaths.add(rootPath + "/jslib/yui/build/utilities/utilities.js");
        String htmlWithNoScripts = "A <head>ABC\n";
        combiner.createFile(htmlWithNoScripts, file.getPath());
        combiner.writeModifiedHTML(htmlWithNoScripts, file);
        assertTrue(file.exists());
        String expectedFileContent = "A <head>\n\t<script src=\"test.js\"></script>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"test.css\"/>\nABC\n";
        StringBuffer actualContent = new StringBuffer();
        combiner.getFileContentAsString(file, actualContent);
        assertEquals(expectedFileContent, actualContent.toString());
    }

    public void testSuffixWillBeAppendedToFilesIfProvided() throws Exception {
        File file = new TestFile("webapp/test.html");
        FileTestUtils.deleteFile(file);
        String html = "<html>\n\t" +
                         "<head>\n\t" +
                         "<script src=\"jslib/rapidjs/TreeGrid.js\"/>\n\t" +
                         "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/rapidjs/treegrid.css\"/>\n\t" +
                         "</head>\n\t" +
                         "<body>\n\t" +
                         "<div>sezgin</div>\n\t" +
                         "</body>\n\t</html>";
        FileTestUtils.generateFile("webapp/jslib/rapidjs/TreeGrid.js", "");
        FileTestUtils.generateFile("webapp/css/rapidjs/treegrid.css", "");
        FileTestUtils.generateFile(file.getPath(), html);

        args = new String[]{"-" + JsCssCombiner.FILE_PATH_OPTION, file.getPath(),
                "-" + JsCssCombiner.TARGET_PATH_OPTION, targetPath,
                "-" + JsCssCombiner.SUFFIX_OPTION, "_v0-3-1",
                "-" + JsCssCombiner.APP_PATH_OPTION, new TestFile("webapp").getPath()};

        combiner.run(args);

        File modifiedFile = new TestFile("test.html");
        assertTrue(modifiedFile.exists());
        String expectedHtml = "<html>\n\t" +
                         "<head>\n\t" +
                         "<script src=\"test_v0-3-1.js\"></script>\n\t" +
                         "<link rel=\"stylesheet\" type=\"text/css\" href=\"test_v0-3-1.css\"/>\n" +
                         "</head>\n\t" +
                         "<body>\n\t" +
                         "<div>sezgin</div>\n\t" +
                         "</body>\n\t</html>\n";

        assertEquals(expectedHtml, FileUtils.readFileToString(modifiedFile));
        assertTrue(new TestFile("test_v0-3-1.js").exists());
        assertTrue(new TestFile("test_v0-3-1.css").exists());
    }
     public void testWebBasePrefixWillBeAppendedToFilesIfProvided() throws Exception {
        File file = new TestFile("webapp/test.html");
        FileTestUtils.deleteFile(file);
        String html = "<html>\n\t" +
                         "<head>\n\t" +
                         "<script src=\"jslib/rapidjs/TreeGrid.js\"/>\n\t" +
                         "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/rapidjs/treegrid.css\"/>\n\t" +
                         "</head>\n\t" +
                         "<body>\n\t" +
                         "<div>sezgin</div>\n\t" +
                         "</body>\n\t</html>";
        FileTestUtils.generateFile("webapp/jslib/rapidjs/TreeGrid.js", "");
        FileTestUtils.generateFile("webapp/css/rapidjs/treegrid.css", "");
        FileTestUtils.generateFile(file.getPath(), html);

        args = new String[]{"-" + JsCssCombiner.FILE_PATH_OPTION, file.getPath(),
                "-" + JsCssCombiner.TARGET_PATH_OPTION, targetPath,
                "-" + JsCssCombiner.WEB_BASE_PREFIX_OPTION, "/Rapid-Suite/",
                "-" + JsCssCombiner.APP_PATH_OPTION, new TestFile("webapp").getPath()};

        combiner.run(args);

        File modifiedFile = new TestFile("test.html");
        assertTrue(modifiedFile.exists());
        String expectedHtml = "<html>\n\t" +
                         "<head>\n\t" +
                         "<script src=\"/Rapid-Suite/test.js\"></script>\n\t" +
                         "<link rel=\"stylesheet\" type=\"text/css\" href=\"/Rapid-Suite/test.css\"/>\n" +
                         "</head>\n\t" +
                         "<body>\n\t" +
                         "<div>sezgin</div>\n\t" +
                         "</body>\n\t</html>\n";

        assertEquals(expectedHtml, FileUtils.readFileToString(modifiedFile));
        assertTrue(new TestFile("test_v0-3-1.js").exists());
        assertTrue(new TestFile("test_v0-3-1.css").exists());
    }

    public void testModifiedImageUrlsInCssFiles() throws Exception {
        File file = new TestFile("webapp/test.html");
        FileTestUtils.deleteFile(file);
        String html = "<html>\n\t" +
                         "<head>\n\t" +
                         "<script src=\"jslib/rapidjs/TreeGrid.js\"/>\n\t" +
                         "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/rapidjs/treegrid.css\"/>\n\t" +
                         "</head>\n\t" +
                         "<body>\n\t" +
                         "<div>sezgin</div>\n\t" +
                         "</body>\n\t</html>";

        String cssContent = ".r-tree{background: transparent url( '../../images/sprite.png' ) repeat-x scroll 0pt -1300px;}.r-tree-header{background: transparent url( ../../images/rapidjs/filter.png ) repeat-x scroll 0pt -1300px;}\n" +
                "r-tree-body{background: transparent url( \"../../images/rapidjs/components/folder.png\" ) repeat-x scroll 0pt -1300px;}";
        FileTestUtils.generateFile("webapp/jslib/rapidjs/TreeGrid.js", "");
        FileTestUtils.generateFile("webapp/css/rapidjs/treegrid.css", cssContent);
        FileTestUtils.generateFile("webapp/images/sprite.png", "");
        FileTestUtils.generateFile("webapp/images/rapidjs/filter.png", "");
        FileTestUtils.generateFile("webapp/images/rapidjs/components/folder.png", "");
        FileTestUtils.generateFile(file.getPath(), html);


        args = new String[]{"-" + JsCssCombiner.FILE_PATH_OPTION, file.getPath(),
                "-" + JsCssCombiner.TARGET_PATH_OPTION, targetPath,
                "-" + JsCssCombiner.MEDIA_PATH_OPTION, "newImages",
                "-" + JsCssCombiner.SUFFIX_OPTION, "_v0-3-1",
                "-" + JsCssCombiner.APP_PATH_OPTION, new TestFile("webapp").getPath()};

       combiner.run(args);
       File cssFile = new TestFile("test_v0-3-1.css");
       assertTrue(cssFile.exists());

       String newCssContent =  ".r-tree{background: transparent url( 'newImages/sprite_v0-3-1.png' ) repeat-x scroll 0pt -1300px;}.r-tree-header{background: transparent url( newImages/filter_v0-3-1.png) repeat-x scroll 0pt -1300px;}\n" +
                "r-tree-body{background: transparent url( \"newImages/folder_v0-3-1.png\" ) repeat-x scroll 0pt -1300px;}\n";

       assertEquals(newCssContent, FileUtils.readFileToString(cssFile));
       assertTrue(new TestFile("newImages/sprite_v0-3-1.png").exists());
       assertTrue(new TestFile("newImages/filter_v0-3-1.png").exists());
       assertTrue(new TestFile("newImages/folder_v0-3-1.png").exists());

    }

    
}

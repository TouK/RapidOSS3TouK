package com.ifountain.rcmdb.domain.generation

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Apr 27, 2008
* Time: 12:32:04 AM
* To change this template use File | Settings | File Templates.
*/
class ClassParserTest extends RapidCmdbTestCase{
    def baseDir = "../../testOutput"

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        def baseDirFile = new File(baseDir);
        FileUtils.deleteDirectory (baseDirFile);
        baseDirFile.mkdirs();

    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        FileUtils.deleteDirectory (new File(baseDir));
    }

    public void testClassParser()
    {
        def classFile = new File("${baseDir}/MyClass.groovy");

        classFile.setText("""beforepackage1
beforepackage2

        package package1;
betweenpackage_import1
betweenpackage_import2

    import com.ifountain.Class1;
import com.ifountain.Class2 as changedClass2;
import com.ifountain.Class3;
 class MyClass    {
    beforePart1
    beforePart2
    beforePart3
    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}
    autogenerated1
    autogenerated2
    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}
    afterPart1
    afterPart2
    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}
    autogenerated4
    autogenerated5
    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}
    afterPart4
    afterPart5
}""");
        ClassContent returnedClassContent = ClassParser.parseClass (classFile);
        ClassContent expectedClassContent = new ClassContent();
        expectedClassContent.addLine ("beforepackage1", ClassContentLine.LINE);
        expectedClassContent.addLine ("beforepackage2", ClassContentLine.LINE);
        expectedClassContent.addLine ("", ClassContentLine.LINE);
        expectedClassContent.addLine ("        package package1;", ClassContentLine.PACKAGE_DECLERATION_LINE);
        expectedClassContent.addLine ("betweenpackage_import1", ClassContentLine.LINE);
        expectedClassContent.addLine ("betweenpackage_import2", ClassContentLine.LINE);
        expectedClassContent.addLine ("", ClassContentLine.LINE);
        expectedClassContent.addLine ("    import com.ifountain.Class1;", ClassContentLine.IMPORT_LINE);
        expectedClassContent.addLine ("import com.ifountain.Class2 as changedClass2;", ClassContentLine.IMPORT_LINE);
        expectedClassContent.addLine ("import com.ifountain.Class3;", ClassContentLine.IMPORT_LINE);
        expectedClassContent.addLine (" class MyClass    {", ClassContentLine.CLASS_DECLERATION_LINE);
        expectedClassContent.addLine ("    beforePart1", ClassContentLine.LINE);
        expectedClassContent.addLine ("    beforePart2", ClassContentLine.LINE);
        expectedClassContent.addLine ("    beforePart3", ClassContentLine.LINE);
        expectedClassContent.addLine ("    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    autogenerated1", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    autogenerated2", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    afterPart1", ClassContentLine.LINE);
        expectedClassContent.addLine ("    afterPart2", ClassContentLine.LINE);
        expectedClassContent.addLine ("    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    autogenerated4", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    autogenerated5", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    afterPart4", ClassContentLine.LINE);
        expectedClassContent.addLine ("    afterPart5", ClassContentLine.LINE);
        expectedClassContent.addLine ("}", ClassContentLine.LINE);
        assertEquals (expectedClassContent, returnedClassContent);
        assertNull (returnedClassContent.getParentClass());
        def implementedClasses = returnedClassContent.getImplementedClasses();
        assertEquals (0, implementedClasses.size());
    }

    public void testClassParserWithMultipleClassDeclerationLines()
    {
        def classFile = new File("${baseDir}/MyClass.groovy");
        classFile.setText("""beforepackage1
beforepackage2

        package package1;
betweenpackage_import1
betweenpackage_import2

    import com.ifountain.Class1;
import com.ifountain.Class2 as changedClass2;
import com.ifountain.Class3;
class MyClass    extends    ParentClass implements Map,
List,    Array
{
    beforePart1
    beforePart2
    beforePart3
    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}
    autogenerated1
    autogenerated2
    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}
    afterPart1
    afterPart2
    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}
    autogenerated4
    autogenerated5
    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}
    afterPart4
    afterPart5
}""");
        ClassContent returnedClassContent = ClassParser.parseClass (classFile);
        ClassContent expectedClassContent = new ClassContent();
        expectedClassContent.addLine ("beforepackage1", ClassContentLine.LINE);
        expectedClassContent.addLine ("beforepackage2", ClassContentLine.LINE);
        expectedClassContent.addLine ("", ClassContentLine.LINE);
        expectedClassContent.addLine ("        package package1;", ClassContentLine.PACKAGE_DECLERATION_LINE);
        expectedClassContent.addLine ("betweenpackage_import1", ClassContentLine.LINE);
        expectedClassContent.addLine ("betweenpackage_import2", ClassContentLine.LINE);
        expectedClassContent.addLine ("", ClassContentLine.LINE);
        expectedClassContent.addLine ("    import com.ifountain.Class1;", ClassContentLine.IMPORT_LINE);
        expectedClassContent.addLine ("import com.ifountain.Class2 as changedClass2;", ClassContentLine.IMPORT_LINE);
        expectedClassContent.addLine ("import com.ifountain.Class3;", ClassContentLine.IMPORT_LINE);
        expectedClassContent.addLine ("class MyClass    extends    ParentClass implements Map,", ClassContentLine.CLASS_DECLERATION_LINE);
        expectedClassContent.addLine ("List,    Array", ClassContentLine.CLASS_DECLERATION_LINE);
        expectedClassContent.addLine ("{", ClassContentLine.CLASS_DECLERATION_LINE);
        expectedClassContent.addLine ("    beforePart1", ClassContentLine.LINE);
        expectedClassContent.addLine ("    beforePart2", ClassContentLine.LINE);
        expectedClassContent.addLine ("    beforePart3", ClassContentLine.LINE);
        expectedClassContent.addLine ("    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    autogenerated1", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    autogenerated2", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    afterPart1", ClassContentLine.LINE);
        expectedClassContent.addLine ("    afterPart2", ClassContentLine.LINE);
        expectedClassContent.addLine ("    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    autogenerated4", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    autogenerated5", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    ${ClassParser.AUTO_GENERATED_CODE_COMMENT}", ClassContentLine.AUTO_GENERATED_LINE);
        expectedClassContent.addLine ("    afterPart4", ClassContentLine.LINE);
        expectedClassContent.addLine ("    afterPart5", ClassContentLine.LINE);
        expectedClassContent.addLine ("}", ClassContentLine.LINE);
        assertEquals (expectedClassContent, returnedClassContent);
        def importLines = returnedClassContent.getLines(ClassContentLine.IMPORT_LINE);
        assertEquals(3, importLines.size());
        assertEquals("    import com.ifountain.Class1;", importLines[0].line);
        assertEquals("import com.ifountain.Class2 as changedClass2;", importLines[1].line);
        assertEquals("import com.ifountain.Class3;", importLines[2].line);
        assertEquals ("ParentClass", returnedClassContent.getParentClass());
        def implementedClasses = returnedClassContent.getImplementedClasses();
        assertEquals (3, implementedClasses.size());
        assertEquals ("Map", implementedClasses[0]);
        assertEquals ("List", implementedClasses[1]);
        assertEquals ("Array", implementedClasses[2]);
    }
}
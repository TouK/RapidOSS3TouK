package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 29, 2009
* Time: 11:33:09 AM
* To change this template use File | Settings | File Templates.
*/
class GetRootClassMethodTest extends RapidCmdbTestCase{
    public void testGetRootDomainClassMethod()
    {
        GroovyClassLoader gcl = new GroovyClassLoader();
        def model1Name = "ParentModel";
        def model2Name = "Level1ChildModel";
        def model3Name = "Level2ChildModel";
        def model4Name = "Model4";
        def model1MetaProps = [name: model1Name]
        def model2MetaProps = [name: model2Name, parentModel: model1Name]
        def model3MetaProps = [name: model3Name, parentModel: model2Name]
        def model4MetaProps = [name: model4Name]

        def model1Text = ModelGenerationTestUtils.getModelText(model1MetaProps, [], [], []);
        def model2Text = ModelGenerationTestUtils.getModelText(model2MetaProps, [], [], []);
        def model3Text = ModelGenerationTestUtils.getModelText(model3MetaProps, [], [], []);
        def model4Text = ModelGenerationTestUtils.getModelText(model4MetaProps, [], [], []);
        gcl.parseClass(model1Text + model2Text + model3Text+model4Text);
        def parentClass = gcl.loadClass(model1Name)
        def level1ChildClass = gcl.loadClass(model2Name)
        def level2ChildClass = gcl.loadClass(model3Name)
        def model4Class = gcl.loadClass(model4Name)
        def grailsDomainClasses = [new DefaultGrailsDomainClass(parentClass),
        new DefaultGrailsDomainClass(level1ChildClass),
        new DefaultGrailsDomainClass(level2ChildClass),
        new DefaultGrailsDomainClass(model4Class)]

        GetRootClassMethod method = new GetRootClassMethod(grailsDomainClasses[0], grailsDomainClasses);
        assertEquals (parentClass, method.rootClass)

        method = new GetRootClassMethod(grailsDomainClasses[1], grailsDomainClasses);
        assertEquals (parentClass, method.rootClass)

        method = new GetRootClassMethod(grailsDomainClasses[2], grailsDomainClasses);
        assertEquals (parentClass, method.rootClass)

        method = new GetRootClassMethod(grailsDomainClasses[3], grailsDomainClasses);
        assertEquals (model4Class, method.rootClass)
    }
}
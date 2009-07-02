package com.ifountain.rcmdb.domain.validator;

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import org.springframework.validation.BeanPropertyBindingResult
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.domain.method.RapidDomainClassProperty
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty;


/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 25, 2009
 * Time: 6:13:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidGrailsDomainClassValidatorTest extends RapidCmdbTestCase{
    GroovyClassLoader gcl;
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        gcl = new GroovyClassLoader();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }
    
    public void testSupport()
    {

        Map classes = initializePluginAndClasses ([:]);
        GrailsDomainClass domainClass = new DefaultGrailsDomainClass(classes.parent);
        RapidGrailsDomainClassValidator validator = new RapidGrailsDomainClassValidator();
        validator.setDomainClass (domainClass);
        assertFalse (validator.supports(Object.class));
        assertFalse (validator.supports(classes.child));
        assertTrue (validator.supports(classes.parent));
    }

    public void testValidateWithWrappedObject()
    {
        def invalidPropValue = "invalid"
        def invalidPropMessage = "invalid.property"
        def replacementParts = [
                child:[
                        ["static\\s*constraints\\s*=\\s*\\{", """static constraints={
                                prop1(nullable:false, validator:{val, obj ->
                                    ${DataStore.class.name}.put("passedObject", obj);
                                    if(val == "${invalidPropValue}")
                                    {
                                        return ["${invalidPropMessage}"]
                                    }
                                }

                            );
                            rel1(nullable:true)
                            """
                        ]
                ],
                parent:[["prop1\\s*=\\s*\"\"", "prop1"]]
        ]
        Map classes = initializePluginAndClasses (replacementParts);
        Class childClass = classes.child;


        
        GrailsDomainClass domainClass = new DefaultGrailsDomainClass(childClass);
        def allProps = []
        domainClass.getPersistentProperties().each{GrailsDomainClassProperty p->
            allProps.add(new RapidDomainClassProperty(name:p.name, type:p.getType(), isRelation:false));
        }

        def getNonFederatedPropertyListCalled = false;
        childClass.metaClass.'static'.getNonFederatedPropertyList = {->
            getNonFederatedPropertyListCalled = true;
            return allProps
        }

        RapidGrailsDomainClassValidator validator = new RapidGrailsDomainClassValidator();
        validator.setDomainClass (domainClass);
        validator.setMessageSource (new MessageSourceImpl());
        def domainObject = childClass.newInstance ();
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(domainObject, domainObject.getClass().getName());

        //test blank
        validator.validate (new Wrapper(domainObject:domainObject), domainObject,errors);
        assertTrue (getNonFederatedPropertyListCalled);
        assertSame(errors, domainObject.errors);
        assertTrue (errors.hasErrors());
        println errors.allErrors
        assertEquals ("nullable", errors.allErrors[0].code);

        //test validator
        getNonFederatedPropertyListCalled = false;
        errors = new BeanPropertyBindingResult(domainObject, domainObject.getClass().getName());
        domainObject = childClass.newInstance ();
        domainObject.prop1 = invalidPropValue;
        validator.validate (new Wrapper(domainObject:domainObject), domainObject,errors);
        assertTrue (getNonFederatedPropertyListCalled);
        assertTrue (errors.hasErrors());
        assertEquals (invalidPropMessage, errors.allErrors[0].code);
        assertTrue (DataStore.get("passedObject") instanceof Wrapper);
    }


    public void testIfReturnedNonFederatyedPropIsNotConstrainedPropertyItWillBeDiscarded()
        {
            Map classes = initializePluginAndClasses ([:]);
            Class childClass = classes.child;

            GrailsDomainClass domainClass = new DefaultGrailsDomainClass(childClass);
            def allProps = [new RapidDomainClassProperty(name:"nonExistingProp", type:String, isRelation:false)]
            domainClass.getPersistentProperties().each{GrailsDomainClassProperty p->
                allProps.add(new RapidDomainClassProperty(name:p.name, type:p.getType(), isRelation:false));
            }

            def getNonFederatedPropertyListCalled = false;
            childClass.metaClass.'static'.getNonFederatedPropertyList = {->
                getNonFederatedPropertyListCalled = true;
                return allProps
            }

            RapidGrailsDomainClassValidator validator = new RapidGrailsDomainClassValidator();
            validator.setDomainClass (domainClass);
            validator.setMessageSource (new MessageSourceImpl());
            def domainObject = childClass.newInstance ();
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(domainObject, domainObject.getClass().getName());



            validator.validate (new Wrapper(domainObject:domainObject), domainObject,errors);
            assertTrue (getNonFederatedPropertyListCalled);
            assertSame(errors, domainObject.errors);
            assertFalse (errors.hasErrors());
        }


    private Map initializePluginAndClasses(Map additionalParts)
    {
        def parentModelName = "RapidGrailsDomainClassValidatorTestParentModel";
        def childModelName = "RapidGrailsDomainClassValidatorTestChildModel";
        def relatedModelName = "RapidGrailsDomainClassValidatorTestRelatedModel";
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:childModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];

        def parentModelMetaProps = [name:parentModelName]
        def childModelMetaProps = [name:childModelName, parentModel:parentModelName]
        def relatedModelMetaProps = [name:relatedModelName]
        def modelProps = [ prop1];
        def keyPropList = [];
        String parentModelString = ModelGenerationTestUtils.getModelText(parentModelMetaProps, [], modelProps, keyPropList, [], additionalParts["parent"])
        String childModelString = ModelGenerationTestUtils.getModelText(childModelMetaProps, [], [], [], [rel1], additionalParts["child"])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, [], modelProps, keyPropList, [revrel1], additionalParts["related"])
        this.gcl.parseClass(parentModelString+childModelString+relatedModelString);
        Class parentModelClass = this.gcl.loadClass(parentModelName);
        Class childModelClass = this.gcl.loadClass(childModelName);
        Class relatedModelClass = this.gcl.loadClass(relatedModelName);
        return [parent:parentModelClass, child:childModelClass, related:relatedModelClass];
    }
}

class Wrapper{
    def getPropCallParams = [];
    def domainObject;
    public Object getProperty(String propName)
    {
        getPropCallParams.add(propName);
        return domainObject.getProperty(propName)
    }

    public void setProperty(String propertyName, Object value)
    {
        domainObject.setProperty(propertyName, value)    
    }

    def propCallparams()
    {
        return getPropCallParams;
    }
}

class MessageSourceImpl implements MessageSource
{

    public String getMessage(String s, Object[] objects, String s1, Locale locale) {
        return "message";
    }

    public String getMessage(String s, Object[] objects, Locale locale) {
        return "message";
    }

    public String getMessage(MessageSourceResolvable messageSourceResolvable, Locale locale) {
        return "message"; //To change body of implemented methods use File | Settings | File Templates.
    }
    
}

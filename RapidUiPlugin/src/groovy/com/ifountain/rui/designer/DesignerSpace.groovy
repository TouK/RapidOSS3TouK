package com.ifountain.rui.designer

import java.lang.reflect.Field
import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.converter.RapidConvertUtils
import org.apache.commons.lang.StringUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 20, 2009
* Time: 11:49:31 AM
*/
class DesignerSpace {
    private static DesignerSpace designerSpace;
    public static final PACKAGE_NAME = "com.ifountain.rui.designer.model"
    private Map uiElements;
    private List uiElementClasses;
    private Map uiElementClassMap;
    private Map parentClasses;
    private Map keyProperties;
    private static int nextId = 0;
    private DesignerSpace() {
        nextId = 0;
        uiElements = [:]
        uiElementClassMap = [:]
        keyProperties = [:]
        parentClasses = [:]
        uiElementClasses = _getUiClasses();
        uiElementClasses.each {
            uiElementClassMap.put(it.name, it);
            uiElements[it] = [:]
            keyProperties[it] = getKeyProperties(it)
        }
        populateParentClasses();
    };

    public static destroy() {
        designerSpace = null;
    }

    public static DesignerSpace getInstance() {
        if (designerSpace == null) {
            designerSpace = new DesignerSpace();
        }
        return designerSpace;
    }

    public String getMetaData() {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        builder.UiElements {
            builder.UiElement(designerType: "WebPages", display: "Web Pages", help: "Web Pages.html", imageExpanded: "images/rapidjs/component/tools/folder_open.gif", imageCollapsed: "images/rapidjs/component/tools/folder.gif") {
                builder.Children {
                    builder.Child(isMultiple: true, designerType: "WebPage")
                }
            }
            uiElementClasses.each {Class c ->
                def uiElementClassMetaData = getMetaData(c);
                if (uiElementClassMetaData != null && uiElementClassMetaData.designerType != null)
                {
                    createMetaXml(builder, uiElementClassMetaData);
                }
            }
        }
        return sw.toString();
    }
    def createMetaXml(builder, uiElementClassMetaData)
    {
        def uiElementProperties = [:]
        def metaChildren = uiElementClassMetaData.remove("childrenConfiguration")
        def metaProperties = uiElementClassMetaData.remove("propertyConfiguration")
        uiElementClassMetaData.each {String propName, Object propValue ->
            if (propName != "properties") {
                uiElementProperties[propName] = propValue;
            }
        }

        def configuredChildren = [];
        builder.UiElement(uiElementProperties) {
            builder.Properties {
                metaProperties.each {String propName, metaPropertyConfiguration ->
                    builder.Property(metaPropertyConfiguration);
                }
            }
            builder.Children {
                metaChildren.each {metaChildConfiguration ->
                    if (metaChildConfiguration.metaData != null)
                    {
                        configuredChildren.add(metaChildConfiguration.remove("metaData"));
                    }
                    builder.Child(metaChildConfiguration);

                }
            }
        }
        configuredChildren.each {childConfiguration ->
            createMetaXml(builder, childConfiguration);
        }
    }

    public Map getMetaData(Class uiClass) {
        Map metaData = uiClass.metaData();
        Map propertyConfiguration = metaData.propertyConfiguration ? metaData.propertyConfiguration : [:]
        Map tempPropConfig = [:]
        def uiElementPropertiesMap = [:];
        uiClass.metaClass.getProperties().each {
            uiElementPropertiesMap[it.name] = it;
        }
        def uiElementInstance = uiClass.newInstance()
        propertyConfiguration.each {String propName, Map config ->
            if (config.isVisible == null || config.isVisible != false) {
                MetaBeanProperty field = uiElementPropertiesMap[propName]
                if (config == null)
                {
                    config = [:];
                    propertyConfiguration.put(propName, config);
                }
                config.name = propName;
                if (field != null)
                {
                    config.type = config.type == null ? getType(field) : config.type

                    def isRequired = config.required != null ? config.required : isRequired(config);
                    config.required = isRequired;
                    config.descr = config.descr != null ? config.descr : "";
                    config.defaultValue = config.defaultValue != null ? config.defaultValue : uiElementInstance[propName] == null ? "" : String.valueOf(uiElementInstance[propName]);
                }
                def inlist = config.validators ? config.validators.inList ? config.validators.inList : null : null;
                if (inlist != null && !inlist.isEmpty() && config.inList == null) {
                    config.inList = inlist.join(",")
                }
                config.remove("validators");
                tempPropConfig[propName] = config
            }
        }
        metaData.propertyConfiguration = tempPropConfig;
        return metaData;
    }

    private String getType(MetaBeanProperty field)
    {
        if (String.class.isAssignableFrom(field.type)) {return "String"}
        if (Double.class.isAssignableFrom(field.type)) {return "Float"}
        if (Number.class.isAssignableFrom(field.type)) {return "Number"}
        if (Boolean.class.isAssignableFrom(field.type)) {return "Boolean"}
        if (Date.class.isAssignableFrom(field.type)) {return "Date"}
        return null;
    }

    public static isRequired(Map config)
    {
        def validators = config.validators;
        if (validators != null)
        {
            return validators.key || (validators.blank != null && !validators.blank) || (validators.nullable != null && !validators.nullable)
        }
        else
        {
            return false;
        }
    }

    public UiElmnt addUiElement(Class uiElementClass, Map props) {
        if (uiElementClassMap.containsKey(uiElementClass.name)) {
            def mc = uiElementClass.metaClass;
            def uiElementProps = [:];
            props.each {propName, propValue ->
                MetaBeanProperty metaProp = mc.getMetaProperty(propName)
                if (metaProp && metaProp.getSetter() != null) {
                    if (propValue != null) {
                        def fieldType = metaProp.getType();
                        def converter = RapidConvertUtils.getInstance().lookup(fieldType);
                        try {
                            propValue = converter.convert(fieldType, propValue);
                        }
                        catch (org.apache.commons.beanutils.ConversionException e) {
                            throw new Exception("Cannot convert value <${propValue}> for property <${propName}> to type ${fieldType.name} for ${getShortName(uiElementClass)}")
                        }
                    }
                    uiElementProps[propName] = propValue
                }
            }
            def uiElementKey = calculateKey(uiElementClass, uiElementProps);
            def uiElementInstance = uiElementClass.newInstance();
            uiElementInstance._designerKey = uiElementKey;
            uiElementProps.each {key, value ->
                uiElementInstance[key] = value;
            }
            validateUiElement(uiElementClass, uiElementInstance)
            insertUiElement(uiElementClass, uiElementInstance);
            return uiElementInstance;
        }
        else {
            throw new Exception("Class ${uiElementClass.name} cannot be found among UiElement classes");
        }
    }

    public List getUiClasses() {
        return uiElementClasses;
    }

    public Class getUiClass(String className) {
        return uiElementClassMap[className];
    }

    public UiElmnt getUiElement(Class uiElementClass, String key) {
        def elements = uiElements[uiElementClass];
        if (elements) {
            return elements[key];
        }
        return null;
    }

    public Map getUiElements(Class uiElementClass) {
        return uiElements[uiElementClass];
    }
    private void validateUiElement(Class uiElementClass, UiElmnt uiElementInstance) {
        def metaData = uiElementClass.metaData();
        def propertyConfiguration = metaData.propertyConfiguration;
        if (propertyConfiguration) {
            propertyConfiguration.each {propName, config ->
                def validators = config.validators;
                if (validators) {
                    if (validators.key) {
                        checkIsBlank(uiElementInstance, propName)
                        checkIsNull(uiElementInstance, propName)
                    }
                    if (validators.blank == false) {
                        checkIsBlank(uiElementInstance, propName)
                    }
                    if (validators.nullable == false) {
                        checkIsNull(uiElementInstance, propName)
                    }
                    if (validators.matches != null) {
                        checkMatches(uiElementInstance, propName, validators.matches);
                    }
                    if (validators.inList != null) {
                        checkInList(uiElementInstance, propName, validators.inList);
                    }
                }
            }
        }
    }

    private void checkIsBlank(UiElmnt uiElementInstance, String propName) {
        def propertyValue = uiElementInstance[propName];
        if (propertyValue instanceof String && StringUtils.isBlank((String) propertyValue)) {
            throw new Exception("Property <${propName}> of ${getShortName(uiElementInstance.class)} cannot be blank.");
        }
    }
    private void checkIsNull(UiElmnt uiElementInstance, String propName) {
        def propertyValue = uiElementInstance[propName];
        if (propertyValue == null) {
            throw new Exception("Property <${propName}> of ${getShortName(uiElementInstance.class)} cannot be null.");
        }
    }

    private void checkMatches(UiElmnt uiElementInstance, String propName, String expression) {
        def propertyValue = uiElementInstance[propName];
        if (!propertyValue.toString().matches(expression)) {
            throw new Exception("Property <${propName}> of ${getShortName(uiElementInstance.class)} does not match expression <${expression}>.");
        }
    }
    private void checkInList(UiElmnt uiElementInstance, String propName, List validValues) {
        def propertyValue = uiElementInstance[propName];
        if (!validValues.contains(propertyValue)) {
            throw new Exception("Property <${propName}> of Model1 is not one of <${validValues.join(', ')}>.");
        }

    }
    private void insertUiElement(Class uiElementClass, UiElmnt uiElementInstance) {
        def parents = parentClasses[uiElementClass];
        parents.each {parentClass ->
            if (uiElements[parentClass].containsKey(uiElementInstance._designerKey)) {
                throw new Exception("Another instance of ${getShortName(parentClass)} with keys ${getKeyString(parentClass, uiElementInstance)} exists.")
            }
        }
        parents.each {parentClass ->
            uiElements[parentClass][uiElementInstance._designerKey] = uiElementInstance;
        }
    }

    private String getShortName(Class uiElementClass) {
        return StringUtils.substringAfter(uiElementClass.name, "${PACKAGE_NAME}.Ui");
    }
    private String getKeyString(Class uiElementClass, UiElmnt uiElementInstance) {
        def keyStringArray = [];
        def keyset = keyProperties[uiElementClass];
        keyset.each {
            keyStringArray.add("<${it}:${uiElementInstance[it]}>")
        }
        return keyStringArray.join(" ");
    }
    private String calculateKey(Class uiElementClass, Map props) {
        def keyPropValues = [];
        def keyset = keyProperties[uiElementClass];
        keyset.each {propName ->
            def propValue = props[propName];
            if (propValue != null) {
                keyPropValues.add(String.valueOf(propValue));
            }
        }
        if (keyPropValues.isEmpty()) {
            keyPropValues.add(String.valueOf(nextId++))
        }
        return keyPropValues.join("_")
    }
    private List getKeyProperties(Class uiElementClass) {
        def keySet = [];
        def metaData = uiElementClass.metaData();
        def propertyConfiguration = metaData.propertyConfiguration;
        if (propertyConfiguration) {
            propertyConfiguration.each {propName, config ->
                def validators = config.validators;
                if (validators && validators.key) {
                    keySet.add(propName);
                }
            }
        }
        return keySet;
    }
    private List _getUiClasses() {

        GroovyClassLoader classLoader = DesignerSpaceClassLoaderFactory.getDesignerClassLoader();
        String path = PACKAGE_NAME.replace('.', '/');
        def resources = classLoader.getResources(path).toList();
        def dirs = [];
        resources.each {URL resource ->
            dirs.add(new File(resource.getFile()));
        }
        def classes = classLoader.getLoadedClasses().findAll {it.name.startsWith(PACKAGE_NAME)};
        for (File directory: dirs) {
            classes.addAll(findClasses(directory, PACKAGE_NAME));
        }
        return classes;

    }
    private List findClasses(File directory, String packageName) throws ClassNotFoundException {
        def classes = [];
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file: files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class") && file.getName().indexOf("closure") < 0) {
                classes.add(this.class.getClassLoader().loadClass(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    private void populateParentClasses() {
        uiClasses.each {Class currentClass ->
            def parents = [currentClass];
            parentClasses[currentClass] = parents;
            while (currentClass.getSuperclass() != UiElmnt) {
                currentClass = currentClass.getSuperclass();
                parents.add(currentClass);
            }
        }
    }

}
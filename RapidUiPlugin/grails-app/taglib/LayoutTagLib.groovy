import com.ifountain.rui.util.TagLibUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 19, 2009
* Time: 9:57:23 AM
* To change this template use File | Settings | File Templates.
*/
class LayoutTagLib {
    static namespace = "rui"
    static stringUnitProps = [position:"position", body:"body", gutter:"gutter"]
    def layout = {attrs, body ->
        def layoutXmlString = TagLibUtils.getConfigAsXml("Layout", attrs, [], body(), true);
        def layoutXml = new XmlSlurper().parseText(layoutXmlString);
        def layoutConfig = createLayoutMap(layoutXml, null);
        out << render(template:"/tagLibTemplates/layout/layout", model:[layout:layoutConfig]);
    }

    def createLayoutMap(layoutNode, parentLayout)
    {
        def layoutId = layoutNode.@id?.text();
        def layoutProps = [attributes:[id:layoutId]];
        layoutProps.attributes.putAll (layoutNode.attributes());
        if(parentLayout)
        {
            layoutProps.attributes.parentLayout = parentLayout;
        }
        def units = [];
        layoutProps.units = units;
        def centerExists = false;
        layoutNode.LayoutUnit.each{layoutUnitNode->
            def unitConfig = [:];
            def unitAttributes = layoutUnitNode.attributes();
            centerExists = unitAttributes.position =="center" ||centerExists; 
            def unitContent = [];
            unitAttributes.each{attributeName, attributeValue->
                attributeValue = stringUnitProps.containsKey(attributeName)?"'${attributeValue}'":attributeValue;
                unitContent.add("${attributeName}:${attributeValue}");
            }
            unitConfig.attributes = unitAttributes;
            unitConfig.content = "{${unitContent.join (",")}}";
            layoutUnitNode.InnerLayout.each{innerLayoutNode->
                unitConfig.childLayout = createLayoutMap(innerLayoutNode, layoutId);
            }
            units.add(unitConfig);
        }
        if(!centerExists)
        {
            units.add([attributes:[position:"center", gutter:""], content:"{position:'center', gutter:''}"])
        }
        return layoutProps;
    }

    def innerLayout = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("InnerLayout", attrs, [], body(), true);
    }

    def layoutUnit = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("LayoutUnit", attrs, [], body(), true);
    }
}
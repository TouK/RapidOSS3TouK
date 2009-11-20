class EditAreaTagLib {
    def editArea = { attrs ->
        out << "<script>"
        out << """editAreaLoader.scripts_to_load[editAreaLoader.scripts_to_load.length]="yahoo-dom-event";""";
        out << "editAreaLoader.init({"
        StringBuffer bf = new StringBuffer();
        attrs.each{attrName, attrValue->
            if(attrValue == "true" || attrValue == "false")
            {
                bf.append(attrName).append(":").append(attrValue).append(",")
            }
            else
            {
                bf.append(attrName).append(":\"").append(attrValue).append("\",")

            }
        }
        def attrStr = bf.toString();
        if(attrStr.length() > 0)
        {
            attrStr = attrStr.substring(0, attrStr.length()-1);
        }
        out <<  attrStr
        out << "})"
        out << "</script>"
    }
}

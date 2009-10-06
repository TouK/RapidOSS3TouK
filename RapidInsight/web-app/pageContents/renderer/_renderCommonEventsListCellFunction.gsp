<script>
    function renderCommonEventListCellFunction(key, value, data, el){
        if(key == "changedAt" || key == 'createdAt' || key == 'clearedAt' || key == 'willExpireAt'){
            if(value == "0" || value == "")
            {
                return "never"
            }
            else
            {
                try
                {
                    var d = new Date();
                    d.setTime(parseFloat(value))
                    return d.format("d M H:i:s");
                }
                catch(e)
                {}
            }
        }
        else if(key == "severity")
        {
            switch(value)
            {
                case '5' : return "Critical";
                case '4' : return "Major";
                case '3' : return "Minor";
                case '2' : return "Warning";
                case '1' : return "Indeterminate";
                case '0' : return "Clear";
                default  : return "";
            }
        }
        else if(key == "elementName" || key=="identifier"){
            YAHOO.util.Dom.setStyle(el, 'color', 'blue')
        }
        return value;
     }
     ${componentName}.renderCellFunction = renderCommonEventListCellFunction;
</script>
<script>
    function addToSelect${id}() {
        moveAllSelected${id}FromSelectToSelect(document.getElementById("available${id}Select"), document.getElementById('${id}Select'))
        update${id}Input();
    }
    function removeFromSelect${id}() {
        moveAllSelected${id}FromSelectToSelect(document.getElementById('${id}Select'), document.getElementById('available${id}Select'))
        update${id}Input();
    }
    function update${id}Input() {
        var ${id}Select = document.getElementById('${id}Select');
        var users = [];
        for (var index = 0; index < ${id}Select.options.length; index ++) {
            users.push(${id}Select.options[index].value);
        }
        document.getElementById("${inputName}").value = users.join(",");
    }
    function collectSelected${id}FromSelect(aSelect)
    {
        var selectedIndices = new Array();
        for (var i = 0; i < aSelect.options.length; i++)
        {
            if (aSelect.options[i].selected == true)
            {
                selectedIndices[selectedIndices.length] = i;
            }
        }
        return selectedIndices;
    }


    function moveFromSelected${id}ToSelected${id}(index, fromSelect, toSelect)
    {
        if (index > -1)
        {
            var associatedOption = fromSelect.options[index];
            fromSelect.remove(index);
            try
            {
                toSelect.add(associatedOption, null);//to the end of the select
            }
            catch(ex)
            {
                toSelect.add(associatedOption);//IE only
            }

        }
    }


    function moveAllSelected${id}FromSelectToSelect(fromSelect, toSelect)
    {
        var arrayOfSelectedIndices = collectSelected${id}FromSelect(fromSelect);
        for (var i = arrayOfSelectedIndices.length - 1; i >= 0; i--)
        {
            moveFromSelected${id}ToSelected${id}(arrayOfSelectedIndices[i], fromSelect, toSelect);
        }
    }
</script>
<input type="hidden" name="${inputName}" id="${inputName}" value="<%
    def allContent=[];
    toListContent.each{allContent.add(it[valueProperty])};
    print allContent.join(',');
%>"/>
<div id="${id}">
    <table style="border:none;width:auto">
        <tbody>
            <tr>
                <td><div>${fromListTitle}</div></td>
                <td></td>
                <td><div>${toListTitle}</div></td>
            </tr>
            <tr>
                <td>
                    <div>
                        <select style="overflow:auto;width:200px;border:1px solid #A8B9CF" size="15" multiple="true" name="available${id}Select" id="available${id}Select">
                            <g:each in="${fromListContent}" status="i" var="fromSelectItem">
                                <option value="${valueProperty?fromSelectItem[valueProperty]:fromSelectItem}">${nameProperty?fromSelectItem[nameProperty]:fromSelectItem}</option>
                            </g:each>
                        </select>
                    </div>
                </td>
                <td>
                    <div>
                        <span><span><button type="button" onclick="addToSelect${id}()"> >> </button></span></span>
                    </div>
                    <div>
                        <span><span><button type="button" onclick="removeFromSelect${id}()"> << </button></span></span>
                    </div>
                </td>
                <td>
                    <div>
                        <select style="overflow:auto;width:200px;border:1px solid #A8B9CF" size="15" multiple="true" name="${id}Select" id="${id}Select">
                            <g:each in="${toListContent}" status="i" var="toSelect">
                                <option value="${valueProperty?toSelect[valueProperty]:toSelect}">${nameProperty?toSelect[nameProperty]:toSelect}</option>
                            </g:each>
                        </select>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
</div>
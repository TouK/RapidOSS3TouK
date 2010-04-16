<script>
    var ListToList = function(id, inputName){
        this.id = id;
        this.inputName = inputName;
        if(!window.listToLists){
            window.listToLists = {};
        }
        window.listToLists[id] = this;
    };
    ListToList.prototype = {
        addToSelect: function() {
            var fromSelect = document.getElementById('available' + this.id +'Select');
            var toSelect = document.getElementById(this.id + 'Select') 
            this.moveAllSelectedFromSelectToSelect(fromSelect, toSelect)
            this.updateInput();
        <g:if test="${showCounts}">
            this.updateCounts(fromSelect, toSelect);
        </g:if>
        },
        removeFromSelect: function() {
            var fromSelect = document.getElementById('available' + this.id +'Select');
            var toSelect = document.getElementById(this.id + 'Select') 
            this.moveAllSelectedFromSelectToSelect(toSelect, fromSelect)
            this.updateInput();
        <g:if test="${showCounts}">
            this.updateCounts(fromSelect, toSelect);
        </g:if>
        },
        updateInput : function() {
            var select = document.getElementById(this.id + 'Select');
            var users = [];
            for (var index = 0; index < select.options.length; index ++) {
                users.push(select.options[index].value);
            }
            document.getElementById(this.inputName).value = users.join(",");
        },
        updateCounts : function(fromSelect, toSelect){
             var fromCount = fromSelect.options.length;
             var toCount = toSelect.options.length;
             document.getElementById('${id}fromTitle').innerHTML = '${fromListTitle} (' + fromCount+ ')';
             document.getElementById('${id}toTitle').innerHTML = '${toListTitle} (' + toCount+ ')';
        },
        collectSelectedFromSelect: function(aSelect){
            var selectedIndices = new Array();
            for (var i = 0; i < aSelect.options.length; i++)
            {
                if (aSelect.options[i].selected == true)
                {
                    selectedIndices[selectedIndices.length] = i;
                }
            }
            return selectedIndices;
        },
        moveFromSelectedToSelected :function (index, fromSelect, toSelect){
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
        },

        moveAllSelectedFromSelectToSelect: function (fromSelect, toSelect){
            var arrayOfSelectedIndices = this.collectSelectedFromSelect(fromSelect);
            for (var i = arrayOfSelectedIndices.length - 1; i >= 0; i--)
            {
                this.moveFromSelectedToSelected(arrayOfSelectedIndices[i], fromSelect, toSelect);
            }
        }
    }
    new ListToList('${id}', '${inputName}')
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
                <%
                    def fromTitle = showCounts ? "${fromListTitle} (${fromListContent.size()})" : fromListTitle
                    def toTitle = showCounts ? "${toListTitle} (${toListContent.size()})" : toListTitle
                %>
                <td><div class="title" id="${id}fromTitle">${fromTitle}</div></td>
                <td></td>
                <td><div class="title" id="${id}toTitle">${toTitle}</div></td>
            </tr>
            <tr>
                <td>
                    <div>
                        <select style="overflow:auto;width:${width? width + 'px':'200px'};border:1px solid #A8B9CF" size="15" multiple="true" name="available${id}Select" id="available${id}Select">
                            <g:each in="${fromListContent}" status="i" var="fromSelectItem">
                                <option value="${valueProperty?fromSelectItem[valueProperty]:fromSelectItem}">${nameProperty?fromSelectItem[nameProperty]:fromSelectItem}</option>
                            </g:each>
                        </select>
                    </div>
                </td>
                <td>
                    <div>
                        <span><span><button id="${id}ListsAdd" type="button" onclick="window.listToLists['${id}'].addToSelect()"> >> </button></span></span>
                    </div>
                    <div>
                        <span><span><button id="${id}ListsRemove" type="button" onclick="window.listToLists['${id}'].removeFromSelect()"> << </button></span></span>
                    </div>
                </td>
                <td>
                    <div>
                        <select style="overflow:auto;width:${width? width + 'px':'200px'};border:1px solid #A8B9CF" size="15" multiple="true" name="${id}Select" id="${id}Select">
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
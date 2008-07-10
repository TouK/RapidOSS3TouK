
function render(){

    var dh = YAHOO.ext.DomHelper;
    var layout = new YAHOO.ext.BorderLayout(document.body, {
        north:{
            initialSize:100,
            split:true,
            titlebar: true,
            collapsible:true
        },
        west:{
            initialSize:300,
            split:true
        },
        center:{
            autoScroll:true
        }
    });

    layout.beginUpdate();
    layout.add('north', new YAHOO.ext.ContentPanel(dh.append(document.body, {tag:'div', html:"header"}), {}));
    layout.add('west', new YAHOO.ext.ContentPanel(dh.append(document.body, {tag:'div',
            html:'filters'}), {}));
    var searchContainer = dh.append(document.body, {tag:'div'});
    var searchConfig = {
        id:'searchList',
        url:'/RapidCMDB/script/run/getDevices',
        searchQueryParamName:'query',
        rootTag:'Results',
        contentPath:'Result',
        indexAtt:'id',
        totalCountAttribute:'Total',
        offsetAttribute:'Offset',
        sortOrderAttribute:'sortOrder',
        fields:['id', 'name', 'creationClassName', 'vendor', 'description', 'location'],
        menuItems:{ item1 : { url: "url1" }, item2 : { url: "url2", condition : function(data) {return data == "3001"} }, item3 : { url: "url3" } } ,
        menuItemUrlParamName: 'id'
    }

    searchList = new YAHOO.rapidjs.rcmdb.SearchList(searchContainer, searchConfig)
    layout.add('center', new YAHOO.rapidjs.rcmdb.SearchListPanel(searchList, {fitToFrame:true}));
    layout.endUpdate();
    searchList.poll();
}


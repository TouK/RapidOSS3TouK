<script>
    ${componentName}.addToolbarButton({
        className:'r-filterTree-groupAdd',
        scope:this,
        tooltip: 'Add group',
        click:function() {
            var queryGroupForm = YAHOO.rapidjs.Components['saveQueryGroupForm'];
            queryGroupForm.show(createURL('queryGroupForm.gsp', {mode:'create', type:'${queryType}'}));
            queryGroupForm.popupWindow.show();

        }
    });
    ${componentName}.addToolbarButton({
        className:'r-filterTree-queryAdd',
        scope:this,
        tooltip: 'Add query',
        click:function() {
            var queryForm = YAHOO.rapidjs.Components['saveQueryForm'];
            var searchClass='';
            var searchComponent=YAHOO.rapidjs.Components['${searchComponentName}'];
            if(searchComponent)
            {
				searchClass=searchComponent.getSearchClass();
			}
            queryForm.show(createURL('queryForm.gsp', {mode:'create', type:'${queryType}', searchComponentType:'${searchComponentType}', searchInEnabled:'${searchInEnabled != null ? searchInEnabled:true}',searchClass:searchClass}));
            queryForm.popupWindow.show();
        }
    });
</script>
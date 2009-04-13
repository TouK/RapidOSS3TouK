<script>
	var ruleTree = YAHOO.rapidjs.Components['ruleTree'];


    ruleTree.addToolbarButton({
        className:'r-filterTree-queryAdd',
        scope:this,
        tooltip: 'Add Rule',
        click:function() {
            var ruleContent = YAHOO.rapidjs.Components['ruleContent'];
        	ruleContent.show(createURL('rsMessageRuleForm.gsp',{mode:'create'}),'New Rule Details');
        }
    });

    ruleTree.poll();

</script>
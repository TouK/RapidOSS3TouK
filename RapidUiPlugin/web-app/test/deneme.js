
var config = {
    url:'error.xml',
    id:'autocomp',
    rootTag:'Suggestions',
    contentPath:'Suggestion',
    fields:['Name']
}
var dh = YAHOO.ext.DomHelper
var autoComp = new YAHOO.rapidjs.component.Autocomplete(document.getElementById('searchDiv'), config);

YAHOO.util.Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'center', body: 'right', resize: true, gutter: '1px' },
                { position: 'left', width: 250, resize: true, body: 'left', scroll: false},
            ]
        });
    layout.render();

    })


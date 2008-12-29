/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.GMap = function(container, config) {
    YAHOO.rapidjs.component.GMap.superclass.constructor.call(this, container, config);
    YAHOO.ext.util.Config.apply(this, config);

    var events = {
        'markerClick' : new YAHOO.util.CustomEvent('markerClick')
    };
    YAHOO.ext.util.Config.apply(this.events, events);
    this.configureTimeout(config);
    this.markers = [];
    this.locations = {};
    this.renderingFinished = false;
    this.pollInterval = 0;
    YAHOO.util.Event.addListener(window, 'unload', function() {
        GUnload();
    });

    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title: this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);


};

YAHOO.extend(YAHOO.rapidjs.component.GMap, YAHOO.rapidjs.component.PollingComponentContainer, {

    handleSuccess: function(response) {
        this.clearData();
        var data = new YAHOO.rapidjs.data.RapidXmlDocument(response);
        var node = data.rootNode;
        if (node) {
            var locations = node.getElementsByTagName(this.contentPath);
            var bounds = new GLatLngBounds();
            for (var index = 0; index < locations.length; index++) {
                var locationNode = locations[index];
                this.createLocation(locationNode);
            }
            this.putMarkersOnMap();
        }
    },

    createLocation: function(locationNode) {
        var lat = parseFloat(locationNode.getAttribute(this.latitudeAttributeName));
        var lng = parseFloat(locationNode.getAttribute(this.longitudeAttributeName));
        var address = locationNode.getAttribute(this.addressAttributeName);
        if (!this.locations[address]) {
            var tooltip = "";
            if (this.tooltipAttributeName) {
                tooltip = locationNode.getAttribute(this.tooltipAttributeName);
            }
            var marker = locationNode.getAttribute(this.markerAttributeName);
            this.locations[address] = {lat:lat, lng: lng, tooltip:tooltip, marker: marker, node:locationNode};
        }
    },

    render: function() {
        if (GBrowserIsCompatible()) {
            this.map = new GMap2(this.body.dom);
            this.map.addControl(new GLargeMapControl());
            this.map.setCenter(new GLatLng(0, 0), 1);

		    // Create our "tiny" marker icon
            this.baseIcon = new GIcon();
            /*			this.baseIcon.shadow = "http://labs.google.com/ridefinder/images/mm_30_shadow.png";
                        this.baseIcon.iconSize = new GSize(12, 20);
                        this.baseIcon.shadowSize = new GSize(22, 20);
                        this.baseIcon.iconAnchor = new GPoint(6, 20);
                        this.baseIcon.infoWindowAnchor = new GPoint(5, 1);
            */
            this.baseIcon.shadow = "http://www.google.com/mapfiles/shadow50.png";
            this.baseIcon.iconSize = new GSize(20, 34);
            this.baseIcon.shadowSize = new GSize(37, 34);
            this.baseIcon.iconAnchor = new GPoint(9, 34);
            this.baseIcon.infoWindowAnchor = new GPoint(9, 2);

        }
    },

    clearData: function() {
        while (this.markers.length > 0) {
            var marker = this.markers.pop();
            GEvent.clearInstanceListeners(marker);
            this.map.removeOverlay(marker);
        }
        this.locations = {};
    },

    createMarker: function(point, loc) {
        // Do we need to clean up icons created????
        var icon = new GIcon(this.baseIcon);
        icon.image = loc.marker;
        var marker = new GMarker(point, icon);
        var component = this;
        var node = loc.node;
        var mapEvents = this.events;
        var component = this;
        GEvent.addListener(marker, "click", function(e) {
            if (component.tooltipAttributeName) {
                marker.openInfoWindowHtml(loc.tooltip);
            }
            mapEvents['markerClick'].fireDirect(node);
        });

        this.markers.push(marker);
        return marker;
    },


    putMarkersOnMap: function() {
        var bounds = new GLatLngBounds();
        var index = 0;
        for (var address in this.locations) {
            var loc = this.locations[address];
            var point = new GLatLng(loc.lat, loc.lng);
            var marker = this.createMarker(point, loc);
            this.map.addOverlay(marker);
            bounds.extend(point);
        }
        var zoom = this.map.getBoundsZoomLevel(bounds);
        var lng = bounds.getCenter().lng();
        var lat = bounds.getCenter().lat()
        if (zoom < 6) {
            lat += 1;
        }
        var center = new GLatLng(lat, lng);
        this.map.setZoom(zoom);
        this.map.setCenter(center);
//		this.map.setZoom(this.map.getBoundsZoomLevel(bounds));
        //		this.map.setCenter(bounds.getCenter());
    },
    resize : function(width, height) {
        this.body.setHeight(height - this.header.offsetHeight);
        if (this.renderingFinished == false) {
            this.render();
            this.renderingFinished = true;
        }

//			this.poll();

    }
});


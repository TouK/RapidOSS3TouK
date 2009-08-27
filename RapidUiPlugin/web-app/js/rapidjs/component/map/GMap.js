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
    if(!YAHOO.rapidjs.component.GMap.isGoogleLibIncluded){
        document.write('<script type="text/javascript" src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=' + config.googleKey + '"></script>')
        YAHOO.rapidjs.component.GMap.isGoogleLibIncluded = true;        
    }
    YAHOO.rapidjs.component.GMap.superclass.constructor.call(this, container, config);
    this.locationTagName = "Location";
    this.lineTagName = "Line";
    this.iconTagName = "Icon";
    this.latitudeField = "lat"
    this.longitudeField = "lng"
    this.addressField = "address"
    this.markerField = "marker"
    this.tooltipField = "tooltip"
    this.lineSize = 5;
    this.latitudeField = "lat"
    this.lineStartLatField = "startLat"
    this.lineEndLatField = "endLat"
    this.lineStartLngField = "startLng"
    this.lineEndLngField = "endLng"
    this.lineColorField = "color"
    this.iconWidthField = "width"
    this.iconHeightField = "height"
    this.iconSourceField = "src"
    this.defaultIconWidth = 32
    this.defaultIconHeight = 32
    YAHOO.ext.util.Config.apply(this, config);

    var events = {
        'markerClicked' : new YAHOO.util.CustomEvent('markerClicked'),
        'iconClicked' : new YAHOO.util.CustomEvent('iconClicked'),
        'lineClicked' : new YAHOO.util.CustomEvent('lineClicked')
    };
    YAHOO.ext.util.Config.apply(this.events, events);
    this.configureTimeout(config);
    this.markers = [];
    this.locations = {};
    this.lineConfigs = [];
    this.lines = [];
    this.iconConfigs = [];
    this.icons = [];
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
            var locations = node.getElementsByTagName(this.locationTagName);
//            var bounds = new GLatLngBounds();
            for (var index = 0; index < locations.length; index++) {
                var locationNode = locations[index];
                this.createLocation(locationNode);
            }
            var lines = node.getElementsByTagName(this.lineTagName);
            for (var index = 0; index < lines.length; index++) {
                var lineNode = lines[index];
                this.createLineConfig(lineNode);
            }
            var icons = node.getElementsByTagName(this.iconTagName);
            for (var index = 0; index < icons.length; index++) {
                var iconNode = icons[index];
                this.createIconConfig(iconNode);
            }
            this.putItemsOnMap();
        }
    },

    createLocation: function(locationNode) {
        var lat = parseFloat(locationNode.getAttribute(this.latitudeField));
        var lng = parseFloat(locationNode.getAttribute(this.longitudeField));
        var address = locationNode.getAttribute(this.addressField);
        if (!this.locations[address]) {
            var tooltip = locationNode.getAttribute(this.tooltipField) || "";
            var marker = locationNode.getAttribute(this.markerField);
            this.locations[address] = {lat:lat, lng: lng, tooltip:tooltip, marker: marker, node:locationNode};
        }
    },

    createLineConfig: function(lineNode) {
        var startLat = parseFloat(lineNode.getAttribute(this.lineStartLatField));
        var startLng = parseFloat(lineNode.getAttribute(this.lineStartLngField));
        var endLat = parseFloat(lineNode.getAttribute(this.lineEndLatField));
        var endLng = parseFloat(lineNode.getAttribute(this.lineEndLngField));
        var color = lineNode.getAttribute(this.lineColorField) || "#ff0000";
        var tooltip = lineNode.getAttribute(this.tooltipField) || "";   
        this.lineConfigs[this.lineConfigs.length] = {startLat:startLat, startLng: startLng, endLat:endLat, endLng: endLng, node:lineNode, color:color, tooltip:tooltip};
    },

    createIconConfig: function(iconNode) {
        var lat = parseFloat(iconNode.getAttribute(this.latitudeField));
        var lng = parseFloat(iconNode.getAttribute(this.longitudeField));
        var srcValue = iconNode.getAttribute(this.iconSourceField);
        var src = srcValue;
        if(srcValue.indexOf("http") < 0){
            src = getUrlPrefix() + srcValue
        }
        var iconWidth = 0;
        var iconHeight = 0;
        try{
            iconWidth = parseFloat(iconNode.getAttribute(this.iconWidthField));
        }
        catch(error){}
        try{
            iconHeight = parseFloat(iconNode.getAttribute(this.iconHeightField));
        }
        catch(error){}
        var tooltip = iconNode.getAttribute(this.tooltipField) || "";
        this.iconConfigs[this.iconConfigs.length] = {lat:lat, lng: lng, tooltip:tooltip, src: src, node:iconNode, width:iconWidth || this.defaultIconWidth, height:iconHeight || this.defaultIconHeight};
    },

    render: function() {
        if (GBrowserIsCompatible()) {
            this.map = new GMap2(this.body.dom);
            this.map.addControl(new GLargeMapControl());
            this.map.setCenter(new GLatLng(0, 0), 1);

		    // Create our "tiny" marker icon
            this.baseMarkerIcon = new GIcon();
            this.baseMarkerIcon.shadow = "http://www.google.com/mapfiles/shadow50.png";
            this.baseMarkerIcon.iconSize = new GSize(20, 34);
            this.baseMarkerIcon.shadowSize = new GSize(37, 34);
            this.baseMarkerIcon.iconAnchor = new GPoint(9, 34);
            this.baseMarkerIcon.infoWindowAnchor = new GPoint(9, 2);

            this.baseIcon = new GIcon();
            this.baseIcon.iconSize = new GSize(this.defaultIconWidth, this.defaultIconHeight);
            this.baseIcon.iconAnchor = new GPoint(8, 8);
            this.baseIcon.infoWindowAnchor = new GPoint(9, 2);

        }
    },

    clearData: function() {
        while (this.markers.length > 0) {
            var marker = this.markers.pop();
            GEvent.clearInstanceListeners(marker);
            this.map.removeOverlay(marker);
        }
        while (this.lines.length > 0) {
            var line = this.lines.pop();
            GEvent.clearInstanceListeners(line);
            this.map.removeOverlay(line);
        }
        while (this.icons.length > 0) {
            var icon = this.icons.pop();
            GEvent.clearInstanceListeners(icon);
            this.map.removeOverlay(icon);
        }
        this.locations = {};
        this.lineConfigs = [];
        this.iconConfigs = [];
    },

    createMarker: function(point, loc) {
        // Do we need to clean up icons created????
        var icon = new GIcon(this.baseMarkerIcon);
        icon.image = loc.marker;
        var marker = new GMarker(point, icon);
        var component = this;
        var node = loc.node;
        var mapEvents = this.events;
        var component = this;
        GEvent.addListener(marker, "click", function(latLng) {
            if(loc.tooltip != ''){
                marker.openInfoWindowHtml(loc.tooltip);
            }
            mapEvents['markerClicked'].fireDirect(node);
        });

        this.markers.push(marker);
        return marker;
    },
    createIcon: function(point, iconConfig) {
        // Do we need to clean up icons created????
        var icon = new GIcon(this.baseIcon);
        icon.image = iconConfig.src;
        icon.iconSize = new GSize(iconConfig.width, iconConfig.height);
        var marker = new GMarker(point, icon);
        var component = this;
        var node = iconConfig.node;
        var mapEvents = this.events;
        var component = this;
        GEvent.addListener(marker, "click", function(latLng) {
            if(iconConfig.tooltip != ''){
                marker.openInfoWindowHtml(iconConfig.tooltip);
            }
            mapEvents['iconClicked'].fireDirect(node);
        });

        this.icons.push(marker);
        return marker;
    },

    createLine: function(lineConfig, startPoint, endPoint){
        var line = new GPolyline([startPoint,endPoint], lineConfig.color, this.lineSize);
        var mapEvents = this.events;
        var node = lineConfig.node;
        var component = this;
        var gmap = this.map;
        GEvent.addListener(line, "click", function(latLng) {
            if (lineConfig.tooltip != '') {
                gmap.openInfoWindowHtml(latLng, lineConfig.tooltip);
            }
            mapEvents['lineClicked'].fireDirect(node);
        });
        this.lines.push(line);
        return line;
    },


    putItemsOnMap: function() {
        var bounds = new GLatLngBounds();
        var index = 0;
        for (var address in this.locations) {
            var loc = this.locations[address];
            var point = new GLatLng(loc.lat, loc.lng);
            var marker = this.createMarker(point, loc);
            this.map.addOverlay(marker);
            bounds.extend(point);
        }
        for (var index = 0; index < this.iconConfigs.length; index++) {
            var iconConfig = this.iconConfigs[index];
            var point = new GLatLng(iconConfig.lat, iconConfig.lng);
            var icon = this.createIcon(point, iconConfig);
            this.map.addOverlay(icon);
            bounds.extend(point);
        }
        for (var index = 0; index < this.lineConfigs.length; index++) {
            var lineConfig = this.lineConfigs[index];
            var startPoint =  new GLatLng(lineConfig.startLat, lineConfig.startLng);
            var endPoint =  new GLatLng(lineConfig.endLat, lineConfig.endLng);
            var line = this.createLine(lineConfig, startPoint, endPoint);
            this.map.addOverlay(line);
            bounds.extend(startPoint);
            bounds.extend(endPoint);
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

YAHOO.rapidjs.component.GMap.isGoogleLibIncluded = false;


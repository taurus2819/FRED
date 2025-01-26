<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <title>FRED Database</title>

        <link rel="stylesheet" href="./plugin/GeoExt-1.0/resources/css/geoext-all.css" type="text/css" />

        <script type="text/javascript" language="javascript" src="./scripts/ext-base.js"></script>
        <script type="text/javascript" language="javascript" src="./scripts/ext-all.js"></script>
        <script type="text/javascript" language="javascript" src="./scripts/util.js"></script>
        <script type="text/javascript" language="javascript" src="./plugin/OpenLayers-2.13.1/OpenLayers.js"></script>
        <script type="text/javascript" language="javascript" src="./plugin/GeoExt-1.0/script/GeoExt.js"></script>
        <script type="text/javascript" language="javascript" src="./plugin/proj4js/proj4js-compressed.js"></script>
        <script type="text/javascript" language="javascript" src="./plugin/proj4js/defs/EPSG4272.js"></script>
        <script type="text/javascript" language="javascript" src="./plugin/proj4js/defs/EPSG3857.js"></script>
        <script type="text/javascript" language="javascript" src="//maps.googleapis.com/maps/api/js?key=AIzaSyBj_MCLMBKMcNvUXelP9pfEmlCsHN_nbX0"></script>
        <script type="text/javascript" language="javascript" src="./scripts/wfsQuery.js"></script>          
          

        <style type="text/css">
            .legend {
                padding-left: 18px;
            }
            .x-tree-node-el {
                border-bottom: 1px solid #ddd;
                padding-bottom: 0px;
            }
            .x-tree-ec-icon {
                width: 2px;
            }

            .x-form-item {
                display: none;
            }
        </style>

        <script type="text/javascript">

            OpenLayers.ProxyHost = "proxy.jsp?url=";
            var host = getFQHostName();
            var p3857 = new OpenLayers.Projection("EPSG:3857");
            var p3857 = new OpenLayers.Projection("EPSG:3857");    
            var p4326 = new OpenLayers.Projection("EPSG:4326");
            var p4272 = new OpenLayers.Projection("EPSG:4272");
            var p900913 = new OpenLayers.Projection("EPSG:900913");
            var fredLayer;
            var popup;
            var tree;
            var map;    
            var checker = true;

            //imported stuff
            var mylons = new Array();
            var mylats = new Array();
            var num_markers = 0;
            var marker = new Array();
            marker[1] = null;
            marker[2] = null;
            var ptLat = new Array();
            ptLat[1] = -1;
            ptLat[2] = -1;
            var ptLong = new Array();
            ptLong[1] = -1;
            ptLong[2] = -1;
            env = '';
            var tminx, tmaxx;
            var tminy, tmaxy;
            var cnt_x, cnt_y;
            var minlon, maxlon;
            var minlat, maxlat;
            var thislat, thislon;
            var nwmarker, nemarker, semarker, swmarker, polygon;
            //end imported stuff
            
            var lineFeature;
            
            var geoserver_url = host.concat("/webmaps/gns/wms");
            var geology_url = host.concat("/webmaps/geology/wms");
            var esri_url = host.concat("/webmaps/gis/services"); 

            function go() {

                // custom layer node UI class
                var LayerNodeUI = Ext.extend(
                    GeoExt.tree.LayerNodeUI,
                    new GeoExt.tree.TreeNodeUIEventMixin()
                );

                map = new OpenLayers.Map("map", {
                    controls: [
                        new OpenLayers.Control.Navigation(),
                        new OpenLayers.Control.PanZoomBar({position: new OpenLayers.Pixel(2, 15)}),
                        new OpenLayers.Control.ScaleLine({position: new OpenLayers.Pixel(710, 450)}),
                        new OpenLayers.Control.MousePosition({prefix:"Longitude: ",separator:", Latitude: "}),
                        new OpenLayers.Control.LayerSwitcher({ascending: false}),
                        new OpenLayers.Control.KeyboardDefaults()
                    ],
                    eventListeners: {
                        "changebaselayer": mapBaseLayerChanged,
                        "zoomend": changeBaseLayerBasedOnZoom
                    },
                    projection: p900913,
                    units: "m",
                    displayProjection: p4326,
                    minZoomLevel: 1,
                    numZoomLevels: 20,
                    maxExtent: new OpenLayers.Bounds(-20037508.34, -20037508.34, 20037508.34, 20037508.34)
                });

                var googleH = new OpenLayers.Layer.XYZ(
                    "Google Hybrid",
                    "https://mt0.google.com/vt/lyrs=y&hl=en&bgcolor=0x000000&x=\$\{x\}&y=\$\{y\}&z=\$\{z\}" +
                    "&key=AIzaSyBj_MCLMBKMcNvUXelP9pfEmlCsHN_nbX0",
                    {
                        wrapDateLine: true,
                        projection: p900913,
                        sphericalMercator: true,
                    },
                    {
                        displayInLayerSwitcher: false,
                        tileOptions: {crossOriginKeyword: 'anonymous'}
                    }
                );
                var googleP = new OpenLayers.Layer.XYZ(
                    "Google Physical",
                    "https://mt1.google.com/vt/lyrs=m&hl=en&x=\$\{x\}&y=\$\{y\}&z=\$\{z\}" +
                    "&key=AIzaSyBj_MCLMBKMcNvUXelP9pfEmlCsHN_nbX0",
                    {
                        wrapDateLine: true,
                        projection: p900913,
                        sphericalMercator: true
                    },
                    {
                        displayInLayerSwitcher: false,
                        tileOptions: {crossOriginKeyword: 'anonymous'}
                    }
                );
                var scale_topo_googleP = new OpenLayers.Layer.XYZ(
                    "NZ Topographic Maps",
                    "https://mt1.google.com/vt/lyrs=p&hl=en&x=\$\{x\}&y=\$\{y\}&z=\$\{z\}" +
                    "&key=AIzaSyBj_MCLMBKMcNvUXelP9pfEmlCsHN_nbX0",
                    {
                        wrapDateLine: true,
                        projection: p900913,
                        sphericalMercator: true
                    },
                    {
                        displayInLayerSwitcher: false,
                        tileOptions: {crossOriginKeyword: 'anonymous'}
                    }
                );

                linzTopo250 = new OpenLayers.Layer.WMS(
                    "LINZ Topo 250",
                    esri_url + "/basemaps/topo250/ImageServer/WmsServer",
                    {layers: "topo250", transparent: false, tiled: true, srs: "EPSG:3857", minZoomLevel: 0, maxZoomLevel: 4 },
                    {displayInLayerSwitcher: false, isBaseLayer: false, visibility: true, projection: p3857, wrapDateLine: true}
                );
                var linzTopo250_osm = new OpenLayers.Layer.OSM("LINZ Topo 250");

                linzTopo50 = new OpenLayers.Layer.WMS(
                    "LINZ Topo 50",
                    esri_url + "/basemaps/topo50/ImageServer/WmsServer",
                    {layers: "topo50", transparent: false, tiled: true, srs: "EPSG:3857", minZoomLevel: 0, maxZoomLevel: 4 },
                    {displayInLayerSwitcher: false, isBaseLayer: false, visibility: true, projection: p3857, wrapDateLine: true}
                );    
                var linzTopo50_osm = new OpenLayers.Layer.OSM("LINZ Topo 50");    

                var osm = new OpenLayers.Layer.OSM("Open Street Map");    

                linzTopo50_osm = new OpenLayers.Layer.OSM("LINZ Topo 50");    
                linzTopo250_osm = new OpenLayers.Layer.OSM("LINZ Topo 250");

                oneGeolNZ = new OpenLayers.Layer.WMS(
                    "New Zealand Geology",
                    geology_url,
                    {layers: "gns:NZL_GNS_1M_Lithostratigraphy", transparent: true, tiled: true, srs: "EPSG:900913"},
                    {displayInLayerSwitcher: true, isBaseLayer: false, visibility: false, projection: p900913, opacity: 0.6, wrapDateLine: true}
                );

                dtm = new OpenLayers.Layer.WMS(
                    "New Zealand DTM",
                    esri_url +"/basemaps/nzdtm_shade/MapServer/WmsServer",
                    {layers: "NZ_DTM_SHADE", transparent: true, tiled: true, srs: "EPSG:3857"},
                    {displayInLayerSwitcher: true, isBaseLayer: false, visibility: false, projection: p3857, wrapDateLine: true}
                );

                //has been removed, maybe replaced in the future
                /*ortho = new OpenLayers.Layer.WMS(
                    "New Zealand Orthophotos",
                     geoserver_url,
                    {layers: "gns:GIS.NZ_ORTHOPHOTO", transparent: true, tiled: true, srs: "EPSG:900913"},
                    {displayInLayerSwitcher: true, isBaseLayer: false, visibility: false, projection: p900913, wrapDateLine: true}
                );*/

                master = new OpenLayers.Layer.WMS(
                    "Masterfile areas",
                     geoserver_url,
                    {layers: "gns:MASTERFILE_AREAS", transparent: true, tiled: true, srs: "EPSG:900913", minZoomLevel: 0, maxZoomLevel: 4 },
                    {displayInLayerSwitcher: true, isBaseLayer: false, visibility: true, projection: p900913, wrapDateLine: true,
                     maxScale: 2500000, numZoomLevels: 4}
                );    

                fredLayer = new OpenLayers.Layer.WMS(
                    "FRED samples",
                     geoserver_url,
                    {layers: "gns:pg_fred_site_view", transparent: true, tiled: true, srs: "EPSG:900913", minZoomLevel: 0, maxZoomLevel: 4 },
                    {displayInLayerSwitcher: true, isBaseLayer: false, visibility: true, projection: p900913, wrapDateLine: true}
                );    

                var pointSelectControl = new OpenLayers.Control.WMSGetFeatureInfo({
                    infoFormat: "application/vnd.ogc.gml",
                    maxFeatures: 100,
                    layers: [fredLayer]
                });


                // Layer for polygon vertices
                markers = new OpenLayers.Layer.Markers( "Search Polygon",{'displayInLayerSwitcher': false});

                /*
                 * Layer style
                 */
                // we want opaque external graphics and non-opaque internal graphics
                var layer_style = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
                layer_style.fillOpacity = 0.2;
                layer_style.graphicOpacity = 0.5;


                /*
                 * Blue style
                 */
                var style_blue = OpenLayers.Util.extend({}, layer_style);
                style_blue.strokeColor = "blue";
                style_blue.fillColor = "blue";

                /*
                 * Red style
                 */
                style_red = {
                    strokeColor: "#FF0000",
                    strokeOpacity: 0.5,
                    strokeWidth: 5,
                    pointRadius: 6,
                    pointerEvents: "visiblePainted"
                };

                vectorLayer = new OpenLayers.Layer.Vector("Simple Geometry", {style: layer_style, 'displayInLayerSwitcher': false});

                map.addLayers([googleH, googleP, scale_topo_googleP, linzTopo50, linzTopo250, dtm, oneGeolNZ, 
                    fredLayer, master, vectorLayer, markers]);

                
                var googleCentre = new OpenLayers.LonLat(174, -41).transform(
                    new OpenLayers.Projection("EPSG:4272"), 
                    new OpenLayers.Projection("EPSG:900913"));
                map.setCenter(googleCentre, 5);
            
                AutoSizeAnchored = OpenLayers.Class(OpenLayers.Popup.Anchored, {
                    'autoSize': true
                });
            
                map.events.register("click", map, addVertex);

                mapPanel = new GeoExt.MapPanel({
                    region: 'center',
                    height: 500,
                    width: 800,
                    map: map
                });
                    
                oneGeolNZ.resolutions =  googleH.resolutions;    
                
                map.setCenter(new OpenLayers.LonLat(174, -41).transform(p4272, p900913), 5);
            };
            
            function addMarker(ll, popupClass, popupContentHTML, closeBox, overflow) {
                var size = new OpenLayers.Size(10,10);
                var offset = new OpenLayers.Pixel(-5, -5);
                var feature = new OpenLayers.Feature(markers, ll);
                feature.closeBox = closeBox;
                feature.popupClass = popupClass;
                feature.data.popupContentHTML = popupContentHTML;
                feature.data.overflow = (overflow) ? "auto" : "hidden";

                var marker = feature.createMarker();
                marker.icon = new OpenLayers.Icon('./plugin/OpenLayers-2.13.1/img/marker.png',size,offset);

                markers.addMarker(marker);
            }    
                
            function mapBaseLayerChanged(event) {
                
                if(event.layer.name != "NZ Topographic Maps")    {
                    linzTopo50.setVisibility(false);
                    linzTopo250.setVisibility(false);
                }
            }
            
            function changeBaseLayerBasedOnZoom(event)    {
                    
                var curScale = map.getScale();
                
                if(map.baseLayer.name == "NZ Topographic Maps")    {
                    if(curScale > 500000)    {
                        linzTopo50.setVisibility(false);
                        linzTopo250.setVisibility(false);
                    } 
                    else if(curScale > 50000)    {
                        
                        linzTopo50.setVisibility(false);
                        linzTopo250.setVisibility(true);
                    }
                    else    {
                        linzTopo250.setVisibility(false);
                        linzTopo50.setVisibility(true);
                    }
                }                
            }
                
            function toggleLayers()    {
                state = checker;
                checker = !checker;
                if(state == false)    {//switch all on    
                    //alert("switching all on");
                    oneGeolNZ.setVisibility(true);
                    fredLayer.setVisibility(true);
                    resultLayer.setVisibility(true);
                }
                else    {
                    //alert("switching all off");
                    oneGeolNZ.setVisibility(false);
                    fredLayer.setVisibility(false);
                    resultLayer.setVisibility(false);
                }
                //alert("refreshed yet?");
            }

            /*
             * Creates & submits WFS filter query. 
             * Ajax response updates content of 'wfsResponse' DOM element
             */
            function getFeatureList()
            {
                if (true)
                {
                    var url =     "<%=request.getContextPath()%>/wfsProxy?" + 
                        "request=GetFeature&" +
                        "service=wfs&" +
                        "version=1.0.0&" +
                        "typename=gns:pg_fred_site_view&" +
                        "outputFormat=GML2&" +
                        "PropertyName=feature_id&" +
                        "filter=<Filter " + 
                        "xmlns:gml='http://www.opengis.net/gml'>" + 
                        "<Intersects><PropertyName>shape</PropertyName>" +
                        "<gml:Polygon srsName='EPSG:4326'><gml:outerBoundaryIs><gml:LinearRing>" +
                        "<gml:coordinates>" + createPolygon() + "</gml:coordinates>" +
                        "</gml:LinearRing></gml:outerBoundaryIs></gml:Polygon></Intersects></Filter>";    
                                
                    sndWFSRequest(url,'');
                }        
            }
            
            /*
             * Sets a vertex where user has clicked in the map. Coordinates are stored in current map projection,
             * i.e. not necessarily in geographic lat/lon.
             */
            function addVertex(e)    {

                var lonlat = map.getLonLatFromViewPortPx(e.xy);
                var bounds = map.getExtent().toBBOX();

                //if (num_markers == 25) return;
                ll = new OpenLayers.LonLat(lonlat.lon,lonlat.lat);
                popupClass = AutoSizeAnchored;
                popupContentHTML = 1;
                addMarker(ll, popupClass, popupContentHTML);
                num_markers++;

                var magic=6378137;
                var deg2rad=0.017453292519943295;
                var pi=3.141592653589793;
                londd=lonlat.lon/(magic*deg2rad);
                latdd=(2*Math.atan(Math.exp(lonlat.lat/magic))-(pi/2))/deg2rad;

                marker[num_markers] = new OpenLayers.Geometry.Point(lonlat.lon, lonlat.lat);

                mylons[num_markers]=lonlat.lon;
                mylats[num_markers]=lonlat.lat;

                if (num_markers > 2){
                    vectorLayer.eraseFeatures(lineFeature);
                    vectorLayer.removeFeatures(lineFeature);
                    vectorLayer.destroyFeatures(lineFeature);
                }

                if (num_markers > 1){
                    var pointList = [];
                    for(var p=1; p<=num_markers; ++p) {
                        newPoint = marker[p];
                        pointList.push(newPoint);
                    }
                    newPoint = marker[1];
                    pointList.push(newPoint);
                    lineFeature = new OpenLayers.Feature.Vector(
                        new OpenLayers.Geometry.LineString(pointList),null,style_red);
                    vectorLayer.addFeatures(lineFeature);
                }                
            }
            
            /*
             * Creates a space-separated list of coordinates (x,y), representing the polygon in geographic coordinates.
             */
            function createPolygon()    {
                var poly = '';

                //build a polygon by iterating through vertices
                for(var p=1; p<=num_markers; ++p) {
                    poly += lonToDD(mylons[p]);
                    poly += ',';
                    poly += latToDD(mylats[p]);
                    poly += ' ';
                }
                //close polygon
                poly += lonToDD(mylons[1]);
                poly += ',';
                poly += latToDD(mylats[1]);
                
                return poly;
            }
            
            /*
             * Clears the polygon.
             */
            function clearPolygon(){
                for(var p=1; p<=num_markers; ++p) {
                    mylons[p]='';
                    mylats[p]='';
                }
                markers.clearMarkers();
                vectorLayer.eraseFeatures(lineFeature);
                vectorLayer.removeFeatures(lineFeature);
                vectorLayer.destroyFeatures(lineFeature);
            
                document.getElementById('wfsResponse').innerHTML = '';
                num_markers = 0;
            }

            /*
             * Converts coordinate into decimal degree.
             */
            function latToDD(srcLat)    {

                var magic=6378137;
                var deg2rad=0.017453292519943295;
                var pi=3.141592653589793;
                latdd=(2*Math.atan(Math.exp(srcLat/magic))-(pi/2))/deg2rad;
                return latdd;
            }

            function lonToDD(srcLon)    {
                var magic=6378137;
                var deg2rad=0.017453292519943295;
                var pi=3.141592653589793;
                londd=srcLon/(magic*deg2rad);    
                return londd;
            }
            
            /*
             * Sets alement values of the parent window & closes the popup window.
             */
            function returnToParent(commaString)    {
            
                var idList;

                if(commaString.length > 0)    
                    idList = commaString.split(",");
                else 
                    idList = new Array();
                    
                //alert("Returning to parent");
                if(idList== null || idList.length == 0)    {
                    alert("The selected polygon does not contain any features.");
                    clearPolygon();
                }
                else    {
            
                    var doc = window.opener.document;
                    doc.getElementById('idList').value = commaString;
                    doc.getElementById('polygon').value = createPolygon();

                    var statementDiv = doc.getElementById("isPolygon");
                    statementDiv.innerHTML = "Features selected: " + idList.length;
                                        
                    window.close();    
                    
                }
            }
            
            function cancel()    {
                var doc = window.opener.document;
                doc.getElementById('idList').value = '';
                doc.getElementById('polygon').value = '';

                var statementDiv = doc.getElementById("isPolygon");
                statementDiv.innerHTML = "No features selected.";
                window.close();    
            }
        
            window.onload = go;
    
        </script>

    </head>
    <body>

        <div id="loading"></div>
        <table border="0" cellpadding="3" cellspacing="2" width="960">
            <tr class="midColour"><th colspan="2">Polygon definiton</th></tr>
            <tr>
                <td>
                    <div id="map" style="width: 800px; height: 500px"></div>
                </td>
            </tr>

            <tr>
                <td colspan="2">&nbsp;
                    <div id="wfsResponse"/>
                </td>
            </tr>

            <tr>
                <td colspan="2">
                    <div style="float:left;padding-left:50px;">
                        <input type="button" value="Submit" onClick="getFeatureList();">
                    </div>
                    <div style="float:left;padding-left:50px;">
                        <input type="button" value="Cancel" onClick="cancel();">
                    </div>
                    <div style="float:left;padding-left:50px;">
                        <button onclick="clearPolygon();">Clear</button>
                    </div>
                </td>
            </tr>
            <tr><td colspan="2">&nbsp;</td></tr>
            <tr>
                <td colspan="2">
                    <div id="contentWrapInner">
                        Use the scale bar on the left to zoom in. Drag to pan. Shift-drag to zoom to a particular region.
                    </div>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <div id="contentWrapInner">
                        Left-click in the map to draw a polygon to define the region of interest.
                    </div>
                </td>
            </tr>
        </table>
    </body>
</html>
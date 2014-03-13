<link rel="stylesheet" href="http://maps.gns.cri.nz/ext-2.2.1/resources/css/ext-all.css" type="text/css" />
<link rel="stylesheet" href="http://maps.gns.cri.nz/GeoExt/resources/css/geoext-all.css" type="text/css" />
	
<script src="/online/scripts/ajax.js"></script>
<script src="/online/scripts/fade.js"></script>
<script src="/online/scripts/locate.js"></script>
<script src="/online/scripts/contextHint.js"></script>
	
<script type="text/javascript" language="javascript" src="http://maps.gns.cri.nz/scripts/ext-base.js"></script>
<script type="text/javascript" language="javascript" src="http://maps.gns.cri.nz/scripts/ext-all.js"></script>
<script type="text/javascript" language="javascript" src="http://maps.gns.cri.nz/scripts/OpenLayers-2.11/OpenLayers.js"></script>
<script type="text/javascript" language="javascript" src="http://maps.gns.cri.nz/scripts/GeoExt.js"></script>
<script type="text/javascript" language="javascript" src="http://maps.gns.cri.nz/scripts/proj4js-compressed.js"></script>
<script type="text/javascript" language="javascript" src="http://maps.gns.cri.nz/scripts/EPSG4272.js"></script>
<script type="text/javascript" language="javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>

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
	
		var p4326 = new OpenLayers.Projection("EPSG:4326");
		var p4272 = new OpenLayers.Projection("EPSG:4272");
		var p900913 = new OpenLayers.Projection("EPSG:900913");
		var selectedLayer;
		var petlabLayer;
		var popup;
		var tree;
		var map;	
		var checker = true;
        
        var geoserver_url = "http://maps.gns.cri.nz/geoserver/wms";
		
		function go() {
			
			// custom layer node UI class
	        var LayerNodeUI = Ext.extend(
	            GeoExt.tree.LayerNodeUI,
	            new GeoExt.tree.TreeNodeUIEventMixin()
	        );
	        OpenLayers.Map.prototype.Z_INDEX_BASE.Control = 1500;
			map = new OpenLayers.Map("map", {
				controls: [
					new OpenLayers.Control.Navigation(),
	                new OpenLayers.Control.PanZoomBar({position: new OpenLayers.Pixel(2, 15)}),
	                new OpenLayers.Control.ScaleLine({position: new OpenLayers.Pixel(630, 450)}),
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
			
			var googleH = new OpenLayers.Layer.Google(
				"Google Hybrid",
				{type: google.maps.MapTypeId.SATELLITE, sphericalMercator: true, numZoomLevels: 20},
				{displayInLayerSwitcher: false, visibility: true}
			);

			var googleP = new OpenLayers.Layer.Google(
				"Google Physical",
				{type: google.maps.MapTypeId.TERRAIN, sphericalMercator: true, numZoomLevels: 20},
				{displayInLayerSwitcher: false, visibility: false}
			);
			
			var scale_topo_googleP = new OpenLayers.Layer.Google(
					"NZ Topographic Maps",
					{type: google.maps.MapTypeId.TERRAIN, sphericalMercator: true, numZoomLevels: 20},
					{displayInLayerSwitcher: false, visibility: false, minScale: 100000000, maxScale: 1000000}
				);

			linzTopo250 = new OpenLayers.Layer.WMS(
					"LINZ Topo 250",
					"http://wms.data.linz.govt.nz/2196be5e2a3f48179fddb966dd15add4/r/wms",
					{layers: "r:x798", transparent: false, srs: "EPSG:900913", tiled: true, minZoomLevel: 0, maxZoomLevel: 4 },
					{displayInLayerSwitcher: false, isBaseLayer: false, visibility: true, projection: p900913, wrapDateLine: true}
			);
			var linzTopo250_osm = new OpenLayers.Layer.OSM("LINZ Topo 250");

			linzTopo50 = new OpenLayers.Layer.WMS(
					"LINZ Topo 50",
					"http://wms.data.linz.govt.nz/2196be5e2a3f48179fddb966dd15add4/r/wms",
					{layers: "r:x767", transparent: false, srs: "EPSG:900913", tiled: true, minZoomLevel: 0, maxZoomLevel: 4 },
					{displayInLayerSwitcher: false, isBaseLayer: false, visibility: true, projection: p900913, wrapDateLine: true}
			);	
			var linzTopo50_osm = new OpenLayers.Layer.OSM("LINZ Topo 50");	
						
			var osm = new OpenLayers.Layer.OSM("Open Street Map");	
				
				linzTopo50_osm = new OpenLayers.Layer.OSM("LINZ Topo 50");	
				linzTopo250_osm = new OpenLayers.Layer.OSM("LINZ Topo 250");
	
			oneGeolNZ = new OpenLayers.Layer.WMS(
					"New Zealand Geology",
					geoserver_url,
					{layers: "gns:NZL_GNS_1M_Lithostratigraphy", transparent: true, tiled: true, srs: "EPSG:900913"},
					{displayInLayerSwitcher: true, isBaseLayer: false, visibility: false, projection: p900913, opacity: 0.6, wrapDateLine: true}
			);
			
			dtm = new OpenLayers.Layer.WMS(
					"New Zealand DTM",
					geoserver_url,
					{layers: "gns:GIS.NZ_DTM_SHADE", transparent: true, tiled: true, srs: "EPSG:900913"},
					{displayInLayerSwitcher: true, isBaseLayer: false, visibility: false, projection: p900913, wrapDateLine: true}
			);
			
			ortho = new OpenLayers.Layer.WMS(
					"New Zealand Orthophotos",
					geoserver_url,
					{layers: "gns:GIS.NZ_ORTHOPHOTO", transparent: true, tiled: true, srs: "EPSG:900913"},
					{displayInLayerSwitcher: true, isBaseLayer: false, visibility: false, projection: p900913, wrapDateLine: true}
			);
			
			master = new OpenLayers.Layer.WMS(
					"Masterfile areas",
					geoserver_url,
					{layers: "gns:MASTERFILE_AREAS_SHP", transparent: true, tiled: true, srs: "EPSG:900913", minZoomLevel: 0, maxZoomLevel: 4 },
					{displayInLayerSwitcher: true, isBaseLayer: false, visibility: true, projection: p900913, wrapDateLine: true, 
                     maxScale: 2500000, numZoomLevels: 4}
			);	
				
			fred = new OpenLayers.Layer.WMS(
					"FRED samples",
					geoserver_url,
					{layers: "gns:FR.FRED_SITE_VIEW", transparent: true, tiled: true, srs: "EPSG:900913", minZoomLevel: 0, maxZoomLevel: 4 },
					{displayInLayerSwitcher: true, isBaseLayer: false, visibility: true, projection: p900913, wrapDateLine: true}
			);	
						
			var pointSelectControl = new OpenLayers.Control.WMSGetFeatureInfo({
				infoFormat: "application/vnd.ogc.gml",
				maxFeatures: 100,
				layers: [fred]
			});		
			
			pointSelectControl.events.register("getfeatureinfo", this, wmsSelect);
			map.addControl(pointSelectControl);				
			pointSelectControl.activate();	
				
			map.addLayers([googleH, googleP, linzTopo50, linzTopo250, dtm, ortho, oneGeolNZ, fred, master]);    //removed for now, LINZ layer stopped working: , scale_topo_googleP
			
			map.setCenter(new OpenLayers.LonLat(174, -41).transform(p4326, p900913), 4);
				
			mapPanel = new GeoExt.MapPanel({
				region: 'center',
				height: 500,
				width: 800,
				map: map
			});
								
			oneGeolNZ.resolutions =  googleH.resolutions;	
			fred.resolutions = googleH.resolutions;
			
		};
		
		function mapBaseLayerChanged(event) {
			
			if(event.layer.name != "NZ Topographic Maps")	{
				linzTopo50.setVisibility(false);
				linzTopo250.setVisibility(false);
			}
		}
		
		function changeBaseLayerBasedOnZoom(event)	{
				
			var curScale = map.getScale();
			
			if(map.baseLayer.name == "NZ Topographic Maps")	{
				if(curScale > 500000)	{
					linzTopo50.setVisibility(false);
					linzTopo250.setVisibility(false);
				} 
				else if(curScale > 50000)	{
					
					linzTopo50.setVisibility(false);
					linzTopo250.setVisibility(true);
				}
				else	{
					linzTopo250.setVisibility(false);
					linzTopo50.setVisibility(true);
				}
			}				
		}
			
		function toggleLayers()	{
			state = checker;
			checker = !checker;
			if(state == false)	{//switch all on	
				//alert("switching all on");
				oneGeolNZ.setVisibility(true);
				fred.setVisibility(true);
				resultLayer.setVisibility(true);
			}
			else	{
				//alert("switching all off");
				oneGeolNZ.setVisibility(false);
				fred.setVisibility(false);
				resultLayer.setVisibility(false);
			}
			//alert("refreshed yet?");
		}
		
			function selectFeature(feature) {

				destroyPopup();
				createPopup(feature, feature.attributes.collDate);
			}
		
			function unselectFeature(feature) {
				destroyPopup();
			}

			function wmsSelect(wmsRequest) {	//called in response to GetFeatureInfo request
			
				destroyPopup();
				var features = wmsRequest.features;
								
				if (features.length == 1) {
					//alert("wmsselect.features.1");
					var selectedFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(features[0].geometry.x, features[0].geometry.y), null, null);
					
					selectedFeature.attributes = { 
                        id: features[0].attributes["FEATURE_ID"], 
						number: features[0].attributes["FR_NUMBER"], 
						feature_type: features[0].attributes["FEATURE_TYPE"], 
						locality: features[0].attributes["LOCALITY"]};
					selectedFeature.geometry.transform(p4326, p900913);
					if (selectedLayer != null) {
						selectedLayer.addFeatures(selectedFeature);
					} 
					createPopup(selectedFeature);
		        }
				else if (features.length > 1) {
					//alert("wmsselect.features.n");
					var transformedFeatures = new Array();
					
					for(var i=0; i<features.length; i++)	{
						var selectedFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(features[i].geometry.x, features[i].geometry.y), null, null);
						selectedFeature.attributes = {
						id: features[0].attributes["FEATURE_ID"], 
                        number: features[i].attributes["FR_NUMBER"], 
						feature_type: features[i].attributes["FEATURE_TYPE"], 
						locality: features[i].attributes["LOCALITY"]};
						selectedFeature.geometry.transform(p4326, p900913);
						if (selectedLayer != null) {
							selectedLayer.addFeatures(selectedFeature);
						} 
						transformedFeatures.push(selectedFeature);
					}
					
					createMultiPopup(transformedFeatures);
		        }
			}

			function createPopup(feature) {
				
				var popupContents;
				popupContents = "<div>";
				popupContents += "<table border='0'>";
				popupContents += "<tr><td><b>Number:</b>&nbsp;</td><td><a href='javascript:loadDetails(" + feature.attributes.id + ")'>" + feature.attributes.number + "</a></td></tr>";
				popupContents += "<tr><td><b>Feature Type:</b>&nbsp;</td><td>" + feature.attributes.feature_type + "</td></tr>";
				popupContents += "<tr><td><b>Locality:</b>&nbsp;</td><td>" + feature.attributes.locality + "</td></tr>";	
				
				popupContents += "<tr><td colspan=\"2\">&nbsp;</td></tr>";
				popupContents += "</table>";
				popupContents += "</div>";
				if (feature.layer == null) {
					feature.layer = fred;
				} 
				
				popup = new GeoExt.Popup({
					title: "FRED Sample Locations Summary",
					feature: feature,
					location: feature,
					html: popupContents,
					width: 250,
					height: 150,
					collapsible: true,
					autoScroll: true
				});
				popup.show();
			}

			function createMultiPopup(features) {
							
				var popupContents;
				popupContents = "<div>";

				for(var i=0; i<features.length; i++)	{
					popupContents += "<table border='0'>";
					popupContents += "<tr><td><b>Number:</b>&nbsp;</td><td><a href='javascript:loadDetails(" + features[i].attributes.id + ")'>" + features[i].attributes.number + "</a></td></tr>";
					popupContents += "<tr><td><b>Feature Type:</b>&nbsp;</td><td>" + features[i].attributes.feature_type + "</td></tr>";
					popupContents += "<tr><td><b>Locality:</b>&nbsp;</td><td>" + features[i].attributes.locality + "</td></tr>";
										
					popupContents += "<tr><td colspan=\"2\">&nbsp;</td></tr>";
					popupContents += "</table>";
				}	
				
				if (features[0].layer == null) {
					features[0].layer = fred;
				} 
				popupContents += "</div>";
				
				popup = new GeoExt.Popup({
					title: "FRED Sample Locations Summary",
					feature: features[0],
					location: features[0],
					html: popupContents,
					width: 250,
					height: 150,
					collapsible: true,
					autoScroll: true
				});
				popup.show();
			}

			function destroyPopup() {
				if (popup != null) {
					popup.destroy();
				}
				if (selectedLayer != null) {
					selectedLayer.destroyFeatures();
				}
			}
			
			function loadDetails(feature_id)	{
				var url = "./detail.jsp?FeatID="+ feature_id;
                window.open(url,'_blank')
			}
		
		window.onload = go;
	
	</script>
		
		<div id="loading">
			<table border="0" cellpadding="3" cellspacing="2" width="900">
				<tr>
					<td><div id="map" style="width: 720px; height: 500px"></div></td>
				</tr>
			</table>
		</div>

	
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.site.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sitikond
 */
public class OrigCoordInfoUtil {

       
    public static class OrigCoordEpsgFormatDetail{
        int epsg;
        String format;
        
        private OrigCoordEpsgFormatDetail(int epsg, String format){
            this.epsg = epsg;
            this.format = format;
        }
    }
    
    private static final Map<Integer, OrigCoordEpsgFormatDetail> ORIGID_ORIGCOORD_LIST = createMap();
    
    private static Map<Integer, OrigCoordEpsgFormatDetail> createMap() {
        Map<Integer, OrigCoordEpsgFormatDetail> result = new HashMap<>();
        result.put(1, new OrigCoordEpsgFormatDetail(27219, "EN"));
        result.put(2, new OrigCoordEpsgFormatDetail(2119, "EN"));
        result.put(3, new OrigCoordEpsgFormatDetail(27232, "EN"));
        result.put(4, new OrigCoordEpsgFormatDetail(2132, "EN"));
        result.put(5, new OrigCoordEpsgFormatDetail(27217, "EN"));
        result.put(6, new OrigCoordEpsgFormatDetail(2117, "EN"));
        result.put(7, new OrigCoordEpsgFormatDetail(5519, "EN"));
        result.put(8, new OrigCoordEpsgFormatDetail(27214, "EN"));
        result.put(9, new OrigCoordEpsgFormatDetail(2114, "EN"));
        result.put(10, new OrigCoordEpsgFormatDetail(27105, "EN"));
        result.put(11, new OrigCoordEpsgFormatDetail(2105, "EN"));
        result.put(12, new OrigCoordEpsgFormatDetail(27225, "EN"));
        result.put(13, new OrigCoordEpsgFormatDetail(2125, "EN"));
        result.put(14, new OrigCoordEpsgFormatDetail(27218, "EN"));
        result.put(15, new OrigCoordEpsgFormatDetail(2118, "EN"));
        result.put(16, new OrigCoordEpsgFormatDetail(27200, "gridref"));
        result.put(17, new OrigCoordEpsgFormatDetail(27292, "gridref"));
        result.put(18, new OrigCoordEpsgFormatDetail(27208, "EN"));
        result.put(19, new OrigCoordEpsgFormatDetail(2108, "EN"));
        result.put(20, new OrigCoordEpsgFormatDetail(27221, "EN"));
        result.put(21, new OrigCoordEpsgFormatDetail(2121, "EN"));
        result.put(22, new OrigCoordEpsgFormatDetail(27223, "EN"));
        result.put(23, new OrigCoordEpsgFormatDetail(2123, "EN"));
        result.put(24, new OrigCoordEpsgFormatDetail(27216, "EN"));
        result.put(25, new OrigCoordEpsgFormatDetail(2116, "EN"));
        result.put(26, new OrigCoordEpsgFormatDetail(27227, "EN"));
        result.put(27, new OrigCoordEpsgFormatDetail(2127, "EN"));
        result.put(28, new OrigCoordEpsgFormatDetail(4167, "DD"));
        result.put(29, new OrigCoordEpsgFormatDetail(4272, "DD"));
        result.put(30, new OrigCoordEpsgFormatDetail(4673, "DD"));
        result.put(31, new OrigCoordEpsgFormatDetail(27220, "EN"));
        result.put(32, new OrigCoordEpsgFormatDetail(2120, "EN"));
        result.put(33, new OrigCoordEpsgFormatDetail(27292, "EN"));
        result.put(34, new OrigCoordEpsgFormatDetail(27215, "EN"));
        result.put(35, new OrigCoordEpsgFormatDetail(2115, "EN"));
        result.put(36, new OrigCoordEpsgFormatDetail(27228, "EN"));
        result.put(37, new OrigCoordEpsgFormatDetail(2128, "EN"));
        result.put(38, new OrigCoordEpsgFormatDetail(27200, "EN"));
        result.put(39, new OrigCoordEpsgFormatDetail(27230, "EN"));
        result.put(40, new OrigCoordEpsgFormatDetail(2130, "EN"));
        result.put(41, new OrigCoordEpsgFormatDetail(27222, "EN"));
        result.put(42, new OrigCoordEpsgFormatDetail(2122, "EN"));
        result.put(44, new OrigCoordEpsgFormatDetail(27224, "EN"));
        result.put(45, new OrigCoordEpsgFormatDetail(2124, "EN"));
        result.put(46, new OrigCoordEpsgFormatDetail(27206, "EN"));
        result.put(47, new OrigCoordEpsgFormatDetail(2106, "EN"));
        result.put(48, new OrigCoordEpsgFormatDetail(27207, "EN"));
        result.put(49, new OrigCoordEpsgFormatDetail(2107, "EN"));
        result.put(50, new OrigCoordEpsgFormatDetail(27231, "EN"));
        result.put(51, new OrigCoordEpsgFormatDetail(2131, "EN"));
        result.put(52, new OrigCoordEpsgFormatDetail(27209, "EN"));
        result.put(53, new OrigCoordEpsgFormatDetail(2109, "EN"));
        result.put(54, new OrigCoordEpsgFormatDetail(27226, "EN"));
        result.put(55, new OrigCoordEpsgFormatDetail(2126, "EN"));
        result.put(56, new OrigCoordEpsgFormatDetail(27210, "EN"));
        result.put(57, new OrigCoordEpsgFormatDetail(2110, "EN"));
        result.put(58, new OrigCoordEpsgFormatDetail(27212, "EN"));
        result.put(59, new OrigCoordEpsgFormatDetail(2112, "EN"));
        result.put(60, new OrigCoordEpsgFormatDetail(27211, "EN"));
        result.put(61, new OrigCoordEpsgFormatDetail(2111, "EN"));
        result.put(62, new OrigCoordEpsgFormatDetail(27213, "EN"));
        result.put(63, new OrigCoordEpsgFormatDetail(2113, "EN"));
        result.put(64, new OrigCoordEpsgFormatDetail(27229, "EN"));
        result.put(65, new OrigCoordEpsgFormatDetail(2129, "EN"));
        result.put(67, new OrigCoordEpsgFormatDetail(210001, "EN"));
        result.put(68, new OrigCoordEpsgFormatDetail(32359, "EN"));
        result.put(69, new OrigCoordEpsgFormatDetail(27291, "gridref"));
        result.put(70, new OrigCoordEpsgFormatDetail(27291, "EN"));
        result.put(73, new OrigCoordEpsgFormatDetail(4326, "DD"));
        result.put(71, new OrigCoordEpsgFormatDetail(2193, "EN"));
        result.put(72, new OrigCoordEpsgFormatDetail(2193, "gridref"));
        result.put(74, new OrigCoordEpsgFormatDetail(2982, "EN"));
        result.put(77, new OrigCoordEpsgFormatDetail(3788, "EN"));
        result.put(78, new OrigCoordEpsgFormatDetail(3789, "EN"));
        result.put(79, new OrigCoordEpsgFormatDetail(3793, "EN"));
        return Collections.unmodifiableMap(result);
    }
    
    public static class OrigCoord{

        
        private int epsg;
        private String format;
        private String gridref;
        private String latitude;
        private String longitude;
        private Double easting;
        private Double northing;
//        int epsg, String gridref, Double easting, Double northing, String latitude, String longitude, String format, String auditMsg

        public int getEpsg() {
            return epsg;
        }

        public String getFormat() {
            return format;
        }

        public String getGridref() {
            return gridref;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public Double getEasting() {
            return easting;
        }

        public Double getNorthing() {
            return northing;
        }

        public void setEpsg(int epsg) {
            this.epsg = epsg;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public void setGridref(String gridref) {
            this.gridref = gridref;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public void setEasting(Double easting) {
            this.easting = easting;
        }

        public void setNorthing(Double northing) {
            this.northing = northing;
        }      
        
        @Override
        public String toString() {
            return "OrigCoord{" + "epsg=" + getEpsg() + ","
                    + " format=" + getFormat() + ","
                    + " gridref=" + getGridref() + ","
                    + " latitude=" + getLatitude() + ","
                    + " longitude=" + getLongitude() + ","
                    + " easting=" + getEasting() + ", "
                    + " northing=" + getNorthing() + '}';
        }
    }
    
    public static OrigCoord getJson(int system_id, String origCoord) throws JsonProcessingException, IOException {
         
        JsonNode siteDetails = null;
        ObjectMapper mapper = new ObjectMapper();
        if (ORIGID_ORIGCOORD_LIST.containsKey(system_id)) {
            OrigCoordEpsgFormatDetail ocefd = ORIGID_ORIGCOORD_LIST.get(system_id);
            String parts[] = origCoord.split("\\|");
          String js = "{\"epsg\":" + ocefd.epsg  + ", \"format\":\"" + ocefd.format + "\", " ;
          switch (ocefd.format) {
              case "DD":
                  if (parts.length!=2) {
                      return null;
                  }
                  js += "\"latitude\":\"" + parts[0] + "\", \"longitude\":\"" + parts[1] + "\"";
                  break;
              case "gridref":
                  if (parts.length!=3) {
                      return null;
                  }
                  js += "\"gridref\":\"" + parts[0] + "/";
                  if (parts[1].endsWith("0") && parts[2].endsWith("0")) {
                     js += parts[1].substring(0,3) + parts[2].substring(0,3);
                  } else {
                     js += parts[1]+ parts[2];
                  }
                  js += "\"";
                  break;
              case "EN":
                  if (parts.length!=2) {
                      return null;
                  }
                  js += "\"easting\":" + parts[0] + ", \"northing\":" + parts[1];
                  break;             
          }
          js += "}";
          siteDetails = mapper.readTree(js);
          OrigCoord oc = mapper.treeToValue(siteDetails, OrigCoord.class);
          return oc;
        } else {
            return null;
        }  
//        return siteDetails;
    }
    
    
}

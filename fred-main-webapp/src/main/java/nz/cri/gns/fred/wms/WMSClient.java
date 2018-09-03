package nz.cri.gns.fred.wms;

import com.lowagie.text.Image;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/** This is used for the locality map as well as generating reports. Richard says that it is implemented this way
 * to get decent performance when generating large reports with many maps. Maps are (probably?) cached
 * near the web service.
 * 
 * @author sorenh, richardt
 */
public class WMSClient {
    private static final Logger log = Logger.getLogger(WMSClient.class.getName());
    
    public static URL generateMapURL(double xCentre, double yCentre, int distance, int width, int height, String layers) {
        String serverURL = null;
        String srs = null;
        try {
            if (layers.contains("topo50")) {
                serverURL = getArcGIS() + "services/basemaps/topo50/ImageServer/WmsServer";
                srs="&crs=EPSG:27200";
            } else if (layers.contains("topo250")) {
                serverURL = getArcGIS() + "services/basemaps/topo250/ImageServer/WmsServer";
                srs="&crs=EPSG:27200";
            } else {
                serverURL = getWMS();
                srs="&srs=EPSG:27200";
            }
            String wmsops= "?service=WMS&request=GetMap";
            String layer = "&layers=" + layers;
            String other = "&format=image/png";

            double aspectRatio = 4.0 / 3;
            double stretch = distance;
            double xShift = stretch * aspectRatio;
            double yShift = stretch;

            String bbox = "&bbox=" + (xCentre - xShift) + "," + (yCentre - yShift) + "," 
                    + (xCentre + xShift) + "," + (yCentre + yShift);

            return new URL(serverURL + wmsops+ layer + "&width=" + width + "&height=" + height + srs + other + bbox);
        } catch (MalformedURLException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String getMapURL(double xCentre, double yCentre, int distance, int width, int height, 
            String layers, String outDir) {
        URL url = generateMapURL(xCentre, yCentre, distance, width, height, layers);

        return processMap(url, width, height, outDir);
    }

    private static String processMap(URL url, int width, int height, String outDir) {
        Image image = null;
        
        try {
            Logger.getLogger(WMSClient.class.getName()).log(Level.INFO, url.toExternalForm());
            BufferedImage rawImage = ImageIO.read(url);
            final Graphics2D g = rawImage.createGraphics();
            Composite origComposite = g.getComposite();
            
            g.setColor(Color.BLACK);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            int middleX = (int) (width / 2)-9;
            int middleY = (int) (height / 2)-9;

            g.translate(middleX, middleY);

            //Draw marker
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g.setColor(Color.YELLOW);
            g.fill(new Ellipse2D.Double(0, 0, 20, 20));
            g.setComposite(origComposite);

            //Draw circle around market
            g.setColor(Color.BLACK);
            g.drawOval(0, 0, 20, 20);

            g.translate(-middleX, -middleY);
            File outFile = new File(outDir+getTempFileName());
            ImageIO.write(rawImage, "PNG", outFile);

            return outFile.getName();
        
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Could not retrieve map: "+url, ex);
        }
        return "/nomap.png";
    }
    
    private static String getTempFileName() {
        Random random = new Random();
        return "locmap"+random.nextInt()+ ".png";
    }
    
    public static String getBacklogMapURL(double left, double top, double right, double bottom, int width, int height) {
        try {
            Context env = (Context)new InitialContext().lookup("java:comp/env");        
            String serverURL = (String)env.lookup("FRED_WMS");
            String wms = "?request=Getmap";
            String layers = "&layers=gns:bluemarble,FR.BACKLOG_STATUS,MASTERFILE_AREAS";
            String styles = "&styles=,,fred_mfile_outlines";
            String other = "&srs=EPSG:27200&format=image%2Fpng";
            String bbox = "&bbox=" + left + "," + bottom + "," + right + "," + top;
            String size ="&width=480&height=580";
       
            return serverURL + wms + layers + styles + other + bbox + size;
            
        } catch (NamingException e) {
			Logger.getLogger(WMSClient.class.getName()).log(Level.SEVERE, null, e); 
            return "/nomap.png";
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
            return "/nomap.png";
        }
    }
    
    
    private static String getArcGIS() {
        try {
            Context env = (Context)new InitialContext().lookup("java:comp/env");        
            return (String)env.lookup("ESRI_AGS");
            
        } catch (NamingException e) {
			Logger.getLogger(WMSClient.class.getName()).log(Level.SEVERE, null, e); 
            return "noagsserver";
        } 
    }
    
    private static String getWFS() {
        try {
            Context env = (Context)new InitialContext().lookup("java:comp/env");        
            return (String)env.lookup("FRED_WFS");
            
        } catch (NamingException e) {
			Logger.getLogger(WMSClient.class.getName()).log(Level.SEVERE, null, e); 
            return "nowfsserver";
        } 
    }
    
    private static String getWMS() {
        try {
            Context env = (Context)new InitialContext().lookup("java:comp/env");        
            return (String)env.lookup("FRED_WMS");
            
        } catch (NamingException e) {
			Logger.getLogger(WMSClient.class.getName()).log(Level.SEVERE, null, e); 
            return "nowmsserver";
        } 
    }
    
    
}


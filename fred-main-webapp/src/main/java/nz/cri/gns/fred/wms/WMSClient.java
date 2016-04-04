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
import javax.swing.ImageIcon;

/**
 *
 * @author richardt
 */
public class WMSClient {

    private static final Logger log = Logger.getLogger(WMSClient.class.getName());
    
    public static URL generateMapURL(double xCentre, double yCentre, int distance, int width, int height, String layers) {
        String serverURL = null;
        try {
            if (layers.contains("-7")) {
                serverURL = "https://data.linz.govt.nz/services;key=2196be5e2a3f48179fddb966dd15add4/wms?SERVICE=WMS&request=GetMap";
                    //"http://wms.data.linz.govt.nz/2196be5e2a3f48179fddb966dd15add4/r/wms?service=WMS&request=GetMap";
                    //maps.gns.cri.nz/mapserver?SERVICE=WMS&version=1.1.0&request=GetMap
                    //&layers=x767
                    //&srs=EPSG%3A27200&bbox=2692000,6042200,2697000,6047200&width=620&height=500&format=image%2Fjpeg
            } else {
                serverURL = "https://maps.gns.cri.nz/geoserver/wms?service=WMS&request=GetMap";
            //&layers=gns:FR.FRED_SITE_VIEW
                //&srs=EPSG%3A27200&bbox=2692000,6042200,2697000,6047200&width=620&height=500&format=image%2Fjpeg
            }
            String layer = "&layers=" + layers;
            String other = "&srs=EPSG:27200&format=image/png";

            double aspectRatio = 4.0 / 3;
            double stretch = distance;
            double xShift = stretch * aspectRatio;
            double yShift = stretch;

            String bbox = "&bbox=" + (xCentre - xShift) + "," + (yCentre - yShift) + "," + (xCentre + xShift) + "," + (yCentre + yShift);

            return new URL(serverURL + layer + "&width=" + width + "&height=" + height + other + bbox);
        } catch (MalformedURLException ex) {
            Logger.getLogger(WMSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String getMapURL(double xCentre, double yCentre, int distance, int width, int height, String layers, String outDir) {
        URL url = generateMapURL(xCentre, yCentre, distance, width, height, layers);

        return processMap(url, width, height, outDir);
    }

    private static String processMap(URL url, int width, int height, String outDir) {
        Image image = null;
        log.log(Level.INFO, "Map image outDir: {0}", outDir);
        try {
            log.log(Level.INFO, "Reading image from: {0}", url);
            BufferedImage rawImage = (BufferedImage) new ImageIcon(url).getImage();
            final Graphics2D g = rawImage.createGraphics();
            Composite origComposite = g.getComposite();
            log.log(Level.INFO, "Finished reading map image from: {0}", url);
            
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
            log.log(Level.INFO, "Writing image to: {0}", outFile.getAbsolutePath());
            ImageIO.write(rawImage, "PNG", outFile);
            log.log(Level.INFO, "Image written to: {0}", outFile.getAbsolutePath());
            return outFile.getName();

        
        } catch (Exception ex) {
            Logger.getLogger(WMSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "/nomap.png";
    }
    
    private static String getTempFileName() {
        Random random = new Random();
        return "locmap"+random.nextInt()+ ".png";
    }
    
    public static String getBacklogMapURL(double left, double top, double right, double bottom, int width, int height) {
        try {
            String serverURL = "https://maps.gns.cri.nz/geoserver/wms?request=Getmap";
            String layers = "&layers=gns:bluemarble,FR.BACKLOG_STATUS,MASTERFILE_AREAS";
            String styles = "&styles=,,fred_mfile_outlines";
            String other = "&srs=EPSG:27200&format=image%2Fpng";
            String bbox = "&bbox=" + left + "," + bottom + "," + right + "," + top;
            String size ="&width=480&height=580";
       
            return serverURL + layers + styles + other + bbox + size;
            
        } catch (Exception ex) {
            Logger.getLogger(WMSClient.class.getName()).log(Level.SEVERE, null, ex);
            return "/nomap.png";
        }
    }
}

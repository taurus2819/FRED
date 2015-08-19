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

/**
 *
 * @author richardt
 */
public class WMSClient {

    public static URL generateMapURL(double xCentre, double yCentre, int distance, int width, int height, String layers) {
        String serverURL = null;
        try {
            if (layers.contains("x7")) {
                serverURL = "http://wms.data.linz.govt.nz/2196be5e2a3f48179fddb966dd15add4/r/wms?service=WMS&request=GetMap";
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

        try {

            BufferedImage rawImage = ImageIO.read(url);
            final Graphics2D g = rawImage.createGraphics();
            Composite origComposite = g.getComposite();
            
            g.setColor(Color.BLACK);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            int middleX = (int) (width / 2);
            int middleY = (int) (height / 2);

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
            Logger.getLogger(WMSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "/nomap.png";
    }
    
    private static String getTempFileName() {
        Random random = new Random();
        return "locmap"+random.nextInt()+ ".png";
    }
}

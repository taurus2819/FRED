package nz.cri.gns.fred.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;

/**
 * Util class to handle special characters conversions, cleansing, etc.
 *
 * This will be used for web pages and generated reports.
 *
 * Jsoup references: 
 * https://www.w3schools.com/charsets/ref_html_ascii.asp
 * https://www.codota.com/code/java/methods/org.jsoup.nodes.Document$OutputSettings/charset
 *
 *
 * @author ercilla
 */
public class SpecialCharactersFormatting {

    private static String lineBreakDelimeter = "\n";

    /**
     * Cleanup a line of text.
     * @param input
     * @return 
     */
    public String getText(String input) {

        String formattedText = "";

        if (input != null) {
            String temp = replaceUserEncodedHTMLCodes(input);
            Document doc = Jsoup.parse(temp);
            doc.outputSettings().charset("ASCII");
            doc.outputSettings().escapeMode(EscapeMode.xhtml);

            // This bit works in DEV and UAT env (linux).
            // I'll remove this block if the doc.text() below works.
            //            formattedText = doc.body().html();
            
            formattedText = doc.text();
            
        }
        return formattedText;

    }

    /**
     * Cleanup paragraphs.   Jsoup trims line breaks (\n) , so we will process line by line, and manually add the line break after the special characters conversions.
     * @param input
     * @return 
     */
    public String getHTML(String input) {

        String[] linesOfText;
        String formattedText = "";

        if (input != null) {
        
            linesOfText = input.split(lineBreakDelimeter);

            for (int i = 0; i < linesOfText.length; i++) {

                String temp = replaceUserEncodedHTMLCodes(linesOfText[i]);

                Document doc = Jsoup.parse(temp);
                // We can use ISO-8859-1, but ASCII works fine.
                doc.outputSettings().charset("ASCII");  
                doc.outputSettings().escapeMode(EscapeMode.xhtml);

                formattedText += doc.html() + "<br/>";
            }
        }
       return formattedText;

    }


/**
 *  If the user manually insert HTML code, somehow the ampersand is being encoded as -->  &amp;amp;
*    replacing all   "&amp;"    as   "&"  to properly interpret HTML code.
 * @param input
 * @return 
 */    
    private String replaceUserEncodedHTMLCodes(String input) {
        
        input = input.replaceAll("&amp;", "&");  
       
        return input;
    }

    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.util.test;

import nz.cri.gns.fred.util.SpecialCharactersFormatting;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author ercilla
 */
public class SpecialCharactersFormattingTest {

    @Test
    public void testGetTextForMacrons() {

        SpecialCharactersFormatting specialCharactersFormatting = new SpecialCharactersFormatting();

        String a1 = specialCharactersFormatting.getText("ā");
        CharSequence expectedResult_a = "ā";
         assertTrue(a1.contains(expectedResult_a));

        String e1 = specialCharactersFormatting.getText("ē");
        CharSequence expectedResult_e = "ē";
        assertTrue(e1.contains(expectedResult_e));
        
        String i1 = specialCharactersFormatting.getText("ī");
        CharSequence expectedResult_i = "ī";
        assertTrue(i1.contains(expectedResult_i));
        
        String o1 = specialCharactersFormatting.getText("ō");
        CharSequence expectedResult_o = "ō";
        assertTrue(o1.contains(expectedResult_o));
        
        String u1 = specialCharactersFormatting.getText("ū");
        CharSequence expectedResult_u = "ū";
        assertTrue(u1.contains(expectedResult_u));

    }

    @Test
    public void testGetTextForSymbols() {

        SpecialCharactersFormatting specialCharactersFormatting = new SpecialCharactersFormatting();

        // plus
        String plus1 = specialCharactersFormatting.getText("+");
        CharSequence expectedResult_plus = "+";
        assertTrue(plus1.contains(expectedResult_plus));

        // equality sign
        String equality1 = specialCharactersFormatting.getText("=");
        CharSequence expectedResult_equality = "=";
        assertTrue(equality1.contains(expectedResult_equality));

        // ampersand
        String ampersand1 = specialCharactersFormatting.getText("&");
        CharSequence expectedResult_ampersand = "&";
        assertTrue(ampersand1.contains(expectedResult_ampersand));
        
        // double quote
        String doubleQuote1 = specialCharactersFormatting.getText("\"");
        CharSequence expectedResult_doubleQuote = "\"";
        assertTrue(doubleQuote1.contains(expectedResult_doubleQuote));
        
        // single quote
        String singleQuote1 = specialCharactersFormatting.getText("\'");
        CharSequence expectedResult_singleQuote = "\'";
         assertTrue(singleQuote1.contains(expectedResult_singleQuote));
        
    }


}

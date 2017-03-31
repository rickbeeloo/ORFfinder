/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.Blast;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import org.biojava.nbio.core.search.io.Hit;
import org.biojava.nbio.core.search.io.Result;
import org.biojava.nbio.core.search.io.blast.BlastXMLParser;

/**
 * Deze class is verantwoordelijk voor het parse van de BLAST resultaten.
 * @author projectgroep 12
 */
public class BlastParser {
    
    //instantie variabele
    private File xmlInputFile;
    private double maxEValue;
    private List<Result> results;
    
    /**
     * Constructor
     * @param FilexmlInput Het XML bestand dat ingeladen moet worden
     * @param eValCutOff De E-value cut-off.
     */
    public BlastParser(File FilexmlInput, double eValCutOff) {
       xmlInputFile = FilexmlInput;
       maxEValue = eValCutOff;
    }
    
    /**
     * Deze methode parsed de resultaten in het XML bestand waarin de BLAST 
     * resultaten zijn opgeslagen. 
     */
    public void parse() {
        try {
            BlastXMLParser parser = new BlastXMLParser();
            parser.setFile(xmlInputFile);
            results = parser.createObjects(maxEValue);
        } catch (IOException ex) {
            showError("Cannot open XML file");
        } catch (ParseException ex) {
            showError("Wrong XML format");
        }
    }
    
    /**
     * Deze methode haalt alle top x hits op uit het XML bestand. Deze top Hit 
     * objecten worden opgeslagen in een ArryList en geretouneerd. 
     * @param top Het aantal top hits dat weergegeven moet worden
     * @return  retouneert een ArrayList met x aantal Hit objecten.
     */
    public ArrayList<Hit> getTopHits(int top) {
        ArrayList<Hit> hits = new ArrayList<>();
        Result res = results.get(0);
        Iterator<Hit> iter = res.iterator();
        int counter = 0;
        while (iter.hasNext() && counter < top) {
            Hit hit = iter.next();
            hits.add(hit);            
            counter++;
        } 
        return hits;
    }
    
    /**
     * Deze methode laat een Error pop-up zien met daarin het meegegeven bericht.
     * @param mssg Het bericht dat weergegeven moet worden in de pop-up.
     */
    private static void showError(String mssg) {
        JOptionPane.showMessageDialog(null, mssg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
    
}

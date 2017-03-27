/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.ORFSearching;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.transcription.Frame;

/**
 * Deze class zoekt ORF's in de ingegeven sequentie.
 * @author RICK
 */

public class ORFFinder {
    
    //class variabele
    private static final String STOP = "*";
    //private static final Pattern MY_PATTERN = Pattern.compile("(\\*.+?\\*)"); //kan gebruikt worden voor het zoeken tussen stop codons
    private static final Pattern PATTERN = Pattern.compile("(M.+?\\*)");   //zoek tussen M en stop codons
   
    //instantie variabele
    private String targetSeq;
    private ArrayList<ORFSequence> ORFs;
    private int minLen;
    private Frame ORFFrame;
    
    public ORFFinder(String seq, int minORFlen, Frame frame) {
        targetSeq = seq;
        ORFs = new ArrayList<>();
        minLen = minORFlen;   
        ORFFrame = frame;
    }
      
    /**
     * Deze methode zoekt naar ORF's in een String sequentie. 
     * @throws CompoundNotFoundException 
     */
    public void search() throws CompoundNotFoundException {
       Matcher matcher = PATTERN.matcher(targetSeq);   
       while (matcher.find()) {
           String orf = matcher.group(1).replace("*", ""); //stopcodons niet opslaan in sequentie
           if (orf.length() >= minLen) {
             ORFs.add(new ORFSequence(orf,matcher.start(),matcher.end(), ORFFrame)); 
           } 
       }
    }
        
   /**
    * @return Een ArrayList met daarin alle ORF's als ORFSequente objecten die gevonden zijn in de sequentie.
    */   
    public ArrayList<ORFSequence> getORFs() {
       return ORFs; 
    }
    

    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.FileLoading;

import java.util.HashMap;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.template.Sequence;
import org.biojava.nbio.core.sequence.transcription.Frame;

/**
 *
 * @author RICK
 */
public class EasyPrinter {
    
    //class variabele
    private static final String NEWLINE = System.getProperty("line.separator");
    
    
    /**
     * Deze methode ontvangt een Object en bepaald
     * of dit object een HashMap of DNAsequence object is
     * en roept de methode aan die nodig is voor het printen
     * van de informatie in dit object.
     * @param obj
     * @return 
     */
     public static String print(Object obj) {
        String txtOutput = "";
        if (obj instanceof HashMap ) {
            txtOutput = HashMapPrinter(obj);
        }
        if (obj instanceof DNASequence) {
            txtOutput = DNAseqPrinter(obj);
        }
        else {
            //*ignore* wrong type
        }
        return txtOutput;
     }
    
     /**
      * Deze methode wordt gebruikt voor het printen van HashMaps met daarin als value
      * Sequence objecten of subclasses daarvan. 
      * @param obj een HashMap object
      * @return een String met daarin de reading frames en bijbehorende sequenties.
      */
    private static String HashMapPrinter(Object obj) {
        HashMap map = (HashMap)obj;
        String txt = "";
        int reverseCount = 1; //nodig voor het bepalen van de strand
            for (int i=1; i < 7; i++) {
                Frame frame = ReadingFrameCalculator.numbToFrame(i);
                Sequence seq = (Sequence) map.get(frame);
                char strand = ((frame.toString().contains("REVERSE"))? '-':'+');
                int strandNumb = ((i <=3)? i: reverseCount++ );
                txt +=("" + strand + strandNumb + seq + NEWLINE ); //"" to make everything a String
            }
            return txt;
    }
        
    /**
     * Deze methode ontvangt een DNASequence object en retouneert de sequentie
     * uit dit object.
     * @param obj Een DNASequence object.
     * @return Geeft de DNA sequentie in het object terug als String.
     */
    private static String DNAseqPrinter(Object obj) {
        DNASequence DNAOjbect = (DNASequence)obj;
        return(DNAOjbect.getSequenceAsString());
        
    }
    
}

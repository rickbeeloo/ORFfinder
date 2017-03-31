/*
Datum laatste update: 31-03-17
Projectgroep 12: Enrico Schmitz, Thomas Reinders en Rick Beeloo
Functionaliteit: De gebruiker kan een FASTA bestand inladen. In de sequentie
			     kunnen vervolgens ORF's gezocht worden die verder geannoteerd 
			     kunnen worden door gebruikt te maken van een BLAST search.
Bekende bugs:    Als de gebruiker het tijdelijke BLAST bestand verwijderd zal de
                 data niet opgeslagen kunnen worden in de database.

 */
package AnnotationViewer.FileLoading;


import java.util.HashMap;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.transcription.Frame;

/**
 * Deze class wordt gebruikt voor het berekenen van de reading frame sequenties (zowel RNA als proteïne).
 * @author projectgroep 12
 */

public class ReadingFrameCalculator {
    
    //instantie variabele
    private DNASequence inputDNA;
    private HashMap<Frame, RNASequence> RNAreadingFrames = new HashMap<>();
    private HashMap<Frame, ProteinSequence> protReadingFrames = new HashMap<>();
 
    /**
     * Contstructor
     * @param inputDNAobj Een DNASequence object met daarin een DNA sequentie. 
     */
    public ReadingFrameCalculator(DNASequence inputDNAobj) {
        inputDNA = inputDNAobj;
    }

    /**
     * Deze methode berekent de reading frames (zowel RNA als proteïnen)
     */
    public void calculate() {
        for (int i = 1; i < 7; i++) {
            Frame frame = numbToFrame(i);
            RNASequence RNASeq = inputDNA.getRNASequence(frame);
            RNAreadingFrames.put(frame, RNASeq);
            protReadingFrames.put(frame, RNASeq.getProteinSequence());
        }
    }

    /**
     * @return Retouneert een HashMap met als key het reading frame en als value de bijbehorende RNA sequentie.
     */
    public HashMap<Frame, RNASequence> getAllRNARFs() {
        return RNAreadingFrames;
    }
    
    /**
     * @return Retouneert een HashMap met als key het reading frame en als value de bijbehorende proteïne sequentie.
     */
    public HashMap<Frame, ProteinSequence> getAllProtRFs() {
       return protReadingFrames; 
    }
    
  
    /**
     * Deze methode bepaald op basis van het reading frame de strand (antisense/sense)
     * @param frame een Frame object waarvan de strand bepaald moet worden.
     * @return een char (-/+) die de strand representeert.
     */
    public static char getStrand(Frame frame) {
        if (frame.toString().contains("REVERSED")) {
            return '-';
        }
        else {
            return '+';
        }  
    }
    
    /**
     * Deze methode converteert een nummer naar een Frame object.
     * @param numb Een nummer dat geconverteerd moet worden naar een Frame object.
     * @return een Frame object.
     */
    public static Frame numbToFrame(int numb) {
        switch (numb) {
            case 1:
                return Frame.ONE;
            case 2:
                return Frame.TWO;
            case 3:
                return Frame.THREE;
            case 4:
                return Frame.REVERSED_THREE;
            case 5:
                return Frame.REVERSED_TWO;
            case 6:
                return Frame.REVERSED_ONE;
            default:
                return Frame.getDefaultFrame(); //Als geen geldig nummer wordt meegegeven geef dan het standaard Frame terug
            }
    }
}



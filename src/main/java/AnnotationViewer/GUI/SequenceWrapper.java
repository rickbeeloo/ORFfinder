/*
Datum laatste update: 31-03-17
Projectgroep 12: Enrico Schmitz, Thomas Reinders en Rick Beeloo
Functionaliteit: De gebruiker kan een FASTA bestand inladen. In de sequentie
			     kunnen vervolgens ORF's gezocht worden die verder geannoteerd 
			     kunnen worden door gebruikt te maken van een BLAST search.
Bekende bugs:    Als de gebruiker het tijdelijke BLAST bestand verwijderd zal de
                 data niet opgeslagen kunnen worden in de database.

 */
package AnnotationViewer.GUI;

import AnnotationViewer.ORFSearching.ORFSequence;
import java.util.ArrayList;
import java.util.HashMap;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.transcription.Frame;

/**
 * Deze class functioneert als centraal opslagpunt voor alle informatie die afhankelijk is van de 
 * ingeladen DNA sequentie(reading frame sequenties en ORFs)
 * @author projectgroep 12
 */
public class SequenceWrapper {

    //instantie variabele
    private DNASequence DNAobj;
    private HashMap<Frame, RNASequence> RNAReadingFrameMap;
    private HashMap<Frame, ProteinSequence> protReadingFrameMap;
    private ArrayList<ORFSequence> allORFs;
    
    /**
     * Deze class funtioneert als verzamel class van alle infromatie betreft de door de gebruiker
     * ingeladen DNA sequentie: RNA/prot reading frame sequenties en (eventueel) gevonden ORF's.
     */
    public SequenceWrapper() {}

    /**
     * Deze methode ontvangt een input DNA sequentie en voegt deze toe aan de sequence wrapper.
     * @param inputDNA een DNASequence object.
     */
    public void setDNASeq(DNASequence inputDNA) {
        DNAobj = inputDNA;
    }
    
    /**
     * Deze methode ontvangt een HashMap met als keys Frame objecten en als values
     * RNASequence objecten en voegt deze toe aan de sequence wrapper.
     * @param frames 
     */
    public void setRNAReadingFrames(HashMap<Frame, RNASequence> frames) {
        RNAReadingFrameMap = frames;
    }
    
    /**
     * Deze methode ontvangt een HashMap met als keys Frame objecten en als values
     * ProteinSequence objecten en voegt deze toe aan de sequence wrapper. 
     * @param frames 
     */
    public void setProtReadingFrames(HashMap<Frame, ProteinSequence> frames) {
        protReadingFrameMap = frames;
    }
    
    /**
     * Deze methode ontvangt een ArrayList met daarin ORFsequentie objecten en deze
     * worden opgeslagen in de sequenceWrapper.
     * @param foundORFs 
     */
    public void setORFs(ArrayList<ORFSequence> foundORFs) {
        allORFs = foundORFs;
    }
    
    /**
     * @return Retouneert een DNASeuqnece object.
     */
    public DNASequence getDNAseq() {
        return DNAobj;
    }
       
    /**
     * @return Retouneert een HashMap met als keys Frame objecten en als values 
     * RNASequence objecten.
     */
    public HashMap<Frame, RNASequence> getRNAReadingFrames() {
        return RNAReadingFrameMap;
    }
    
    /**
     * @return Retouneert een HashMap met als keys Frame objecten en als values 
     * ProtienSequence objecten.
     */
    public HashMap<Frame, ProteinSequence> getProtReadingFrames() {
        return protReadingFrameMap;
    }
    
    /**
     * @return Retouneert een ArrayList met de gevonden ORF's als ORFSequence objecten.
     */
    public ArrayList<ORFSequence> getORFs() {
        return allORFs;
    }

}

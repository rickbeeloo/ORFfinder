/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.ORFSearching;


import java.util.ArrayList;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.search.io.Hit;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.transcription.Frame;
/**
 *Deze class functioneert als DNASequence wrapper. Dit is een uitbreiding op het bestaande
 * DNASequence object uit de Biojava library. 
 * @author RICK
 */
public class ORFSequence {
    
    //class variabele
    private static int ORFCounter;
    
    //instantie variabele
    private ProteinSequence ORFSeq;
    private String ORFID;
    private Frame readingFrame;
    private char strand;
    private int startPos;
    private int stopPos; 
    private ArrayList<Hit> blastHits;
 
    
    
    ORFSequence(String sequence, int start, int stop, Frame rf) throws CompoundNotFoundException {
        ORFCounter++;
        ORFID = "ORF_" + ORFCounter;
        ORFSeq = new ProteinSequence(sequence);
        startPos = start;
        stopPos = stop;
        readingFrame = rf;
        blastHits = new ArrayList<>();
        
    }
    
    /**
     * @return Retouneert de positie in de sequentie waar het ORF start.
     */
    public int getStart() {
        return startPos;
    }
    
    /**
     * @return Retouneert de positie in de sequentie waar het ORF eindigd. 
     */
    public int getStop() {
        return stopPos;
    }
    
    /**
     * @return Retouneert het ORF ID. 
     */
    public String getID() {
        return ORFID;
    }
    
    /**
     * @return Retouneert de aminozuur sequentie van het ORF.
     */
    public String getAAseq() {
        return ORFSeq.getSequenceAsString();
    }
    
    /**
     * @return Retouneert het reading frame waarin het ORF zich bevindt.
     */
    public Frame getRF() {
        return readingFrame;
    }
    
    /**
     * @return Retouneert de strand: antisense/sense waarin het ORF ligt.
     */
    public char getStrand() {
        rfToStrand();
        return strand;
    }

    /**
     * Deze methode bepaald op basis van het reading Frame de strand (sense/anisense).
     */
    private void rfToStrand() {
       String frame = getRF().toString();
       strand = ((frame.contains("REVERSE"))? '-':'+'); //Als het Frame het woord reverse bevat is dit de antisense (-) strand.
    }
    
   
}
 
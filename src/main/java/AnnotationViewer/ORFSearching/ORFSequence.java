/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.ORFSearching;

import AnnotationViewer.FileLoading.ReadingFrameCalculator;
import java.util.ArrayList;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.search.io.Hit;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.transcription.Frame;

/**
 * Deze class functioneert als DNASequence wrapper. Dit is een uitbreiding op
 * het bestaande DNASequence object uit de Biojava library.
 *
 * @author projectgroep 12
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

    /**
     * Constructor
     * @param sequence De ORF sequentie (proteïnen) als String object
     * @param start De start positie van het ORF als int
     * @param stop De stop positie van het ORF als int
     * @param rf Het reading frame waarop het ORF ligt als char (+ of -)
     * @throws CompoundNotFoundException Gooit een Exception als de input geen proteïne sequentie is.
     */
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
        return ORFSeq.getSequenceAsString().replace("mRNA", "");
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
        return (ReadingFrameCalculator.getStrand(readingFrame));
    }

}

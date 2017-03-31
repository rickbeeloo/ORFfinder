/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.FileLoading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import javax.swing.JOptionPane;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

/**
 *
 * @author projectgroep 12
 */
public class FileParser {

    //instantie variabele
    private File bestand;
    private DNASequence DNAobj;

    /**
     * Constructor
     *
     * @param input Het FASTA bestand object.
     */
    public FileParser(File input) {
        bestand = input;
    }

    /**
     * Deze methode parsed de informatie in het FASTA bestand en slaat deze data
     * op in een DNASequence object.
     *
     * @throws IOException Gooit een exception als het FASTA bestand niet
     * gelezen kan worden.
     */
    public void parse() throws IOException {
        LinkedHashMap<String, DNASequence> data;
        try {
            data = FastaReaderHelper.readFastaDNASequence(bestand);
            DNAobj = getFirstEntry(data).getValue(); //laad alleen de eerste sequentie in als een multiple FASTA file 
        } catch (NoSuchElementException ex) {        //wordt ingeladen.
            showError("No FASTA DNA sequences present in the file.");
        } catch (FileNotFoundException ex) {
            showError("Cannot find the requested file.");
        } catch (Exception ex) {
            showError("Please provide a valid FASTA file.");
        }
    }

    /**
     * @return een DNASequence object
     */
    public DNASequence getDNA() {
        return DNAobj;
    }

    /**
     * Deze methode retouneert de eerste entry in het FASTA bestand.
     *
     * @param data een LinkedHashMap met daarin als key de sequentie header en
     * als value een DNASequence header object.
     * @return De eerste entry uit de LinkedHashMap.
     */
    private Entry<String, DNASequence> getFirstEntry(LinkedHashMap<String, DNASequence> data) {
        return data.entrySet().iterator().next();
    }

    /**
     * Deze methode laat een Error pop-up zien met daarin het meegegeven
     * bericht.
     *
     * @param mssg Het bericht dat weergegeven moet worden in de pop-up.
     */
    private static void showError(String mssg) {
        JOptionPane.showMessageDialog(null, mssg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

}

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;

/**
 * Deze class is verantwoordelijk voor het open, opslaan en sluit acties
 * van bestanden.
 * @author projectgroep12
 */
public class FileHandler {
    
    /**
     * Deze methode opent een file chooser en retouneert het door de gebruiker
     * selecteerde bestand.
     * @return Een File object.
     * @throws IOException Gooit een exception als het bestand niet geopend kan worden.
     */
    public static File openFile() throws IOException {
        JFileChooser chooser = new JFileChooser();
        int openedCorrect = chooser.showOpenDialog(null);
        if (openedCorrect == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            throw new FileNotFoundException();
        }
    }
   
    /**
     * Deze methode sluit een BufferedReader.
     * @param reader een BufferReader object.
     * @throws IOException Gooit een exception als het meegegeven object niet gesloten kan worden.
     */
    public static void closeFile(BufferedReader reader) throws IOException {
            reader.close();
    }
    
}

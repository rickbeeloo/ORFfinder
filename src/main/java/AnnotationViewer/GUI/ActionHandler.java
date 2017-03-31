/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.GUI;

import AnnotationViewer.Blast.BlastJobManager;
import AnnotationViewer.DataStorage.LengthException;
import AnnotationViewer.DataStorage.Saver;
import AnnotationViewer.FileLoading.EasyPrinter;
import AnnotationViewer.FileLoading.FileHandler;
import AnnotationViewer.FileLoading.FileParser;
import AnnotationViewer.FileLoading.ReadingFrameCalculator;
import AnnotationViewer.ORFSearching.ORFFinder;
import AnnotationViewer.ORFSearching.ORFSequence;
import AnnotationViewer.ORFSearching.ORFhighLighter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.transcription.Frame;

/**
 * Deze class zorgt voor het afhandelen van alle acties die de gebruiker teweeg brengt door het klikken
 * op een button in de GUI.
 * @author projectgroep 12
 */
public class ActionHandler {

    //class variables
    private static Boolean saveInDatabase;

    //instance variables
    private SequenceWrapper refSeqWrapper;

    public ActionHandler() {
        saveInDatabase = true;
        refSeqWrapper = new SequenceWrapper();
    }

    /**
     * Deze methode ontvangt een Boolean die aangeeft of de data moet worden
     * opgeslagen in de database
     *
     * @param save Boolean die aangeeft of de data opgeslagen moet worden in de
     * database.
     */
    public void setSaveStatus(Boolean save) {
        saveInDatabase = save;
    }

    /**
     * @return Retouneert of de data moet worden opgeslagen in de database
     */
    public static Boolean getSaveStatus() {
        return saveInDatabase;
    }

    /**
     * Deze methode parsed alle informatie in het FASTA bestand en roept dan de
     * Saver aan om de FASTA header en de FASTA sequentie in de database op te
     * slaan.
     *
     * @throws IOException gooit een exception als het FASTA bestand niet
     * gelezen kan worden.
     * @throws AnnotationViewer.DataStorage.LengthException
     */
    public void parseFile() throws IOException, LengthException {
        FileParser parser = new FileParser(FileHandler.openFile());
        parser.parse();
        if (EasyPrinter.print(parser.getDNA()).length() < 1000) {
            refSeqWrapper.setDNASeq(parser.getDNA());
            Saver.setConnection(); //open de connectie voor volgende opslaan acties
            if (getSaveStatus() == true) {
                Saver.save(parser.getDNA());
            }
        } else {
            throw new LengthException();
        }

    }

    /**
     * Deze methode berekent de reading frames (zowel RNA als Proteïne).
     */
    public void calcRFs() {
        ReadingFrameCalculator calc = new ReadingFrameCalculator(refSeqWrapper.getDNAseq());
        calc.calculate(); //calculate reading frames
        refSeqWrapper.setRNAReadingFrames(calc.getAllRNARFs());
        refSeqWrapper.setProtReadingFrames(calc.getAllProtRFs());
    }

    /**
     * Deze methode laat de gevonden ORF's zien in de output TextArea.
     *
     * @param outputField JTextArea waarin de proteïnen sequenties getoond
     * moeten worden.
     */
    public void showProteinRFs(JTextArea outputField) {
        HashMap<Frame, ProteinSequence> proteinFrames = refSeqWrapper.getProtReadingFrames();
        outputField.setText(EasyPrinter.print(proteinFrames));
    }

    /**
     * Deze methode wordt aangeroepen om een keuze te geven aan de gebruiker om
     * de data wel of niet op te slaan in de database.
     *
     * @param txt de text die in het dailoog venster moet komen te staan.
     * @param title de titel van het dialoog venster.
     */
    public void saveChoiceAction(String txt, String title) {
        int dialogResult = JOptionPane.showConfirmDialog(null, txt, title, JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.NO_OPTION) {
            saveInDatabase = false;
        }
    }

    /**
     * Deze methode start een Thread die elke 20 seconde controleerd of er al
     * een BLAST opdracht klaar is.
     */
    public void ResultCheckAction() {
        Thread t = new Thread(() -> {
            while (true) { //oneindig om de BLAST taken te controleren gedurende de gehele tijd dat het programma runt.
                try {
                    BlastJobManager.checkIfJobIsDone();
                    Thread.sleep(20000);
                } catch (InterruptedException ex) {
                    //de thread herstarten
                    ResultCheckAction();
                }
            }
        });
        t.start();
    }

    /**
     * Deze methode instantieert een ORFfinder, verantwoordelijk voor het zoeken
     * van de ORF's in alle reading frames.
     */
    public void searchORFs() {
        int minORFLen = showIntegerInputDialog("Minimal ORF length", "INPUT");
        ArrayList<ORFSequence> allORFs = new ArrayList<>();
        HashMap<Frame, ProteinSequence> proteinFrames = refSeqWrapper.getProtReadingFrames();
        for (int i = 1; i < proteinFrames.size() + 1; i++) {
            try {
                Frame frame = ReadingFrameCalculator.numbToFrame(i);
                ProteinSequence protSeq = proteinFrames.get(frame);
                ORFFinder finder = new ORFFinder(protSeq.getSequenceAsString(), minORFLen, frame);
                finder.search();
                allORFs.addAll(finder.getORFs());
            } catch (CompoundNotFoundException ex) {
                showError("Cannot save this as protein sequences.");
            }
        }
        refSeqWrapper.setORFs(allORFs);
        if (getSaveStatus() == true) {
            Saver.save(allORFs);
        }
    }

    /**
     * Deze methode opent een InputDialog waarin de gebruiker de minimale ORF
     * lengte kan ingeven. De ingevoerde waarde wordt gecontroleerd of dit ook
     * daadwerkelijk een nummer is en als dat zo is wordt deze omgezet naar een
     * integer en geretouneerd.
     *
     * @return De minimale ORF lengte als int.
     */
    private int showIntegerInputDialog(String mssg, String title) {
        String inputValue = JOptionPane.showInputDialog(null, mssg, title, JOptionPane.INFORMATION_MESSAGE);
        int minInputLen = 0;
        if (inputValue != null) {
            try {
                minInputLen = Integer.parseInt(inputValue);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please provide a number", "ERROR", JOptionPane.ERROR_MESSAGE);
                showIntegerInputDialog(mssg, title);
            }
        }
        return minInputLen;
    }

    /**
     * Deze methode zorgt voor het markeren van de gevonden ORF's in de
     * referentie TextArea.
     *
     * @param outputField JTextField met de tekst die gemarkeerd moet worden.
     */
    public void highLightORFs(JTextArea outputField) {
        ORFhighLighter marker = new ORFhighLighter(refSeqWrapper.getORFs(), outputField);
        marker.highlight();
    }

    /**
     * Deze methode laat een Error-pop up zien met daarin het meegegeven
     * bericht.
     *
     * @param mssg Een bericht dat getoond moet worden in de pop-up.
     */
    private static void showError(String mssg) {
        JOptionPane.showMessageDialog(null, mssg, "ERROR", 0);
    }

}

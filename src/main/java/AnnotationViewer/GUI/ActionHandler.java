/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.GUI;

import AnnotationViewer.ORFSearching.ORFSequence;
import AnnotationViewer.ORFSearching.ORFFinder;
import AnnotationViewer.ORFSearching.ORFhighLighter;
import AnnotationViewer.Blast.BlastJobManager;
import AnnotationViewer.Blast.BLASTInputForm;
import AnnotationViewer.Blast.Blast;
import AnnotationViewer.DataStorage.Saver;
import AnnotationViewer.FileLoading.EasyPrinter;
import AnnotationViewer.FileLoading.FileHandler;
import AnnotationViewer.FileLoading.FileParser;
import AnnotationViewer.Blast.JobAlreadyInQueue;
import AnnotationViewer.DataStorage.LengthException;
import AnnotationViewer.FileLoading.ReadingFrameCalculator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.transcription.Frame;

/**
 * Deze class is veranwtoordelijk voor het afhandelen van alle acties die teweeg worden gebracht
 * door de gebruiker. 
 * @authors projectGroep 12
 */
public class ActionHandler {

    //class variables
    private static SequenceWrapper refSeqWrapper; 
    private static JTextArea outputField;
    private static BlastJobManager jobManager;


    public ActionHandler() {}

    /**
     * Deze methode zet de sequentie wrapper op zodat de DNA sequenties, ORF's en BLAST jobs in 
     * een centrale class kunnen worden opgeslagen.
     * @param GUISeqWrapper Een sequentie wrapper waarin alle data rondom de ingeladen DNA sequentie in opgeslagen moet worden.
     */
    public static void setReferenceWrapper(SequenceWrapper GUISeqWrapper) {
        refSeqWrapper = GUISeqWrapper;
    }

    /**
     * Deze methode wordt gebruikt voor het maken van een referentie naar een textArea waarin vervolgens
     * alle resultaten getoond kunnen worden.
     * @param outputTextArea Een textArea waarin alle resultaten van de acties getoond moeten worden.
     */
    public static void setReferenceOutput(JTextArea outputTextArea) {
        outputField = outputTextArea; 
    }
   
    /**
     * Deze methode zet de manager die gebruikt moet worden voor het afhandelen van alle taken
     * die betrekking hebben op het BLASTen. 
     * @param manager Een manager waarin BLAST jobs toegevoegd kunnen worden.
     */
    public static void setBlastJobManager(BlastJobManager manager) {
        jobManager = manager; //manages the blast jobs and check if these are finished.
    }

    /**
     * Deze methode is nodig voor het extraheren van alle informatie uit het FASTA bestand dat
     * door de gebruiker wordt ingeladen. De methode zal eerst zorgen voor het parse van alle inforamtie
     * waarna de DNA seqentie wordt vertaald in alle 6 de reading frames.
     */
    public void OpenAction() {
        try {
            parseFile();
            calcRFs();
            showProteinRFs();
        } catch (LengthException ex) {
            showError("This sequence is to long! \n The max sequence length is 1000bp");
        } catch (IOException ex) {
           //*ignore* De gebruiker heeft het filedialog afgesloten.
        }
    }

    /**
     * Deze methode roept alle methodes aan die nodig zijn voor het zoeken van ORF's (rekening houdend
     * met de door de gebruiker ingegeven minimale ORF lengte) en het markeren van deze ORF's in de
     * referentie TextArea. 
     */
    public void ORFAction() {
        int minORFLen = showInputDialog();
        searchORFs(minORFLen);
        highLightORFs();
    }

    /**
     * Deze methode start een Thread die elke 20 minuten controleerd of er al een BLAST opdracht 
     * klaar is.
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
     * Deze methode bouwt een input formulier waarin de gebruiker de gewenste BLAST parameters kan instellen
     * @param ORFobj Een ORFSequence object waarvan de sequentie geblast moet worden.
     */
    public void BLASTinputAction(ORFSequence ORFobj) {
        BLASTInputForm form = new BLASTInputForm(ORFobj);
        form.show();
    }

    /**
     * Deze methode verzend een request naar de NCBI server en geeft de informatie dan door
     * aan de BLAST job manager. 
     * @param ORFobj Een ORFSequence object waarvan de sequentie geblast moet worden.
     * @param tmpOutput Een output bestand waarna de BLAST resultaten geschreven kunnen worden (XML).
     * @param program Het gewenste BLAST programma (blastn, blastp, tblastn, tblastx).
     * @param db De gewenste database waartegen een BLAST search uitgevoerd moet worden.
     * @param eValCutOff  De E-value cut-off
     * @param numberTopHits Het maximaal aantal hits die getoond moeten worden aan de gebruiker.
     */
    public void PerfromBlastAction(ORFSequence ORFobj, String tmpOutput, String program, String db, double eValCutOff, int numberTopHits) {
        try {
            Blast blast = new Blast(ORFobj.getAAseq(), tmpOutput, program, db, eValCutOff, numberTopHits);
            blast.sendRequest();
            BlastJobManager.addJob(blast, ORFobj.getID());
        } catch (JobAlreadyInQueue ex) {
            showError("The job is already in the queue!");
        } catch (Exception ex) {
            showError("An unknown exception occured.");
        }
    }

    /**
     * Deze methode parsed alle informatie in het FASTA bestand en roept dan de 
     * Saver aan om de FASTA header en de FASTA sequentie in de database op te slaan.
     * @throws IOException gooit een exception als het FASTA bestand niet gelezen kan worden.
     */
    private void parseFile() throws IOException, LengthException {
        FileParser parser = new FileParser(FileHandler.openFile());
        parser.parse();
        if (EasyPrinter.print(parser.getDNA()).length() < 1000) {
            refSeqWrapper.setDNASeq(parser.getDNA()); //save input DNA in wrapper
            Saver.setConnection(); //connect to database for subsequent savings
            Saver.save(parser.getDNA()); 
        }
        else {
            throw new LengthException();
        }
        
    }

    
    
    /**
     * Deze methode berekent de reading frames (zowel RNA als Proteïne). 
     */
    private void calcRFs() {
        ReadingFrameCalculator calc = new ReadingFrameCalculator(refSeqWrapper.getDNAseq());
        calc.calculate(); //calculate reading frames
        refSeqWrapper.setRNAReadingFrames(calc.getAllRNARFs());
        refSeqWrapper.setProtReadingFrames(calc.getAllProtRFs());
    }

    /**
     * Deze methode laat de gevonden ORF's zien in de referentie TextArea.
     */
    private void showProteinRFs() {
        HashMap<Frame, ProteinSequence> proteinFrames = refSeqWrapper.getProtReadingFrames();
        outputField.setText(EasyPrinter.print(proteinFrames));
    }

    /**
     * Deze methode opent een InputDialog waarin de gebruiker de minimale ORF lengte kan ingeven.
     * De ingevoerde waarde wordt gecontroleerd of dit ook daadwerkelijk een nummer is en als dat zo
     * is wordt deze omgezet naar een integer en geretouneerd. 
     * @return De minimale ORF lengte als int. 
     */
    private int showInputDialog() {
        String inputValue =  JOptionPane.showInputDialog(null, "Minimal ORF length: ", "INPUT", JOptionPane.INFORMATION_MESSAGE);
        int minInputLen = 0;
        if(inputValue != null) {
          try{
              minInputLen = Integer.parseInt(inputValue);
          }  
          catch(NumberFormatException ex) {
              JOptionPane.showMessageDialog(null,"Please provide a number","ERROR",JOptionPane.ERROR_MESSAGE);
              showInputDialog();
          }
        }
        return minInputLen;
    }
    
    /**
     * Deze methode instantieert een ORFfinder, verantwoordelijk voor het zoeken van de ORF's 
     * in alle reading frames.
     * @param minORFLen De minimale ORF lengte.
     */
    private void searchORFs(int minORFLen) {
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
        Saver.save(allORFs);
    }

    /**
     * Deze methode zorgt voor het markeren van de gevonden ORF's in de referentie TextArea.
     */
    private void highLightORFs() {
        ORFhighLighter marker = new ORFhighLighter(refSeqWrapper.getORFs(), outputField);
        marker.highlight();
    }

    /**
     * Deze methode laat een Error-pop up zien met daarin het meegegeven bericht.
     * @param mssg Een bericht dat getoond moet worden in de pop-up.
     */
    private static void showError(String mssg) {
        JOptionPane.showMessageDialog(null, mssg, "ERROR", 0);
    }

}

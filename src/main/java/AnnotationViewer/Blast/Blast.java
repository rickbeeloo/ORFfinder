/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.Blast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.biojava.nbio.core.search.io.Hit;
import org.biojava.nbio.ws.alignment.qblast.BlastProgramEnum;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastAlignmentProperties;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastOutputProperties;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastService;

/**
 * Deze class is verantwoordelijk voor het uitvoeren van BLAST searches tegen de
 * NCBI database. De code uit de BioJava handleiding is gebruikt als basis voor
 * deze class.
 *
 * @author projectgroep 12
 */
public class Blast {

    //instantie variabele
    private String sequence;
    private String blastProgram;
    private String blastDatabase;
    private NCBIQBlastService service;
    private NCBIQBlastAlignmentProperties props;
    private ArrayList<Hit> blastResults;
    private NCBIQBlastOutputProperties outputProps;
    private BlastParser parser;
    private File XMLFile;
    private Thread t;
    private String rid;
    private double maxEval;
    private int top;

    /**
     * Constructor
     *
     * @param seq De sequentie die geBLAST moet worden.
     * @param program Het BLAST programma dat gebruikt moet worden.
     * @param db De database waartegen geBLAST moet worden
     * @param eValCutOff De E-value cut-off die gebruikt moet worden.
     * @param numberTopHits Het aantal hits dat geretouneerd moet worden.
     */
    public Blast(String seq, String program, String db, double eValCutOff, int numberTopHits) {
        sequence = seq;
        blastProgram = program;
        blastDatabase = db;
        maxEval = eValCutOff;
        top = numberTopHits;
        setServices();
    }

    /**
     * Deze methode zorgt voor het opstellen van de alignment opties (database
     * en BLAST programma) en het instellen van het maximaal aantal alginements
     * dat gemaakt moet worden.
     */
    private void setServices() {
        try {
            service = new NCBIQBlastService();
            setAlignmentOptions();
            setOutputOptions();
        } catch (ProgramException ex) {
            showError("please provide a valid datbase: \n blastn, blastp, tblastn, tblastx");
        }
    }

    /**
     * Deze methode is verantwoordelijk voor het instellen van de alignment
     * opties.
     *
     * @throws ProgramException
     */
    private void setAlignmentOptions() throws ProgramException {
        props = new NCBIQBlastAlignmentProperties();
        props.setBlastProgram(getBlastProgram(blastProgram));
        props.setBlastDatabase(blastDatabase);
    }

    /**
     * Deze methode is verantwoordelijk voor het instellen van de output opties.
     */
    private void setOutputOptions() {
        outputProps = new NCBIQBlastOutputProperties();
        outputProps.setAlignmentNumber(100); //this is used as default
    }

    /**
     * Deze methode zet een BLAST programma als String om naar een BlastProgram
     * object die gebruikt kan worden door de biojava BLAST service.
     *
     * @param program Een String object dat het gewenste BLAST programma bevat.
     * @return Geeft een BlastProgramEnum object terug corresponderend met de
     * ingegeven String.
     * @throws ProgramException Gooit een exception als het String object niet
     * kan worden omgezet naar een BlastProgramEnum object.
     */
    private BlastProgramEnum getBlastProgram(String program) throws ProgramException {
        switch (program.toLowerCase()) {
            case "blastp":
                return BlastProgramEnum.blastp;
            case "blastn":
                return BlastProgramEnum.blastp;
            case "tblastn":
                return BlastProgramEnum.tblastn;
            case "tblastx":
                return BlastProgramEnum.tblastx;
            default:
                throw new ProgramException();
        }
    }

    /**
     * Deze mmethode start een nieuwe Thread waarin een request wordt gestuurd
     * naar de NCBI server. Deze thread blijft "levend" zolang de BLAST server
     * nog geen resultaat heeft geretouneerd.
     *
     *
     * @throws Exception Gooit een Exception als er geen verbinding gemaakt kan
     * worden met de NCBI server.
     */
    public void sendRequest() throws Exception {
        t = new Thread(() -> {
            try {
                rid = null;
                //stuur een BLAST request en sla het ID op.
                rid = service.sendAlignmentRequest(sequence, props);
                System.out.println(rid);
                while (!service.isReady(rid)) {
                    Thread.sleep(5000);
                }
                readResults(rid);
            } catch (IOException ex) {
                showError("Cannot connect to NCBI please check your internet connection");
            } catch (Exception ex) {
                showError("An unknown exception has occured");
            }
        });
        t.start();
    }

    /**
     * Deze methode controleerd of de request Thread de data van de BLAST server
     * heeft ontvangen of niet.
     *
     * @return Retouneert of de thread nog actief is als Boolean
     */
    public boolean checkStatus() {
        return (t.isAlive());
    }

    /**
     * Aangepast voorbeeld uit de BioJava handleiding. Deze methode haalt op
     * basis van het door de NCBI server teruggegeven BLAST ID het resultaat op
     * en slaat dit op in een XML bestand.
     *
     * @param rid BLAST job ID (geretouneerd door de NCBI server)
     * @throws IOException Gooit een exception als er niet naar het bestand kan
     * worden geschreven.
     * @throws Exception Gooi een exception als er een onbekende fout optreed.
     */
    private void readResults(String rid) throws IOException, Exception {
        InputStream inStream = service.getAlignmentResults(rid, outputProps);
        XMLFile = new TempFile(inStream).getFile();  
    }
    
    /**
     * Deze methode zorgt voor het instantiëren van de BLAST parser en het
     * aanroepen van de parse methoden in deze parser.
     */
    public void parse() {
        parser = new BlastParser(XMLFile,maxEval);
        parser.parse();
        XMLFile.delete(); 
    }

    /**
     * Deze methode retouneert de gevonden top x BLAST hits. Waarbij x een
     * meegegeven getal is bij instantiatie van deze class.
     *
     * @return
     */
    public ArrayList<Hit> getHits() {
        return parser.getTopHits(top);
    }

    /**
     * Deze methode retouneert het BLAST job ID geretouneert door de NCBI server
     *
     * @return BLAST job ID geretouneert door de NCBI server.
     */
    public String getBlastJobID() {
        return rid;
    }

    /**
     * Deze methode laat een Error pop-up zien met daarin het meegegeven
     * bericht.
     *
     * @param mssg Het bericht dat weergegeven moet worden in de pop-up.
     */
    private void showError(String mssg) {
        JOptionPane.showMessageDialog(null, mssg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}

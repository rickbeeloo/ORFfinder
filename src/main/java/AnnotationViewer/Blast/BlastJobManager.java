/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.Blast;

import AnnotationViewer.DataStorage.Saver;
import AnnotationViewer.GUI.ActionHandler;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Deze class is verantwoordelijk voor het bijhouden van alles wat te maken
 * heeft met BLAST request. Deze class laat de jobs zien aan de gebruiker, houdt
 * bij of er BLAST jobs klaar zijn en als de BLAST job klaar is wordt de data
 * opgelsagen in de database.
 *
 * @author projectgroep 12
 */
public class BlastJobManager {

    //class variabele
    private static JTable outputTable;
    private static ArrayList<BlastJob> jobs;
    private static ArrayList<Boolean> finished;
    private static HashMap<String, Integer> rowManager;
    private static int row;
    private static final String BLASTWEB = "https://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Get&RID=";

    /**
     * Empty constructor
     */
    public BlastJobManager() {}

    /**
     * Deze methode ontvangt een referentie JTable waarin alle BLAST jobs komen
     * te staan.
     *
     * @param output Een JTable waarin alle BLAST jobs komen te staan.
     */
    public static void setOutputTable(JTable output) {
        outputTable = output;
        finished = new ArrayList<>();
        jobs = new ArrayList<>();
        rowManager = new HashMap<>();
        setListener();
    }

    /**
     * Deze methode voegt een listener toe aan de tabel. Als de BLAST job klaar
     * is kan de gebruiker op deze link klikken om naar de resultaat pagina te
     * gaan in de webbrowser.
     */
    private static void setListener() {
        outputTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = outputTable.getSelectedRow();
                int col = outputTable.getSelectedColumn();
                try {
                    if (col == 1) { //alleen als de gebruiker op de cel in de juiste kolom klikt
                        String value = (String) outputTable.getValueAt(row, col);
                        String url = value.split("\"")[1];
                        openURL(url);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    showError("This job isn't ready yet!");
                }
            }
        });
    }

    /**
     * Deze methode opent de resultaat pagina in de webbrowser.
     *
     * @param url De URL die geopend moet worden in de webbrowser.
     */
    private static void openURL(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (URISyntaxException | IOException e) {
            showError("Cannot open requested webpage!");
        }
    }

    /**
     * Deze methode controleerd of de gebruiker de sequentie al heeft geblast zo
     * niet dan wordt de BLAST job toegevoegd aan de tabel.
     *
     * @param BlastObj Het Blast object dat toegevoegd moet worden aan de tabel.
     * @param ID Het ID dat gebruikt moet worden om de BLAST job te
     * identificeren.
     * @throws JobAlreadyInQueue Gooit een exception als deze Job al in de tabel
     * staat.
     */
    public static void addJob(Blast BlastObj, String ID) throws JobAlreadyInQueue {
        BlastJob job = new BlastJob(BlastObj, ID);
        if (!jobs.contains(job)) {
            jobs.add(job);
            finished.add(false);
            rowManager.put(job.getID(), row++);
            showUnFinishedJob(job);
        } else {
            throw new JobAlreadyInQueue();
        }
    }

    /**
     * @return retouneert een Boolean die aangeeft of alle jobs in de wachtrij
     * (op dit moment) klaar zijn.
     */
    public Boolean everyThingDone() {
        HashSet<Boolean> set = new HashSet<>(finished);
        if (set.size() == 1) {
            return set.iterator().next();
        } else {
            return false;
        }
    }

    /**
     * Deze methode contorleert of een Job een in de wachtrij klaar is.
     */
    public static void checkIfJobIsDone() {
        for (int i = 0; i < jobs.size(); i++) {
            BlastJob job = jobs.get(i);
            if (job.checkStatus() == false && finished.get(i) == false) {
                finished.set(i, true);
                showFinishedJob(job);
            }
        }
    }

    /**
     * @return retouneert een ArrayList met alle Jobs die op dit moment in de
     * wachtrij staan.
     */
    public static ArrayList<BlastJob> getCurrectJobs() {
        return jobs;
    }

    /**
     * Deze methode laat een Job in de tabel als deze nog niet klaar is.
     *
     * @param job
     */
    private static void showUnFinishedJob(BlastJob job) {
        DefaultTableModel model = (DefaultTableModel) outputTable.getModel();
        model.addRow(new Object[]{job.getID(), "NOT FINISHED YET"});
    }

    /**
     * Deze methode laat een job zien in de tabel als deze klaar is en zet dan
     * een hyperlink in de tabel waarmee de gebruiker naar de resultaat
     * webpagina kan.
     *
     * @param job
     */
    private static void showFinishedJob(BlastJob job) {
        String ID = job.getID();
        updateJobStatus(createHyperLink(job.getBlastObj().getBlastJobID()), rowManager.get(ID));
        if (ActionHandler.getSaveStatus()) {
            Saver.save(job); //de BLAST resultaten worden opgeslagen in de database. 
        }

    }

    /**
     * Deze methode update een rij in de tabel met de gegeven status
     *
     * @param status Een String object met daarin de status van de job
     * @param row De row waarin de status moet worden weergegeven.
     */
    private static void updateJobStatus(String status, int row) {
        outputTable.setValueAt(status, row, 1);
    }

    /**
     * Deze methode maakt een hyperlink aan
     *
     * @param ID Het BLAST ID die geretouneerd is door de NCBI server.
     * @return Een String object die de hyerplink bevat.
     */
    private static String createHyperLink(String ID) {
        String adres = BLASTWEB + ID;
        String hyperLink = "<html><a href=\"" + adres + "\">GO TO RESULTS</a></html>";
        return hyperLink;
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

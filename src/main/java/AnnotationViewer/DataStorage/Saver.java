/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.DataStorage;

import AnnotationViewer.Blast.Blast;
import AnnotationViewer.Blast.BlastJob;
import AnnotationViewer.ORFSearching.ORFSequence;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.biojava.nbio.core.search.io.Hit;
import org.biojava.nbio.core.search.io.Hsp;
import org.biojava.nbio.core.sequence.DNASequence;

/**
 * Deze class vewerkt de data die opgeslagen moet worden in de database en roept
 * de ConnectionHandler aan om deze data ook daadwerkelijk op te slaan in de
 * database.
 *
 * @author projectgroep 12
 */
public class Saver {

    //class variabele
    private static final String INS = "INSERT INTO ";
    private static final String VAL = "VALUES ";
    private static final String OPEN = "(";
    private static final String CLOSE = ")";
    private static final String QUOTE = "\"";
    private static final String COMMA = ",";
    private static final String SEL = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String EQUALS = "=";
    private static ConnectionHandler handler;
    private static int lastSeqID;
    private static HashMap<String, Integer> ORFIDs;

    
    /**
     * Deze methode opent de verbinding met de database
     */
    public static void setConnection() {
        handler = new ConnectionHandler();
        handler.setSettings("jdbc:mysql://localhost:3307/mydb", "root", "usbw");
        handler.openConnection();
    }

    /**
     * Deze methode ontvangt een BlastJob van de BlastJobManager als deze klaar
     * is en slaat de bitsocre, e-value, sequentie, identiteit, positives, gaps,
     * het ORF_id en de proteïnen accessiecode op in de database.
     *
     * @param job
     */
    public static void save(BlastJob job) {
        String ORFID = job.getID();
        Blast blastObj = job.getBlastObj();
        //bouw basis query
        String table = "blast";
        String[] cols = {"bitScore", "evalue", "sequence", "identity", "positives", "gaps", "orf_ORFID", "protein_accession"};
        //haal de hit informatie uit het Blast object.
        blastObj.parse();
        for (Hit hit : blastObj.getHits()) {
            try {
                saveProtein(hit.getHitDef(), hit.getHitAccession()); //sla het proteïnen op als deze nog niet in de protein tabel staat.
                String insertQuery = genInsert(table, cols, extractHitData(hit, ORFID));
                handler.insert(insertQuery);
            } catch (SQLException ex) {
                showError("Not able to insert into blast table");
            }
        }
    }

    /**
     * Deze methode slaat de proteïnen naame en proteïne accessiecode op in de
     * datbase als deze nog niet in de databas staat.
     *
     * @param header De header van het de BLAST Hit
     * @param accession De accesiecode van de BLAST hit
     */
    public static void saveProtein(String header, String accession) {
        try {
            //controleer of het proteïnen als in de database is opgeslagen
            String RetrQuery = genRetr("protein", "accession", "accession", accession);
            ResultSet rs = handler.retrieve(RetrQuery);
            if (rs.next() == false) { //accesiecode (primary key) niet in tabel--> dus insert proteïne
                String table = "protein";
                String[] col = {"accession", "name"};
                Object[] data = {accession, fitHeader(header)}; //kap waarde af als deze groter is dan 100 karakters.
                String insQuery = genInsert(table, col, data);
                handler.insert(insQuery);
            }
        } catch (SQLException ex) {
            showError("Not able to insert into protein table");
        }
    }

    /**
     * Deze methode ontvangt een ArrayList met de gevonden ORF's en slaat de
     * strand, start en stop positie van iedere ORF op in de database.
     *
     * @param orfs
     */
    public static void save(ArrayList<ORFSequence> orfs) {
        String table = "orf";
        String[] cols = {"start", "stop", "strand", "sequence_seqID"};
        ORFIDs = new HashMap<>();
        for (ORFSequence orf : orfs) {
            try {
                //invoegen van de data
                Object[] data = {orf.getStart(), orf.getStop(), orf.getStrand(), lastSeqID};
                String insertQuery = genInsert(table, cols, data);
                int id = handler.insert(insertQuery);
                //het opslaan van de AUTO_INCREMENT ID's (voor het koppelen van de BLAST resultaten).
                ORFIDs.put(orf.getID(), id);
            } catch (SQLException ex) {
                showError("Not able to insert into ORF table");
            }
        }
    }

    /**
     * Deze methode slaat de door de gebruiker ingeladen DNA sequentie op in de
     * database
     *
     * @param dnaObj Het DNA object met daarin een DNA sequentie.
     */
    public static void save(DNASequence dnaObj) {
        try {
            //invoegen van de data
            String seq = dnaObj.getSequenceAsString().toLowerCase().replace("mrna", ""); //do not save mRNA prefix
            String table = "sequence";
            String[] cols = {"header", "sequence"};
            Object[] data = {dnaObj.getOriginalHeader(), seq};
            String insertQuery = genInsert(table, cols, data);
            //het opslaan van de AUTO_INCREMENT ID's (voor het koppelen van de ORF's)
            lastSeqID = handler.insert(insertQuery);
        } catch (SQLException ex) {
            showError("Not able to insert into sequence table");
        }
    }

    /**
     * Deze methode ontvangt het HIT object en het ORF id en gebruikt deze om de
     * informatie te extraheren die opgeslagen moet worden in database.
     *
     * @param hit Een HIT object waarvan de data opgeslagen moet worden.
     * @param ORFID Een ORFobject waarbij de BLAST hit hoort.
     * @return Retouneert de data die ingevoegd moet worden.
     */
    private static Object[] extractHitData(Hit hit, String ORFID) {
        Hsp specs = hit.iterator().next();
        Object[] data = {specs.getHspBitScore(), specs.getHspEvalue(), specs.getHspHseq(),
            specs.getHspIdentity(), specs.getHspPositive(), specs.getHspGaps(), ORFIDs.get(ORFID), hit.getHitAccession()};
        return data;
    }

    /**
     * Deze methode controleert of de header langer is dan 100 karakters en als
     * dit het geval is wordt deze header afkapt tot 100 karakters.
     *
     * @param header De header die ingevoegd moet worden.
     * @return de eventueel ingekorte header.
     */
    private static String fitHeader(String header) {
        if (header.length() > 100) {
            return header.substring(0, 100);
        } else {
            return header;
        }
    }

    /**
     * Deze methode genereert een query voor het ophalen van data uit de
     * database.
     *
     * @param table De tabel waar de data uit opgehaald moet worden.
     * @param target Kolom die aan een voorwaarde moet voldoen
     * @param col De kolom waarde die opgehaald moet worden.
     * @param condition De conditie waar de target kolom aan moet voldoen.
     * @return Een query die de gewenste data opgehaald uit de database.
     */
    private static String genRetr(String table, String target, String col, String condition) {
        String query = SEL + col + FROM + table + WHERE + target + EQUALS + QUOTE + condition + QUOTE;
        return (query);
    }

    /**
     * Deze methode genereert een insert querty waarmee data in de databse kan
     * worden ingevoegd.
     *
     * @param table De table waarin de data moet worden ingevoegd.
     * @param cols Een String array met daarin de kolommen waar de data in
     * opgeslagen moet worden
     * @param data Een Object array met daarin de data die ingevoegd moet worden
     * (overeenkomend met de kolom volgorde).
     * @return Een insert query waarmee de gewenste data kan worden opgeslagen
     * in de database.
     */
    private static String genInsert(String table, String[] cols, Object[] data) {
        String query = INS + table + OPEN
                + Arrays.toString(cols).replaceAll("\\[|\\]", "")
                + CLOSE + VAL + OPEN;
        for (int i = 0; i < data.length; i++) {
            if (i != data.length - 1) {
                query += QUOTE + data[i] + QUOTE + COMMA;
            } else {
                query += QUOTE + data[i] + QUOTE + CLOSE;
            }
        }
        return (query);
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

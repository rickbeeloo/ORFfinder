/*
Datum laatste update: 31-03-17
Projectgroep 12: Enrico Schmitz, Thomas Reinders en Rick Beeloo
Functionaliteit: De gebruiker kan een FASTA bestand inladen. In de sequentie
			     kunnen vervolgens ORF's gezocht worden die verder geannoteerd 
			     kunnen worden door gebruikt te maken van een BLAST search.
Bekende bugs:    Als de gebruiker het tijdelijke BLAST bestand verwijderd zal de
                 data niet opgeslagen kunnen worden in de database.

 */
package AnnotationViewer.ORFSearching;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

/**
 * Deze class functioneert in het markeren van substirngs in strings en vervolgens
 * het opzetten van een listener die controleerd of de gebruiker op een gemarkeerd gebied
 * klikt.
 * @author projectgroep 12
 */
public class ORFhighLighter {

    //class variabele
    public static final Color markColor = Color.decode("#F59FE9"); //lichtblauwe kleur

    //instantie variabele
    private ArrayList<ORFSequence> targets;
    private JTextArea textArea;
    private Boolean[] highlighted;
    private ORFSequence[] markedORFs;
    private Highlighter highLighter;
    private Highlighter.HighlightPainter painter;
    private int inputLength;

    /**
     * Constructor
     * @param ORFs Een ArrayList met ORF objecten waar zich de sequenties bevinden die gemarkeerd moeten worden.
     * @param targetTextArea De JTextArea waarin de tekst staat waarin de substring gemarkeerd moeten worden.
     */
    public ORFhighLighter(ArrayList<ORFSequence> ORFs, JTextArea targetTextArea) {
        targets = ORFs;
        textArea = targetTextArea;
        inputLength = targetTextArea.getText().length();
        highlighted = new Boolean[inputLength];
        markedORFs = new ORFSequence[inputLength];
    }

    /**
     * Deze methode markeert alle gevonden ORF's in de textArea waar de
     * sequenties zich in bevinden. Aan deze markeringen wordt tevens een
     * listener toegevoegd die "luistert" of de gebruiker op een markeerd gebied
     * klikt.
     */
    public void highlight() {
        buildHighlighter();
        removePreviousListener(); //Als de gebruiker een nieuw bestand inlaad moet de vorige listener verwijderd worden.
        startListener();
        for (ORFSequence target : targets) {
            String targetSeq = target.getAAseq();
            String txt = textArea.getText();
            try {
                int p0 = txt.indexOf(targetSeq); //start index van ORF
                while (p0 >= 0) {
                    int p1 = p0 + targetSeq.length();
                    highLighter.addHighlight(p0, p1, painter);
                    for (int i = p0 + 1; i < p1; i++) {
                        markedORFs[i] = target;
                        highlighted[i] = true;
                    }
                    p0 = txt.indexOf(targetSeq, p0 + 1);
                }
            } catch (BadLocationException ex) {
                showError("Cannot find text to highlight");
            }
        }
    }

    /**
     * Deze methode verwijderd de vorige caretListener als een gebruiker een
     * andere file inlaadt.
     */
    private void removePreviousListener() {
        highLighter.removeAllHighlights();
        for (CaretListener listener : textArea.getCaretListeners()) {
            textArea.removeCaretListener(listener);
        }
    }

    /**
     * Deze methode voegt een listener toe aan de JTextArea waarin de ORF's
     * gemarkeerd zijn en roept de action methode aan als de gebruiker op een
     * markering klikt.
     */
    private void startListener() {
        textArea.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                try {
                    if (e.getDot() == e.getMark() && highlighted[e.getDot()]) {
                        action(e.getDot());
                    }
                } catch (Exception ex) {
                    //*ignogre*
                    //De gebruiker klikt naast de markering
                }

            }
        });
    }

    /**
     * Deze methode bouwt de HighLighter.
     */
    private void buildHighlighter() {
        highLighter = textArea.getHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(markColor);
    }

    /**
     * Deze methode wordt aangeroepen als de gebruiker op een markering klikt en
     * laat de kenmerken van deze markering in een pop-up zien.
     *
     * @param loc De locatie van de aangeklikte markering.
     */
    private void action(int loc) {
        ORFPopUp popUp = new ORFPopUp(markedORFs[loc]);
        popUp.show();
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

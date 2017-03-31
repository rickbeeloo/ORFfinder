/*
Datum laatste update: 31-03-17
Projectgroep 12: Enrico Schmitz, Thomas Reinders en Rick Beeloo
Functionaliteit: De gebruiker kan een FASTA bestand inladen. In de sequentie
			     kunnen vervolgens ORF's gezocht worden die verder geannoteerd 
			     kunnen worden door gebruikt te maken van een BLAST search.
Bekende bugs:    Als de gebruiker het tijdelijke BLAST bestand verwijderd zal de
                 data niet opgeslagen kunnen worden in de database.

 */
package AnnotationViewer.Blast;

import AnnotationViewer.ORFSearching.ORFSequence;
import AnnotationViewer.ORFSearching.SpringUtilities;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * Deze class bouwt het BLAST input formulier waarin de gebruiker de gewenste
 * BLAST parameters kan ingeven.
 *
 * @author projectgroep 12
 */
public class BLASTInputForm {

    //instantie variabele
    private JFrame frame;
    private ORFSequence ORFobj;

    /**
     * Constructor
     *
     * @param inputORFObj ORFobject waarin de sequentie zit die geBLAST moet
     * worden.
     */
    public BLASTInputForm(ORFSequence inputORFObj) {
        ORFobj = inputORFObj;
    }

    /**
     * Deze methode zorgt voor het weergeven van het BLAST input formulier.
     */
    public void show() {
        buildFrame();
    }

    /**
     * Deze methode bouwt de basis van het frame en roept de methode aan die
     * vervolgens alle verdere keuzes toevoegd.
     */
    private void buildFrame() {
        frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(600, 300);
        frame.setName("BLAST_pop_up");
        addParameterChoices();
        frame.setVisible(true);
    }

    /**
     * Deze methode zorgt voor het toevoegen van alle componenten aan het Frame
     * die de gebruiker in staat stellen om de parameters voor de BLAST search
     * in te stellen.
     */
    //edited from https://docs.oracle.com/javase/tutorial/uiswing/layout/spring.html
    private void addParameterChoices() {
        JPanel panel = new JPanel(new SpringLayout());
        ArrayList<JLabel> labels = createLabels(new String[]{"Program", "Database", "E-value cut-off", "Sequence"});
        ArrayList<JComboBox> comboBoxes = createComboFields(new String[][]{{"blastp", "tblastn"}, {"nr", "swissprot"}});
        ArrayList<JTextField> textFields = createInputFields(new String[]{"", ORFobj.getAAseq()}, 10);

        ArrayList<JComponent> inputFields = mergeBoxesAndFields(comboBoxes, textFields);
        addPairs(panel, labels, inputFields);
        SpringUtilities.makeCompactGrid(panel, 4, 2, 6, 6, 6, 6);
        frame.add(panel);
        addBlastAction(comboBoxes.get(0), comboBoxes.get(1), textFields.get(0));
    }

    /**
     * Deze methode ontvangt een 2D array met daarin de data voor de ComboBoxes
     *
     * @param text een 2D array met daarin de text die opgenomen moet worden als
     * keuze bij iedere JComboBox
     * @return Retouneert een ArrayList met de JComboBoxes met de gewenste text.
     */
    private ArrayList<JComboBox> createComboFields(String[][] text) {
        ArrayList<JComboBox> comboBoxes = new ArrayList<>();
        for (int i = 0; i < text.length; i++) {
            comboBoxes.add(createComboBox(text[i]));
        }
        return comboBoxes;
    }

    /**
     * Deze methode ontvangt een String array met daarin de text voor ieder
     * textField en maakt hiermee textFields aan.
     *
     * @param txt De text die opgenomen moet worden per JTextField.
     * @param size De grootte de jTextFields.
     * @return Retouneert een ArrayList met daarin de JTextFields.
     */
    private ArrayList<JTextField> createInputFields(String[] txt, int size) {
        ArrayList<JTextField> textFields = new ArrayList<>();
        for (int i = 0; i < txt.length; i++) {
            textFields.add(new JTextField(txt[i], size));
        }
        return textFields;
    }

    /**
     * Deze methode maakt een nieuwe ArrayList met daarin de JComboBoxen en
     * JTextFields.
     *
     * @param boxes Een ArrayList met daarin JComboBox objecten.
     * @param fields Een ArrayList met daarin JTextFields.
     * @return Retouneert een ArrayList waarin zowel de boxes als fields zitten.
     */
    private ArrayList<JComponent> mergeBoxesAndFields(ArrayList<JComboBox> boxes, ArrayList<JTextField> fields) {
        ArrayList<JComponent> merged = new ArrayList<>();
        merged.addAll(boxes);
        merged.addAll(fields);
        return merged;
    }

    /**
     * Deze methode creeert de labels die aangeven welke keuzes de gebruiker kan
     * maken.
     *
     * @return Retouneert een ArrayList van JLabels terug.
     */
    private ArrayList<JLabel> createLabels(String[] txt) {
        ArrayList<JLabel> labels = new ArrayList<>();
        for (int i = 0; i < txt.length; i++) {
            labels.add(new JLabel(txt[i], JLabel.TRAILING));
        }
        return labels;
    }

    /**
     * Deze methode zorgt voor het combineren van het label met het
     * daarbijbehorende component.
     *
     * @param panel Het JPanel waarop alle paren (label + input component)
     * weergegeven moeten worden.
     * @param labels De jLabels die gebruikt moeten worden bij de paren.
     * @param inputs De input componenten (JTextFields/JComboBoxes) die gebruikt
     * moeten worden bij de paren.
     */
    private void addPairs(JPanel panel, ArrayList<JLabel> labels, ArrayList<JComponent> inputs) {
        for (int i = 0; i < labels.size(); i++) {
            JLabel label = labels.get(i);
            JComponent inputField = inputs.get(i);
            panel.add(label);
            label.setLabelFor(inputField);
            panel.add(inputField);
        }
    }

    /**
     * Deze methode zorgt voor het maken van een "RUN BLAST" button en koppelt
     * hier tevens ook een ationListener aan vast. Bij het klikken op de button
     * wordt de blast search uitgevoerd.
     *
     * @param programField De JCombobox waarin de gebruiker het BLAST programma
     * aangeeft.
     * @param dbField De JCombobox waarin de gebruiker de database waartegen
     * geblast moet worden aangeeft.
     * @param EvalField Het JTextField waarin de E-value cut-off staat.
     * @param seqField het JTextField waarin de ORF sequentie staat die geblast
     * moet worden.
     * @return Geeft een JPanel terug met daarin de BLAST button.
     */
    private void addBlastAction(JComboBox programField, JComboBox dbField, JTextField EvalField) {
        JPanel submitPanel = new JPanel();
        JButton blastButton = new JButton("RUN BLAST");
        blastButton.addActionListener((ActionEvent e) -> {
            try {
                String Eval = EvalField.getText();
                if (Eval.length() > 0 && isDouble(Eval)) {
                    Blast blast = new Blast(
                            ORFobj.getAAseq(),
                            (String) programField.getSelectedItem(),
                            (String) dbField.getSelectedItem(),
                            Double.parseDouble(EvalField.getText()),
                            10);
                    blast.sendRequest();
                    BlastJobManager.addJob(blast, ORFobj.getID());
                    frame.dispose();
                } else {
                    showError("Please provide a valid E-value cut-off.");
                }
            } catch (Exception ex) {
                showError("An unexpected error occured.");
            }
        });
        submitPanel.add(blastButton);
        frame.add(submitPanel);
    }

    /**
     * Deze methode controleerd of een String omgezet kan worden naar een Double
     *
     * @param input Een input String object waarvan gecontroleerd moet worden of
     * het een Double is.
     * @return Geeft een Boolean terug waarbij true aangeeft dat de String kan
     * worden omgezet naar een Double
     */
    private Boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Deze methode maakt een JComboBox aan met daarin de meegegeven items.
     *
     * @param items De items die in de JComboBox moeten verschijnen.
     * @return Een JComboBox met daarin de gegeven items.
     */
    private JComboBox createComboBox(String[] items) {
        return (new JComboBox<>(items));
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

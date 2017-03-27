/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.Blast;

import AnnotationViewer.GUI.ActionHandler;
import AnnotationViewer.FileLoading.FileHandler;
import AnnotationViewer.ORFSearching.ORFSequence;
import AnnotationViewer.ORFSearching.SpringUtilities;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * Deze class bouwt het BLAST input formulier waarin de gebruiker de gewenste BLAST
 * parameters kan ingeven. 
 * @author projectgroep 12
 */
public class BLASTInputForm {

    //instantie variabele
    private JFrame frame;
    private ORFSequence ORFobj;
  

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
        //maken van het panel
        JPanel panel = new JPanel(new SpringLayout());

        //maken van de labels
        JLabel[] labels = getLabels();
        
        //maken van de input fields
        JComboBox programField = createComboBox(new String[]{"blastp", "tblastn"}); //tblastn and tblastx could be added but make no sense here
        JComboBox dbField = createComboBox(new String[]{"nr", "swissprot"});
        JTextField EvalField = new JTextField(10);
        JTextField seqField = new JTextField(ORFobj.getAAseq(), 10); //prefill this with the ORF sequence
        seqField.setEditable(false); //do not let the user change the AA sequence

        //alles toevoegen aan het panel
        Component[] inputFields = {programField, dbField, EvalField, seqField}; 
        addPairs(panel,labels, inputFields);
        
        //het maken van een BLAST button met daaraan de BLAST actie gekoppeld
        JPanel submitPanel = addBlastAction(programField, dbField, EvalField, seqField);
              
        //lay out van het panel vast stellen
        SpringUtilities.makeCompactGrid(panel,
                4, 2,  //rijen, kolommen
                6, 6,  //initX, initY
                6, 6); //xPad, yPad

        frame.add(panel);
        frame.add(submitPanel);
    }
    
    

    /**
     * Deze methode creeert de labels die aangeven welke keuzes 
     * de gebruiker kan maken.
     * @return Geeft een array van JLabels terug.
     */
    private JLabel[] getLabels() {
        JLabel[] labels = new JLabel[4];
        String[] txt = {"Program","Database","E-value cut-off","Sequence"};
        for (int i=0; i<4; i++) {
           labels[i] = new JLabel(txt[i], JLabel.TRAILING);
        }
        return labels;
    }
    
    /**
     * Deze methode zorgt voor het combineren van het label met het daarbijbehorende 
     * component. 
     * @param panel Het JPanel waarop alle paren (label + input component) weergegeven moeten worden.
     * @param labels De jLabels die gebruikt moeten worden bij de paren.
     * @param inputs De input componenten (JTextFields/JComboBoxes) die gebruikt moeten worden bij de paren.
     */
    private void addPairs(JPanel panel, JLabel[] labels, Component[] inputs) {
        for (int i=0; i<4; i++) {
            JLabel label = labels[i];
            Component inputField = inputs[i];
            panel.add(label);
            label.setLabelFor(inputField);
            panel.add(inputField);
        }
    }
    
    /**
     * Deze methode zorgt voor het maken van een "RUN BLAST" button en koppelt hier tevens ook een ationListener
     * aan vast. Bij het klikken op de button wordt de blast search uitgevoerd.
     * @param programField De JCombobox waarin de gebruiker het BLAST programma aangeeft.
     * @param dbField De JCombobox waarin de gebruiker de database waartegen geblast moet worden aangeeft.
     * @param EvalField Het JTextField waarin de E-value cut-off staat.
     * @param seqField het JTextField waarin de ORF sequentie staat die geblast moet worden.
     * @return Geeft een JPanel terug met daarin de BLAST button. 
     */
    private JPanel addBlastAction(JComboBox programField, JComboBox dbField, JTextField EvalField, JTextField seqField) {
        JPanel submitPanel = new JPanel();
        JButton blast = new JButton("RUN BLAST");
        blast.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String Eval = EvalField.getText();
                    if (Eval.length()> 0 && isDouble(Eval)) {
                        ActionHandler handler = new ActionHandler();
                        handler.PerfromBlastAction(
                                ORFobj,
                                FileHandler.saveFile("output_" + ORFobj.getID() + ".txt").getAbsolutePath(),//open temp output
                                (String) programField.getSelectedItem(),
                                (String) dbField.getSelectedItem(),
                                Double.parseDouble(EvalField.getText()),
                                10 //default top 10 hits due to performance                
                        );
                        frame.dispose();
                    }
                    else {
                        showError("Please provide a valid E-value cut-off.");
                    }

                } catch (IOException ex) {
                    //*ignore* De gebruiker heeft geen outputfile opgegeven 
                }
            }
        });
   submitPanel.add(blast);
   return submitPanel;
    }

    /**
     * Deze methode controleerd of een String omgezet kan worden naar een Double
     * @param input Een input String object waarvan gecontroleerd moet worden of het een Double is.
     * @return Geeft een Boolean terug waarbij true aangeeft dat de String kan worden omgezet naar een Double
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
     * @param items De items die in de JComboBox moeten verschijnen.
     * @return Een JComboBox met daarin de gegeven items.
     */
    private JComboBox createComboBox(String[] items) {
        return (new JComboBox<>(items));
    }
    
    /**
     * Deze methode laat een Error pop-up zien met daarin het meegegeven bericht.
     * @param mssg Het bericht dat weergegeven moet worden in de pop-up.
     */
    private void showError(String mssg) {
        JOptionPane.showMessageDialog(null, mssg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

}

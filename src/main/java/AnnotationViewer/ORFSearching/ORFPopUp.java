/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.ORFSearching;

import AnnotationViewer.Blast.BLASTInputForm;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;


/**
 * Deze class bouwt een ORF pop up window waarin de kenmerken van het ORF komen te staan.
 * @author projectgroep 12
 */
public class ORFPopUp {

    //instantie variabele
    private JFrame frame;
    private ORFSequence ORFobj;
    
    /**
     * Constructor
     * @param inputORFobj ORFSequence object waarvan de data in het pop-up venster moet worden
     * weergegeven.
     */
    public ORFPopUp(ORFSequence inputORFobj) {
        ORFobj = inputORFobj;
    }

    /**
     * Deze methode zorgt voor het weergeven van een JFrame met daarin
     * de kenmerken van het gevonden ORF.
     */
    public void show() {
        frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(600, 300);
        addTable();
        addBlastButton();
        frame.setVisible(true);
    }

    /**
     * Deze methode maakt maakt een Jtable met daarin de kenmerken van het ORF en voegt deze
     * vervolgens toe aan het JFrame.
     */
    private void addTable() {
        Object rowData[][]
                = {
                    {"ORF ID: ", ORFobj.getID()},
                    {"ORF AA seq: ", ORFobj.getAAseq()},
                    {"ORF start pos: ", ORFobj.getStart()},
                    {"ORF stop pos: ", ORFobj.getStop()},
                    {"ORF strand: ", ORFobj.getStrand()},
                  };
        Object columnNames[] = {"", ""};
        JTable table = createImmutableTable(rowData, columnNames);
        JScrollPane panel = new JScrollPane(table);
        frame.add(panel, BorderLayout.CENTER);
    }

    /**
     * Deze methode zorgt voor het aanmaken van een JTable die niet meer 
     * te bewerken is. 
     * @param rowData een Object[][] waarin de data staat die in de tabel moet.
     * @param columnNames een Object[] waarin de kolomnamen van de tabel staan.
     * @return een Jtable die niet te bewerken is.
     */
    private JTable createImmutableTable(Object[][] rowData, Object[] columnNames) {
        //aangepast van http://stackoverflow.com/questions/9919230/disable-user-edit-in-jtable
        JTable table = new JTable(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        ;
        };
        table.getTableHeader().setUI(null); 
        return table;
    }

    /**
     * Deze methode zorgt voor het toevoegen van een BLAST button aan het JFrame. 
     * Deze button heet een ActionListener die de actie doorgeeft aan de ActionHandler.
     */
    private void addBlastButton() {
        JButton blast = new JButton("BLAST");
        blast.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BLASTInputForm form = new BLASTInputForm(ORFobj);
                form.show();
                frame.dispose();
            }
        });
        frame.add(blast);
    }
}

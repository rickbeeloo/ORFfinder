/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.FileLoading;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *Deze class if verantwoordelijk voor het open, save en sluit acties
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
    
    /**
     * Deze methode opent een opent een save diaglog waarbij het bestand standaard 
     * "output.txt" wordt genoemd.
     * @return een File object
     * @throws IOException Gooit een exception als het geselcteerde bestand niet als output
     * gebruikt kan worden.
     */
    public static File saveFile() throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setSelectedFile(new File("output.txt"));
        chooser.setFileFilter(new FileNameExtensionFilter("text file", "txt"));
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        else {
          throw new IOException(); //geen bestand geselecteerd.
        }
    }
   
    /**
     * Deze methode opent een opent een save diaglog waarbij het bestand standaard 
     * een naam krijgt op basis van het meegegeven String object.
     * @param outputTitle Een String object met daarin de gewenste titel van het output bestand.
     * @return een File object
     * @throws IOException Gooit een exception als het geselcteerde bestand niet als output
     * gebruikt kan worden.
     */
    public static File saveFile(String outputTitle) throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setSelectedFile(new File(outputTitle));
        chooser.setFileFilter(new FileNameExtensionFilter("text file", "txt"));
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        else {
         throw new IOException(); //geen bestand geselecteerd.
        }
        
    }
     
     
    /**
     * Deze methode laat een Error pop-up zien met daarin het meegegeven bericht.
     * @param mssg Het bericht dat weergegeven moet worden in de pop-up.
     */
    private static void showError(String mssg) {
        JOptionPane.showMessageDialog(null, mssg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
    
    
}

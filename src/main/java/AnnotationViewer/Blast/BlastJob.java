/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnnotationViewer.Blast;

import java.util.Objects;

/**
 * Deze class dient als wrapper voor een Blast object naardat deze als request
 * is verstuurd naar de NCBI server.
 * @author projectogroep 12
 * 
 */
public class BlastJob {

    //instantie variabele
    private Blast BLASTObj;
    private String jobID;

    public BlastJob(Blast BLASTdata, String ID) {
        BLASTObj = BLASTdata;
        jobID = ID;
    }
    
    /**
     * @return retouneert de status van de BLAST job. Dus of de NCBI server
     * de resultaten al heeft teruggestuurd naar de applicatie of niet.
     */
    public Boolean checkStatus() {
        return BLASTObj.checkStatus();
    }
        
    /**
     * @return retouneert het job ID.
     */
    public String getID() {
        return jobID;
    }
    
    /**
     * @return retouneert het Blast object
     */
    public Blast getBlastObj() {
        return BLASTObj;
    }
    
    
    /**
     * Deze methode vergelijkt twee Blast objecten op basis van hun job ID, dit
     * job ID is afgeleid van het ORF ID.
     * @param o Een Blast object om mee te vergelijken.
     * @return retouneert True als beide objecten hetzelfde Blast job ID hebben.
     */
    @Override
    public boolean equals(Object o) {
        BlastJob compareObj = (BlastJob)o;
        return(compareObj.getID().equals(this.getID())); 
    }

    /**
     * @return De hashcode van een BlastJob object op basis van het Blast job ID.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.getID()); 
        return hash;
    }
    
   

}

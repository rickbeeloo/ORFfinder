/*
Datum laatste update: 31-03-17
Projectgroep 12: Enrico Schmitz, Thomas Reinders en Rick Beeloo
Functionaliteit: De gebruiker kan een FASTA bestand inladen. In de sequentie
			     kunnen vervolgens ORF's gezocht worden die verder geannoteerd 
			     kunnen worden door gebruikt te maken van een BLAST search.
Bekende bugs:    Als de gebruiker het tijdelijke BLAST bestand verwijderd zal de
                 data niet opgeslagen kunnen worden in de database.

 */
package AnnotationViewer.DataStorage;

/**
 * Deze exception wordt gegooid als de gebruiker een te lange sequentie inlaad. 
 * @author projectgroep12
 */
public class LengthException extends Exception{}

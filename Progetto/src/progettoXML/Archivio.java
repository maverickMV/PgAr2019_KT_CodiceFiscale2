package progettoXML;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class Archivio 
{
	ArrayList <Persona> persone = new ArrayList<Persona>();		// arrayList con i dati del primo file di input
	ArrayList <String> codiciFiscali = new ArrayList<String>();		// arrayList con i codici fiscali da controllare
	ArrayList <String> codFiscUnmatch = new ArrayList<String>();		// arrattyList per i codiciFiscali che non hanno un riscontro nel file "codiciFiscali.xml"
	ArrayList <String> codFiscInvalidi = new ArrayList<String>();
	
	public Archivio ()
	{
		XMLInputFactory xmlif = null;
		XMLStreamReader xmlr = null;
		try 
		{
			xmlif = XMLInputFactory.newInstance();
			xmlr = xmlif.createXMLStreamReader("inputPersone.xml", new FileInputStream("inputPersone.xml"));
		}
		catch (Exception e)
		{
			System.out.println("Errore file non trovato");			// messaggio di errore se non riesce a caricare il file
		}
		try
		{
			while (xmlr.hasNext())
			{
				if (xmlr.getEventType() == XMLStreamConstants.START_ELEMENT)
				{
					if (xmlr.getLocalName() == "persona")
					{
						String[] valori = new String[5];
						for (int i = 0; i <5; i++)
						{
							xmlr.next();
							xmlr.next();
							xmlr.next();
							valori[i] = xmlr.getText();
							xmlr.next();
						}
						persone.add(new Persona(valori[0], valori[1], valori[2], valori[3], valori[4]));
						
						
						xmlr.next(); 
					}
				}
				
				xmlr.next();
			}
		}
		catch (Exception e)
		{
			System.out.println("Errore di compilamento");	// messaggio di errore se si verifica un'errore durante l'acquisizione di dati dal file
		}
		
		
		
		setCodiciFiscali();	// per impostare l'arrayList dei codici fiscali presi in input dal file "codiciFiscali.xml"
		confrontoCodiciFiscali(); // per confrontare i codici Fiscali
		creaOutputFile(); // per creare l'output per la fase 4 dell'esercizio
		
		
	} // chiusura Costruttore 
	
	public void setCodiciFiscali ()		// per riempire il secondo arrayList
	{
		XMLInputFactory xmlif = null;
		XMLStreamReader xmlr = null;
		
		try 
		{
			xmlif = XMLInputFactory.newInstance();
			xmlr = xmlif.createXMLStreamReader("codiciFiscali.xml", new FileInputStream("codiciFiscali.xml"));		// carica il file codicifiscali.xml
		}
		catch (Exception e)
		{
			System.out.println("Errore file con i codici fiscali non trovato");			// messaggio di errore se non riesce a caricare il file
		}
		try
		{
			while (xmlr.hasNext())			// ciclo per acquisire tutti i dati del file
			{
				if (xmlr.getEventType() == XMLStreamConstants.START_ELEMENT)
				{
					if (xmlr.getLocalName() == "codice")		// verifica il nome del tag di apertura e succesivamente memorizza il contenuto testuale
					{
						xmlr.next();
						
							
						
						if (testCodiceFiscale(xmlr.getText()))		// se il codice fiscale è valido lo carica sull'arrayList
							codiciFiscali.add(xmlr.getText());
						else
							codFiscInvalidi.add(xmlr.getText());
					}
				}
				xmlr.next();
			}
		}
		catch (Exception e)
		{
			System.out.println("Errore scansione codici fiscali");
		}
	}
	
	public boolean testCodiceFiscale (String codice)		// metodo per la verifica dell'adeguatezza del codice fiscale
	{
		
		int[] posizioniNum = new int[] {6,7,9,10,12,13,14};
		int[] posizioniLett = new int[] {0,1,2,3,4,5,8,11,15};
		char[] valoreMese = new char[] {'A','B','C','D','E','H','L','M','P','R','S','T'};
		int[] numeroGiorniMese = new int[] {31,28,31,30,31,30,31,31,30,31,30,31};
		boolean cond = true;
		String controllo;		// usata per controllare la validità di nome e congome
		String consControllo;
		String vocControllo;
		
		//CONTROLLO LUNGHEZZA CODICE
		if (codice.length() != 16)
			return false;
		//CONTROLLO POSIZIONE NUMERI
		for (int i : posizioniNum)	//verifico che ci siano i numeri alle posizioni giuste
		{
			cond = cond && (codice.charAt(i) >= '0' && codice.charAt(i) <= '9');
		}
		if (!cond)		// condizione che ci siano i numeri alle posizioni giuste
			return false;
		// CONTROLLO POSIZIONE LETTERE
		for (int i : posizioniLett)	//verifico che ci siano le lettere alle posizioni giuste
		{
			cond = cond && (codice.charAt(i) >= 'A' && codice.charAt(i) <= 'Z');
		}
		if (!cond)		// condizione che ci siano le lettere alle posizioni giuste
			return false;
		
		//CONTROLLO MESE
		cond = false;		// settaggio necessario per far funzionare il successivo costrutto for con '||'
		for (int i = 0; i <valoreMese.length; i++)		// ciclo che verifica che la lettera per il mese usato sia valida
		{
			cond = cond || (valoreMese[i] == codice.charAt(8));
			if (cond)		// controlla che il numero di giorni sia giusto
			{
				int giorno = ((int)(codice.charAt(9))-48)*10;
				giorno += (int)(codice.charAt(10))-48-40;
				
				if (giorno >0)		// passaggio necessario per evitare errore nel caso il codiceFiscale sia di una donna (perchè in quel caso avrebbe la data
				{					// aumentata di 40
					
				}
				else
					giorno += 40;
				if (giorno > numeroGiorniMese[i])
					return false;
			}
		}
		if (!cond)		// test per il carattere mese
			return false;
		
		// CONTROLLO CARATTERE DI CONTROLLO
		// viene utlizzato un elemento qualsiasi dell'arrayList persone perchè serve solo il metodo contenuto dentro di esso
		cond = persone.get(0).getCodiceFiscale().setcControllo(codice.substring(0, 15)) == codice.charAt(15); // test per vedere se il carattere di controllo è giusto
		if (!cond)		// controllo condizione precedente
			return false;
		
		// CONTROLLO COGNOME
		controllo = codice.substring(0, 3);		
		consControllo = persone.get(0).getCodiceFiscale().getConsonanti(controllo);
		vocControllo = persone.get(0).getCodiceFiscale().getVocali(controllo);
		if (!controllo.equals(consControllo+vocControllo))		// se le vocali nonsono state messe dopo le consonanti ritorna FALSE
			return false;
		//CONTROLLO NOME
		controllo = codice.substring(3, 6);
		consControllo = persone.get(0).getCodiceFiscale().getConsonanti(controllo);
		vocControllo = persone.get(0).getCodiceFiscale().getVocali(controllo);
		if (!controllo.equals(consControllo+vocControllo))		// se le vocali nonsono state messe dopo le consonanti ritorna FALSE
			return false;
		
		return true;		// nel caso tutti i test hanno un riscontro positivo ritorna il valore TRUE
		
	}
	public void confrontoCodiciFiscali()	// per terminare la fase 3 e ottenere l'elenco dei CodiciFiscali presenti
	{
		boolean cond = false;
		for (int i = 0; i < persone.size(); i++)
		{
			cond = false;
			for (int j = 0; j< codiciFiscali.size(); j++)
			{
				cond = cond || (persone.get(i).getCodiceFiscale().getCodiceFiscale().equals(codiciFiscali.get(j)));
				
			}
			if (!cond)		// se non c'è stato alcun riscontro setta il valore di codice fiscale a "ASSENTE"
			{
				codFiscUnmatch.add(persone.get(i).getCodiceFiscale().getCodiceFiscale());
				persone.get(i).getCodiceFiscale().setCodiceFiscale("ASSENTE");
			}
		}
	}
	
	public void creaOutputFile ()	// metodo per creare l'output per la fase 4 dell'esercizio			da inserire poi nel COSTRUTTORE
	{
		XMLOutputFactory xmlof = null;
		XMLStreamWriter xmlw = null;
		
		try
		{
			xmlof = XMLOutputFactory.newInstance();
			xmlw = xmlof.createXMLStreamWriter(new FileOutputStream("codiciPersone.xml"), "utf-8");
			xmlw.writeStartDocument("utf-8","1.0");
		}
		catch (Exception e)
		{
			System.out.println("Errore nell'inizializzazione del writer");
		}
		try
		{
			xmlw.writeCharacters("\n");
			xmlw.writeStartElement("output");
			
			xmlw.writeCharacters("\n\t");
			
			xmlw.writeStartElement("persone");
			xmlw.writeAttribute("numero", Integer.toString(persone.size()));
			
			for (int i = 0; i < persone.size(); i++)
			{
				xmlw.writeCharacters("\n\t\t");
				xmlw.writeStartElement("persona");
				xmlw.writeAttribute("id", Integer.toString(i));
				xmlw.writeCharacters("\n\t\t\t");
					xmlw.writeStartElement ("nome");
						xmlw.writeCharacters(persone.get(i).getNome());
					xmlw.writeEndElement(); // chiusura tag NOME
					xmlw.writeCharacters("\n\t\t\t");	
					xmlw.writeStartElement("cognome");
						xmlw.writeCharacters(persone.get(i).getCognome());
					xmlw.writeEndElement(); // chiusura tag COGNOME
					xmlw.writeCharacters("\n\t\t\t");
					xmlw.writeStartElement("sesso");
						xmlw.writeCharacters(Character.toString(persone.get(i).getSesso()));
					xmlw.writeEndElement(); // chiusura tag SESSO
					xmlw.writeCharacters("\n\t\t\t");
					xmlw.writeStartElement("comune_nascita");
						xmlw.writeCharacters(persone.get(i).getlNascita());
					xmlw.writeEndElement(); // chiusura tag COMUNE NASCITA
					xmlw.writeCharacters("\n\t\t\t");
					xmlw.writeStartElement("data_nascita");
						xmlw.writeCharacters(persone.get(i).getdNascita());
					xmlw.writeEndElement(); // chiusura tag DATA NASCITA
					xmlw.writeCharacters("\n\t\t\t");
					xmlw.writeStartElement("codice_fiscale");
						xmlw.writeCharacters(persone.get(i).getCodiceFiscale().getCodiceFiscale());
					xmlw.writeEndElement(); // chiusura tag CODICE FISCALE
					xmlw.writeCharacters("\n\t\t");
				xmlw.writeEndElement();		// chiusura tag PERSONA
			}
			xmlw.writeCharacters("\n\t");
			xmlw.writeEndElement();		// chiusura tag PERSONE
			xmlw.writeCharacters("\n\t");
			xmlw.writeStartElement("codici");
			xmlw.writeCharacters("\n\t\t");
			xmlw.writeStartElement("invalidi");
			xmlw.writeAttribute("numero", Integer.toString(codFiscInvalidi.size()));
			for (String valore : codFiscInvalidi)	// ciclo per stampare i codici fiscali invalidi
			{
				xmlw.writeCharacters("\n\t\t\t");
				xmlw.writeStartElement("codice");
					xmlw.writeCharacters(valore);
				xmlw.writeEndElement(); // chiusura tag CODICE
			}
			xmlw.writeCharacters("\n\t\t");
			xmlw.writeEndElement(); // chiusura tag INVALIDI
			xmlw.writeCharacters("\n\t\t");
			xmlw.writeStartElement("spaiati");
			xmlw.writeAttribute("numero", Integer.toString(codFiscUnmatch.size()));	// ciclo per stampare i codici fiscali spaiati
			for (String valore : codFiscUnmatch)
			{
				xmlw.writeCharacters("\n\t\t\t");
				xmlw.writeStartElement("codice");
					xmlw.writeCharacters(valore);
				xmlw.writeEndElement(); // chiusura tag CODICE
			}
			xmlw.writeCharacters("\n\t\t");
			xmlw.writeEndElement(); // chiusura tag INVALIDI
			xmlw.writeCharacters("\n\t");
			xmlw.writeEndElement();		// chiusura tag CODICI
			xmlw.writeCharacters("\n");
			xmlw.writeEndElement();		// chiusura tag RADICE
			
			xmlw.writeEndDocument();
			xmlw.flush();
			xmlw.close();
		}
		catch (Exception e)
		{
			System.out.println("Errore nella creazione del file di output");
		}
	}
}

/*
 * spaiati sono i codici fiscali generati che non combaciano o quelli del file in input che non combnaciano?
 * 
 * sostituire l'oggetto arrayList con Set per codici fiscali
 */

package progettoXML;

import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class CodiceFiscale 
{
	private String codiceFiscale;
	private String nome;
	private String cognome;
	private char sesso;
	private String dNascita;	// data Nascita
	private String lNascita;	// luogo Nascita
	private char cControllo;	// carattere di controllo
	
	
	public CodiceFiscale(String _nome, String _cognome, String _dNascita, char _sesso, String _lNascita)
	{
		this.sesso = _sesso;
		this.nome = setNome(_nome);
		this.cognome = setCognome(_cognome);
		this.dNascita = setdNascita(_dNascita);
		this.lNascita = setlNascita(_lNascita);
		codiceFiscale = cognome+nome+dNascita+lNascita;
		cControllo = setcControllo(codiceFiscale);
		codiceFiscale += cControllo;
	}
	
	public char setcControllo (String codice)		// per generare il carattere di controllo					errore nei calcoli esce la lettera sbagliata
	{
		int somma = 0;
		int[] valoriDispari = new int[] {1,0,5,7,9,13,15,17,19,21,2,4,18,20,11,3,6,8,12,14,16,10,22,25,24,23};
		int pos;
		
		for (int i = 0;i < codice.length(); i++ )
		{
			if (i%2 != 0)		// se la posizione del carattere è PARI
			{
				if (codice.charAt(i) >= 'A' && codice.charAt(i) <= 'Z')
				{
					somma += (int)(codice.charAt(i))-65;
				}
				else
				{
					somma += (int)(codice.charAt(i))-48;
				}
			}
			else	// se la posizione del carattere è DISPARI
			{
				if (codice.charAt(i) >= 'A' && codice.charAt(i) <= 'Z')
				{
					pos = (int)(codice.charAt(i))-65;
					somma += valoriDispari[pos];
				}
				else
				{
					pos = (int)(codice.charAt(i))-48;
					somma += valoriDispari[pos];
				}
			}						// chiusura ELSE esterno
		}	// chiusura FOR
		
		somma = somma%26;
		somma += 65;
		return (char)(somma);
		
	}	// chiusura metodo CARATTERE DI CONTROLLO
	
	private String setlNascita (String parola)	// per la ricerca nel documento comuni.xml
	{
		XMLInputFactory xmlif = null;
		XMLStreamReader xmlr = null;
		String luogo = "";
		
		try 
		{
			xmlif = XMLInputFactory.newInstance();
			xmlr = xmlif.createXMLStreamReader("comuni.xml", new FileInputStream("comuni.xml"));
		}
		catch (Exception e)
		{
			System.out.println("Errore file non trovato");			// messaggio di errore se non riesce a caricare il file
		}
		
		try
		{
			while (luogo.equals(""))
			{
				if (xmlr.getEventType() == XMLStreamConstants.START_ELEMENT)
				{
					if (xmlr.getLocalName() == "comune")
					{
						xmlr.next();
						xmlr.next();
						xmlr.next();
						if (xmlr.getText().equalsIgnoreCase(parola))
						{
							xmlr.next();
							xmlr.next();
							xmlr.next();
							xmlr.next();
							luogo = xmlr.getText();
						}
					}
				}
				xmlr.next();
			}
		}
		catch (Exception e)
		{
			System.out.println("Errore, comune non trovato");	// messaggio di errore se si verifica un'errore durante l'acquisizione di dati dal file
		}
		
		return luogo;		// ritorna il codice associato al comune inserito
	}
	private String setdNascita (String parola)
	{
		char tmp;
		String rimp;
		char mese = setMese(parola.substring(5,7));
		if (sesso == 'M')		// controlla se la Persona è maschio
			return (parola.substring(2, 4)+mese+parola.substring(8));
		else		// se è femmina aggiunge 40 al giorno di nascita
		{
			tmp = parola.charAt(8);
			rimp = (char)(tmp+4)+parola.substring(9);
			return (parola.substring(2, 4)+mese+rimp);
		}
	}
	
	private char setMese (String parola)
	{
		if (parola.charAt(0) == '0' && (parola.charAt(1) >= '1' && parola.charAt(1) <= '5'))
			return ((char)(parola.charAt(1)+16));
		else if (parola.charAt(0)== '0' && parola.charAt(1) == '6')
			return 'H';
		else if (parola.charAt(0) == '0' && (parola.charAt(1) == '7' || parola.charAt(1) == '8'))
			return ((char) (parola.charAt(1)+21));
		else if (parola.charAt(0) == '0' && parola.charAt(1) == '9')
			return 'P';
		else
			return ((char)(parola.charAt(1)+34));
	}
	
	private String setNome (String parola)
	{
		if (parola.length() == 1)
			return (parola+"XX");
		else if (parola.length() == 2)
			return (parola+"X");
		else if (parola.length() == 3)
			return parola;
		else
		{
			String cons = getConsonanti(parola);
			String voc = getVocali(parola);
			if (cons.length() < 3)
				return (cons+voc.substring(0,(3-cons.length())));
			else if (cons.length() > 3)
				return (cons.charAt(0)+cons.substring(2, 4));
			else
				return (cons.substring(0,3));
		}
		
	}
	
	private String setCognome (String parola)
	{
		if (parola.length() == 1)
			return (parola+"XX");
		else if (parola.length() == 2)
			return (parola+"X");
		else if (parola.length() == 3)
			return parola;
		else
		{
			String cons = getConsonanti(parola);
			String voc = getVocali(parola);
			if (cons.length() < 3)
				return (cons+voc.substring(0,(3-cons.length())));
			else
				return (cons.substring(0,3));
		}
	}
	
	public String getConsonanti(String parola)
	{
		char tmp;
		String consonanti = "";
		for (int i= 0; i<parola.length(); i++)
		{
			tmp = parola.charAt(i);
			if (tmp == 'A' || tmp == 'E' || tmp == 'I' || tmp == 'O' || tmp == 'U')
			{
				
			}
			else
			{
				consonanti += tmp;
			}
		} // fine FOR 
		return consonanti;
	}
	public String getVocali(String parola)
	{
		char tmp;
		String vocali = "";
		for (int i= 0; i<parola.length(); i++)
		{
			tmp = parola.charAt(i);
			if (tmp == 'A' || tmp == 'E' || tmp == 'I' || tmp == 'O' || tmp == 'U')
			{
				vocali += tmp;
			}
			else
			{
				
			}
		} // fine FOR 
		return vocali;
	}
	
	public String getNomeCodFisc ()
	{
		return this.nome;
	}
	public String getCognomeCodFisc ()
	{
		return this.cognome;
	}
	public String getdNascita ()
	{
		return dNascita;
	}
	public String getlNascita ()
	{
		return lNascita;
	}
	public char getSesso ()
	{
		return sesso;
	}
	public String getCodiceFiscale ()
	{
		return codiceFiscale;
	}
	
	public void setCodiceFiscale (String valore)
	{
		codiceFiscale = valore;
	}
	
	
}

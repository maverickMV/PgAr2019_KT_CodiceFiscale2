package progettoXML;

public class Persona 
{
	private CodiceFiscale cdFiscale;
	
	private String nome;
	private String cognome;
	private char sesso;
	private String dNascita;	// data Nascita
	private String lNascita;	// luogo Nascita
	
	private char cControllo;	// carattere di controllo
	
	public Persona (String _nome, String _cognome, String _sesso, String _lNascita, String _dNascita)
	{
		this.sesso = _sesso.charAt(0);
		this.nome = _nome;
		this.cognome = _cognome;
		this.dNascita = _dNascita;
		this.lNascita = _lNascita;
		creaCodiceFiscale();
	}
	public void creaCodiceFiscale ()
	{
		cdFiscale = new CodiceFiscale (nome, cognome, dNascita, sesso, lNascita);
	}
	
	public String getNome ()
	{
		return nome;
	}
	
	public String getCognome()
	{
		return cognome;
	}
	
	public String getlNascita()
	{
		return lNascita;
	}
	public String getdNascita()
	{
		return dNascita;
	}
	public char getSesso ()
	{
		return sesso;
	}
	public CodiceFiscale getCodiceFiscale()
	{
		return cdFiscale;
	}
}

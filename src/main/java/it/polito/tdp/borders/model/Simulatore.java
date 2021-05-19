package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {
	
	//modello --> quale è lo stato del sistema a ogni passo 
	
	//per ogni evento: uno stato --> grafo e i suoi vicini
	
	private Graph<Country, DefaultEdge> grafo;
	
	
	//tipi di evento -> coda prioritaria
	
	private PriorityQueue<Evento> queue;
	
	
	//parametri simulazione
	
	private int N_MIGRANTI = 1000;
	private Country partenza;
	
	
	//valori in output
	
	private int T=-1; //numero passi che simuliamo
	private Map<Country, Integer> stanziali; //per ogni country quanti stanziali ci sono
	
	
	
	//inizializzo simulatore
	public void init(Country country, Graph<Country, DefaultEdge> grafo) {
		
		this.partenza=country;
		this.grafo=grafo;
		
		//impostare lo stato iniziale
		this.T=1;
		this.stanziali=new HashMap<Country, Integer>();
		
		for(Country c: this.grafo.vertexSet()) {
			stanziali.put(c, 0);
		}
		
		//creo la coda
		
		this.queue=new PriorityQueue<Evento>();
		
		//inserisco il primo evento
		this.queue.add(new Evento(T, partenza, N_MIGRANTI));
	
		
		
	}
	
	public void run() {
		
		//finchè la coda non si svuota prendo un evento per volta e lo eseguo
		
		Evento e;
		while((e=this.queue.poll()) != null) {
			
			this.T=e.getT(); //T dell'ultimo evento
			
			//simulo evento e
			int nPersone=e.getN();
			Country stato=e.getCountry();
			
			//ottengo i vicini dello stato
			List<Country> vicini= Graphs.neighborListOf(grafo, stato);
			
			//supponiamo n persone:10, si spostano: 5, vicini.size(): 7
			//migrantixStato: 0 perchè viene troncata a int
			
			int migrantixStato=(nPersone/2) / vicini.size();
			
			if(migrantixStato>0) {
				//caso limite del punto 2.4, non faccio niente se è zero
				//qui le persone si possono muovere
				
				for(Country confinante: vicini) { //creo evento per ogni stato confinante a T+1
					
					queue.add(new Evento(e.getT()+1, confinante, migrantixStato));
					
				}
				
			}
			
			int nStanziali = nPersone - migrantixStato*vicini.size();
			this.stanziali.put(stato, this.stanziali.get(stato)+ nStanziali);
		}
	}
	
	public Map<Country, Integer> getStanziali(){
		return this.stanziali;
	}
	
	
	public int getT() {
		return this.T;
	}

}


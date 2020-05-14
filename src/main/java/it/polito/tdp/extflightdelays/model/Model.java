package it.polito.tdp.extflightdelays.model;

import java.util.*;

import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	// i vertici hanno hashcode ed equals implementate quindi conviene definire
	// una IdentityMap
	private Map<Integer, Airport> idMap;
	private ExtFlightDelaysDAO dao;
	// creo una mappa per salvare l'albero di visita e per modellare le relazioni
	// padre figlio
	/*
	 * Userò la mappa per estrarre il percorso
	 */

	private Map<Airport, Airport> visita = new HashMap<>();
	/*
	 * Nel costruttore creo idMap e la riempio
	 */

	public Model() {
		idMap = new HashMap<Integer, Airport>();
		// riempio la mappa, il metodo va modificato per lavorare
		// con l'idMap
		dao = new ExtFlightDelaysDAO();
		dao.loadAllAirports(idMap);
	}

	public void creaGrafo(int x) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// aggiungo i vertici
		for (Airport a : idMap.values()) {
			if (dao.getAirlinesNumber(a) > x) {
				this.grafo.addVertex(a);
			}
		}

		for (Rotta r : dao.getRotte(idMap)) {

			//controllo necessario altrimenti addEdge aggiunge anche i vertici che avevamo escluso 
			//prima di aggiungere un arco è necessario controllare che i vertici siano presenti nell'arco
			
			if (this.grafo.containsVertex(r.getP()) && this.grafo.containsVertex(r.getA())) {
				// prendo l'arco
				DefaultWeightedEdge e = this.grafo.getEdge(r.getP(), r.getA());

				// se questo è null lo aggiungo al grafo altrimenti aggiorno il peso

				if (e == null) {
					Graphs.addEdgeWithVertices(this.grafo, r.getP(), r.getA(), r.getPeso());
				} else {
					double pesovecchio = this.grafo.getEdgeWeight(e);
					double pesoNuovo = pesovecchio + r.getPeso();

					// questo metodo modifica solo il peso dell'arco considerato
					this.grafo.setEdgeWeight(e, pesoNuovo);

				}
			}
		}
	}

	public int nVertici() {
		return this.grafo.vertexSet().size();
	}

	public int nArchi() {
		return this.grafo.edgeSet().size();
	}

	public Collection<Airport> getAereoportiGrafo() {
		return this.grafo.vertexSet();
	}

	public List<Airport> trovaPercorso(Airport p, Airport a) {

		List<Airport> percorso = new ArrayList<Airport>();
		// per scoprire se due aereoporti sono connessi quello che conviene
		// fare è scorrere tutto il grafo e mano a mano scoprire se sono connessi

		// suppongo visita in ampiezza
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.grafo, p);

		// prima di far partire la visita aggancio un TraversalListener
		// per essere notificati ogni volta che lo attraversiamo e salvare mano
		// a mano le visite
		// aggiungo la radice dell'albero di visita
		visita.put(p, null);
		it.addTraversalListener(new TraversalListener<Airport, DefaultWeightedEdge>() {

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			/*
			 * Quando attraversiamo l'arco della visita ci salviamo l'arco
			 */
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {

				Airport sorgente = grafo.getEdgeSource(e.getEdge());
				Airport destinazione = grafo.getEdgeTarget(e.getEdge());

				if (!visita.containsKey(destinazione) && visita.containsKey(sorgente)) {
					// dico che la destinazione si raggiunge da sorgente
					visita.put(destinazione, sorgente);
					// non è orienato quindi anche al contrario
				} else if (visita.containsKey(destinazione) && !visita.containsKey(sorgente)) {
					visita.put(sorgente, destinazione);
				}
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Airport> e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Airport> e) {
				// TODO Auto-generated method stub

			}

		});

		while (it.hasNext()) {
			it.next();
		}

		if (!visita.containsKey(p) || !visita.containsKey(a)) {
			// i due areoporti non sono collegati
			return null;
		}
		Airport step = a;

		// finchè non ritrovo la partenza
		while (!step.equals(p)) {
			percorso.add(step);
			step = visita.get(step);
		}
		percorso.add(p);
		return percorso;
	}

}

package HideAndSeek.hider.singleshot.random;

import java.util.ArrayList;
import java.util.TreeSet;

import org.jgrapht.alg.DijkstraShortestPath;

import HideAndSeek.GraphTraverser;
import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;
import HideAndSeek.hider.HiderLocalGraph;
import Utility.Utils;

/**
 * Produces a K size set of random nodes and then
 * heads for those nodes on the graph to hide in them.
 * 
 * NB: Doesn't allow bias to emerge as a product of low
 * weighted edges.
 * 
 * @author Martin
 *
 */
public class RandomSet extends HiderLocalGraph {

	/**
	 * 
	 */
	private ArrayList<StringVertex> hideSet;

	/**
	 * @param graphController
	 * @param name
	 * @param numberOfHideLocations
	 * @param responsibleAgent
	 */
	public RandomSet(GraphController<StringVertex, StringEdge> graphController, String name, int numberOfHideLocations, GraphTraverser responsibleAgent) {
		
		super(graphController, name, numberOfHideLocations, responsibleAgent);
		
		populateHideSet(createRandomSet(numberOfHideLocations, new TreeSet<StringVertex>()));
		
		currentPath = new ArrayList<StringEdge>();
		
	}
	
	/**
	 * @param graphController
	 * @param numberOfHideLocations
	 */
	public RandomSet(GraphController<StringVertex, StringEdge> graphController, int numberOfHideLocations) {
		
		this(graphController, "", numberOfHideLocations, null);
		
	}
	
	/**
	 * @param graphController
	 * @param numberOfHideLocations
	 * @param responsibleAgent
	 */
	public RandomSet(GraphController<StringVertex, StringEdge> graphController, int numberOfHideLocations, GraphTraverser responsibleAgent) {
		
		this(graphController, "", numberOfHideLocations, responsibleAgent);
		
	}
	
	/**
	 * @param graphController
	 * @param name
	 * @param numberOfHideLocations
	 */
	public RandomSet(GraphController<StringVertex, StringEdge> graphController, String name, int numberOfHideLocations) {
		
		this(graphController, name, numberOfHideLocations, null);
		
	}
	
	/**
	 * @param hideSet
	 */
	protected void populateHideSet(ArrayList<StringVertex> hideSet) {
		
		this.hideSet = hideSet;
		
	}
	
	/**
	 * @return
	 */
	protected ArrayList<StringVertex> getHideSet() {
		
		return this.hideSet;
		
	}
	
	/**
	 * @param size
	 * @param ignoreSet
	 * @return
	 */
	protected ArrayList<StringVertex> createRandomSet(int size, TreeSet<StringVertex> ignoreSet) {
		
		ArrayList<StringVertex> hideSet = new ArrayList<StringVertex>();
		
		if (ignoreSet.size() > (graphController.vertexSet().size() - size)) { 
			
			Utils.talk(toString(), "Too many nodes to disregard supplied. Hiding completely randomly.");
			
			ignoreSet = new TreeSet<StringVertex>();
			
		}
		
		StringVertex randomVertex = randomNode();
		
		while ( hideSet.size() != size ) {
			
			if (!hideSet.contains(randomVertex) && !ignoreSet.contains(randomVertex)) {
				
				hideSet.add(randomVertex);
				
			}
			
			randomVertex = randomNode();
			
			// Double check.
			// if ( ignoreSet.size() + hideSet.size() >= graphController.vertexSet().size()) ignoreSet = new TreeSet<StringVertex>();
			
		}
		
		return hideSet;
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.hider.Hider#hideHere(HideAndSeek.graph.StringVertex)
	 */
	@Override
	public boolean hideHere(StringVertex vertex) {

		// Utils.talk(toString(), "Current node: " + vertex + " Random set: " + hideSet + " Hide locations: " + hideLocations);
		
		// If we land on a node in our random set, hide here
		if ( hideSet.contains(vertex) ) { 
			
			hideSet.remove(vertex);
			
			return true;
		
		}
		
		return false;
		
	}
  
	/**
	 * 
	 */
	ArrayList<StringEdge> currentPath;
	
	/* (non-Javadoc)
	 * @see HideAndSeek.hider.HiderLocalGraph#nextNode(HideAndSeek.graph.StringVertex)
	 */
	@Override
	public StringVertex nextNode(StringVertex currentNode) {
		
		super.nextNode(currentNode);
		
		// If we're on a path to a node in the random set, follow this first
		if ( currentPath.size() > 0 ) {
			
			return edgeToTarget(currentPath.remove(0), currentNode);
			
		}
		
		// Otherwise, try and calculate a path to the next node in the set
		if ( hideSet.size() > 0 && localGraph.containsVertex( hideSet.get(0) ) ) {
			
			DijkstraShortestPath<StringVertex, StringEdge> dsp = new DijkstraShortestPath<StringVertex, StringEdge>(localGraph, currentNode, hideSet.get(0));
	    	
			// If no path available, return random connected node
			if (dsp.getPathEdgeList() == null || dsp.getPathEdgeList().size() == 0) return connectedNode(currentNode);
			
			currentPath = new ArrayList<StringEdge>(dsp.getPathEdgeList());
			
			return edgeToTarget(currentPath.remove(0), currentNode);
			
		}
		
		return connectedNode(currentNode);
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#startNode()
	 */
	@Override
	public StringVertex startNode() {
		
		return randomNode();
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.hider.Hider#endOfRound()
	 */
	@Override
	public void endOfRound() {
		
		super.endOfRound();
		
		populateHideSet(createRandomSet(numberOfHideLocations, new TreeSet<StringVertex>()));
		
	}

}

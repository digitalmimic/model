package HideAndSeek.hider.singleshot.distance;

import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;
import HideAndSeek.hider.HidingAgent;
import Utility.Utils;

/**
 * 
 * Employ a random (0 to N) fixed distance between each hide location
 * 
 * @author Martin
 *
 */
public class RandomFixedDistance extends HidingAgent {
	
	/**
	 * @param graph
	 */
	public RandomFixedDistance(GraphController <StringVertex, StringEdge> graphController, int numberOfHideLocations) {
	
		super(graphController, numberOfHideLocations);
		
		setRandomMinHideDistance();

	}
	
	/**
	 * 
	 */
	private void setRandomMinHideDistance() {
		
		minHideDistance = ((int)(Math.random() * graphController.vertexSet().size()));
		
	}
	
    /**
     * 
     */
    protected int minHideDistance;
	
	/* (non-Javadoc)
	 * @see HideAndSeek.hider.Hider#hideHere(HideAndSeek.graph.StringVertex)
	 */
	public boolean hideHere(StringVertex vertex) {
		
		Utils.talk(toString(), "exploredNodes.size() " + exploredNodes.size() + " minHideDistance: " + minHideDistance );
		
		if (minHideDistance == 0) { return true; }
		
		if ( exploredNodes.size() > 0 && ( exploredNodes.size() % minHideDistance == 0 ) ) { 
			
			return true;
			
		}
		
		return false;
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.hider.Hider#nextNode(HideAndSeek.graph.StringVertex)
	 */
	@Override
	public StringVertex nextNode(StringVertex currentNode) {
		
		return connectedNode(currentNode);
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.hider.Hider#startNode()
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
		
		setRandomMinHideDistance();
	
	}
	
}
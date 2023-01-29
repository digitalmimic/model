package org.kclhi.hands.hider.singleshot.cost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kclhi.hands.graph.GraphController;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;
import org.kclhi.hands.hider.HidingAgent;

/**
 * A hider who's tendency to choose cheap edges over random edges
 * is manually set.
 * 
 * Relies on edges being of variable weight.
 * 
 * @author Martin
 */
public class VariableGreedyStaticBetween extends VariableGreedy {

	
	/**
	 * @param graphController
	 * @param numberOfHideLocations
	 * @param bias
	 */
	public VariableGreedyStaticBetween(
			GraphController <StringVertex, StringEdge> graphController,
			int numberOfHideLocations, double bias) {
		super(graphController, numberOfHideLocations, bias);
		
	}

	/* (non-Javadoc)
	 * @see HideAndSeek.GraphTraverser#startNode()
	 */
	@Override
	public StringVertex startNode() {

		return currentNode();
		
	}
	
}

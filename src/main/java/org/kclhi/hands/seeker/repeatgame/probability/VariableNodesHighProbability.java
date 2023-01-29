package org.kclhi.hands.seeker.repeatgame.probability;

import org.kclhi.hands.graph.GraphController;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;

/**
 * Extends the standard High Probability Seeker adding the capacity
 * to specify the number of high probability nodes to use to direct
 * a search.
 * 
 * @author Martin
 *
 */
public class VariableNodesHighProbability extends HighProbability {
	
	public VariableNodesHighProbability(
			GraphController <StringVertex, StringEdge> graphController, int predictiveNodes, boolean variablePredictiveNodes) {
		
		this(graphController, "", predictiveNodes, variablePredictiveNodes);
		
	}

	public VariableNodesHighProbability(
			GraphController<StringVertex, StringEdge> graphController,
			String name, int predictiveNodes, boolean variablePredictiveNodes) {
		
		super(graphController, name);
		
		this.predictiveNodes = predictiveNodes;
		
		if (variablePredictiveNodes) strategyOverRounds = true;
			
	}
	
}

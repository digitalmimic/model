package HideAndSeek.seeker.repeatgame;

import HideAndSeek.graph.GraphController;
import HideAndSeek.graph.StringEdge;
import HideAndSeek.graph.StringVertex;

/**
 * Extends the standard High Probability Seeker adding the capacity
 * to specify the number of high probability nodes to use to direct
 * a search
 * 
 * @author Martin
 *
 */
public class VariableHighProbability extends HighProbability {
	
	public VariableHighProbability(
			GraphController <StringVertex, StringEdge> graphController, int predictiveNodes) {
		super(graphController);
		
		this.predictiveNodes = predictiveNodes;
		
	}
	
}
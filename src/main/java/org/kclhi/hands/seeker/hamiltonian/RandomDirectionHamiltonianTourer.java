package org.kclhi.hands.seeker.hamiltonian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.alg.EulerianCircuit;

import org.kclhi.hands.graph.GraphController;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;
import org.kclhi.hands.seeker.SeekingAgent;

/**
 * If a Hamiltonian circuit exists in the graph, follow it
 * one way, or the other, with equal percentage. 
 * 
 * This is seen as the optimal strategy in the literature
 * by Gal and Alpern.
 * 
 * @author Martin
 *
 */
public class RandomDirectionHamiltonianTourer extends HamiltonianTourer {

	/**
	 * @param graphController
	 */
	public RandomDirectionHamiltonianTourer(GraphController<StringVertex, StringEdge> graphController) {
		
		super(graphController);
		
	}

	/* (non-Javadoc)
	 * @see org.kclhi.hands.seeker.SeekingAgent#endOfRound()
	 */
	public void endOfRound() {
		
		super.endOfRound();
		
		if ( currentTour.size() > 0 ) {
			
			if ( Math.random() < 0.5 ) {
				
				Collections.reverse(currentTour);
				
			}
			
		}
		
	}

}

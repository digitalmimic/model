package org.kclhi.hands.hider.singleshot.random;

import org.kclhi.hands.graph.GraphController;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;

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
public class RandomSetStaticBetween extends RandomSet {
  
  /**
  * @param graphController
  * @param numberOfHideLocations
  */
  public RandomSetStaticBetween(GraphController<StringVertex, StringEdge> graphController, int numberOfHideLocations) {
    
    super(graphController, numberOfHideLocations);
    
  }
  
  /* (non-Javadoc)
  * @see HideAndSeek.GraphTraverser#startNode()
  */
  @Override
  public StringVertex startNode() {
    
    return currentNode();
    
  }
  
}

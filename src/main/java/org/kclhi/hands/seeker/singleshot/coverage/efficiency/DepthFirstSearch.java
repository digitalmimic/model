package org.kclhi.hands.seeker.singleshot.coverage.efficiency;

import org.kclhi.hands.graph.GraphController;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;

/**
* @author Martin
*
*/
public class DepthFirstSearch extends org.kclhi.hands.seeker.singleshot.coverage.DepthFirstSearch {
  
  /**
  * @param graphController
  */
  public DepthFirstSearch(GraphController<StringVertex, StringEdge> graphController) {
    
    super(graphController);
    
  }
  
  /* (non-Javadoc)
  * @see org.kclhi.hands.seeker.SeekingAgent#searchCriteria()
  */
  public boolean searchCriteria() {
    
    return uniquelyVisitedNodes().size() != graphController.vertexSet().size();
    
  }
  
}

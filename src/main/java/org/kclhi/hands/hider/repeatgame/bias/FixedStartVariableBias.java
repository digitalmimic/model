package org.kclhi.hands.hider.repeatgame.bias;

import org.kclhi.hands.graph.GraphController;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;

/**
* @author Martin
*/
public class FixedStartVariableBias extends VariableBias {
  
  /**
  * @param graphController
  * @param name
  * @param numberOfHideLocations
  * @param bias
  */
  public FixedStartVariableBias(
  GraphController <StringVertex, StringEdge> graphController, String name,
  int numberOfHideLocations, double bias) {
    super(graphController, name, numberOfHideLocations, bias);
    
  }
  
  /**
  * @param graphController
  * @param numberOfHideLocations
  * @param bias
  */
  public FixedStartVariableBias(
  GraphController <StringVertex, StringEdge> graphController,
  int numberOfHideLocations, double bias) {
    this(graphController, "", numberOfHideLocations, bias);
    
  }
  
  /* (non-Javadoc)
  * @see HideAndSeek.GraphTraverser#startNode()
  */
  @Override
  public StringVertex startNode() {
    
    return firstNode();
    
  }
  
}

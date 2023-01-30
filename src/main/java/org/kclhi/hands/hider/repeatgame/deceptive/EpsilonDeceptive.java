package org.kclhi.hands.hider.repeatgame.deceptive;

import org.kclhi.hands.utility.Utils;
import org.kclhi.hands.graph.GraphController;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;

/**
* Chooses to hide in a certain 'bias set' according to a factor
* Epsilon.
* 
* @author Martin
*
*/
public class EpsilonDeceptive extends Deceptive {
  
  private double epsilon;
  
  /**
  * @param graphController
  * @param numberOfHideLocations
  * @param deceptiveNodes
  * @param deceptionDuration
  * @param epsilon percentage of time to choose to hide using the a deceptive set
  */
  public EpsilonDeceptive(
  GraphController <StringVertex, StringEdge> graphController,
  int numberOfHideLocations, int deceptiveNodes, int deceptionDuration, double epsilon) {
    
    super(graphController, numberOfHideLocations, deceptiveNodes, deceptionDuration);
    
    this.epsilon = epsilon;
    
  }
  
  /**
  * @param graphController
  * @param numberOfHideLocations
  * @param deceptiveNodes
  * @param deceptionDuration
  * @param repeatInterval
  * @param startRound
  * @param refreshDeceptiveSet
  */
  public EpsilonDeceptive(
  GraphController <StringVertex, StringEdge> graphController,
  int numberOfHideLocations, int deceptiveNodes, int deceptionDuration, int repeatInterval, int repeatDuration, boolean refreshDeceptiveSet) {
    
    super(graphController, numberOfHideLocations, deceptiveNodes, deceptionDuration, repeatInterval, repeatDuration, refreshDeceptiveSet);
    
  }
  
  
  /**
  * 
  */
  @Override
  public void endOfRound() {
    
    super.endOfRound();
    
    double randomValue = Math.random() ;
    
    Utils.talk(toString(), "Random value: " + randomValue + " | " + "Epsilon: " + epsilon);
    
    // If random value falls under epsilon, play the deceptive set
    if (randomValue < epsilon) {
      
      Utils.talk(toString(), "Using deceptive set.");
      
      populateDeceptiveSet(deceptiveSet);
      
    } else {
      
      Utils.talk(toString(), "Hiding randomly.");
      
      if ( refreshDeceptiveSet ) createDeceptiveSet(deceptiveNodes);
      
      populateHideSet(createRandomSet(numberOfHideLocations, nodesUsedDeceptively));
      
    }
    
  }
  
}

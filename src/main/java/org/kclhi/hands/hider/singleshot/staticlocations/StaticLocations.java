package org.kclhi.hands.hider.singleshot.staticlocations;

import java.util.ArrayList;

import org.kclhi.hands.utility.Utils;
import org.kclhi.hands.graph.GraphController;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;
import org.kclhi.hands.hider.singleshot.random.RandomSet;

/**
* Produces a K size set of random nodes and then
* heads for those nodes on the graph EACH round.
* 
* @author Martin
*
*/
public class StaticLocations extends RandomSet {
  
  /**
  * 
  */
  private ArrayList<StringVertex> staticSet;
  
  /**
  * @param graphController
  * @param numberOfHideLocations
  */
  public StaticLocations(GraphController<StringVertex, StringEdge> graphController, int numberOfHideLocations) {
    
    super(graphController, numberOfHideLocations);
    
    this.staticSet = new ArrayList<StringVertex>(getHideSet());
    
    Utils.talk(toString(), "Constructor static set:" + staticSet.toString());
    
  }
  
  /* (non-Javadoc)
  * @see HideAndSeek.GraphTraverser#startNode()
  */
  @Override
  public StringVertex startNode() {
    
    return currentNode();
    
  }
  
  /* (non-Javadoc)
  * @see HideAndSeek.hider.Hider#endOfRound()
  */
  @Override
  public void endOfRound() {
    
    super.endOfRound();
    
    Utils.talk(toString(), "Static set:" + this.staticSet.toString());
    
    populateHideSet(new ArrayList<StringVertex>(this.staticSet));
    
  }
  
}

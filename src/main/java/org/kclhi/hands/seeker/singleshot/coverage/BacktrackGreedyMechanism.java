package org.kclhi.hands.seeker.singleshot.coverage;

import java.util.List;

import org.kclhi.hands.GraphTraverser;
import org.kclhi.hands.OpenTraverserStrategy;
import org.kclhi.hands.graph.GraphController;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;


/**
* @author Martin
*
*/
public class BacktrackGreedyMechanism extends BacktrackGreedy implements OpenTraverserStrategy {
  
  /**
  * @param graphController
  * @param name
  */
  public BacktrackGreedyMechanism(GraphController<StringVertex, StringEdge> graphController, GraphTraverser responsibleAgent) {
    
    super(graphController, responsibleAgent);
    
    if ( responsibleAgent == null ) graphController.deregisterTraversingAgent(this);
    
  }
  
  /* (non-Javadoc)
  * @see org.kclhi.hands.seeker.singleshot.coverage.NearestNeighbour#nextNode(HideAndSeek.graph.StringVertex)
  */
  public StringVertex nextNode(StringVertex currentNode) {
    
    this.currentNode = currentNode;
    
    return super.nextNode(currentNode);
    
  }
  
  /* (non-Javadoc)
  * @see HideAndSeek.GraphTraversingAgent#connectedNode(HideAndSeek.graph.StringVertex)
  */
  public StringVertex connectedNode(StringVertex currentNode) {
    
    return super.connectedNode(currentNode);
    
  }
  
  /* (non-Javadoc)
  * @see org.kclhi.hands.seeker.singleshot.LeastConnectedFirst#getConnectedEdges(HideAndSeek.graph.StringVertex)
  */
  @Override
  public List<StringEdge> getConnectedEdges(StringVertex currentNode) {
    
    return super.getConnectedEdges(currentNode);
    
  }
  
  /* (non-Javadoc)
  * @see org.kclhi.hands.seeker.singleshot.preference.LeastConnectedFirst#getConnectedEdge(HideAndSeek.graph.StringVertex, java.util.List)
  */
  @Override
  public StringEdge getConnectedEdge(StringVertex currentNode, List<StringEdge> connectedEdges) {
    
    return super.getConnectedEdge(currentNode, connectedEdges);
    
  }
  
  public void atStart(StringVertex startNode) {
    
    super.atStart(startNode);
    
  }
  
  /* (non-Javadoc)
  * @see org.kclhi.hands.seeker.SeekingAgent#atNode()
  */
  public void atNode() {
    
    super.atNode();
    
  }
  
  /* (non-Javadoc)
  * @see HideAndSeek.GraphTraversingAgent#nextNodeAccepted(HideAndSeek.graph.StringVertex)
  */
  public void atNextNode(StringVertex nextNode) {
    
    super.atNextNode(nextNode);
    
  }
  
}

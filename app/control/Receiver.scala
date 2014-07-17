/**
 * @author Francisco Miguel Arámburo Torres - atfm05@gmail.com
 */

package control

/**
 * Interface for EngineRequests that are initiated by the
 * ControlComputeRequest.
 *
 * NOTE: The Receiver and Sender interfaces are designed so that
 * MultiNodeRequests and SingleGraphRequests are implementable.
 */
trait Receiver {

  /**
   * Called by a ControlComputeRequest to initiate an 
   * EngineRequest.
   */
  def receive: Unit

}


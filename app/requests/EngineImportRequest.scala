/**
 * @author Francisco Miguel Arámburo Torres - atfm05@gmail.com
 */

package requests

import play.api.libs.json._

import library.Library
import engine.GraphImport
import shared.DB
import http.OutReports

/**
 * Companion object to correctly build the request.
 */
object EngineImportRequest {

  /**
   * Constructor that creates the request, and executes
   * the validation.
   *
   * @param json of the request.
   * @return a request instance.
   */
  def apply (json: JsValue): EngineImportRequest = {
    val req = new EngineImportRequest(json)
    req.validate
    req
  }

}

/**
 * An in queue request that imports a graph
 * from DB with an algorithm format.
 *
 * @param json of the request.
 */
class EngineImportRequest (json: JsValue) extends QueueRequest {

  /** Token to identify the request. */
  var token: String = null

  /** The algorithm that will format and import the graph. */
  var graph: GraphImport = null

  /** Extra cypher to be used for the graph importation. */
  var tag: String = null

  /** The DB server hostname. */
  var host: String = null

  /** The DB server port. */
  var port: Int = 0

  /** The DB server username. */
  var user: String = null

  /** The DB server password. */
  var password: String = null

  /** The amount of milliseconds which the graph took to be imported. */
  var importTime: Long = 0

  /**
   * Json validation.
   */
  def validate: Unit = {
    (json \ "token").asOpt[String] match {
      case None => errors = errors :+ "'token' field missing."
      case Some(data) => token = data
    }
    (json \ "algorithm").asOpt[String] match {
      case None => errors = errors :+ "'algorithm' field missing."
      case Some(data) => Library(data) match {
        case Some(algo) => graph = algo
        case None => errors = errors :+ s"No such algorithm '$data'"
      }
    }
    (json \ "tag").asOpt[String] match {
      case None => errors = errors :+ "'tag' field missing."
      case Some(data) => tag = data
    }
    (json \ "host").asOpt[String] match {
      case None => errors = errors :+ "'host' field missing."
      case Some(data) => host = data
    }
    (json \ "port").asOpt[Int] match {
      case None => errors = errors :+ "'port' field missing."
      case Some(data) => port = data
    }
    (json \ "user").asOpt[String] match {
      case None =>
      case Some(data) => user = data
    }
    (json \ "password").asOpt[String] match {
      case None => 
      case Some(data) => password = data
    }
  }

  /**
   * Request execution.
   */
  def execute: Unit = {
    DB.host = host
    DB.port = port
    DB.user = user
    DB.password = password
    DB.ping { success =>  
      if (!success) {
        OutReports.Error.unreachableNeo4j(this)
        // Note: At this point the algorithm 
        // is not ready for computation.
        finish()
        return
      }
      if (DB.importedGraphFormat != null) 
        DB.importedGraphFormat.clear
      DB.importedGraphFormat = graph
      graph.importStart(this)
    }
  }

}

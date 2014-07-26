/**
 * @author Francisco Miguel Arámburo Torres - atfm05@gmail.com
 */

package engine

import shared.Neo4j

import play.api.libs.ws.Response

/**
 * Neo4j singleton for Census Engine.
 */
object Database {

  var tag: String = ""

  /** Neo4j current database. */
  private var database: Neo4j = null

  def setDatabase (host: String, port: Int, user: String, password: String) =
    database = new Neo4j(host, port, user, password)

  def query (querystring: String, callback: (Boolean, Response)=>Unit) = 
    database.query(querystring, callback)

  def ping (callback: Boolean=>Unit): Unit = 
    database.ping(callback)

}

/**
 * @author Francisco Miguel Arámburo Torres - atfm05@gmail.com
 */

package controllers

import scala.concurrent.Future

import play.api.libs.json._
import play.api.libs.ws._
import play.api.libs.concurrent.Execution.Implicits._

/**
 * An extern web service which can be called 
 * through http requests.
 */
trait WebService {
  
  /** Port where the web service is listening. */
  var port: Int = 80

  /** Host name where the web service is located. */
  var host: String = "unset"

  /** User for authenticating requests. */
  var user: String = ""

  /** Password for authenticating requests. */
  var password: String = ""

  /** Timeout used for reachability testing. */
  val timeoutLength: Int = 2000

  /**
   * Uses the Play Framework WS api to post something
   * to the web service.
   *
   * @param path to post to.
   * @param data to be posted.
   * @param callback (Response, Boolean)=>Unit callback with 
   *                 second parameter 'true' if there was an error.
   * @return a future that handles the web service response.
   */
  def post (path: String, data: JsValue, callback: (Response, Boolean)=>Unit): Unit = {
    var requestHolder = WS.url(s"http://$host:$port$path").withHeaders("Content-Type" -> "application/json")
    if (user != "" && password != "") {
      requestHolder = requestHolder.withAuth(user, password, com.ning.http.client.Realm.AuthScheme.BASIC)
    }
    requestHolder.post(data) map { response =>
      callback(response, false)
    } recover { case _ =>
      callback(null, true)
    }
  }

  /**
   * Makes a request to the web service with HEAD method
   * and a timeout to test reachability.
   *
   * @param callback Boolean=>Unit callback with parameter 
   *                 'true' if ping was successful.
   * @return a future that handles the web service response.
   */
  def ping (callback: Boolean=>Unit): Unit = {
    var requestHolder = WS.url(s"http://$host:$port").withTimeout(2000)
    if (user != "" && password != "") {
      requestHolder = requestHolder.withAuth(user, password, com.ning.http.client.Realm.AuthScheme.BASIC)
    }
    requestHolder.head() map { response => 
      callback(true) 
    } recover { case _ => 
      callback(false) 
    }
  }

}
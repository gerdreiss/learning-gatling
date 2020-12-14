package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

class RuntimeParameters extends Simulation {

  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration: ${testDuration} seconds")
  }


  // 1. Http Configuration
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  // 2. Scenario Definition
  val scn = scenario("Get all video games")
    .forever() {
      exec(getAllVideoGames())
    }

  // 3. Load Scenario
  setUp(scn
    .inject(
      nothingFor(5 seconds),
      rampUsers(userCount).during(rampDuration)))
    .protocols(httpConf)
    .maxDuration(testDuration)

  //
  // helper functions
  //

  def getAllVideoGames(): ChainBuilder =
    exec(http("Get all video games")
      .get("videogames")
      .check(status.is(200)))

  def getSpecificVideoGame(id: Int): ChainBuilder =
    exec(http("Get specific video game")
      .get(s"videogames/$id")
      .check(status.is(200)))

  def userCount: Int =
    getProperty("USERS", "5").toInt

  def rampDuration: FiniteDuration =
    getProperty("RAMP_DURATION", "10").toInt.seconds

  def testDuration: FiniteDuration =
    getProperty("TEST_DURATION", "60").toInt.seconds

  private def getProperty(propertyName: String, defaultValue: String): String = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }


}

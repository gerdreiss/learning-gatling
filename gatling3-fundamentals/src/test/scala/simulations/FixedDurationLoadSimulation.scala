package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class FixedDurationLoadSimulation extends Simulation {

  // 1. Http Configuration
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  // 2. Scenario Definition
  val scn = scenario("Fix duration load simulation")
    .forever {
      exec(getAllVideoGames())
        .pause(5)
        .exec(getSpecificVideoGame(2))
        .pause(5)
        .exec(getAllVideoGames())
    }

  // 3. Load Scenario
  setUp(scn
    .inject(
      nothingFor(5 seconds),
      atOnceUsers(10),
      rampUsers(50).during(30 seconds)))
    .protocols(httpConf)
    .maxDuration(1 minute)


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

}

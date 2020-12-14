package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class CsvFeederToCustom extends Simulation {
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")
  //.proxy(Proxy("localhost", 3124))

  var idNumbers = (1 to 10).iterator

  val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getSpecificVideoGame(): ChainBuilder =
    repeat(10) {
      feed(customFeeder)
        .exec(http("Get specific video game")
          .get("videogames/${gameId}")
          //.check(jsonPath("$.name").is("${gameName}"))
          .check(status.is(200)))
        .pause(1)
    }

  val scn = scenario("Csv Feeder test")
    .exec(getSpecificVideoGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}

package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseBodyAndExtract extends Simulation {

  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  val scn = scenario("check JSON Path")

    .exec(http("get specific video game")
      .get("videogames/1")
      .check(jsonPath("$.name").is("Resident Evil 4")))

    .exec(http("get all video games")
      .get("videogames")
      .check(jsonPath(s"${1}.id").saveAs("gameId")))

    .exec { session => println(session); session }

    .exec(http("get specific video game")
      .get("videogames/${gameId}")
      .check(jsonPath("$.name").is("Gran Turismo 3"))
      .check(bodyString.saveAs("responseBody")))

    .exec { session => println(session("responseBody").as[String]); session }

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}

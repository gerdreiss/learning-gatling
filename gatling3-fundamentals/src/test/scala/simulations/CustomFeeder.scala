package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

class CustomFeeder extends Simulation {
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  val idNumbers = (21 to 30).iterator
  val rnd = new Random()


  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next()
    , "name" -> s"Game-${randomString(5)}"
    , "releaseDate" -> randomDate(LocalDate.now())
    , "reviewScore" -> rnd.nextInt(100)
    , "category" -> s"Category-${randomString(6)}"
    , "rating" -> s"Rating-${randomString(4)}"
  ))

  def randomString(length: Int): String =
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString

  def randomDate(startDate: LocalDate): String =
    startDate.minusDays(rnd.nextInt(30))
      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  def postNewGame(): ChainBuilder =
    repeat(10) {
      feed(customFeeder)
        .exec(
          http("Post new game")
            .post("videogames/")
            .body(StringBody(
              """|{
                 |  "id": ${gameId},
                 |  "name": "${name}",
                 |  "releaseDate": "${releaseDate}",
                 |  "reviewScore": ${reviewScore},
                 |  "category": "${category}",
                 |  "rating": "${rating}"
                 |}""".stripMargin
            ))
            .asJson
            .check(status.is(200)))
        .pause(1)
    }

  def postNewGameFromTemplate(): ChainBuilder =
    repeat(10) {
      feed(customFeeder)
        .exec(
          http("Post new game")
            .post("videogames/")
            .body(ElFileBody("bodies/template.json"))
            .asJson
            .check(status.is(200)))
        .pause(1)
    }

  val scn = scenario("Post new games")
    .exec(postNewGameFromTemplate())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}

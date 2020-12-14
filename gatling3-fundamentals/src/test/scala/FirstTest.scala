import io.gatling.core.Predef._
import io.gatling.http.Predef._

class FirstTest extends Simulation {

  // 1. Http Configuration
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")
    .proxy(Proxy("localhost", 8888))

  // 2. Scenario Definition
  val scn = scenario("The First Test")
    .exec(http("Get All Games").get("videogames"))

  // 3. Load Scenario
  setUp(scn.inject(atOnceUsers(1)))
    .protocols(httpConf)
}

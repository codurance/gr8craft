package gr8craft

import akka.actor.ActorRef
import gr8craft.ApplicationFactory.createApplication
import gr8craft.inspiration.Inspiration
import gr8craft.messages.{Start, Stop}

class ApplicationRunner(scheduler: ActorRef) {
  def startTwitterBot() {
    scheduler ! Start
  }

  def stop() {
    scheduler ! Stop
  }
}

object ApplicationRunner {

  def inspirations: Set[Inspiration] = Set(
    new Inspiration("the 4 rules of simple design", "http://martinfowler.com/bliki/BeckDesignRules.html"),
    new Inspiration("teamwork", "http://www.csu.edu.au/__data/assets/pdf_file/0008/917018/Eight-Behaviors-for-Smarter-Teams-2.pdf"),
    new Inspiration("code", "http://www.stevemcconnell.com/cc1.htm"),
    new Inspiration("domain modelling with F#", "https://vimeo.com/97507575"),
    new Inspiration("Software Craftsmanship", "http://codurance.com/2015/09/10/interview-for-jaxenter/"),
    new Inspiration("best practices in building apps", "http://12factor.net/"),
    new Inspiration("cohesion", "https://pragprog.com/magazines/2010-12/cohesive-software-design"),
    new Inspiration("coupling", "http://martinfowler.com/ieeeSoftware/coupling.pdf"),
    new Inspiration("composition over inheritance", "http://www.thoughtworks.com/insights/blog/composition-vs-inheritance-how-choose"),
    new Inspiration("the 4 rules of simple design", "http://blog.thecodewhisperer.com/2013/12/07/putting-an-age-old-battle-to-rest/"),
    new Inspiration("unbreakable domain models", "https://youtu.be/ZJ63ltuwMaE?list=PL_aPVo2HeGF-7o9SPO5arFrAaU8bcIjba"),
    new Inspiration("test doubles", "http://www.martinfowler.com/bliki/TestDouble.html"),
    new Inspiration("OOP vs. FP", "http://steve-yegge.blogspot.co.uk/2006/03/execution-in-kingdom-of-nouns.html"),
    new Inspiration("Accidental and essential complication", "https://vimeo.com/79106557"),
    new Inspiration("API design", "http://www.javacodegeeks.com/2013/04/how-to-design-a-good-regular-api.html"),
    new Inspiration("living documentation", "https://leanpub.com/livingdocumentation"),
    new Inspiration("pragmatism", "http://unclejamal.github.io/2015/08/31/catmatic.htmlf"),
    new Inspiration("microservices", "http://philcalcado.com/2015/09/08/how_we_ended_up_with_microservices.html"),
    new Inspiration("the 4 rules of simple design", "https://leanpub.com/4rulesofsimpledesign/"),
    new Inspiration("Apprenticeship Patterns", "http://shop.oreilly.com/product/9780596518387.do"),
    new Inspiration("design decisions ", "http://codurance.com/2015/06/17/inflection-point/")
  )

  def main(args: Array[String]) {
    val application: ApplicationRunner = createApplication(initalInspirations = inspirations)

    application.startTwitterBot()
  }
}

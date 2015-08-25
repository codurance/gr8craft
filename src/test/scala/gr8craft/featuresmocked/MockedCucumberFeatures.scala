package gr8craft.featuresmocked

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(features = Array("src/test/resources"), glue = Array("gr8craft.featuresmocked"))
class MockedCucumberFeatures {
}

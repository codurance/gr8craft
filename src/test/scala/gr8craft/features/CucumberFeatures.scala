package gr8craft.features

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@CucumberOptions(tags = Array("@end-to-end"))
@RunWith(classOf[Cucumber])
class CucumberFeatures {
}

package com.codurance.gr8craft.features

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(glue = Array("com.codurance.gr8craft.features"), tags = Array("~@work-in-progress"))
class IntegrativeFeatureTests {
}

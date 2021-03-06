/*
 * Copyright © 2017-2018 AT&T Intellectual Property.
 * Modifications Copyright © 2018 IBM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.ccsdk.cds.blueprintsprocessor.rest.service

import org.junit.Test
import org.junit.runner.RunWith
import org.onap.ccsdk.cds.blueprintsprocessor.core.BluePrintProperties
import org.onap.ccsdk.cds.blueprintsprocessor.core.BlueprintPropertyConfiguration
import org.onap.ccsdk.cds.blueprintsprocessor.rest.BluePrintRestLibConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertNotNull


@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [BluePrintRestLibConfiguration::class, BlueprintPropertyConfiguration::class, BluePrintProperties::class])
@TestPropertySource(properties =
["blueprintsprocessor.restclient.sample.type=basic-auth",
    "blueprintsprocessor.restclient.sample.url=http://localhost:8080",
    "blueprintsprocessor.restclient.sample.userId=sampleuser"])

class BluePrintRestLibPropertyServiceTest {

    @Autowired
    lateinit var bluePrintRestLibPropertyService: BluePrintRestLibPropertyService

    @Test
    fun testRestClientProperties() {
        val properties = bluePrintRestLibPropertyService.restClientProperties("blueprintsprocessor.restclient.sample")
        assertNotNull(properties, "failed to create property bean")
        assertNotNull(properties.url, "failed to get url property in property bean")
    }

    @Test
    fun testBlueprintWebClientService() {
        val blueprintWebClientService = bluePrintRestLibPropertyService.blueprintWebClientService("sample")
        assertNotNull(blueprintWebClientService, "failed to create blueprintWebClientService")
    }

}


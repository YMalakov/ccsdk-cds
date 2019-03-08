/*
 * Copyright © 2017-2019 AT&T, Bell Canada
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

package org.onap.ccsdk.apps.blueprintsprocessor.rest.service

import org.apache.http.message.BasicHeader
import org.onap.ccsdk.apps.blueprintsprocessor.rest.BasicAuthRestClientProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.nio.charset.Charset
import java.util.*

class BasicAuthRestClientService(private val restClientProperties: BasicAuthRestClientProperties) :
    BlueprintWebClientService {

    override fun headers(): Array<BasicHeader> {
        val encodedCredentials = setBasicAuth(restClientProperties.username, restClientProperties.password)
        val params = arrayListOf<BasicHeader>()
        params.add(BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        params.add(BasicHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
        params.add(BasicHeader(HttpHeaders.AUTHORIZATION, "Basic $encodedCredentials"))
        return params.toTypedArray()
    }

    override fun host(uri: String): String {
        return restClientProperties.url + uri
    }

    private fun setBasicAuth(username: String, password: String): String {
        val credentialsString = "$username:$password"
        return Base64.getEncoder().encodeToString(credentialsString.toByteArray(Charset.defaultCharset()))
    }
}
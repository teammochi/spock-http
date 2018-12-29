package com.teammochi.util.spock

import groovy.json.JsonSlurper
import com.teammochi.oss.util.http.HttpSpecification

class AnimalsApiTests extends HttpSpecification {

    void setupSpec() {
        println "Setup Spec Called"
    }

    void 'All breeds list should contain terriers'() {
        when:
        get('https://dog.ceo/api/breeds/list')

        then:
        expect200() // method missing same as:
        expectStatus(200) // same as:
        200 == httpResponse.statusLine?.statusCode
        def json = new JsonSlurper().parse(httpResponse.entity?.content)
        json && json.size() > 0
        json.message?.contains('terrier')

        where:
        a << [1, 2, 3]
    }

}

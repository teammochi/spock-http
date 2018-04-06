package com.teammochi.util.spock

import groovy.json.JsonSlurper
import com.teammochi.util.spock.HttpSpec
import org.apache.http.HttpResponse

class AnimalsApiTests extends HttpSpec {

    void 'All breeds list should contain terriers'() {
        when:
        HttpResponse response = get('https://dog.ceo/api/breeds/list')

        then:
        expect200() // method missing same as:
        expectStatus(200) // same as:
        200 == response.statusLine?.statusCode
        def json = new JsonSlurper().parse(response.entity?.content)
        json && json.size() > 0
        json.message?.contains('terrier')
    }

}

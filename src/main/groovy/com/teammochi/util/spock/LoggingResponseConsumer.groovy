package com.teammochi.util.spock

import groovy.transform.CompileStatic
import org.apache.http.HttpResponse

import java.util.function.Consumer

@CompileStatic
class LoggingResponseConsumer implements Consumer<HttpResponse> {

    static LoggingResponseConsumer INSTANCE = new LoggingResponseConsumer()

    private LoggingResponseConsumer() {

    }

    @Override
    void accept(HttpResponse httpResponse) {
        println "status line: $httpResponse.statusLine"
        println "     entity: ${httpResponse.entity?.content?.text}"
    }
}
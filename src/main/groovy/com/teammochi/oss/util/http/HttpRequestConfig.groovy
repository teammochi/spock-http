package com.teammochi.oss.util.http

import groovy.transform.CompileStatic
import org.apache.http.HttpResponse

import java.util.function.Consumer
import java.util.regex.Pattern

@CompileStatic
class HttpRequestConfig {
    protected static final Pattern ON_STATUS_METHOD_PATTERN = ~/on(\d+)/

    protected Map<String, String> headers = [:]
    protected Map<String, String> parameters = [:]
    protected Consumer<HttpResponse> successHandler = LoggingResponseConsumer.INSTANCE
    protected Consumer<HttpResponse> failureHandler = LoggingResponseConsumer.INSTANCE
    protected Map<Integer, Consumer<HttpResponse>> responseHandlers = [:]

    void headers(Map<String, String> headers) {
        this.headers.putAll(headers)
    }

    void parameters(Map<String, String> parameters) {
        this.parameters.putAll(parameters)
    }

    void onSuccess(Consumer<HttpResponse> handler) {
        successHandler = handler
    }

    void onFailure(Consumer<HttpResponse> handler) {
        failureHandler = handler
    }

    /**
     * <pre>{@code
     * get(...) {
     *     onStatuses(
     *          200: {resp -> println 'Success!'},
     *          400: {resp -> println 'Failure!'}
     *     )
     * }
     * }</pre>
     * @param handlers
     */
    void onStatuses(Map<Integer, Consumer<HttpResponse>> handlers) {
        responseHandlers.putAll(handlers)
    }

    void handleResponse(HttpResponse response) {
        int statusCode = response.statusLine.statusCode
        //println "Status code: $statusCode, handlers: ${responseHandlers}, handler: ${responseHandlers[statusCode]?.class}"
        (responseHandlers[statusCode] as Consumer<HttpResponse>)?.accept(response)
        if (statusCode < 400) {
            successHandler?.accept(response)
        } else {
            failureHandler?.accept(response)
        }
    }

    def methodMissing(String name, Object[] args) {
        //println "Method missing, trying to match code: $name, $args"
        // Intercept method that starts with find.
        if (name ==~ ON_STATUS_METHOD_PATTERN) {
            //println(name =~ ON_STATUS_METHOD_PATTERN)
            def statusCode = (name =~ ON_STATUS_METHOD_PATTERN).group(1) as Integer

            responseHandlers[statusCode] = args[0] as Consumer<HttpResponse>

            //println "method matched, status code: $statusCode"
        } else {
            throw new MissingMethodException(name, this.class, args)
        }
    }

    Map<String, String> getHeaders() {
        return headers
    }

    Map<String, String> getParameters() {
        return parameters
    }
}
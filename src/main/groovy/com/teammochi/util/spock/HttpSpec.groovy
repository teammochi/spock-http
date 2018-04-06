package com.teammochi.util.spock

import org.apache.http.HttpResponse
import org.apache.http.client.methods.*
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.BufferedHttpEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.util.regex.Pattern

class HttpSpec extends Specification {


    protected static final Pattern EXPECT_STATUS_METHOD_PATTERN = ~/expect(\d+)/

    @AutoCleanup("close")
    protected CloseableHttpClient client
    @AutoCleanup("close")
    protected CloseableHttpResponse response

    private HttpResponse executeRequest(String url, HttpRequestBase request, HttpRequestConfig config) {
        URIBuilder uri = new URIBuilder(url)
        config.parameters.each { key, value ->
            uri.setParameter(key, value)
        }

        request.setURI(uri.build())
        config.headers.each { String key, String value ->
            request.setHeader(key, value)
        }

        response = client.execute(request)

        response.setEntity(new BufferedHttpEntity(response.getEntity()))

        config.handleResponse(response)
        return response
    }

    def setup() {
        client = HttpClients.createDefault()
    }


    HttpResponse delete(final String url, final @DelegatesTo(HttpRequestConfig) Closure closure = {}) {
        final config = new HttpRequestConfig()
        config.with closure

        HttpDelete httpRequest = new HttpDelete()

        return executeRequest(url, httpRequest, config)
    }

    HttpResponse get(final String url, final @DelegatesTo(HttpRequestConfig) Closure closure = {}) {
        final config = new HttpRequestConfig()
        config.with closure

        HttpGet httpRequest = new HttpGet()

        return executeRequest(url, httpRequest, config)
    }

    HttpResponse patch(final String url, final @DelegatesTo(HttpEntityRequestConfig) Closure closure = {}) {
        final config = new HttpEntityRequestConfig()
        config.with closure

        HttpPatch httpRequest = new HttpPatch()
        httpRequest.setEntity(config.entity)

        return executeRequest(url, httpRequest, config)
    }

    HttpResponse post(final String url, final @DelegatesTo(HttpEntityRequestConfig) Closure closure = {}) {
        final config = new HttpEntityRequestConfig()
        config.with closure

        HttpPost httpRequest = new HttpPost()
        httpRequest.setEntity(config.entity)

        return executeRequest(url, httpRequest, config)
    }

    HttpResponse put(final String url, final @DelegatesTo(HttpEntityRequestConfig) Closure closure = {}) {
        final config = new HttpEntityRequestConfig()
        config.with closure

        HttpPut httpRequest = new HttpPut()
        httpRequest.setEntity(config.entity)

        return executeRequest(url, httpRequest, config)
    }

    boolean expectStatus(int statusCode) {
        assert statusCode == response?.statusLine?.statusCode: "Expected '${statusCode}', got '${response?.statusLine?.statusCode}'. Status Line: (${response?.statusLine})"

        return true
    }

    def methodMissing(String name, args) {

        if (name ==~ EXPECT_STATUS_METHOD_PATTERN) {

            def statusCode = (name =~ EXPECT_STATUS_METHOD_PATTERN)[0][1] as Integer

            return expectStatus(statusCode)
        } else {
            throw new MissingMethodException(name, this.class, args)
        }

        return true
    }
}




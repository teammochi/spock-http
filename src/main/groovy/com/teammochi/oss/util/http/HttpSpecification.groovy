package com.teammochi.oss.util.http

import org.apache.http.HttpResponse
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.junit.Before
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.util.regex.Pattern

/**
 * Adds methods from {@link HttpUtils} and some custom expect methods to Spock Specifications.
 */
class HttpSpecification extends Specification implements HttpUtils {

    protected static final Pattern EXPECT_STATUS_METHOD_PATTERN = ~/expect(\d+)/

    @AutoCleanup("close")
    protected CloseableHttpClient httpClient

    @AutoCleanup("close")
    protected CloseableHttpResponse httpResponse

    @Before
    def setupHttpClient() {
        println "HttpSpecification setup called"
        httpClient = HttpClients.createDefault()
    }

    /**
     * Calls {@link HttpUtils#delete(org.apache.http.client.HttpClient, java.lang.String)} with the client created in
     * the setup method.
     */
    HttpResponse delete(final String url, final @DelegatesTo(HttpRequestConfig) Closure closure = {}) {
        return httpResponse = delete(httpClient, url, closure)
    }

    /**
     * Calls {@link HttpUtils#get(org.apache.http.client.HttpClient, java.lang.String)} with the client created in
     * the setup method.
     */
    HttpResponse get(final String url, final @DelegatesTo(HttpRequestConfig) Closure closure = {}) {
        return httpResponse = get(httpClient, url, closure)
    }

    /**
     * Calls {@link HttpUtils#patch(org.apache.http.client.HttpClient, java.lang.String)} with the client created in
     * the setup method.
     */
    HttpResponse patch(final String url, final @DelegatesTo(HttpEntityRequestConfig) Closure closure = {}) {
        return httpResponse = patch(httpClient, url, closure)
    }

    /**
     * Calls {@link HttpUtils#post(org.apache.http.client.HttpClient, java.lang.String)} with the client created in
     * the setup method.
     */
    HttpResponse post(final String url, final @DelegatesTo(HttpEntityRequestConfig) Closure closure = {}) {
        return httpResponse = post(httpClient, url, closure)
    }

    /**
     * Calls {@link HttpUtils#put(org.apache.http.client.HttpClient, java.lang.String)} with the client created in
     * the setup method.
     */
    HttpResponse put(final String url, final @DelegatesTo(HttpEntityRequestConfig) Closure closure = {}) {
        return httpResponse = put(httpClient, url, closure)
    }

    /**
     * Checks the response status code of the last request against the given code. This method has a return value so
     * they can be used in Spock Specs then, and expect blocks
     * {@link http://spockframework.org/spock/docs/1.2/spock_primer.html#_expect_blocks}.
     * @param statusCode
     * @return true if statusCode matches - fails with assertion error otherwise.
     */
    boolean expectStatus(int statusCode) {
        return expectStatus(httpResponse, statusCode)
    }

    /**
     * Checks the response status code against the given code. This method has a return value so they can be used in
     * Spock Specs then, and expect blocks
     * {@link http://spockframework.org/spock/docs/1.2/spock_primer.html#_expect_blocks}.
     * @param response
     * @param statusCode
     * @return true if statusCode matches - fails with assertion error otherwise.
     */
    boolean expectStatus(HttpResponse response, int statusCode) {
        assertStatusEquals(response, statusCode)
        return true
    }

    /**
     * Used to match expect[statusCode] methods. Eg. expect200() will call the {@link #expectStatus(int)} method with
     * the status code of '200'. expect200(response) will call {@link #expectStatus(org.apache.http.HttpResponse, int)}.
     * @param name
     * @param args
     * @return true if statusCode matches - fails with assertion error otherwise.
     */
    def methodMissing(String name, args) {

        if (name ==~ EXPECT_STATUS_METHOD_PATTERN) {

            def statusCode = (name =~ EXPECT_STATUS_METHOD_PATTERN)[0][1] as Integer

            if (args) {
                return expectStatus(args[0], statusCode)
            }
            return expectStatus(statusCode)
        } else {
            throw new MissingMethodException(name, this.class, args)
        }

        return true
    }
}




package com.teammochi.oss.util.http

import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.*
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.BufferedHttpEntity

trait HttpUtils {

    private HttpResponse executeRequest(
            final HttpClient client,
            final String url,
            final HttpRequestBase request,
            final HttpRequestConfig config) {
        URIBuilder uri = new URIBuilder(url)
        config.parameters.each { key, value ->
            uri.setParameter(key, value)
        }

        request.setURI(uri.build())
        config.headers.each { String key, String value ->
            request.setHeader(key, value)
        }

        HttpResponse response = client.execute(request)

        response.setEntity(new BufferedHttpEntity(response.getEntity()))

        config.handleResponse(response)
        return response
    }

    HttpResponse delete(
            final HttpClient client,
            final String url,
            final @DelegatesTo(HttpRequestConfig) Closure closure = {}) {
        final config = new HttpRequestConfig()
        config.with closure

        HttpDelete httpRequest = new HttpDelete()

        return executeRequest(client, url, httpRequest, config)
    }

    HttpResponse get(
            final HttpClient client,
            final String url,
            final @DelegatesTo(HttpRequestConfig) Closure closure = {}) {
        final config = new HttpRequestConfig()
        config.with closure

        HttpGet httpRequest = new HttpGet()

        return executeRequest(client, url, httpRequest, config)
    }

    HttpResponse patch(
            final HttpClient client,
            final String url,
            final @DelegatesTo(HttpEntityRequestConfig) Closure closure = {}) {
        final config = new HttpEntityRequestConfig()
        config.with closure

        HttpPatch httpRequest = new HttpPatch()
        httpRequest.setEntity(config.entity)

        return executeRequest(client, url, httpRequest, config)
    }

    HttpResponse post(
            final HttpClient client,
            final String url,
            final @DelegatesTo(HttpEntityRequestConfig) Closure closure = {}) {
        final config = new HttpEntityRequestConfig()
        config.with closure

        HttpPost httpRequest = new HttpPost()
        httpRequest.setEntity(config.entity)

        return executeRequest(client, url, httpRequest, config)
    }

    HttpResponse put(
            final HttpClient client,
            final String url,
            final @DelegatesTo(HttpEntityRequestConfig) Closure closure = {}) {
        final config = new HttpEntityRequestConfig()
        config.with closure

        HttpPut httpRequest = new HttpPut()
        httpRequest.setEntity(config.entity)

        return executeRequest(client, url, httpRequest, config)
    }

    void assertStatusEquals(HttpResponse response, int statusCode) {
        assert statusCode == response?.statusLine?.statusCode: "Expected '${statusCode}', got '${response?.statusLine?.statusCode}'. Status Line: (${response?.statusLine})"
    }
}

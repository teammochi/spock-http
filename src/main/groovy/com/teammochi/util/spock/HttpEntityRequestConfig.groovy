package com.teammochi.util.spock

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.xml.MarkupBuilder
import org.apache.http.HttpEntity
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.apache.http.entity.FileEntity
import org.apache.http.entity.InputStreamEntity
import org.apache.http.entity.StringEntity

@CompileStatic
class HttpEntityRequestConfig extends HttpRequestConfig {
    private HttpEntity entity

    void entity(HttpEntity httpEntity) {
        this.entity = httpEntity
    }

    void entity(String content, ContentType type = null, String charset = null) {
        entity(new StringEntity(content, type?.mimeType, charset))
    }

    void entity(File content, ContentType type = null) {
        entity(new FileEntity(content, type))
    }

    void entity(InputStream content, ContentType type = null, long length = -1l) {
        entity(new InputStreamEntity(content, length, type))
    }

    void entity(byte[] content, ContentType type = null) {
        entity(new ByteArrayEntity(content, type))
    }

    void entity(byte[] content, ContentType type = null, int offset, int length) {
        entity(new ByteArrayEntity(content, offset, length, type))
    }

    void jsonEntity(Object content, boolean prettyPrint = false) {
        String json = JsonOutput.toJson(content)
        if (prettyPrint) {
            json = JsonOutput.prettyPrint(json)
        }
        entity(json, ContentType.APPLICATION_JSON)
    }

    void jsonEntity(boolean prettyPrint = false, @DelegatesTo(JsonBuilder) Closure content) {
        final builder = new JsonBuilder()
        builder.with content

        String json = builder.toString()
        if (prettyPrint) {
            json = JsonOutput.prettyPrint(json)
        }
        entity(json, ContentType.APPLICATION_JSON)
    }

    void xmlEntity(@DelegatesTo(MarkupBuilder) Closure content) {
        def writer = new StringWriter()
        def builder = new MarkupBuilder(writer)
        builder.with content

        entity(writer.toString(), ContentType.APPLICATION_XML)
    }

    HttpEntity getEntity() {
        return entity
    }
}
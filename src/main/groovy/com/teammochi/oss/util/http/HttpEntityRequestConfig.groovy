package com.teammochi.oss.util.http

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.xml.MarkupBuilder
import org.apache.http.HttpEntity
import org.apache.http.entity.*

/**
 * Request configuration that includes an entity, used in any HttpRequest that can accept a body.
 */
@CompileStatic
class HttpEntityRequestConfig extends HttpRequestConfig {
    private HttpEntity entity

    /**
     * Sets this config's entity to the given entity.
     */
    void entity(HttpEntity httpEntity) {
        this.entity = httpEntity
    }

    /**
     * Sets the entity to a {@link StringEntity} with given content, content type, and charset.
     */
    void entity(String content, ContentType type = null, String charset = null) {
        entity(new StringEntity(content, type?.mimeType, charset))
    }

    /**
     * Sets the entity to a {@link FileEntity} with given content, and content type.
     */
    void entity(File content, ContentType type = null) {
        entity(new FileEntity(content, type))
    }

    /**
     * Sets the entity to a {@link InputStreamEntity} with given content, content type, and length.
     */
    void entity(InputStream content, ContentType type = null, long length = -1l) {
        entity(new InputStreamEntity(content, length, type))
    }

    /**
     * Sets the entity to a {@link ByteArrayEntity} with given content and content type.
     */
    void entity(byte[] content, ContentType type = null) {
        entity(new ByteArrayEntity(content, type))
    }

    /**
     * Sets the entity to a {@link ByteArrayEntity} with given content, content type, offset and length.
     */
    void entity(byte[] content, ContentType type = null, int offset, int length) {
        entity(new ByteArrayEntity(content, offset, length, type))
    }

    /**
     * Converts the given object to a JSON String, and calls
     * {@link #entity(java.lang.String, org.apache.http.entity.ContentType)} with
     * {@link ContentType#APPLICATION_JSON ContentType.APPLICATION_JSON}.
     * @param content See {@link JsonOutput#toJson(java.lang.Object)}
     * @param prettyPrint Pretty-prints the Json to the entity. Note that this can slow down performance since the
     * content needs to be converted to a JSON String first, then re-parsed and re-printed pretty-ly.
     */
    void jsonEntity(Object content, boolean prettyPrint = false) {
        String json = JsonOutput.toJson(content)
        if (prettyPrint) {
            json = JsonOutput.prettyPrint(json)
        }
        entity(json, ContentType.APPLICATION_JSON)
    }

    /**
     * Converts the given closure to a JSON String, and calls
     * {@link #entity(java.lang.String, org.apache.http.entity.ContentType)} with
     * {@link ContentType#APPLICATION_JSON ContentType.APPLICATION_JSON}.
     * @param content See {@link JsonBuilder}
     * @param prettyPrint Pretty-prints the Json to the entity. Note that this can slow down performance since the
     * content needs to be converted to a JSON String first, then re-parsed and re-printed pretty-ly.
     */
    void jsonEntity(boolean prettyPrint = false, @DelegatesTo(JsonBuilder) Closure content) {
        final builder = new JsonBuilder()
        builder.with content

        String json = builder.toString()
        if (prettyPrint) {
            json = JsonOutput.prettyPrint(json)
        }
        entity(json, ContentType.APPLICATION_JSON)
    }

    /**
     * Converts the given closure to an XML String, and calls
     * {@link #entity(java.lang.String, org.apache.http.entity.ContentType)} with
     * {@link ContentType#APPLICATION_XML ContentType.APPLICATION_XML}.
     * @param content See {@link MarkupBuilder}
     */
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
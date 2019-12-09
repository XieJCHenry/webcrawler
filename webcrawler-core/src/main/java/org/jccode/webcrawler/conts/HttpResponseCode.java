package org.jccode.webcrawler.conts;

/**
 * HttpResponseCode
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/9 14:08
 * @Version 1.0
 **/
public enum HttpResponseCode {

    OK(200),
    CREATED(201),
    ACCEPTED(202),
    PARTIAL_CONTENT(206),

    MOVED_PERMANENTLY(301),
    FOUND(302),
    NOT_MODIFIED(304),

    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),

    INTERNAL_SERVER_ERROR(500),
    BAD_GATEWAY(502),
    SERVICE_UNABALIABLE(503);


    public int code;

    HttpResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

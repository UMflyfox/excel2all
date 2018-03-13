package com.aliens.command.excel.template.model;

/**
 * Created by hejialin on 2018/3/12.
 */
public class Template {

    private String header;

    private String tail;

    private String bodyPrefix;

    private String body;

    private String bodySuffix;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public String getBodyPrefix() {
        return bodyPrefix;
    }

    public void setBodyPrefix(String bodyPrefix) {
        this.bodyPrefix = bodyPrefix;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBodySuffix() {
        return bodySuffix;
    }

    public void setBodySuffix(String bodySuffix) {
        this.bodySuffix = bodySuffix;
    }

}

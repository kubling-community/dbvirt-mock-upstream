package com.kubling.dbvirt.mocks.upstream.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
public class Expectation {

    public enum HTTPMethod {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE
    }

    String path;
    HTTPMethod method;
    String resource;
    Integer returnCode;
    @Builder.Default
    Boolean once = Boolean.FALSE;
    Expectation andThenUnlock;
}

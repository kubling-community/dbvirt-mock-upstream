package com.kubling.dbvirt.mocks.upstream;

import com.kubling.dbvirt.mocks.upstream.model.Expectation;

import java.io.IOException;

@FunctionalInterface
public interface ResponseEventListener {
    void react(Expectation expectation) throws IOException;
}

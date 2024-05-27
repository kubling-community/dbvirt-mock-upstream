package com.kubling.dbvirt.mocks.upstream;

import com.kubling.dbvirt.mocks.upstream.model.Expectation;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.mockwebserver.DefaultMockServer;
import io.fabric8.mockwebserver.dsl.DelayPathable;
import io.fabric8.mockwebserver.dsl.MockServerExpectation;
import io.fabric8.mockwebserver.dsl.ReturnOrWebsocketable;
import io.fabric8.mockwebserver.dsl.TimesOnceableOrHttpHeaderable;

import java.io.IOException;
import java.util.Objects;

public class Utils {

    public static Object readContentTree(String resource) throws IOException {
        if (Objects.isNull(resource) || resource.isEmpty()) return null;
        return Serialization.jsonMapper().readTree(readResourceContent(resource));
    }

    public static String readResourceContent(String resource) throws IOException {
        if (Objects.isNull(resource) || resource.isEmpty()) return null;
        return new String(Utils.class.getClassLoader()
                .getResourceAsStream(resource).readAllBytes());
    }

    public static DelayPathable<ReturnOrWebsocketable<TimesOnceableOrHttpHeaderable<Void>>> buildPathable(
            DefaultMockServer server, Expectation expectation) {

        MockServerExpectation mockServerExpectation = server.expect();
        DelayPathable<ReturnOrWebsocketable<TimesOnceableOrHttpHeaderable<Void>>> delayPathable;
        if (expectation.getMethod().equals(Expectation.HTTPMethod.POST))
            delayPathable = mockServerExpectation.post();
        else if (expectation.getMethod().equals(Expectation.HTTPMethod.PUT))
            delayPathable = mockServerExpectation.put();
        else if (expectation.getMethod().equals(Expectation.HTTPMethod.PATCH))
            delayPathable = mockServerExpectation.patch();
        else if (expectation.getMethod().equals(Expectation.HTTPMethod.DELETE))
            delayPathable = mockServerExpectation.delete();
        else
            delayPathable = mockServerExpectation.get();

        return delayPathable;
    }
}

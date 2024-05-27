package com.kubling.dbvirt.mocks.upstream;

import com.kubling.dbvirt.mocks.upstream.model.Expectation;
import com.kubling.dbvirt.mocks.upstream.model.MockServerConfig;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.mockwebserver.DefaultMockServer;
import io.fabric8.mockwebserver.MockServer;
import io.fabric8.mockwebserver.dsl.DelayPathable;
import io.fabric8.mockwebserver.dsl.ReturnOrWebsocketable;
import io.fabric8.mockwebserver.dsl.TimesOnceableOrHttpHeaderable;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class UpstreamSystems {

    List<MockServer> servers = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        new UpstreamSystems().init();
    }

    void init() throws IOException {
        servers.addAll(startFakeServers(
                List.of("kubernetes_server_1_expect.yaml", "kubernetes_server_2_expect.yaml", "github_server_expect.yaml")));
    }

    MockServer startFakeServer(String config) throws IOException {

        MockServerConfig mockServerConfig = Serialization.yamlMapper().readValue(config, MockServerConfig.class);
        DefaultMockServer server = new DefaultMockServer();

        for (final Expectation expectation : mockServerConfig.getExpectations()) {

            DelayPathable<ReturnOrWebsocketable<TimesOnceableOrHttpHeaderable<Void>>> delayPathable =
                    Utils.buildPathable(server, expectation);

            final var times = delayPathable
                    .withPath(expectation.getPath())
                    .andReply(new ChainResponseProvider(
                            expectation,
                            expectationListener -> {
                                DelayPathable<ReturnOrWebsocketable<TimesOnceableOrHttpHeaderable<Void>>> innerPathable =
                                        Utils.buildPathable(server, expectationListener);
                                final var innerTimes = innerPathable
                                        .withPath(expectationListener.getPath())
                                        .andReturn(
                                                expectationListener.getReturnCode(),
                                                Utils.readResourceContent(expectationListener.getResource()));
                                if (expectationListener.getOnce())
                                    innerTimes.once();
                                else
                                    innerTimes.always();
                            }));

            if (expectation.getOnce())
                times.once();
            else
                times.always();

        }

        server.start(mockServerConfig.getServerPort());

        return server;

    }

    List<MockServer> startFakeServers(List<String> configs) throws IOException {
        return configs.stream().map(c -> {
            try {
                return startFakeServer(Utils.readResourceContent(c));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toUnmodifiableList());
    }

}

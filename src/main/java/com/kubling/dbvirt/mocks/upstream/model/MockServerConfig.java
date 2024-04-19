package com.kubling.dbvirt.mocks.upstream.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Setter
@Builder
@Jacksonized
public class MockServerConfig {
    Integer serverPort;
    List<Expectation> expectations;
}

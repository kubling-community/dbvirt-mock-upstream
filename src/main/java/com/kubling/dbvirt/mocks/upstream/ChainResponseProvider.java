package com.kubling.dbvirt.mocks.upstream;

import com.kubling.dbvirt.mocks.upstream.model.Expectation;
import io.fabric8.mockwebserver.utils.ResponseProvider;
import okhttp3.Headers;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.util.Map;

public class ChainResponseProvider implements ResponseProvider {

    final Expectation expectation;
    final ResponseEventListener listener;

    public ChainResponseProvider(Expectation expectation, ResponseEventListener listener) {
        this.expectation = expectation;
        this.listener = listener;
    }

    @Override
    public int getStatusCode(RecordedRequest request) {
        return expectation.getReturnCode();
    }

    @Override
    public Headers getHeaders() {
        return Headers.of(Map.of());
    }

    @Override
    public void setHeaders(Headers headers) {
    }

    @Override
    public Object getBody(RecordedRequest request) {
        try {
            return Utils.readContentTree(expectation.getResource());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

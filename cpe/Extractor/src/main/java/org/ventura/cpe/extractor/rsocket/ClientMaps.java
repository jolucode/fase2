package org.ventura.cpe.extractor.rsocket;

import org.springframework.messaging.rsocket.RSocketRequester;

import java.util.HashMap;
import java.util.Map;

public final class ClientMaps {

    private static final Map<String, RSocketRequester> REQESTER_MAP = new HashMap<>();

    public static Map<String, RSocketRequester> getReqesterMap() {
        return REQESTER_MAP;
    }
}

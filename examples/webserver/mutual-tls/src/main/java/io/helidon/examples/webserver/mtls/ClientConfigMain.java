/*
 * Copyright (c) 2020, 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.examples.webserver.mtls;

import io.helidon.config.Config;
import io.helidon.webclient.http1.Http1Client;

/**
 * Setting up {@link io.helidon.webclient.api.WebClient} to support mutual TLS via configuration.
 */
public class ClientConfigMain {

    private ClientConfigMain() {
    }

    /**
     * Start the example.
     * This example executes two requests by Helidon {@link io.helidon.webclient.api.WebClient} which are configured
     * by the configuration.
     * <p>
     * You have to execute either {@link ServerBuilderMain} or {@link ServerConfigMain} for this to work.
     * <p>
     * If any of the ports has been changed, you have to update ports in this main method also.
     *
     * @param args start arguments are ignored
     */
    public static void main(String[] args) {
        Config config = Config.create();
        Http1Client client = Http1Client.builder()
                                        .config(config.get("client"))
                                        .build();

        System.out.println("Contacting unsecured endpoint!");
        System.out.println("Response: " + callUnsecured(client, 8080));

        System.out.println("Contacting secured endpoint!");
        System.out.println("Response: " + callSecured(client, 443));

    }

    static String callUnsecured(Http1Client client, int port) {
        return client.get("http://localhost:" + port)
                     .requestEntity(String.class);
    }

    static String callSecured(Http1Client client, int port) {
        return client.get("https://localhost:" + port)
                .requestEntity(String.class);
    }
}

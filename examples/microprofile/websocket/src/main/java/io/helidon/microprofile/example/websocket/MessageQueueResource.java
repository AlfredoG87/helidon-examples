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

package io.helidon.microprofile.example.websocket;

import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

/**
 * Class MessageQueueResource.
 */
@Path("rest")
public class MessageQueueResource {

    private static final Logger LOGGER = Logger.getLogger(MessageQueueResource.class.getName());

    @Inject
    private MessageQueue messageQueue;

    /**
     * Resource to push string into queue.
     *
     * @param s The string.
     */
    @POST
    @Consumes("text/plain")
    public void push(String s) {
        LOGGER.info("push called '" + s + "'");
        messageQueue.push(s);
    }
}

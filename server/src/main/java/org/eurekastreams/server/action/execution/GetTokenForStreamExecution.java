/*
 * Copyright (c) 2011 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.action.execution;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.GetTokenForStreamRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.service.email.TokenEncoder;

/**
 * Gets a token for the current user for posting to a stream.
 */
public class GetTokenForStreamExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Creates the token. */
    private final TokenEncoder tokenEncoder;

    /** Gets the user's key. */
    private final DomainMapper<Long, byte[]> cryptoKeyDao;

    /**
     * Constructor.
     *
     * @param inTokenEncoder
     *            Creates the token.
     * @param inCryptoKeyDao
     *            Gets the user's key.
     */
    public GetTokenForStreamExecution(final TokenEncoder inTokenEncoder,
            final DomainMapper<Long, byte[]> inCryptoKeyDao)
    {
        tokenEncoder = inTokenEncoder;
        cryptoKeyDao = inCryptoKeyDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        // get current user's crypto key
        Long personId = inActionContext.getPrincipal().getId();
        byte[] key = cryptoKeyDao.execute(personId);

        GetTokenForStreamRequest params = (GetTokenForStreamRequest) inActionContext.getParams();

        return tokenEncoder.encodeForStream(params.getStreamEntityType(), params.getStreamEntityId(), personId, key);
    }
}
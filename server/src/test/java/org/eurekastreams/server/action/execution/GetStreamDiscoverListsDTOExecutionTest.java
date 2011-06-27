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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.dto.DisplayInfoSettable;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.dto.StreamDiscoverListsDTO;
import org.eurekastreams.server.domain.dto.SublistWithResultCount;
import org.eurekastreams.server.domain.strategies.DisplayInfoSettableDataPopulator;
import org.eurekastreams.server.domain.strategies.FollowerStatusPopulator;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.SuggestedStreamsRequest;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test fixture for GetStreamDiscoverListsDTOExecution.
 */
public class GetStreamDiscoverListsDTOExecutionTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper to get suggested people streams.
     */
    private DomainMapper<SuggestedStreamsRequest, List<PersonModelView>> suggestedPersonMapper = context.mock(
            DomainMapper.class, "suggestedPersonMapper");

    /**
     * Mapper to get suggested group streams.
     */
    private DomainMapper<SuggestedStreamsRequest, List<DomainGroupModelView>> suggestedGroupMapper = context.mock(
            DomainMapper.class, "suggestedGroupMapper");

    /**
     * Mapper to get the stream discovery lists that are the same for everyone.
     */
    private DomainMapper<Serializable, StreamDiscoverListsDTO> streamDiscoveryListsMapper = context.mock(
            DomainMapper.class, "streamDiscoveryListsMapper");

    /**
     * Data populator for setting the DisplayName and avatar id on DisplayInfoSettables.
     */
    private DisplayInfoSettableDataPopulator displayInfoSettableDataPopulator = context
            .mock(DisplayInfoSettableDataPopulator.class);

    /**
     * Data populator for setting the follower status on DisplayInfoSettables.
     */
    private FollowerStatusPopulator<DisplayInfoSettable> followerStatusPopulator = context
            .mock(FollowerStatusPopulator.class);

    /**
     * System under test.
     */
    private GetStreamDiscoverListsDTOExecution sut = new GetStreamDiscoverListsDTOExecution(suggestedPersonMapper,
            suggestedGroupMapper, streamDiscoveryListsMapper, displayInfoSettableDataPopulator, //
            followerStatusPopulator);

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final Long personId = 5L;
        final List<PersonModelView> people = new ArrayList<PersonModelView>();
        final List<DomainGroupModelView> groups = new ArrayList<DomainGroupModelView>();
        final SuggestedStreamsRequest request = new SuggestedStreamsRequest(personId, 10);

        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
        final Principal principal = context.mock(Principal.class);

        final StreamDiscoverListsDTO result = new StreamDiscoverListsDTO();

        people.add(new PersonModelView(1L, "a", "foo", "bar", 100L, new Date(), 1L));
        people.add(new PersonModelView(2L, "b", "foo", "bar", 900L, new Date(), 2L)); // 3
        people.add(new PersonModelView(3L, "c", "foo", "bar", 200L, new Date(), 3L));
        people.add(new PersonModelView(4L, "d", "foo", "bar", 800L, new Date(), 4L)); // 5
        people.add(new PersonModelView(5L, "e", "foo", "bar", 300L, new Date(), 5L)); // 9
        people.add(new PersonModelView(6L, "f", "foo", "bar", 200L, new Date(), 6L));
        people.add(new PersonModelView(7L, "g", "foo", "bar", 700L, new Date(), 7L)); // 7

        groups.add(new DomainGroupModelView(8L, "h", "foobar", 50L, new Date(), 8L));
        groups.add(new DomainGroupModelView(9L, "i", "foobar", 250L, new Date(), 9L)); // 10
        groups.add(new DomainGroupModelView(10L, "j", "foobar", 200L, new Date(), 10L));
        groups.add(new DomainGroupModelView(11L, "k", "foobar", 300L, new Date(), 11L)); // 8
        groups.add(new DomainGroupModelView(12L, "l", "foobar", 700L, new Date(), 12L)); // 6
        groups.add(new DomainGroupModelView(13L, "m", "foobar", 900L, new Date(), 13L)); // 2
        groups.add(new DomainGroupModelView(14L, "n", "foobar", 800L, new Date(), 14L)); // 4
        groups.add(new DomainGroupModelView(15L, "o", "foobar", 950L, new Date(), 15L)); // 1

        // displayInfoSettables.addAll(result.getFeaturedStreams());
        // displayInfoSettables.addAll(result.getMostFollowedStreams());
        // displayInfoSettables.addAll(result.getMostRecentStreams());
        // displayInfoSettables.addAll(result.getMostViewedStreams());
        // displayInfoSettables.addAll(result.getSuggestedStreams());
        // displayInfoSettables.addAll(result.getMostActiveStreams().getResultsSublist());

        FeaturedStreamDTO featured = new FeaturedStreamDTO();

        result.setFeaturedStreams(Collections.singletonList(featured));
        result.setMostFollowedStreams(Collections.singletonList((StreamDTO) people.get(0)));
        result.setMostRecentStreams(Collections.singletonList((StreamDTO) people.get(0)));
        result.setMostViewedStreams(Collections.singletonList((StreamDTO) people.get(2)));

        result.setMostActiveStreams(new SublistWithResultCount<StreamDTO>(Collections.singletonList((StreamDTO) groups
                .get(0)), 3L));

        final List<DisplayInfoSettable> combinedList = new ArrayList<DisplayInfoSettable>();

        // featured list
        combinedList.add(featured);

        // most followed
        combinedList.add(people.get(0));

        // most recent
        combinedList.add(people.get(0));

        // most viewed
        combinedList.add(people.get(2));

        // suggestions
        combinedList.add(groups.get(7));
        combinedList.add(groups.get(5));
        combinedList.add(people.get(1));
        combinedList.add(groups.get(6));
        combinedList.add(people.get(3));
        combinedList.add(groups.get(4));
        combinedList.add(people.get(6));
        combinedList.add(groups.get(3));
        combinedList.add(people.get(4));
        combinedList.add(groups.get(1));

        // most active
        combinedList.add(groups.get(0));

        if (displayInfoSettableDataPopulator == null)
        {
            throw new RuntimeException("WTH?");
        }

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(personId));

                oneOf(actionContext).getParams();
                will(returnValue(10));

                oneOf(suggestedPersonMapper).execute(with(IsEqualInternally.equalInternally(request)));
                will(returnValue(people));

                oneOf(suggestedGroupMapper).execute(with(IsEqualInternally.equalInternally(request)));
                will(returnValue(groups));

                oneOf(streamDiscoveryListsMapper).execute(null);
                will(returnValue(result));

                oneOf(displayInfoSettableDataPopulator).execute(with(IsEqualInternally.equalInternally(personId)),
                        with(IsEqualInternally.equalInternally(combinedList)));
                will(returnValue(combinedList));

                oneOf(followerStatusPopulator).execute(with(personId), with(combinedList),
                        with(FollowerStatus.NOTFOLLOWING));
                will(returnValue(combinedList));
            }
        });

        Assert.assertSame(result, sut.execute(actionContext));

        List<StreamDTO> suggestions = result.getSuggestedStreams();

        Assert.assertEquals(10, suggestions.size());

        Assert.assertEquals(15, suggestions.get(0).getId());
        Assert.assertEquals(13, suggestions.get(1).getId());
        Assert.assertEquals(2, suggestions.get(2).getId());
        Assert.assertEquals(14, suggestions.get(3).getId());
        Assert.assertEquals(4, suggestions.get(4).getId());
        Assert.assertEquals(12, suggestions.get(5).getId());
        Assert.assertEquals(7, suggestions.get(6).getId());
        Assert.assertEquals(11, suggestions.get(7).getId());
        Assert.assertEquals(5, suggestions.get(8).getId());
        Assert.assertEquals(9, suggestions.get(9).getId());

        context.assertIsSatisfied();
    }
}

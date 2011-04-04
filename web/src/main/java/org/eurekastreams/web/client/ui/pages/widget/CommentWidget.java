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
package org.eurekastreams.web.client.ui.pages.widget;

import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.PostToStreamComposite;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;
import org.eurekastreams.web.client.ui.common.stream.StreamPanel;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.object.NotePopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.verb.PostPopulator;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;

/**
 * The Eureka Connect "comment widget" - displays a stream for a resource and allows posting.
 */
public class CommentWidget extends Composite
{
    /** Unique ID of entity whose stream is displayed. */
    private final String resourceId;

    /** Description of the resource. */
    private String resourceTitle;

    /** URL of the site containing the resource. */
    private String siteUrl;

    /** Description of the site containing the resource. */
    private String siteTitle;

    /**
     * Constructor.
     *
     * @param view
     *            Unique ID of resource whose stream to display.
     */
    public CommentWidget(final String view)
    {
        resourceId = view;

        StreamScope streamScope = new StreamScope(ScopeType.RESOURCE, resourceId);

        final StreamPanel streamPanel = new StreamPanel(false, new CommentWidgetPostToStreamComposite(streamScope));
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().embeddedWidget());
        streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectCommentWidget());
        initWidget(streamPanel);

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                // hide everything but the post box if the stream is empty
                // but distinguish between an empty stream and no search results
                boolean emptyStream = Session.getInstance().getParameterValue("search") == null
                        && event.getStream().getPagedSet().isEmpty();
                if (emptyStream)
                {
                    streamPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().emptyStream());
                }
                else
                {
                    streamPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().emptyStream());
                }
            }
        });

        String jsonRequest = StreamJsonRequestFactory.addRecipient(EntityType.RESOURCE, resourceId,
                StreamJsonRequestFactory.getEmptyRequest()).toString();

        EventBus.getInstance().notifyObservers(new StreamRequestEvent("", jsonRequest));
        streamPanel.setStreamScope(streamScope, true);
    }

    /**
     * Custom version of the post composite tailored to post activities to a resource stream.
     */
    class CommentWidgetPostToStreamComposite extends PostToStreamComposite
    {
        /** Activity populator. */
        private final ActivityDTOPopulator activityPopulator = new ActivityDTOPopulator();

        /** Checkbox for whether to post to Eureka (used for resource streams). */
        private final CheckBox postToEurekaCheckBox = new CheckBox("Post to Eureka");

        /**
         * Constructor.
         *
         * @param inScope
         *            Stream scope.
         */
        public CommentWidgetPostToStreamComposite(final StreamScope inScope)
        {
            super(inScope);

            postToEurekaCheckBox.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postToEureka());
            getSubTextboxPanel().insert(postToEurekaCheckBox, 0);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onExpand()
        {
            super.onExpand();
            postToEurekaCheckBox.setValue(true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void postMessage()
        {
            ActivityDTO activity = activityPopulator.getActivityDTO(getMesssageText(), EntityType.RESOURCE,
                    resourceId, new PostPopulator(), new NotePopulator());

            activity.setShowInStream(postToEurekaCheckBox.getValue());
            // TODO: put other fields in

            PostActivityRequest postRequest = new PostActivityRequest(activity);
            ActivityModel.getInstance().insert(postRequest);
        }
    }
}
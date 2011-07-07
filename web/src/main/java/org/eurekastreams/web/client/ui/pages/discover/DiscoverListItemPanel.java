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
package org.eurekastreams.web.client.ui.pages.discover;

import java.util.Date;

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * FlowPanel for the four simple lists of StreamDTOs on the Discover page.
 */
public class DiscoverListItemPanel extends FlowPanel
{
    /**
     * The type subtext type to show under the name.
     */
    public enum ListItemType
    {
        /**
         * Mutual follower(s).
         */
        MUTUAL_FOLLOWERS,

        /**
         * Daily viewer(s).
         */
        DAILY_VIEWERS,

        /**
         * Follower(s).
         */
        FOLLOWERS,

        /**
         * Formatted time ago.
         */
        TIME_AGO
    };

    /**
     * Constructor.
     *
     * @param inStreamDTO
     *            the streamDTO to represent
     * @param inListItemType
     *            list item type
     */
    public DiscoverListItemPanel(final StreamDTO inStreamDTO, final ListItemType inListItemType)
    {
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItem());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().listItem());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().person());

        FlowPanel infoPanel = new FlowPanel();
        infoPanel.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemInfo());

        Widget name;

        Page linkPage;
        if (inStreamDTO instanceof PersonModelView)
        {
            linkPage = Page.PEOPLE;
        }
        else
        {
            // assume group
            linkPage = Page.GROUPS;
        }
        String nameUrl = Session.getInstance().generateUrl(//
                new CreateUrlRequest(linkPage, inStreamDTO.getUniqueId()));

        name = new Hyperlink(inStreamDTO.getDisplayName(), nameUrl);
        name.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemName());
        infoPanel.add(name);

        FlowPanel followersPanel = new FlowPanel();
        followersPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowers());

        switch (inListItemType)
        {
        case MUTUAL_FOLLOWERS:
            if (inStreamDTO.getFollowersCount() == 1)
            {
                followersPanel.add(new InlineLabel("1 Mutal Follower"));
            }
            else
            {
                followersPanel.add(new InlineLabel(Integer.toString(inStreamDTO.getFollowersCount())
                        + " Mutal Followers"));
            }
            break;
        case DAILY_VIEWERS:
            if (inStreamDTO.getFollowersCount() == 1)
            {
                followersPanel.add(new InlineLabel("1 Daily Viewer"));
            }
            else
            {
                followersPanel.add(new InlineLabel(Integer.toString(inStreamDTO.getFollowersCount())
                        + " Daily Viewers"));
            }
            break;
        case FOLLOWERS:
            if (inStreamDTO.getFollowersCount() == 1)
            {
                followersPanel.add(new InlineLabel("1 Follower"));
            }
            else
            {
                followersPanel.add(new InlineLabel(Integer.toString(inStreamDTO.getFollowersCount()) + " Followers"));
            }
            break;
        case TIME_AGO:
            DateFormatter dateFormatter = new DateFormatter(new Date());
            InlineLabel dateAdded = new InlineLabel(dateFormatter.timeAgo(inStreamDTO.getDateAdded(), true));
            dateAdded.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowersData());
            followersPanel.add(dateAdded);

            break;
        default:
            break;
        }
        insertActionSeparator(followersPanel);
        if (inStreamDTO.getEntityType() != EntityType.PERSON
                || inStreamDTO.getEntityId() != Session.getInstance().getCurrentPerson().getEntityId())
        {
            // not the current person
            followersPanel.add(new FollowPanel(inStreamDTO));
        }
        infoPanel.add(followersPanel);

        this.add(infoPanel);
    }

    /**
     * Adds a separator (dot).
     *
     * @param panel
     *            Panel to put the separator in.
     */
    private void insertActionSeparator(final Panel panel)
    {
        Label sep = new InlineLabel("\u2219");
        sep.addStyleName(StaticResourceBundle.INSTANCE.coreCss().actionLinkSeparator());
        panel.add(sep);
    }
}

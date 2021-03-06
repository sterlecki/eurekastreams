/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db.notification;

import javax.persistence.Query;

import org.eurekastreams.server.domain.NotificationFilterPreferenceEntity;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.SetUserNotificationFilterPreferencesRequest;

/**
 * Sets a user's notification filter preferences, leaving any not specified alone.
 */
public class SetUserNotificationFilterPreferences extends
        BaseArgDomainMapper<SetUserNotificationFilterPreferencesRequest, Void>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Void execute(final SetUserNotificationFilterPreferencesRequest inRequest)
    {
        // prepare existence query
        Query checkQuery = getEntityManager()
                .createQuery(
                "SELECT COUNT(*) FROM NotificationFilterPreference WHERE personId=:personId AND "
                        + "notifierType=:notifierType AND notificationCategory=:notificationCategory")
                .setParameter("personId", inRequest.getPersonId());

        Person user = null;

        for (NotificationFilterPreferenceDTO dto : inRequest.getPrefList())
        {
            // check if the row already exists
            checkQuery.setParameter("notifierType", dto.getNotifierType());
            checkQuery.setParameter("notificationCategory", dto.getNotificationCategory());
            if ((Long) checkQuery.getSingleResult() == 0)
            {
                // create the preference entry
                if (user == null)
                {
                    user = (Person) getHibernateSession().load(Person.class, inRequest.getPersonId());
                }
                NotificationFilterPreferenceEntity entity = new NotificationFilterPreferenceEntity();
                entity.setPerson(user);
                entity.setNotificationCategory(dto.getNotificationCategory());
                entity.setNotifierType(dto.getNotifierType());
                getEntityManager().persist(entity);
            }
        }

        return null;
    }
}

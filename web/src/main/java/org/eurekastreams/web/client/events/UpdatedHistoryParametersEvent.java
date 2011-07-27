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
package org.eurekastreams.web.client.events;

import java.util.HashMap;

/**
 * History parameters have been updated.
 * 
 */
public class UpdatedHistoryParametersEvent
{
    /**
     * The parameters.
     */
    private HashMap<String, String> paramters;

    /**
     * View has changed.
     */
    private Boolean viewChanged;

    /**
     * Default constructor.
     * 
     * @param inParameters
     *            the updated parameters.
     * @param inViewChanged
     *            if the view changed.
     */
    public UpdatedHistoryParametersEvent(final HashMap<String, String> inParameters, final Boolean inViewChanged)
    {
        paramters = inParameters;
        setViewChanged(inViewChanged);
    }

    /**
     * Get the parameters.
     * 
     * @return the parameters.
     */
    public HashMap<String, String> getParameters()
    {
        return paramters;
    }

    /**
     * @param inViewChanged
     *            the viewChanged to set.
     */
    public void setViewChanged(final Boolean inViewChanged)
    {
        this.viewChanged = inViewChanged;
    }

    /**
     * @return the viewChanged.
     */
    public Boolean getViewChanged()
    {
        return viewChanged;
    }
}

/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     stan
 */

package org.nuxeo.training;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.user.center.profile.UserProfileService;
import org.nuxeo.runtime.api.Framework;

@Operation(id = GetUserProfile.ID, category = Constants.CAT_SERVICES, label = "GetUserProfile", description = "")
public class GetUserProfile {

    public static final String ID = "GetUserProfile";

    @Param(name = "username", required = false)
    String userName;

    @Context
    CoreSession session;

    @OperationMethod
    public DocumentModel run() throws Exception {
        UserProfileService userProfileService = Framework.getLocalService(UserProfileService.class);
        if (userName == null) {
            return userProfileService.getUserProfileDocument(session);
        }
        return userProfileService.getUserProfileDocument(userName, session);
    }

}

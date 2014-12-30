/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Bertrand Chauvin (bchauvin@nuxeo.com)
 */

package org.nuxeo.training.bestbooks;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
@Operation(id=CheckUserPermission.ID, category=Constants.CAT_USERS_GROUPS, label="Check User Permission", description="Check a specific permission on a document for the given username.")
public class CheckUserPermission {

    public static final String ID = "CheckUserPermission";

    // Injections: CoreSession and OperationContext
    @Context
    protected CoreSession session;

    @Context
    protected OperationContext ctx;

    // Parameters
    @Param(name = "username")
    protected String username;

    @Param(name = "permission")
    protected String permission;

    @Param(name = "document id")
    protected String docId;

    @Param(name = "context variable name")
    protected String ctxVar;

    @OperationMethod
    public void run() {
        // Get a NuxeoPrincipal from a username
        UserManager userManager = Framework.getLocalService(UserManager.class);
        NuxeoPrincipal principal = userManager.getPrincipal(username);

        // Get a DocumentRef from a doc id
        IdRef docRef = new IdRef(docId);

        // Check permission
        boolean hasPermission = session.hasPermission(principal, docRef, permission);

        // Put result into a context variable
        ctx.put(ctxVar, hasPermission);
    }

}


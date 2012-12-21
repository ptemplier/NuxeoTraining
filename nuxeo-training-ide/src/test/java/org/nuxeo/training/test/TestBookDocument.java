/*
 * (C) Copyright 2013 Nuxeo SA (http://nuxeo.com/) and contributors.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License (LGPL)
 * version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * Contributors: TAN Sun Seng David <stan@nuxeo.com>
 */
package org.nuxeo.training.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@Deploy({ "org.nuxeo.ecm.automation.core", "nuxeo-training-ide",
        "studio.extensions.Training_Nuxeo" })
@LocalDeploy("nuxeo-training-ide:OSGI-INF/fake-actions-component.xml")
public class TestBookDocument {
    @Inject
    CoreSession session;

    @Test
    public void testBookDoc() throws Exception {
        // studio component loaded ?
        Assert.assertNotNull(
                "The studio component should be loaded",
                Framework.getRuntime().getComponent(
                        "studio.extensions.SunTraining_Nuxeo"));

        // making sure that /default-domain/book-library is automatically
        // created thanks to the structure template
        PathRef bookLibraryDocRef = new PathRef("/default-domain/book-library");
        assertTrue(session.exists(bookLibraryDocRef));
        // making sure that the type is BookLibary
        DocumentModel bookLibraryDoc = session.getDocument(bookLibraryDocRef);
        assertEquals("The Book Libary doc type is", "BookLibrary",
                bookLibraryDoc.getType());
        // and Library as title
        assertEquals("The Book Library title is", "Library",
                bookLibraryDoc.getPropertyValue("dc:title"));

        // Create a Book document model
        DocumentModel bookDoc = session.createDocumentModel(
                "/default-domain/book-library", "book1", "Book");
        // Set a title (my book) + isbn (12345)
        bookDoc.setPropertyValue("dc:title", "my book");
        bookDoc.setPropertyValue("bk:isbn", "12345");
        // Check the publication date is not null and setted to the current date
        assertNotNull(
                "The publication date should be setted with a eventhandler",
                bookDoc.getPropertyValue("bk:publicationDate"));
        // Create the book in the repository
        bookDoc = session.createDocument(bookDoc);

        // Call the automation chain « borrowChain »
        AutomationService automationService = Framework.getLocalService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setCoreSession(session);
        ctx.setInput(bookDoc);
        automationService.run(ctx, "borrowChain");

        bookDoc = session.getDocument(bookDoc.getRef());
        assertNotNull(bookDoc.getPropertyValue("bk:borrowedBy"));
        assertEquals("borrowed",
                session.getCurrentLifeCycleState(bookDoc.getRef()));
        
        ctx = new OperationContext();
        ctx.setCoreSession(session);
        ctx.setInput(bookDoc);
        automationService.run(ctx, "bringBack");
        
        bookDoc = session.getDocument(bookDoc.getRef());
        Assert.assertNull(bookDoc.getPropertyValue("bk:borrowedBy"));
        assertEquals("InLibrary",
                session.getCurrentLifeCycleState(bookDoc.getRef()));

    }

}

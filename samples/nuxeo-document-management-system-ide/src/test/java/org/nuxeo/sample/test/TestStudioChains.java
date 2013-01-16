package org.nuxeo.sample.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.test.RestFeature;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.ecm.platform.uidgen.UIDGenerator;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class, RestFeature.class })
@Deploy({ "org.nuxeo.ecm.platform.uidgen.core",
        "org.nuxeo.ecm.core.persistence",
        "nuxeo-document-management-system-ide",
        "studio.extensions.nuxeo-document-management-system" })
// - Overrides the userdirectory defined in studio and define the datasource
// properties instead of rely on the JNDI
// - Fake hibernate provider so we can use Fn.getNextId in the chains
@LocalDeploy({
        "nuxeo-document-management-system-ide:studio-userdirectory-ds-override.xml",
        "nuxeo-document-management-system-ide:uuid-fake-hibernate-provider.xml" })
public class TestStudioChains {

    public static String UIDManifestCoreID = UIDGenerator.class.getPackage()
            + ".core";

    @Inject
    FeaturesRunner featuresRunner;

    @Inject
    CoreSession session;

    @Inject
    UserManager userManager;

    @Test
    public void testPoRightsSettingCreation() throws ClientException {
        createDAFolder();

        // Create the document of type -> po_request
        CoreSession sandrineSession = openSession(userManager.getPrincipal("sandrine"));

        DocumentModel doc = session.createDocumentModel(
                "/default-domain/workspaces/account_financial/DA",
                "newRequest", "po_request");
        doc = sandrineSession.createDocument(doc);
        doc = sandrineSession.getDocument(doc.getRef());
        closeSession(sandrineSession);

        // Administrator has access because it's the administrator :)
        shouldHaveAccess(doc, "Administrator");

        shouldNotHaveAccess(doc, "carole");
        shouldNotHaveAccess(doc, "xavier");
        // laurent is in the finance group
        shouldHaveAccess(doc, "laurent");
        shouldNotHaveAccess(doc, "christophe");
        shouldNotHaveAccess(doc, "cedric");
        shouldNotHaveAccess(doc, "remi");
        shouldNotHaveAccess(doc, "francois");
        shouldNotHaveAccess(doc, "lionel");
        shouldNotHaveAccess(doc, "herve");
        shouldNotHaveAccess(doc, "michel");
        shouldNotHaveAccess(doc, "stephanie");
        shouldNotHaveAccess(doc, "christopheg");
        // sandrine is the creator
        shouldHaveAccess(doc, "sandrine");
    }

    @Test
    public void testPoRequestTotalPriceCalculation() throws Exception {
        // create a new po_request doc
        // The user fills some materials in the layout
        // create the document
        // makesure the total is the total of all materials quantity * freeduty
        // price
        createDAFolder();
        DocumentModel porequestDoc = session.createDocumentModel(
                "/default-domain/workspaces/account_financial/DA",
                "new_po_request", "po_request");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> materialList = (List<Map<String, Object>>) porequestDoc.getPropertyValue("po_request:material");

        HashMap<String, Object> material1 = new HashMap<String, Object>();
        material1.put("duty_free_price", 76);
        material1.put("quantity", 2);
        materialList.add(material1);

        HashMap<String, Object> material2 = new HashMap<String, Object>();
        material2.put("duty_free_price", 23);
        material2.put("quantity", 3);
        materialList.add(material2);

        HashMap<String, Object> material3 = new HashMap<String, Object>();
        material3.put("duty_free_price", 9);
        material3.put("quantity", 90);
        materialList.add(material3);

        porequestDoc.setProperty("po_request", "material", materialList);

        porequestDoc = session.createDocument(porequestDoc);

        porequestDoc = session.getDocument(porequestDoc.getRef());
        Double total_price = (Double) porequestDoc.getPropertyValue("po_request:total_price");
        assertNotNull("total_price should be setted", total_price);

        assertEquals("total price is", 76 * 2 + 23 * 3 + 9 * 90,
                total_price.intValue());

    }

    /**
     * Create a DA folder for tests
     *
     * @throws ClientException
     */
    protected void createDAFolder() throws ClientException {
        // creating folder /default-domain/workspaces/account_financial/DA
        DocumentModel daFolder = session.createDocumentModel(
                "/default-domain/workspaces/account_financial", "DA", "Folder");

        daFolder = session.createDocument(daFolder);
        ACP acp = session.getACP(daFolder.getRef());
        ACL acl = acp.getACL(ACL.LOCAL_ACL);
        if (acl == null) {
            acl = new ACLImpl(ACL.LOCAL_ACL);
            acp.addACL(acl);
        }
        acl.add(new ACE("members", "ReadWrite", true));

        session.setACP(daFolder.getRef(), acp, true);
        session.save();
    }

    /**
     * Testing that the user doesn't have access to the document
     *
     * @param doc
     * @param user
     * @throws ClientException
     */
    protected void shouldNotHaveAccess(DocumentModel doc, String user)
            throws ClientException {
        CoreSession userSession = null;
        try {
            userSession = openSession(userManager.getPrincipal(user));
            try {
                userSession.getDocument(doc.getRef());
            } catch (ClientException e) {
                if (!(e.getMessage().contains("Failed to get document") || e.getMessage().contains(
                        "Privilege 'Read' is not granted to"))) {
                    Assert.fail(user
                            + " shouldn't have access to the document "
                            + doc.getPathAsString());
                }
                return;
            }
            Assert.fail(user + " shouldn't have access to the document "
                    + doc.getPathAsString());
        } finally {
            if (userSession == null) {
                closeSession(userSession);
            }
        }
    }

    /**
     * Testing that the user have access to the document
     *
     * @param doc
     * @param user
     * @throws ClientException
     */
    protected void shouldHaveAccess(DocumentModel doc, String user)
            throws ClientException {
        CoreSession userSession = null;
        try {
            userSession = openSession(userManager.getPrincipal(user));
            userSession.getDocument(doc.getRef());
        } catch (ClientException e) {
            if (e.getMessage().contains("Failed to get document")
                    || e.getMessage().contains(
                            "Privilege 'Read' is not granted to")) {
                Assert.fail(user + " user should have acces to the document "
                        + doc.getPathAsString());
            }
        } finally {
            if (userSession == null) {
                closeSession(userSession);
            }
        }
    }

    /**
     * Opening a new coreSession
     *
     * @param principal
     * @return
     * @throws ClientException
     */
    protected CoreSession openSession(NuxeoPrincipal principal)
            throws ClientException {
        CoreFeature coreFeature = featuresRunner.getFeature(CoreFeature.class);
        Map<String, Serializable> ctx = new HashMap<String, Serializable>();
        ctx.put("principal", principal);
        return coreFeature.getRepository().getRepositoryHandler().openSession(
                ctx);
    }

    /**
     * closing a coreSession
     *
     * @param session
     */
    protected void closeSession(CoreSession session) {
        CoreInstance.getInstance().close(session);
    }

}

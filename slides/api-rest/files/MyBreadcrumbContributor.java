package org.nuxeo.sample;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.nuxeo.ecm.automation.io.services.contributor.RestContributor;
import org.nuxeo.ecm.automation.io.services.contributor.RestEvaluationContext;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.runtime.api.Framework;

public class MyBreadcrumbContributor implements RestContributor {

    private int maxLevels = 5;

    @Override
    public void contribute(JsonGenerator jg, RestEvaluationContext ec)
            throws ClientException, IOException {
        DocumentModel doc = ec.getDocumentModel();
        int requestLevels = Integer.parseInt(ec.getRequest().getParameter("levels"));
        if(ec.getRequest().getParameter("levels") != null) {
            maxLevels = requestLevels;
        }

        ObjectNode breadcrumbJsonObject = getJsonBreadcrumb(doc);
        jg.writeObject(breadcrumbJsonObject);

    }

    private ObjectNode getJsonBreadcrumb(DocumentModel doc)
            throws PropertyException, ClientException {

        ObjectMapper o = new ObjectMapper();
        ObjectNode breadcrumbObject = o.createObjectNode();

        DocumentRef parentRef = doc.getParentRef();
        CoreSession documentManager = doc.getCoreSession();

        int levels = 0;
        while (parentRef != null && levels < maxLevels) {
            DocumentModel parent = documentManager.getDocument(parentRef);

            ObjectNode parentNode = o.createObjectNode();
            parentNode.put("title", parent.getTitle());
            parentNode.put("id", parent.getId());
            parentNode.put("permalink", Framework.getProperty("nuxeo.url") + "/nxdoc/default/" + parent.getId() + "/view_documents?tabIds=%3A");

            breadcrumbObject.put(parent.getId(), parentNode);

            parent = documentManager.getParentDocument(parentRef);
            if (parent != null) {
                parentRef = parent.getRef();
            } else {
                parentRef = null;
            }

            levels++;
        }

        return breadcrumbObject;
    }

}
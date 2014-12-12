package org.nuxeo.training.myenrichers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.nuxeo.ecm.automation.io.services.enricher.ContentEnricher;
import org.nuxeo.ecm.automation.io.services.enricher.RestEvaluationContext;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

public class ContributorSkeleton implements ContentEnricher {

    @Override
    public void contribute(JsonGenerator jg, RestEvaluationContext ec)
            throws ClientException, IOException {
        DocumentModel doc = ec.getDocumentModel();
        ObjectNode myJsonObject = buildMyObject(doc);
        jg.writeObject(myJsonObject);
    }

    private ObjectNode buildMyObject(DocumentModel doc)
            throws PropertyException, ClientException {

        ObjectMapper o = new ObjectMapper();
        ObjectNode myMainNode = o.createObjectNode();

        // Add your code below

        return myMainNode;
    }
}

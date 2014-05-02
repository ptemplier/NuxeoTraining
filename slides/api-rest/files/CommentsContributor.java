package org.nuxeo.sample;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.nuxeo.ecm.automation.io.services.contributor.RestContributor;
import org.nuxeo.ecm.automation.io.services.contributor.RestEvaluationContext;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.platform.comment.api.CommentManager;
import org.nuxeo.runtime.api.Framework;

public class CommentsContributor implements RestContributor {

    @Override
    public void contribute(JsonGenerator jg, RestEvaluationContext ec)
            throws ClientException, IOException {
        DocumentModel doc = ec.getDocumentModel();
        ObjectNode commentsObject = getComments(doc);
        jg.writeObject(commentsObject);
    }

    private ObjectNode getComments(DocumentModel doc)
            throws PropertyException, ClientException {

        ObjectMapper o = new ObjectMapper();
        ObjectNode commentsObject = o.createObjectNode();
        commentsObject = getAllComments(doc, commentsObject, o);

        return commentsObject;
    }

    private ObjectNode getAllComments(DocumentModel doc, ObjectNode commentsObject, ObjectMapper o) throws ClientException {
        CommentManager cs = Framework.getLocalService(CommentManager.class);
        List<DocumentModel> comments = cs.getComments(doc);

        for(DocumentModel comment : comments) {
            ObjectNode commentNode = o.createObjectNode();
            commentNode.put("commentedDocId", doc.getId().toString());
            commentNode.put("text", comment.getPropertyValue("comment:text").toString());
            commentNode.put("author", comment.getPropertyValue("comment:author").toString());
            // Transform GregorianCalendar to a Date object for formatting
            GregorianCalendar gc =  (GregorianCalendar) comment.getPropertyValue("comment:creationDate");
            String commentDate = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss").format(gc.getTime());
            commentNode.put("date", commentDate);
            commentsObject.put(comment.getId(), commentNode);

            if(cs.getComments(comment).size() > 0) {
                getAllComments(comment, commentsObject, o);
            }
        }
        return commentsObject;
    }
}
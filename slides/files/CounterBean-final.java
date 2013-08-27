/**
 * 
 */

package org.nuxeo.training.seamjsf;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.invalidations.AutomaticDocumentBasedInvalidation;
import org.nuxeo.ecm.platform.ui.web.invalidations.DocumentContextBoundActionBean;

/**
 * Code skeleton for a basic Bean 
 * that will be called by a template
 * to show how JSF and Seam interact
 * 
 * Used to display counters in JSF xhtml templates with:
 * - A method that displays a counter incremented at each reload 
 * - A method that returns the current document's number of children
 * 
 */
@Name("counterBean")
@Scope(ScopeType.CONVERSATION)
@AutomaticDocumentBasedInvalidation
public class CounterBean extends DocumentContextBoundActionBean implements Serializable {

	@In(create = false, required = true)
	CoreSession documentManager;

	@In(create = true)
	NavigationContext navigationContext;
	
    private static final long serialVersionUID = 1L;

    // This method is automatically called
    //  - before your bean will be called by JSF or any other bean
    //  - only if the currentDocument has changed
    //
    // To get the currentDocument, you can directly use
    // the inherited getCurrentDocument method
    @Override
    protected void resetBeanCache(DocumentModel currentDoc) {
    	
    }
    
    // Method that returns a basic counter incremented at each reload
    //int counter = 0;
    public String showCounter() {
    	//counter = counter + 1 ;
    	return "count = ";// + counter;
	}
    
    // Method that returns the current document's number of children
    public Integer countChildren() throws ClientException {
    	DocumentModel currentDoc = navigationContext.getCurrentDocument();
    	List<DocumentRef> children = documentManager.getChildrenRefs(currentDoc.getRef() , null);
    	return children.size();
    }
}

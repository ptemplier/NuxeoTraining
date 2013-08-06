/**
 * 
 */

package org.nuxeo.training.operations;

import java.util.Arrays;
import java.util.List;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

/**
 * @author bchauvin / ataillefer
 */
@Operation(id = AddUserToGroup.ID, category = Constants.CAT_USERS_GROUPS, label = "AddUserToGroup", description = "")
public class AddUserToGroup {

	public static final String ID = "AddUserToGroup";

	@Param(name = "username", required = true)
	protected String username;

	@Param(name = "group", required = true, widget = Constants.W_OPTION, values = {
			"BestBooks", "Managers" })
	protected String group = "BestBooks";

	@OperationMethod(collector = DocumentModelCollector.class)
	public void run() throws ClientException {
		// Call the service
		UserManager userManager = Framework.getLocalService(UserManager.class);

		// Get user
		NuxeoPrincipal user = userManager.getPrincipal(username);

		// Add user to the group if necessary
		if (!user.isMemberOf(group)) {
			String userSchemaName = "user";
			String groupsFieldName = "groups";
			
			DocumentModel userDoc = userManager.getUserModel(username);
			List<String> userGroups = (List<String>) userDoc.getProperty(userSchemaName, groupsFieldName);
			userGroups.add(group);
			userDoc.setProperty(userSchemaName, groupsFieldName, userGroups);
			userManager.updateUser(userDoc);
		}
	}
}

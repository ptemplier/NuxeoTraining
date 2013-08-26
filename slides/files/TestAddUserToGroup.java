package org.nuxeo.training.bestbooks.operations;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
public class TestAddUserToGroup {

    @Inject
    UserManager userManager;

    @Test
    public void shouldAddUserToGroup() throws ClientException {

        String testUsername = "John";
        String testGroup = "bestbooks";

        DocumentModel userModel = userManager.getBareUserModel();
        userModel.setProperty("user", "username", testUsername);
        userManager.createUser(userModel);

        DocumentModel groupModel = userManager.getBareGroupModel();
        groupModel.setProperty("group", "groupname", testGroup);
        userManager.createGroup(groupModel);

        // Check entry parameters
        Assert.assertNotNull("Expected the user to exist",
                userManager.getPrincipal(testUsername));
        Assert.assertNotNull("Expected the group to exist",
                userManager.getGroup(testGroup));

        // Run operation
        AddUserToGroup adduser = new AddUserToGroup();
        adduser.username = testUsername;
        adduser.group = testGroup;
        adduser.run();

        // Get result
        NuxeoPrincipal user = userManager.getPrincipal(testUsername);
        boolean result = user.isMemberOf(testGroup);

        // Check result
        Assert.assertEquals("Expected the user to be added to the group", true,
                result);
    }
}

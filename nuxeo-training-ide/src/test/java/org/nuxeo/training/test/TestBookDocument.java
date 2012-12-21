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

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@Deploy({ "org.nuxeo.ecm.automation.core", "nuxeo-training-ide",
        "studio.extensions.Training_Nuxeo" })
@LocalDeploy("nuxeo-training-ide:OSGI-INF/fake-actions-component.xml")
public class TestBookDocument {
    @Test
    public void testBookDoc() {
        // studio component loaded ?
        Assert.assertNotNull("The studio component should be loaded",
                Framework.getRuntime().getComponent("studio.extensions.SunTraining_Nuxeo"));

    }
}

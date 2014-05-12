/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Bertrand Chauvin
 *     Laurent Doguin
 */

package org.nuxeo.sample;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.glxn.qrgen.QRCode;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;
import org.osgi.framework.Bundle;

/**
 *
 */
public class QRGenService extends DefaultComponent {

    protected String[] values = { "" };

    protected String separator = "|";

    protected Bundle bundle;

    protected Map<String, QRGenDescriptor> QrGenerators;

    public Bundle getBundle() {
        return bundle;
    }

    /**
     * Component activated notification. Called when the component is activated.
     * All component dependencies are resolved at that moment. Use this method
     * to initialize the component.
     * <p>
     * The default implementation of this method is storing the Bundle owning
     * that component in a class field. You can use the bundle object to lookup
     * for bundle resources:
     * <code>URL url = bundle.getEntry("META-INF/some.resource");</code>, load
     * classes or to interact with OSGi framework.
     * <p>
     * Note that you must always use the Bundle to lookup for resources in the
     * bundle. Do not use the classloader for this.
     *
     * @param context the component context. Use it to get the current bundle
     *            context
     */
    @Override
    public void activate(ComponentContext context) {
        bundle = context.getRuntimeContext().getBundle();
        QrGenerators = new HashMap<String, QRGenDescriptor>();
    }

    /**
     * Component deactivated notification. Called before a component is
     * unregistered. Use this method to do cleanup if any and free any resources
     * held by the component.
     *
     * @param context the component context. Use it to get the current bundle
     *            context
     */
    @Override
    public void deactivate(ComponentContext context) {
        bundle = null;
        QrGenerators = null;
    }

    /**
     * Application started notification. Called after the application started.
     * You can do here any initialization that requires a working application
     * (all resolved bundles and components are active at that moment)
     *
     * @param context the component context. Use it to get the current bundle
     *            context
     * @throws Exception
     */
    @Override
    public void applicationStarted(ComponentContext context) throws Exception {
        // do nothing by default. You can remove this method if not used.
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (contribution instanceof QRGenDescriptor) {
            QRGenDescriptor generator = (QRGenDescriptor) contribution;
            if (QrGenerators.get(generator.id) != null) {
                // Two choices -> Override or merge
                // mergeQRGenDescriptor(QrGenerators.get(docProperties.id),
                // valuesDesc);
                QrGenerators.put(generator.id, generator);
            } else {
                QrGenerators.put(generator.id, generator);
            }
            values = generator.values;
            separator = generator.separator;
        }
    }

    private void mergeQRGenDescriptor(QRGenDescriptor oldDesc,
            QRGenDescriptor newDesc) {
        oldDesc.separator = newDesc.separator;
        oldDesc.values = newDesc.values;
        QrGenerators.put(oldDesc.id, oldDesc);
    }

    public void addQR(DocumentModel doc, String xpath, String generatorName)
            throws ClientException {
        FileBlob qrBlob = generateQR(doc, generatorName);
        doc.setPropertyValue(xpath, qrBlob);
        CoreSession session = doc.getCoreSession();
        session.saveDocument(doc);
        session.save();
    }

    public FileBlob generateQR(DocumentModel doc, String qrGeneratorName)
            throws PropertyException, ClientException {
        // Build QR content from the requested values
        QRGenDescriptor qrGenerator = QrGenerators.get(qrGeneratorName);
        if (qrGenerator == null) {
            throw new ClientException("QrGenerator does not exist: "
                    + qrGeneratorName);
        }

        String qrValues = "";
        for (String value : qrGenerator.values) {
            qrValues = qrValues + qrGenerator.separator
                    + doc.getPropertyValue(value).toString();
        }
        // Generate QR file
        File file = QRCode.from(doc.getId() + qrValues).file();
        FileBlob qrBlob = new FileBlob(file);
        return qrBlob;
    }
}


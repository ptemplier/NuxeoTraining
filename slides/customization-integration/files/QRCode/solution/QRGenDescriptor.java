package org.nuxeo.sample;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("qrcontent")
public class QRGenDescriptor {

    @XNode("@id")
    String id;

    @XNode(value = "separator@value")
    String separator;

    @XNodeList(value = "xpath@value", type = String[].class, componentType = String.class)
    String[] values;

}


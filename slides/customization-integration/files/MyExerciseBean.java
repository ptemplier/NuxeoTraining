package org.nuxeo.training.bestbooks;

import java.io.Serializable;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/*
 * Exercise bean containing errors
 */

@Name("myValidator")
@Scope(ScopeType.PAGE)
public class MyValidatorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @In(create = true)
    protected Map<String, String> messages;

    public void checkNumeric() {
        if (isNumeric(value.toString())) {
            return;
        }

        String msg = "You shall not pass";
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                msg, msg);
        throw new ClientException(message);
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}


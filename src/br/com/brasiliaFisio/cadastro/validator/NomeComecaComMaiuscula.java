package br.com.brasiliaFisio.cadastro.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "comecaComMaiuscula")
public class NomeComecaComMaiuscula implements Validator {

	@Override
	public void validate(FacesContext fc, UIComponent component, Object value) throws ValidatorException {

		String valor = value.toString();
		if (!valor.matches("[A-Z].*")) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Nome dever� come�ar com mai�scula, na sequ�ncia min�scula.", null));
		}

	}

}

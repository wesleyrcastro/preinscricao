package br.com.brasiliaFisio.cadastro.util.security;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.brasiliaFisio.cadastro.util.service.NegocioException;

public class TransformaStringSHA512 implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(TransformaStringSHA512.class);

	public static String sha512(String senha) {

		String sen = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte messageDigest[] = md.digest(senha.getBytes("UTF-8"));

			StringBuilder hexString = new StringBuilder();
			for (byte b : messageDigest) {
				hexString.append(String.format("%02X", 0xFF & b));
			}
			sen = hexString.toString();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			logger.error("Erro de criptografia: " + e.getMessage());
			throw new NegocioException("Falha ao criptografar a senha.");
		}
		return sen;

	}
}
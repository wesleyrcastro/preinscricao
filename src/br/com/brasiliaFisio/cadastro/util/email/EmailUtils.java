package br.com.brasiliaFisio.cadastro.util.email;

import java.io.Serializable;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.brasiliaFisio.cadastro.modelo.Inscricao;
import br.com.brasiliaFisio.cadastro.util.jsf.FacesUtil;

public class EmailUtils implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(EmailUtils.class);

	private static final String HOSTNAME = "smtp.brasiliafisio.fst.br";
	private static final String PORT = "587";
	private static final String USERNAME = "marketing@brasiliafisio.fst.br";
	private static final String PASSWORD = "Mo021292";
	private static final String EMAILORIGEM = "marketing@brasiliafisio.fst.br";
	private static final String DESTINO = "brasiliafisio@gmail.com";
	private static final String TITULO = "Cadastro da Pré-inscrição. [Mensagem enviada pelo sistema de Pré-inscrição]";
	private static final String MENSAGEM = "Por gentileza providencie a inscrição do pré-incristo:\n";

	public static void enviaEmail(Inscricao inscricao) {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", HOSTNAME);
		props.put("mail.smtp.port", PORT);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME, PASSWORD);
			}
		});

		try {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(EMAILORIGEM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(DESTINO));
		message.setSubject(TITULO);
		message.setText(MENSAGEM + "Nome: " + inscricao.getAluno().getNome() + "\n" + 
								   "E-Mail: " + inscricao.getAluno().getEmail() + "\n" + 
								   "Cidade: " + inscricao.getAluno().getCidade()+ "-" + inscricao.getAluno().getEstado() + "\n" + 
								   "Tel. Celular: " + inscricao.getAluno().getTelCelular()+ "\n" + 
								   "Tel. Residêncial: " + inscricao.getAluno().getTelResidencial()+ "\n" +
								   "Curso: " + inscricao.getCurso().getNome() + "\n" +
								   (inscricao.getObsAluno() != "" ? "Obs. da inscrição do aluno: " + inscricao.getObsAluno() : ""));

		Transport.send(message);
		FacesUtil.addInfoMessage("Pré-inscrição enviada com sucesso!");

		}catch(Exception e) {
			log.error("Erro de e-mail: " + e.getMessage());
			FacesUtil.addWarnMessage("Ocorreu uma falha no envio da mensagem ao administrador, mas sua pré-inscrição foi feita com sucesso! Confirme com a BrasiliaFisio.");
		}
	}
}

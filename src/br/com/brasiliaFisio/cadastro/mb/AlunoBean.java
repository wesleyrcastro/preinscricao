package br.com.brasiliaFisio.cadastro.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.SafeHtml.WhiteListType;

import br.com.brasiliaFisio.cadastro.anotacao.Transactional;
import br.com.brasiliaFisio.cadastro.dao.DAO;
import br.com.brasiliaFisio.cadastro.dao.DAOAluno;
import br.com.brasiliaFisio.cadastro.modelo.Aluno;
import br.com.brasiliaFisio.cadastro.modelo.Curso;
import br.com.brasiliaFisio.cadastro.modelo.Inscricao;
import br.com.brasiliaFisio.cadastro.modelo.enumeration.Estado;
import br.com.brasiliaFisio.cadastro.modelo.enumeration.Indicacao;
import br.com.brasiliaFisio.cadastro.modelo.enumeration.Mes;
import br.com.brasiliaFisio.cadastro.modelo.enumeration.Sexo;
import br.com.brasiliaFisio.cadastro.util.email.EmailUtils;
import br.com.brasiliaFisio.cadastro.util.jsf.FacesUtil;
import br.com.brasiliaFisio.cadastro.util.security.TransformaStringSHA512;
import br.com.brasiliaFisio.cadastro.validator.Util;

@Named
@ConversationScoped
public class AlunoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(AlunoBean.class);

	@Inject
	private DAO<Aluno> daoGenericoAluno;
	@Inject
	private DAO<Inscricao> daoGenericoInscricao;
	@Inject
	private DAO<Curso> daoGenericoCurso;
	@Inject
	private DAOAluno daoEspecificoAluno;

	@Inject
	private Conversation conversation;

	private List<Curso> cursos = new ArrayList<>();
	private List<Inscricao> inscricoes = new ArrayList<>();
	private List<Integer> anosIncricoes = new ArrayList<>();

	private Aluno aluno = new Aluno();
	private Curso curso = new Curso();
	private Inscricao inscricao = new Inscricao();

	private Integer ano;
	private Mes mes;

	@SafeHtml(whitelistType = WhiteListType.NONE, message = "Informe uma senha valida!")
	@Length(min = 8, max = 30, message = "A senha deve ter entre {min} e {max} caracteres!")
	private String senhaAluno;

	@SafeHtml(whitelistType = WhiteListType.NONE, message = "Informe uma confirmação de senha valida!")
	@Length(min = 8, max = 30, message = "A confirmação de senha deve ter entre {min} e {max} caracteres!")
	private String confirmacaoSenha;

	private Boolean alunoExiste = false;
	private Boolean isOutrosSelecionado = false;

	public void pesquisaAluno() {
		if (conversation.isTransient()) {
			conversation.begin();
		}

		alunoExiste = daoEspecificoAluno.alunoExiste(aluno.getCpf());
		if (!alunoExiste) {
			FacesUtil.addErrorMessage("CPF não encontrado!");
		}
	}

	public String carregaAluno() {
		this.aluno = daoEspecificoAluno.buscarAlunoPorCPF(aluno.getCpf());

		if (TransformaStringSHA512.sha512(senhaAluno).equals(aluno.getSenha())) {
			senhaAluno = null;
			logger.info("USUÁRIO ACESSOU A PRE-INSCRIÇÃO: " + aluno.getCpf() + " - " + aluno.getNome());

			return "/pagina/inscricao/inscricao?faces-redirect=true";
		} else {
			logger.info("USUÁRIO ERRO A SENHA: " + aluno.getCpf() + " - " + aluno.getNome());
			FacesUtil.addErrorMessage("Senha não confere!");

			return "index";
		}
	}

	public String carregaDadosAluno() {
		this.aluno = daoEspecificoAluno.buscarAlunoPorCPF(aluno.getCpf());
		return "/pagina/aluno/manterAluno?faces-redirect=true";
	}

	@Transactional
	public String gravaAluno() {
		if (aluno.getId() == 0) {
			aluno.setId(null);
		}
		if (senhaAluno.equals(confirmacaoSenha) && senhaAluno != null) {
			aluno.setSenha(TransformaStringSHA512.sha512(senhaAluno));
			daoGenericoAluno.adiciona(aluno);

			logger.info("USUÁRIO SE CADASTROU: " + aluno.getCpf() + " - " + aluno.getNome());
			limpaFormularioAluno();

			FacesUtil.addInfoMessage(
					"Cadastro realizado com sucesso! Entre com seu CPF e inscreva-se no curso desejado.");
			FacesUtil.facesContext().getExternalContext().getFlash().setKeepMessages(true);

		} else {
			FacesUtil.addErrorMessage("As senhas não conferem!");

			return "manterAluno";
		}

		if (!conversation.isTransient()) {
			conversation.end();
		}
		return "index?faces-redirect=true";
	}

	@Transactional
	public String alterarAluno() {
		if (senhaAluno.equals(confirmacaoSenha) && senhaAluno != null) {
			aluno.setSenha(TransformaStringSHA512.sha512(senhaAluno));
			daoGenericoAluno.altera(aluno);

			logger.info("USUÁRIO ALTEROU O CADASTRO: " + aluno.getCpf() + " - " + aluno.getNome());
			limpaFormularioAluno();

			FacesUtil.addInfoMessage("Atualizado com sucesso!");
			FacesUtil.facesContext().getExternalContext().getFlash().setKeepMessages(true);

		} else {
			FacesUtil.addErrorMessage("As senhas não conferem!");

			return "manterAluno";
		}

		if (!conversation.isTransient()) {
			conversation.end();
		}
		return "index?faces-redirect=true";
	}

	@Transactional
	public String gravaInscricao() {
		this.inscricao.setAluno(aluno);
		daoGenericoInscricao.adiciona(inscricao);
		this.inscricoes = daoEspecificoAluno.buscarInscricaoPorCPF(aluno.getCpf());

		EmailUtils.enviaEmail(inscricao);
		logger.info("USUÁRIO CADASTROU A PRÉ-INSCRIÇÃO: " + aluno.getCpf() + " - " + aluno.getNome());
		limpaFormularioInscricao();

		return "inscricao";
	}

	public void pesquisaInscricao() {
		inscricoes = daoEspecificoAluno.pesquisa(curso, mes != null ? mes.getValor() : 0, ano, aluno);

		if (Util.isEmpty(this.inscricoes) || this.inscricoes == null)
			FacesUtil.addWarnMessage("Registro não encontrado!");
	}

	public String novo() {
		this.aluno = new Aluno();
		return "manterAluno?faces-redirect=true";
	}

	public String sairManter() {
		if (!conversation.isTransient()) {
			conversation.end();
		}
		return "index?faces-redirect=true";
	}

	public String sairInscricao() {
		if (!conversation.isTransient()) {
			conversation.end();
		}
		return "/pagina/aluno/index?faces-redirect=true";
	}

	public void toggleOutrosStatus() {
		isOutrosSelecionado = inscricao.getIndicacao() == Indicacao.OUTROS;
	}

	private void limpaFormularioAluno() {
		this.aluno = new Aluno();
		senhaAluno = null;
		confirmacaoSenha = null;
	}

	private void limpaFormularioInscricao() {
		this.inscricao = new Inscricao();
	}

	public List<Curso> getListaComboComCurso() {
		if (this.cursos == null || this.cursos.isEmpty()) {
			this.cursos = daoGenericoCurso.listaTodos();
		}
		return cursos;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Estado[] getEstados() {
		return Estado.values();
	}

	public Sexo[] getSexos() {
		return Sexo.values();
	}

	public Indicacao[] getIndicacoes() {
		return Indicacao.values();
	}

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	public Boolean getAlunoExiste() {
		return this.alunoExiste;
	}

	public String getSenhaAluno() {
		return senhaAluno;
	}

	public void setSenhaAluno(String senhaAluno) {
		this.senhaAluno = senhaAluno;
	}

	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}

	public void setConfirmacaoSenha(String confirmacaoSenha) {
		this.confirmacaoSenha = confirmacaoSenha;
	}

	public Inscricao getInscricao() {
		return inscricao;
	}

	public void setInscricao(Inscricao inscricao) {
		this.inscricao = inscricao;
	}

	public List<Curso> getCursos() {
		if (this.cursos == null || this.cursos.isEmpty()) {
			this.cursos = daoEspecificoAluno.listaTodosCursos();
		}
		return cursos;
	}

	public List<Inscricao> getInscricoes() {
//		if (this.inscricoes == null || this.inscricoes.isEmpty()) {
//			this.inscricoes = daoEspecificoAluno.buscarInscricaoPorCPF(aluno.getCpf());
//		}

		return inscricoes;
	}

	public List<Integer> getAnosIncricoes() {
		anosIncricoes = daoEspecificoAluno.anoInscricao();
		return anosIncricoes;
	}

	public Mes[] getMeses() {
		return Mes.values();
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Mes getMes() {
		return mes;
	}

	public void setMes(Mes mes) {
		this.mes = mes;
	}

	public Boolean getIsOutrosSelecionado() {
		return isOutrosSelecionado;
	}

	public void setIsOutrosSelecionado(Boolean isOutrosSelecionado) {
		this.isOutrosSelecionado = isOutrosSelecionado;
	}

}
package br.com.brasiliaFisio.cadastro.dao;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.brasiliaFisio.cadastro.modelo.Aluno;
import br.com.brasiliaFisio.cadastro.modelo.Curso;
import br.com.brasiliaFisio.cadastro.modelo.Inscricao;
import br.com.brasiliaFisio.cadastro.modelo.enumeration.Status;

public class DAOAluno implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	public Boolean alunoExiste(String cpf) {
		TypedQuery<Long> query = this.em.createQuery("SELECT COUNT(a) FROM Aluno a WHERE a.cpf = :pCPF", Long.class);
		query.setParameter("pCPF", cpf);
		return query.getSingleResult() > 0;
	}

	public Aluno buscarAlunoPorCPF(String cpf) {
		TypedQuery<Aluno> query = this.em.createQuery("SELECT a FROM Aluno a WHERE a.cpf = :pCPF", Aluno.class);
		query.setParameter("pCPF", cpf);

		query.setHint("org.hibernate.cacheable", "true");
		return query.getSingleResult();
	}

	public List<Inscricao> buscarInscricaoPorCPF(String cpf) {

		String jpql = "SELECT i FROM Inscricao i JOIN FETCH i.aluno AS a JOIN FETCH i.curso AS c WHERE a.cpf = :pCpfAluno ORDER BY c.nome ASC";
		TypedQuery<Inscricao> query = this.em.createQuery(jpql, Inscricao.class);
		query.setParameter("pCpfAluno", cpf);

		return query.getResultList();
	}

	// Retorna o Ano como um Inteiro
	@SuppressWarnings("unchecked")
	public List<Integer> anoInscricao() {
		Query query = em.createQuery("SELECT DISTINCT YEAR(i.data) FROM Inscricao i", Integer.class);
		List<Integer> anos = query.getResultList();

		return anos;
	}

	public List<Curso> listaTodosCursos() {
		CriteriaBuilder builder = this.em.getCriteriaBuilder();
		CriteriaQuery<Curso> criteria = builder.createQuery(Curso.class);
		Root<Curso> root = criteria.from(Curso.class);
		criteria.select(root);

		criteria.where(builder.equal(root.<Curso> get("status"), Status.ABERTO));
		criteria.orderBy(builder.asc((root.<Curso> get("nome"))));

		return this.em.createQuery(criteria).getResultList();
	}

	// Filtro Dinamico
	public List<Inscricao> pesquisa(Curso curso, Integer mes, Integer ano, Aluno aluno) {

		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		CriteriaQuery<Inscricao> criterio = cb.createQuery(Inscricao.class);
		Root<Inscricao> inscricao = criterio.from(Inscricao.class);
		Predicate conjunction = cb.conjunction();

		if (curso != null) {
			conjunction = cb.and(conjunction, cb.and(cb.equal(inscricao.<Curso> get("curso"), curso)));
		}

		if (mes != 0) {
			Expression<Integer> expression = cb.function("month", Integer.class, inscricao.<Calendar> get("data"));
			conjunction = cb.and(conjunction, cb.equal(expression, mes));
		}

		if (ano != 0) {
			Expression<Integer> expression = cb.function("year", Integer.class, inscricao.<Calendar> get("data"));
			conjunction = cb.and(conjunction, cb.equal(expression, ano));
		}

		if (aluno != null) {
			conjunction = cb.and(conjunction, cb.and(cb.equal(inscricao.<Aluno> get("aluno"), aluno)));
		}

		inscricao.fetch("aluno");
		inscricao.fetch("curso");
		criterio.where(conjunction);
		criterio.orderBy(cb.desc(inscricao.get("data")));
		return this.em.createQuery(criterio).getResultList();
	}

}

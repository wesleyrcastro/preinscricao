package br.com.brasiliaFisio.cadastro.modelo.enumeration;

public enum Mes {

	JANEIRO("Janeiro", 1), FEVEREIRO("Fevereiro", 2), MARCO("Março", 3), ABRIL("Abril", 4), MAIO("Maio", 5), JUNHO(
			"Junho", 6), JULHO("Julho", 7), AGOSTO("Agosto", 8), SETEMBRO("Setembro", 9), OUTUBRO("Outubro", 10), NOVEMBRO(
			"Novembro", 11), DEZEMBRO("Dezembro", 12);

	private Integer valor;

	private String descricao;

	private Mes(String descricao, Integer valor) {

		this.valor = valor;
		this.descricao = descricao;
	}

	public Integer getValor() {
		return valor;
	}

	public String getDescricao() {
		return this.descricao;
	}

}

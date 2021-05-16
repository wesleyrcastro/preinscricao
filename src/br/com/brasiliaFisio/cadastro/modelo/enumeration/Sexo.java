package br.com.brasiliaFisio.cadastro.modelo.enumeration;

public enum Sexo {

	Masculino("Masculino"), Feminino("Feminino");

	private String descricao;

	Sexo(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}

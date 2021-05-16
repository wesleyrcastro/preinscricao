package br.com.brasiliaFisio.cadastro.modelo.enumeration;

public enum Status {

	ABERTO("Aberto"), ENCERRADO("Encerrado");

	private String descricao;

	Status(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}

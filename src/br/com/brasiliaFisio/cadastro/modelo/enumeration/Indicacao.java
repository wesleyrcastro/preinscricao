package br.com.brasiliaFisio.cadastro.modelo.enumeration;

public enum Indicacao {

	FACEBOOK("Facebook"), EMAIL("E-mail"), SITE_BRASILIAFISIO("Site BrasiliaFisio"), INDICACAO_AMIGO("Indicação de um Amigo"), INSTAGRAM("Instagram"), OUTROS("Outros");

	private String descricao;

	Indicacao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}

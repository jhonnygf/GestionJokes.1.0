package com.jonathangf.GestionJokes.entidades;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jonathangf.GestionJokes.utilidades.HibernateUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Clase Language que representa la tabla language de la base de datos.
 */
@NamedQuery(name = "Language.findByTexto", query = "FROM Language l WHERE LOWER(l.language) LIKE :language")
@NamedQuery(name = "Language.noJokes", query = "SELECT l FROM Language l WHERE l.jokeses IS EMPTY")
@Entity
@Table(name = "language")
public class Language implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	@Column(name = "code", length = 2)
	private String code;
	@Column(name = "language")
	private String language;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "language")
	private Set<Jokes> jokeses = new HashSet<Jokes>(0);

	public Language() {
	}

	public Language(int id) {
		this.id = id;
	}

	public Language(int id, String language) {
		this.id = id;
		this.language = language;
	}

	public Language(int id, String code, String language, Set<Jokes> jokeses) {
		this.id = id;
		this.code = code;
		this.language = language;
		this.jokeses = jokeses;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Set<Jokes> getJokeses() {
		return this.jokeses;
	}

	public void setJokeses(Set<Jokes> jokeses) {
		this.jokeses = jokeses;
	}
	
	/**
	 * Método que realiza una NamedQuery para buscar un lenguaje por texto.
	 * @param texto
	 */
	public static void namedQueryTextoLanguage(String texto) {
		HashMap<String, Object> parametros = new HashMap<>();
		parametros.put("language", "%" + texto + "%");
		List<Language> resultados = HibernateUtils.namedQuery("Language.findByTexto", Language.class, parametros);
		if (resultados.isEmpty()) {
			System.out.println("No se encontraron lenguajes que coincidan con el texto: " + texto);
		} else {
			System.out.println("Resultados encontrados:");
			for (Language resultado : resultados) {
				System.out.println("ID: " + resultado.getId() + ", Language: " + resultado.getLanguage());
			}
		}
	}
	
	/**
	 * Método que realiza una NamedQuery para buscar un lenguaje sin jokes asociados.
	 */
	public static void buscarLanguagesSinJokes() {
		List<Language> resultados = HibernateUtils.namedQuery("Language.noJokes", Language.class, null);

		if (resultados.isEmpty()) {
			System.out.println("No hay lenguajes sin jokes.");
		} else {
			resultados.forEach(language -> System.out
					.println("ID: " + language.getId() + ", Language: " + language.getLanguage()));
		}
	}
}

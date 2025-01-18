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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Clase Categories que representa la tabla categories de la base de datos.
 */
@Entity
@Table(name = "categories")
public class Categories implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	@Column(name = "category", nullable = false)
	private String category;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "categories")
	private Set<Jokes> jokeses = new HashSet<Jokes>(0);

	public Categories() {
	}

	public Categories(int id, String category) {
		this.id = id;
		this.category = category;
	}

	public Categories(int id, String category, Set<Jokes> jokeses) {
		this.id = id;
		this.category = category;
		this.jokeses = jokeses;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Set<Jokes> getJokeses() {
		return this.jokeses;
	}

	public void setJokeses(Set<Jokes> jokeses) {
		this.jokeses = jokeses;
	}

	@Override
	public String toString() {
		return "Categories [id=" + id + ", category=" + category + "]";
	}
	
	/**
	 * Método que busca categorías en la tabla categories que coincidan con el texto ingresado.
	 * @param texto
	 */
	public static void queryTextoCategories(String texto) {
		String query = "SELECT * FROM categories WHERE LOWER(category) LIKE :texto";
	    HashMap<String, Object> parametros = new HashMap<>();
	    parametros.put("texto", "%" + texto + "%");

	    List<Categories> resultados = HibernateUtils.nativeQuery(query, Categories.class, parametros);
	    if (resultados.isEmpty()) {
	        System.out.println("No se encontraron categorías que coincidan con el texto: " + texto);
	    } else {
	        System.out.println("Resultados encontrados:");
	        for (Categories resultado : resultados) {
	            System.out.println("ID: " + resultado.getId() + ", Categoría: " + resultado.getCategory());
	        }
	    }
	}
	
	/**
	 * Método que busca la categoría más repetida en la tabla categories.
	 */
	public static void buscarCategoryMasRepetida() {
	    String query = "SELECT c.id, c.category, COUNT(j.id) AS conteo " +
	                   "FROM categories c " +
	                   "JOIN jokes j ON c.id = j.category_id " +
	                   "GROUP BY c.id, c.category " +
	                   "ORDER BY conteo DESC " +
	                   "LIMIT 1";
	    List<Object[]> resultados = HibernateUtils.nativeQuery(query, Object[].class, null);

	    if (resultados.isEmpty()) {
	        System.out.println("No se encontraron categorías relacionadas con chistes.");
	    } else {
	        Object[] resultado = resultados.get(0); 
	        System.out.println("La categoría más repetida es:");
	        System.out.println("ID: " + resultado[0] + ", Categoría: " + resultado[1] + ", relacionada con: " + resultado[2] + " jokes.");
	    }
	}
}

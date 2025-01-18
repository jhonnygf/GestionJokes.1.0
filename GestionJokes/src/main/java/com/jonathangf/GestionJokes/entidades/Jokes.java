package com.jonathangf.GestionJokes.entidades;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jonathangf.GestionJokes.utilidades.HibernateUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Clase Jokes que representa la tabla jokes de la base de datos.
 */
@Entity
@Table(name = "jokes")
public class Jokes implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Categories categories;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Types types;
    @Column(name = "text1")
    private String text1;
    @Column(name = "text2")
    private String text2;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "jokes_flags",
        joinColumns = @JoinColumn(name = "joke_id"),
        inverseJoinColumns = @JoinColumn(name = "flag_id"))
    private Set<Flags> flagses = new HashSet<>();

    public Jokes() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Categories getCategories() {
        return categories;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Types getTypes() {
        return types;
    }

    public void setTypes(Types types) {
        this.types = types;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public Set<Flags> getFlagses() {
        return flagses;
    }

    public void setFlagses(Set<Flags> flagses) {
        this.flagses = flagses;
    }
    
    /**
     * Metodo que realiza una query para buscar jokes que contengan un texto en text1 o text2.
     * @param texto
     */
    public static void queryTextoJokes(String texto) {
		String query = "FROM Jokes WHERE LOWER(text1) LIKE '%" + texto + "%' OR LOWER(text2) LIKE '%" + texto + "%'";
		List<Jokes> resultados = HibernateUtils.getQuery(query, Jokes.class);

		if (resultados.isEmpty()) {
			System.out.println("No se encontraron jokes que contengan el texto: " + texto);
		} else {
			resultados.forEach(joke -> System.out.println("ID: " + joke.getId() + ", Text1: " + joke.getText1()
					+ ", Text2: " + (joke.getText2() != null ? joke.getText2() : "[N/A]")));
		}
	}
    
    /**
     * Metodo que realiza una query para buscar los jokes que no tienen flags.
     */
	public static void buscarJokesSinFlags() {
		String query = "FROM Jokes WHERE flagses IS EMPTY ORDER BY id ASC";

		List<Jokes> resultados = HibernateUtils.getQuery(query, Jokes.class);

		if (resultados.isEmpty()) {
			System.out.println("No hay jokes sin flags.");
		} else {
			resultados.forEach(joke -> System.out.println("ID: " + joke.getId() + ", Text1: " + joke.getText1()
					+ ", Text2: " + (joke.getText2() != null ? joke.getText2() : "null")));
		}
	}
}

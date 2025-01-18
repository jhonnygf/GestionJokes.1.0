package com.jonathangf.GestionJokes.entidades;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.jonathangf.GestionJokes.utilidades.HibernateUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Clase Flags que representa la tabla flags de la base de datos.
 */
@Entity
@Table(name = "flags")
public class Flags implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private int id;
    @Column(name = "flag", nullable = false)
    private String flag;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "flagses")
    private Set<Jokes> jokeses = new HashSet<>();

    public Flags() {}
    public Flags(int id, String flag) {
		this.id = id;
		this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Set<Jokes> getJokeses() {
        return jokeses;
    }

    public void setJokeses(Set<Jokes> jokeses) {
        this.jokeses = jokeses;
    }
    
    /**
     * Metodo que realiza una query para buscar flags que contengan un texto determinado.
     * @param texto
     */
	public static void queryTextoFlags(String texto) {
		String query = "FROM Flags WHERE LOWER(flag) LIKE '%" + texto + "%'";
		List<Flags> resultados = HibernateUtils.getQuery(query, Flags.class);
		if (resultados.isEmpty()) {
			System.out.println("No se encontraron flags que contengan el texto: " + texto);
		} else {
			resultados.forEach(flag -> System.out.println("ID: " + flag.getId() + ", flag: " + flag.getFlag()));
		}
	}
    
    /**
     * Metodo que realiza una query para buscar el flag más repetido en la base de datos. 
     */
    public static void buscarFlagMasRepetido() {
	    String query = "FROM Flags f " +
	                   "JOIN f.jokeses j " +
	                   "GROUP BY f " +
	                   "ORDER BY COUNT(j) DESC";


	    List<Flags> resultados = HibernateUtils.getQuery(query, Flags.class);

	    if (resultados.isEmpty()) {
	        System.out.println("No se encontraron flags asociados a jokes.");
	    } else {
	        Flags flag = resultados.get(0); 
	        System.out.println("El flag más usado es:");
	        System.out.println("ID: " + flag.getId() + ", Flag: " + flag.getFlag());
	    }
	}
}

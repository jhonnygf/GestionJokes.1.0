package com.jonathangf.GestionJokes;

import java.util.Comparator;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.jonathangf.GestionJokes.entidades.Categories;
import com.jonathangf.GestionJokes.entidades.Flags;
import com.jonathangf.GestionJokes.entidades.Jokes;
import com.jonathangf.GestionJokes.entidades.Language;
import com.jonathangf.GestionJokes.entidades.Types;
import com.jonathangf.GestionJokes.utilidades.HibernateUtils;
import com.jonathangf.GestionJokes.utilidades.MenuUtils;

/**
 * Programa para la gestion de chistes con categorias, idiomas y flags de una
 * base de datos local, que permite realizar operaciones CRUD sobre las
 * entidades Jokes, Categories, Language y Flags. Como consultas, inserciones,
 * modificaciones y borrados de objetos. En el caso de los borrados, se tiene en
 * cuenta las relaciones entre las clases y pide al usuario una confirmacion
 * antes de borrar en el caso de que el objeto este relacionado con otros
 * objetos. Si el usuario decide confirmar se eliminan los objetos relacionados
 * y despues el que el usuario ha elegido borrar, si la relacion es M:M primero
 * desvinculara los objetos entre ellos y despues borrara solo el objeto
 * elegido.
 */
public class App {
	static Scanner sc;

	/**
	 * Metodo principal que abre la conexion con la base de datos y llama a los
	 * metodos de gestion de la aplicacion
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		sc = new Scanner(System.in);
		HibernateUtils.quitarLog();
		if (!HibernateUtils.abrirConexion()) {
			System.out.println("Error al abrir la conexión");
			return;
		}
		System.out.println("Conexión abierta");

		tratarMenu();

		sc.close();
		HibernateUtils.cerrarConexion();
		System.out.println("Conexión cerrada. Fin del programa.");
	}

	/**
	 * Metodo que muestra el menu principal y llama a los submenus segun la opcion
	 * elegida
	 */
	public static void tratarMenu() {
		while (true) {
			System.out.println("Menú pricipal");
			System.out.println(MenuUtils.imprimirMenu("Gestión de jokes", "Gestión de categories",
					"Gestión de languages", "Gestión de flags"));
			String opcion = sc.nextLine();
			if (opcion.equals("0"))
				break;
			switch (opcion) {
			case "1" -> tratarSubmenu(Jokes.class, "jokes");
			case "2" -> tratarSubmenu(Categories.class, "categories");
			case "3" -> tratarSubmenu(Language.class, "languages");
			case "4" -> tratarSubmenu(Flags.class, "flags");
			default -> System.out.println("Opción no válida");
			}
		}
	}

	/**
	 * Metodo que muestra el submenu segun la opcion elegida
	 * 
	 * @param string
	 */
	private static void tratarSubmenu(Class<?> clase, String string) {
		while (true) {
			System.out.println("Submenu de " + string);
			System.out.println(MenuUtils.imprimirMenu("Consultas", "Insertar " + string, "Modificar " + string,
					"Borrar " + string));
			String opcion = sc.nextLine();
			if (opcion.equals("0"))
				break;
			switch (opcion) {
			case "1" -> submenuConsultar(clase, string);
			case "2" -> submenuInsertar(string);
			case "3" -> submenuModificar(string);
			case "4" -> submenuBorrar(string);
			default -> System.out.println("Opción no válida");
			}
		}
	}

	/**
	 * Metodo que busca un objeto por texto segun su clase y lo muestra por pantalla
	 * 
	 * @param clase
	 */
	private static void buscarPorTexto(Class<?> clase) {
		String texto = getString("Introduce el texto a buscar: ").toLowerCase().replace("'", "''");
		if (clase.equals(Flags.class)) {
			Flags.queryTextoFlags(texto);
		} else if (clase.equals(Jokes.class)) {
			Jokes.queryTextoJokes(texto);
		} else if (clase.equals(Categories.class)) {
			Categories.queryTextoCategories(texto);
		} else {
			Language.namedQueryTextoLanguage(texto);
		}
	}

	/**
	 * Metodo que muestra el submenu de consultar generico segun la opcion elegida
	 * 
	 * @param clase  Clase de la entidad
	 * @param nombre Nombre de la entidad (String)
	 */
	private static void submenuConsultar(Class<?> clase, String nombre) {
		while (true) {
			System.out.println("Consultar " + nombre);
			submenuQuerys(clase);
			String opcion = sc.nextLine();
			if (opcion.equals("0"))
				break;

			switch (opcion) {
			case "1" -> buscarPorTexto(clase);
			case "2" -> querySegunClase(clase);
			}
		}
	}

	/**
	 * Metodo que muestra el submenu de querys segun la clase elegida
	 * 
	 * @param clase
	 */
	public static void submenuQuerys(Class<?> clase) {
		if (clase.equals(Jokes.class)) {
			System.out.println(MenuUtils.imprimirMenu("Buscar joke por texto", "Buscar jokes sin flags"));
		} else if (clase.equals(Categories.class)) {
			System.out.println(MenuUtils.imprimirMenu("Buscar category por texto", "Buscar category más repetida"));
		} else if (clase.equals(Language.class)) {
			System.out.println(MenuUtils.imprimirMenu("Buscar language por texto", "Buscar languages sin jokes"));
		} else {
			System.out.println(MenuUtils.imprimirMenu("Buscar flag por texto", "Buscar flag más repetido"));
		}
	}

	/**
	 * Metodo que muestra la query segun la clase elegida por el usuario
	 * 
	 * @param clazz
	 */
	public static void querySegunClase(Class<?> clazz) {
		if (clazz.equals(Jokes.class)) {
			Jokes.buscarJokesSinFlags();
		} else if (clazz.equals(Categories.class)) {
			Categories.buscarCategoryMasRepetida();
		} else if (clazz.equals(Language.class)) {
			Language.buscarLanguagesSinJokes();
		} else {
			Flags.buscarFlagMasRepetido();
		}
	}

	/**
	 * Metodo que muestra el submenu de insertar segun la opcion eleg
	 * 
	 * @param string
	 */
	private static void submenuInsertar(String string) {
		if (string.equals("jokes")) {
			insertarJoke();
		} else if (string.equals("categories")) {
			insertar(Categories.class, "Categories");
		} else if (string.equals("languages")) {
			insertar(Language.class, "Languages");
		} else {
			insertar(Flags.class, "Flags");
		}
	}

	/**
	 * Metodo que muestra el submenu de modificar segun la opcion elegida
	 * 
	 * @param string
	 */
	private static void submenuModificar(String string) {
		if (string.equals("jokes")) {
			modificarJoke(Jokes.class, "Jokes");
		} else if (string.equals("categories")) {
			modificar(Categories.class, "Categories");
		} else if (string.equals("languages")) {
			modificar(Language.class, "Languages");
		} else {
			modificar(Flags.class, "Flags");
		}
	}

	/**
	 * Metodo que muestra el submenu de borrar segun la opcion elegida
	 * 
	 * @param string
	 */
	private static void submenuBorrar(String string) {
		if (string.equals("jokes")) {
			borrar(Jokes.class, "Jokes");
		} else if (string.equals("categories")) {
			borrar(Categories.class, "Categories");
		} else if (string.equals("languages")) {
			borrar(Language.class, "Languages");
		} else {
			borrar(Flags.class, "Flags");
		}
	}

	/**
	 * Metodo que borra un objeto de la base de datos segun su clase y su nombre
	 * 
	 * @param <T>
	 * @param clase
	 * @param nombre
	 */
	private static <T> void borrar(Class<T> clase, String nombre) {
		mostrarLista(clase, nombre);
		int id = getInt("Introduzca el ID del " + nombre.toLowerCase() + " a borrar: ");
		T objetoEncontrado = buscarObjeto(clase, nombre, id);

		if (objetoEncontrado != null && eliminarAsociados(nombre, id, objetoEncontrado)) {
			eliminar(nombre, objetoEncontrado);
		}
	}

	/**
	 * Metodo que elimina los objetos asociados antes de borrar el objeto principal
	 * 
	 * @param <T>
	 * @param nombre
	 * @param id
	 * @param objetoEncontrado
	 * @return
	 */
	private static <T> boolean eliminarAsociados(String nombre, int id, T objetoEncontrado) {
		Set<?> asociados = null;
		asociados = eliminarSegunClase(objetoEncontrado, asociados);
		// Si tiene asociados, preguntar si se quieren borrar también
		if (asociados != null && !asociados.isEmpty()) {
			System.out.println("El " + nombre.toLowerCase() + " " + id + " tiene " + asociados.size()
					+ " elementos asociados. Los elementos asociados seran borrados de la base de datos");
			System.out.print("¿Desea borrarlo igualmente? (s/n): ");
			String respuesta = sc.nextLine().trim().toLowerCase();
			if (!respuesta.equalsIgnoreCase("s")) {
				System.out.println("Operación cancelada.");
				return false;
			}
			// Borramos los asociados antes de borrar el objeto principal
			Set<?> copiaAsociados = new HashSet<>(asociados);
			for (Object asociado : copiaAsociados) {
				boolean ok = HibernateUtils.remove(asociado);
				if (!ok) {
					System.out.println("Error al borrar el objeto con ID=" + obtenerId(asociado));
				}
			}
		}
		return true;
	}

	/**
	 * Metodo que elimina los objetos asociados segun la clase del objeto encontrado
	 * en la base de datos, en el caso de los jokes lo que hace es desvincular antes
	 * las flags para no borrar varias innecsariamente por su relacion M:M
	 * 
	 * @param <T>
	 * @param objetoEncontrado
	 * @param asociados
	 * @return
	 */
	private static <T> Set<?> eliminarSegunClase(T objetoEncontrado, Set<?> asociados) {
		if (objetoEncontrado instanceof Categories) {
			asociados = ((Categories) objetoEncontrado).getJokeses();
		} else if (objetoEncontrado instanceof Language) {
			asociados = ((Language) objetoEncontrado).getJokeses();
		} else if (objetoEncontrado instanceof Flags) {
			asociados = ((Flags) objetoEncontrado).getJokeses();
		} else if (objetoEncontrado instanceof Jokes) {
			asociados = desvincularFlags(objetoEncontrado);
		}
		return asociados;
	}

	/**
	 * Metodo que desvincula los flags de un joke antes de borrarlo de la base de
	 * datos.
	 * 
	 * @param <T>
	 * @param objetoEncontrado
	 * @return
	 */
	private static <T> Set<?> desvincularFlags(T objetoEncontrado) {
		Set<Flags> flagsAsociados = ((Jokes) objetoEncontrado).getFlagses();
		if (flagsAsociados != null && !flagsAsociados.isEmpty()) {
			Set<Flags> copiaFlags = new HashSet<>(flagsAsociados);

			for (Flags flag : copiaFlags) {
				flag.getJokeses().remove(objetoEncontrado);
				HibernateUtils.merge(flag);
			}

			flagsAsociados.clear();
			HibernateUtils.merge(objetoEncontrado);
		}

		return flagsAsociados;
	}

	private static <T> T buscarObjeto(Class<T> clase, String nombre, int id) {
		T objetoEncontrado = HibernateUtils.getId(clase, id);
		if (objetoEncontrado == null) {
			System.out.println("No se ha encontrado un " + nombre.toLowerCase() + " con ese id.");
			return null;
		}
		return objetoEncontrado;
	}

	/**
	 * Metodo que elimina un objeto de la base de datos y muestra un mensaje
	 * 
	 * @param <T>
	 * @param nombre
	 * @param objeto
	 */
	private static <T> void eliminar(String nombre, T objeto) {
		boolean ok = HibernateUtils.remove(objeto);
		if (ok) {
			System.out.println(nombre + " borrado correctamente.");
		} else {
			System.out.println("Error al borrar el " + nombre.toLowerCase() + ".");
		}
	}

	/**
	 * Metodo que modifica un objeto de la base de datos y muestra un mensaje
	 * 
	 * @param <T>
	 * @param clase
	 * @param nombre
	 */
	private static <T> void modificar(Class<T> clase, String nombre) {
		T elegido = elegirID(clase, nombre);
		if (elegido != null) {
			String nuevo = getString("Introduzca el nuevo nombre del " + nombre + ": ");
			actualizarSegunClase(clase, elegido, nuevo);
			actualizarBaseDatos(nombre, elegido);
			if (nuevo.isBlank()) {
				System.out.println("Nombre vacío. No se actualiza.");
				return;
			}
		}
	}

	/**
	 * Metodo que actualiza un objeto en la base de datos y muestra un mensaje
	 * 
	 * @param <T>
	 * @param nombre
	 * @param elegido
	 */
	private static <T> void actualizarBaseDatos(String nombre, T elegido) {
		T merged = HibernateUtils.merge(elegido);
		if (merged != null) {
			System.out.println(nombre + " actualizado correctamente.");
		} else {
			System.out.println("Error al actualizar el " + nombre + ".");
		}
	}

	/**
	 * Metodo que actualiza un objeto segun su clase y el nuevo nombre introducido
	 * por el usuario
	 * 
	 * @param <T>
	 * @param clase
	 * @param elegido
	 * @param nuevo
	 */
	private static <T> void actualizarSegunClase(Class<T> clase, T elegido, String nuevo) {
		if (clase.equals(Categories.class)) {
			((Categories) elegido).setCategory(nuevo);
		} else if (clase.equals(Language.class)) {
			((Language) elegido).setLanguage(nuevo);
		} else if (clase.equals(Flags.class)) {
			((Flags) elegido).setFlag(nuevo);
		} else {
			System.out.println("No se puede modificar esta entidad.");
			return;
		}
	}

	/**
	 * Metodo que elige un ID de un objeto de la base de datos
	 * 
	 * @param <T>
	 * @param clase
	 * @param nombre
	 * @return
	 */
	private static <T> T elegirID(Class<T> clase, String nombre) {
		mostrarLista(clase, nombre);
		int id = getInt("Introduzca el ID del " + nombre + " a modificar: ");
		T elegido = HibernateUtils.getId(clase, id);
		if (elegido == null) {
			System.out.println("No se ha encontrado un " + nombre + " con ese id.");
			return null;
		}
		return elegido;
	}

	/**
	 * Metodo que devuelve el ID de un objeto
	 * 
	 * @param objeto
	 * @return
	 */
	private static int obtenerId(Object objeto) {
		if (objeto instanceof Categories) {
			return ((Categories) objeto).getId();
		} else if (objeto instanceof Language) {
			return ((Language) objeto).getId();
		} else if (objeto instanceof Flags) {
			return ((Flags) objeto).getId();
		} else if (objeto instanceof Jokes) {
			return ((Jokes) objeto).getId();
		} else {
			return ((Types) objeto).getId();
		}
	}

	/**
	 * Metodo que devuelve el nombre de un objeto
	 * 
	 * @param objeto
	 * @return
	 */
	private static String obtenerNombre(Object objeto) {
		if (objeto instanceof Categories) {
			return ((Categories) objeto).getCategory();
		} else if (objeto instanceof Language) {
			return ((Language) objeto).getLanguage();
		} else if (objeto instanceof Flags) {
			return ((Flags) objeto).getFlag();
		} else {
			return ((Types) objeto).getType();
		}
	}

	/**
	 * Metodo que muestra una lista de objetos de la base de datos segun su clase y
	 * su nombre
	 * 
	 * @param <T>
	 * @param clase
	 * @param nombre
	 */
	private static <T> void mostrarLista(Class<T> clase, String nombre) {
		List<T> lista = HibernateUtils.getAll(clase);
		if (lista.isEmpty()) {
			System.out.println("No existen " + nombre + " para modificar.");
		}
		System.out.println(nombre + " disponibles:");
		if (clase.equals(Jokes.class)) {
			lista.stream().sorted(Comparator.comparingInt(e -> obtenerId(e))).forEach(e -> System.out
					.println(obtenerId(e) + " - " + ((Jokes) e).getText1() + " - " + ((Jokes) e).getText2()));
		} else {
			lista.stream().sorted(Comparator.comparingInt(e -> obtenerId(e)))
					.forEach(e -> System.out.println(obtenerId(e) + " - " + obtenerNombre(e)));
		}
	}

	/**
	 * Metodo que modifica un joke de la base de datos
	 * 
	 * @param <T>
	 * @param clase
	 * @param nombre
	 */
	private static <T> void modificarJoke(Class<T> clase, String nombre) {
		T elegido = elegirID(clase, nombre);
		if (elegido != null) {
			String text2 = null;
			Types type = elegirIDObligatorio(Types.class, "Types");
			String text1 = getString("Introduce el chiste: ");
			if (type.getType().equals("twopart")) {
				text2 = getString("Introduce la segunda parte del chiste: ");
			}

			Categories category = elegirIDObligatorio(Categories.class, "Categories");
			Language language = elegirIDObligatorio(Language.class, "Languages");
			Set<Flags> flags = elegirFlags();

			((Jokes) elegido).setText1(text1);
			((Jokes) elegido).setText2(text2);
			((Jokes) elegido).setCategories(category);
			((Jokes) elegido).setTypes(type);
			((Jokes) elegido).setLanguage(language);
			((Jokes) elegido).setFlagses(flags);

			actualizarBaseDatos(nombre, elegido);
		}
	}

	/**
	 * Metodo que inserta un objeto en la base de datos segun su clase y su nombre
	 * 
	 * @param <T>
	 * @param clase
	 * @param nombre
	 */
	private static <T> void insertar(Class<T> clase, String nombre) {
		System.out.println("Insertar " + nombre);
		mostrarLista(clase, nombre);
		int id = comprobarID(clase);
		String nuevo = getString("Introduzca el " + nombre.toLowerCase() + ": ");
		T nuevoObjeto = nuevo(clase, id, nuevo);
		if (HibernateUtils.persist(nuevoObjeto)) {
			System.out.println(nombre + " insertado correctamente.");
		} else {
			System.out.println("Error al insertar el " + nombre.toLowerCase() + ".");
		}
	}

	/**
	 * Metodo que crea un objeto segun su clase, su id y su nombre
	 * 
	 * @param <T>
	 * @param clase
	 * @param id
	 * @param nombre
	 * @return
	 */
	private static <T> T nuevo(Class<T> clase, int id, String nombre) {
		T nuevo;
		if (clase.equals(Categories.class)) {
			nuevo = (T) new Categories(id, nombre);
		} else if (clase.equals(Language.class)) {
			nuevo = (T) new Language(id, nombre);
		} else if (clase.equals(Flags.class)) {
			nuevo = (T) new Flags(id, nombre);
		} else {
			System.out.println("No se puede insertar esta entidad.");
			return null;
		}
		return nuevo;
	}

	/**
	 * Metodo que elige un ID de un objeto de la base de datos segun su clase y su
	 * nombre, y comprueba que no exista
	 * 
	 * @param <T>
	 * @param clase
	 * @return
	 */
	private static <T> int comprobarID(Class<T> clase) {
		int id;
		boolean valido = false;
		List<T> objetos = HibernateUtils.getAll(clase);

		do {
			boolean existe = false;
			id = getInt("Introduce ID:");
			existe = comprobarIDenListaDeObjetos(id, objetos, existe);

			if (id <= 0) {
				System.out.println("El ID debe ser mayor que 0.");
			} else if (existe) {
				System.out.println("El ID ya existe.");
			} else {
				valido = true;
			}
		} while (!valido);

		return id;
	}

	/**
	 * Metodo que comprueba si un ID existe en una lista de objetos de la base de
	 * datos
	 * 
	 * @param <T>
	 * @param id
	 * @param objetos
	 * @param existe
	 * @return
	 */
	private static <T> boolean comprobarIDenListaDeObjetos(int id, List<T> objetos, boolean existe) {
		for (T objeto : objetos) {
			if (objeto instanceof Categories && ((Categories) objeto).getId() == id) {
				existe = true;
			} else if (objeto instanceof Language && ((Language) objeto).getId() == id) {
				existe = true;
			} else if (objeto instanceof Flags && ((Flags) objeto).getId() == id) {
				existe = true;
			}
		}
		return existe;
	}

	/**
	 * Metodo que inserta un nuevo joke en la base de datos, validando los datos
	 * introducidos por el usuario
	 */
	public static void insertarJoke() {
		String text2 = null;
		Types type = elegirIDObligatorio(Types.class, "Types");
		String text1 = getString("Introduce el chiste: ");
		if (type.getType().equals("twopart")) {
			text2 = getString("Introduce la segunda parte del chiste: ");
		}

		Categories category = elegirIDObligatorio(Categories.class, "Categories");
		Language language = elegirIDObligatorio(Language.class, "Languages");
		Set<Flags> flags = elegirFlags();

		Jokes joke = new Jokes();
		joke.setText1(text1);
		joke.setText2(text2);
		joke.setCategories(category);
		joke.setTypes(type);
		joke.setLanguage(language);
		joke.setFlagses(flags);

		boolean ok = HibernateUtils.persist(joke);
		if (ok) {
			System.out.println("Joke insertado correctamente con ID: " + joke.getId());
		} else {
			System.out.println("Error al insertar el Joke.");
		}
	}

	/**
	 * Metodo que elige un ID de un objeto de la base de datos y comprueba que no
	 * sea nulo o no exista en la base de datos
	 * 
	 * @param <T>
	 * @param clase
	 * @param nombre
	 * @return
	 */
	private static <T> T elegirIDObligatorio(Class<T> clase, String nombre) {
		while (true) {
			mostrarLista(clase, nombre);
			int id = getInt("Introduzca el ID de " + nombre + ": ");

			T elegido = HibernateUtils.getId(clase, id);
			if (elegido == null) {
				System.out.println(nombre + " con ID: " + id + " no encontrado. Intente de nuevo.");
			} else {
				return elegido;
			}
		}
	}

	/**
	 * Metodo que muestra la lista de flags de la base de datos para que el usuario
	 * elija uno o varios
	 * 
	 * @return
	 */
	private static Set<Flags> elegirFlags() {
		Set<Flags> flagsElegidos = new HashSet<>();
		List<Flags> flags = HibernateUtils.getAll(Flags.class);
		if (flags.isEmpty()) {
			System.out.println("No hay Flags disponibles.");
			return flagsElegidos;
		}

		mostrarLista(Flags.class, "Flags");
		int id;
		do {
			id = getInt("Introduce ID del Flag (pulsa 0 para terminar): ");

			if (id == 0) {
				if (flagsElegidos.isEmpty()) {
					System.out.println("No se eligieron flags. Continuando sin flags.");
				}
			}
			comprobarIDFlag(flagsElegidos, id);
		} while (id != 0);

		return flagsElegidos;
	}

	/**
	 * Metodo que comprueba si un ID de un flag existe en la base de datos y si ya
	 * ha sido elegido
	 * 
	 * @param chosenFlags
	 * @param id
	 */
	private static void comprobarIDFlag(Set<Flags> chosenFlags, int id) {
		Flags flag = HibernateUtils.getById(Flags.class, id);
		if (flag == null && id != 0) {
			System.out.println("Flag con ID " + id + " no encontrado. Inténtalo de nuevo.");
		} else if (chosenFlags.contains(flag)) {
			System.out.println("El Flag con ID " + id + " ya ha sido elegido.");
		} else {
			chosenFlags.add(flag);
			if (id != 0) {
				System.out.println("Flag con ID " + id + " añadido.");
			}
		}
	}

	/**
	 * Metodo que devuelve un entero introducido por el usuario y que comprueba que
	 * es un entero valido
	 * 
	 * @param mensaje
	 * @return
	 */
	public static int getInt(String mensaje) {
		System.out.print(mensaje);
		int entero = -1;
		boolean valido = true;
		do {
			try {
				entero = sc.nextInt();
				sc.nextLine(); // Limpiar el buffer después de leer el entero
				valido = true;
			} catch (InputMismatchException e) {
				System.out.print("No es un entero, prueba otra vez: ");
				sc.nextLine(); // Limpiar el buffer del input no válido
				valido = false;
			}
		} while (!valido);
		return entero;
	}

	/**
	 * Metodo que devuelve un string introducido por el usuario
	 * 
	 * @param mensaje
	 * @return String
	 */
	public static String getString(String mensaje) {
		String input;
		do {
			System.out.print(mensaje);
			input = sc.nextLine().trim();
			if (input.isBlank()) {
				System.out.println(
						"La entrada no puede estar vacía o contener solo espacios en blanco. Inténtelo de nuevo.");
			}
		} while (input.isBlank());
		return input;
	}
}

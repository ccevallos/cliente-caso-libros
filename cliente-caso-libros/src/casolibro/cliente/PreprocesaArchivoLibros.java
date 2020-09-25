package casolibro.cliente;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class PreprocesaArchivoLibros {
	private static String resumenGeneral=null;
	
	public static void main(String[] args) throws Exception {
		//limpiaArchivoLibros("gutindex.txt","listado_libros.txt");
		File carpetaDestino=new File("hilos");
		 if (carpetaDestino.exists()) {
			 carpetaDestino.delete();
		 }
		carpetaDestino.mkdirs();
		separaArchivoLibros("listado_libros.txt","hilos/libros",3);
		
		System.out.println("Termino");
		
	}
	
	public static void separaArchivoLibros(String nombreArchivoOrigen, String nombreArchivoDestino, int numeroArchivos) throws Exception {
		FileInputStream fis = new FileInputStream(nombreArchivoOrigen);
		BufferedReader bf = 
				new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
	
		ObjectOutputStream archivosObjetos []= new ObjectOutputStream [numeroArchivos];
		for (int cont=0; cont<numeroArchivos; cont++) {
			if (archivosObjetos[cont]==null) {
				archivosObjetos[cont]=new ObjectOutputStream(new FileOutputStream(nombreArchivoDestino+cont));
			}
		}

		//se lee de línea en línea el archivo
		String s = bf.readLine();
		int cont=0;
		//se itera por cada fila del archivo de origen
		while (s != null) {
			LibroArchivo la= new LibroArchivo();
			String titulo=s;
			String autor="Desconocido";
			
			if (s != null) {
				int finTitulo=s.indexOf(", by");
				if (finTitulo>0) {
					titulo=s.substring(0,finTitulo).trim();
					autor=s.substring(finTitulo+4).trim();
					int finAutor=autor.indexOf("[");
					if (finAutor>0) {
						autor=autor.substring(0,finAutor).trim();
					}
				
				} 
			}
			la.setTitulo(titulo);
			la.setAutor(autor);

			archivosObjetos[cont].writeObject(la);

			//alterna el contador entre los archivos
			//si llega al último empieza en 0 otra vez
			if(cont==numeroArchivos-1) {
				cont=0;
			}else {
				cont++;
			}
			s = bf.readLine();
		}
		
		bf.close();
		fis.close();
	}
	
	
	public static LibroArchivo obtenerLibroArchivo(ObjectInputStream ois) throws Exception{
		LibroArchivo la = (LibroArchivo)ois.readObject();
		if(la!=null) {
			la.setResumen(obtenerResumen());
		}
		return la;
	}
	
	/**
	 * Resumen, en el cual se selecciona de un texto base un número aleatorio de caracteres entre 0 y 5000 caracteres.
	 * @return
	 */
	public static String obtenerResumen() {
		// Solo se carga el contenido del archivo la primera vez
		if (resumenGeneral==null) {
			resumenGeneral="";
			try {
				InputStream is = new FileInputStream("descripcion.txt");
				int caracter=is.read();
				while(caracter>=0) {
					resumenGeneral+=(char)caracter;
					caracter=is.read();
				}
				is.close();
			}catch(Exception e) {
				System.out.print(e);
			}
		}
		//Se obtiene un aleatorio entre el valor min y max
		int numeroCaracteres = Util.aleatorio(3000, 5000);
		//Se retorna una parte del resumen general
		return resumenGeneral.substring(0,numeroCaracteres);
	}
	
	public static void limpiaArchivoLibros(String nombreArchivoOrigen, String nombreArchivoDestino) throws Exception {
		//Permite leer el catalogo de archivos
		FileInputStream fis = new FileInputStream(nombreArchivoOrigen);
		BufferedReader bf = 
				new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		//Son caracteres que se encuentran al inicio de los
		//textos que deben obviarse
		String caracteresInicioNoValidos=" \t~( [";
		
		String s = bf.readLine();

		//Se crea el nuevo archivo
		FileOutputStream fos = 
				new FileOutputStream(nombreArchivoDestino);
		PrintWriter fow=new PrintWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
		while (s != null) {
			boolean leeSiguiente=true;
			// Se elimina cabeceras
			if (s.startsWith("===")) {
				do {
					s = bf.readLine();	
				}while(!s.startsWith("~ ~ ~"));
			}
			// Solo se incluyen filas validas es decir aquellas
			// que no son cabeceras de columnas o tienen 
			// caracteres especiales al inicio
			if (!s.equals("") && !s.contains("TITLE") &&
				!caracteresInicioNoValidos.contains(
						""+s.charAt(0))&& s.length()>2) {
				// Se recorta los espacios al final y el número
				int fin = s.indexOf("  ");
				// Se identifica este caso especial de caracter
				// usado en lugar de espacio
				if (fin==-1)
					fin=s.indexOf(160);
				if (fin > 0)
					s = s.substring(0, fin);

				// Se concatena con las siguientes lineas que 
				// tienen espacio al inicio
				String s2 = bf.readLine();
				while (s2 != null && !s2.equals("")
						&&leeSiguiente) {
					if (s2.startsWith(" ")) {
						// Si al inicio hay un espacio esta
						// línea es parte de la anterior
						s += s2;
						s2 = bf.readLine();
					}else {
						// Si no tiene espacio al inicio se
						// trata como otro libro
						s=s2;
						// Se evita que se vuelva a leer.
						leeSiguiente=false;
					}
				}
				//Se escribe en el nuevo archivo
				fow.println(s);
			}
			if(leeSiguiente) {
				s = bf.readLine();
			}
		}
		bf.close();
		fow.close();
		fos.close();
		System.out.print("Termino");
	}
}

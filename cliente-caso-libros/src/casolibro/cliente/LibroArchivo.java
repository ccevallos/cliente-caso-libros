package casolibro.cliente;

import java.beans.Transient;
import java.io.Serializable;

public class LibroArchivo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String titulo;
	private String autor;
	private String resumen;
	
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		if(titulo.length()>100) {
			this.titulo = titulo.substring(0,100);
		}else {
			this.titulo = titulo;
		}
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		if(autor.length()>100) {
			this.autor = autor.substring(0,100);
		}else {
			this.autor= autor;
		}
	}
	
	@Transient
	public String getResumen() {
		return resumen;
	}
	public void setResumen(String resumen) {
		this.resumen = resumen;
	}
	
}

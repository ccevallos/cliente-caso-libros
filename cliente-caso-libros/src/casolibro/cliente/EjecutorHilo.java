package casolibro.cliente;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Random;

public class EjecutorHilo implements Runnable {
	private final String urlBase="http://localhost:8080/caso-libros/";
	private final String [] nombresBusqueda= {"Above the","Proud Lady","Islas Filipinas","Aviation Book","Divorce versus"};

	private String nombreArchivo;
	private int numeroTransacciones[];
	//usado para obtener un id aleatorio para actualizacion y eliminacion de libros
	private int idLibroMaximo=20;
	private String numeroThread;
	
	public EjecutorHilo(String nombreArchivo, int [] numeroTransacciones, String numeroThread) {
		super();
		this.nombreArchivo = nombreArchivo;
		this.numeroTransacciones=numeroTransacciones;
		this.numeroThread=numeroThread;
	}
	
	@Override
	public void run() {
		try {
			FileInputStream fis=new FileInputStream(nombreArchivo);
			ObjectInputStream ois=new ObjectInputStream(fis);

			System.out.println("Thread " + numeroThread + " inicia reg:" + numeroTransacciones[0]+
                    " act:" + numeroTransacciones[1]+
                    " eli:" + numeroTransacciones[2]+
                    " cons:" + numeroTransacciones[3]);
			
			//Se obtiene el número mayor de transacciones para iterar despues
			int numeroMayor=0;
			for(int numero:numeroTransacciones) {
				if(numeroMayor<numero) {
					numeroMayor=numero;
				}
			}
			
			//Se itera hasta ejecutar el total de transacciones para le hilo
			for(int cont=0; cont<numeroMayor;cont++) {
				//ejecuta las transacciones de registro
				if(cont<numeroTransacciones[0]) {
					ejecutaRegistro(ois);
				}
				//ejecuta las transacciones de actualizacion
				if(cont<numeroTransacciones[1]) {
					ejecutaActualiza(ois);
				}
				//ejecuta las transacciones de eliminacion
				if(cont<numeroTransacciones[2]) {
					ejecutaElimina();
				}
				//ejecuta las transacciones de consulta
				if(cont<numeroTransacciones[3]) {
					ejecutaConsulta();
				}
			}
			
			ois.close();
			fis.close();
			System.out.println("Thread " + numeroThread + " terminó reg:" + numeroTransacciones[0]+
					                         " act:" + numeroTransacciones[1]+
					                         " eli:" + numeroTransacciones[2]+
					                         " cons:" + numeroTransacciones[3]);
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void ejecutaRegistro(ObjectInputStream ois) throws Exception{
		LibroArchivo l=PreprocesaArchivoLibros.obtenerLibroArchivo(ois);
		if (l!=null) {
			HashMap<String,String> valores= new HashMap<String,String>();
			valores.put("titulo", l.getTitulo());
			valores.put("autor", l.getAutor());
			valores.put("resumen", l.getResumen());
			Util.invocaURLPOST(urlBase+"registroLibro",valores);
		}
	}
	
	public void ejecutaActualiza(ObjectInputStream ois) throws Exception{
		LibroArchivo l=PreprocesaArchivoLibros.obtenerLibroArchivo(ois);
		if (l!=null) {
			HashMap<String,String> valores= new HashMap<String,String>();
			Random ran = new Random();
			Integer id=ran.nextInt(idLibroMaximo);
			valores.put("id", id.toString());
			valores.put("titulo", l.getTitulo());
			valores.put("autor", l.getAutor());
			valores.put("resumen", l.getResumen());
			Util.invocaURLPOST(urlBase+"actualizaLibro",valores);
		}
	}	
	
	public void ejecutaElimina() throws Exception{
		HashMap<String,String> valores= new HashMap<String,String>();
		Random ran = new Random();
		Integer id=ran.nextInt(idLibroMaximo)+1;
		valores.put("id", id.toString());
		Util.invocaURLPOST(urlBase+"eliminaLibro",valores);
	}	

	public void ejecutaConsulta() throws Exception{
		HashMap<String,String> valores= new HashMap<String,String>();
		Random ran = new Random();
		int indice=ran.nextInt(nombresBusqueda.length-1);
		valores.put("nombre", nombresBusqueda[indice]);
		Util.invocaURLPOST(urlBase+"consultaLibro",valores);
	}

}

package casolibro.cliente;

import java.io.File;
import java.util.StringJoiner;

public class ClienteSimuladorTransacciones {

	public static void main(String[] args) throws Exception {
		int numero_hilos = 5;
		/**
		 * Contiene la demanda de transacciones que se ejecutara
		 * Por cada registro se tiene un arreglo de transacciones en el siguiente orden:
		 *  [0] registro, [1] actualiza, [2] elimina, [3] consulta
		 */
		int tiempoEspera=30000;
		
		//Precarga el resumen desde el archivo
		//Esto evita que cuando se ejecuten los threads
		//lean todos del archivo
		PreprocesaArchivoLibros.obtenerResumen();
		
		//borra la carpeta de archivos de hilos si existe
		File carpetaDestino=new File("hilos");
		if (carpetaDestino.exists()) {
		   carpetaDestino.delete();
		}
		//crea la carpeta
		carpetaDestino.mkdirs();
		//Crea un archivo independiente para cada hilo
		//estos archivos almacenan objetos LibroArchivo
		PreprocesaArchivoLibros.separaArchivoLibros("listado_libros.txt","hilos/libros",numero_hilos);
		
		for (int cont = 1; true; cont++) {
			int [] numero_transacciones = numeroTransacciones(cont);//transacciones_escenario[cont];
			System.out.println("Iteracion: " + cont + " Nro: "+ arregloStr(numero_transacciones));
			for(int cont_hilo=0; cont_hilo < numero_hilos; cont_hilo++) {
				String numeroThread=""+cont+"."+cont_hilo;
				EjecutorHilo eh= new EjecutorHilo("hilos/libros"+cont_hilo,numero_transacciones, numeroThread);
				Thread th = new Thread(eh);
				th.start();
			}
			//se hace una pausa antes de seguir con la siguiente carga
			Thread.sleep(tiempoEspera);
		}
	}
	
	public static int[] numeroTransacciones(int t) {
		if(t==1){
			return new int[] {5,0,0,0}; 
		}
		//número de periodo t cada cuanto se repite el patrón
		int periodo=90;
		int valorMaximo=20;
		
		int t2=t%periodo;
		
		int valorAleatorio=Util.aleatorio(0, 3);				
		
		int transaccionesRegistro=(int)(Math.sin(Math.PI*t2/periodo)*valorMaximo) + valorAleatorio;
		int transaccionesActualiza=Util.aleatorio(0, 8);
		int transaccionesElimina=Util.aleatorio(0, 5);
		int transaccionesConsulta=Util.aleatorio(0, 10);
		
		return new int[] {transaccionesRegistro,transaccionesActualiza,transaccionesElimina,transaccionesConsulta};
	}
	
	public static String arregloStr(int[]datos) {
		StringJoiner sj = new StringJoiner(",","{","}");
		for(int i =0; i<datos.length;i++) {
			sj.add(String.valueOf(datos[i]));
		}
		return sj.toString();
	}

}

package casolibro.cliente;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;
import java.util.StringJoiner;

public class Util {
	
	public static void main(String []args) throws Exception{
		//prueba de uso
		HashMap<String,String> valores= new HashMap<String,String>();
		valores.put("titulo", "uno");
		valores.put("autor", "yo");
		valores.put("resumen", "Eso es todo");
	
		String respuesta = invocaURLPOST("http://localhost:8080/caso-libros/registroLibro",valores);
		System.out.println(respuesta);

	}
	
	
	public static String invocaURLPOST(String url, HashMap<String,String> parametros) throws Exception {
		StringBuilder respuesta = new StringBuilder();
		URL urlNet = new URL(url);
		HttpURLConnection conexion = (HttpURLConnection) urlNet.openConnection();
		conexion.setRequestMethod("POST");
		conexion.setDoOutput(true);
		
		StringJoiner parametrosPost = new StringJoiner("&");
		for(String key: parametros.keySet()) {
			String clave=URLEncoder.encode(key, "UTF-8");
			String valor=URLEncoder.encode(parametros.get(key), "UTF-8");
			parametrosPost.add(clave+ "="+ valor);
		}
		byte[] out = parametrosPost.toString().getBytes(StandardCharsets.UTF_8);
		int length = out.length;		
		conexion.setFixedLengthStreamingMode(length);
		conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		OutputStream os = conexion.getOutputStream();
		os.write(out);
		
		//Lee el resultado de la ejecucion
		InputStream is = conexion.getInputStream();

		int caracter = is.read();
		while (caracter >= 0) {
			respuesta.append((char) caracter);
			caracter = is.read();
		}

		is.close();
		conexion.disconnect();

		return respuesta.toString();
	}
	
	public static int aleatorio(int min, int max) {
		//Se obtiene un aleatorio entre el valor min y max
		Random ran = new Random();
		return ran.nextInt(max-min+1) + min;
	}
	
}



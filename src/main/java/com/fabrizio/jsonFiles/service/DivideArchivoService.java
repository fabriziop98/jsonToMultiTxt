package com.fabrizio.jsonFiles.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.regex.Pattern;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fabrizio.jsonFiles.entity.ArchivoTxt;
import com.fabrizio.jsonFiles.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DivideArchivoService {

	private Logger LOGGER = LoggerFactory.getLogger(DivideArchivoService.class);

	@SuppressWarnings("unchecked")
	public void readFile(MultipartFile archivo) {
		JSONParser parser = new JSONParser();
		ArchivoTxt txt = new ArchivoTxt();
		try {
			Object obj = parser.parse(new FileReader(convertMultiPartFileToFile(archivo)));
			
			JSONArray companyList = (JSONArray) obj;
			List<ArchivoTxt> listaTxt = new ArrayList<>();
			
			PrintWriter writer;
			try {
				for (Object o : companyList){
					 JSONObject factura = (JSONObject) o;
					writer = new PrintWriter(FileUtil.RUTA_ARCHIVOS + "factura_"+ (String) factura.get("Number") + ".txt", "UTF-8");
				    writer.println("factura/tipcom=Factura");
				    writer.println("factura/codven=ADM");
				    String numero = (String) factura.get("Number");
				    txt.setFactura(numero);
				    writer.println("factura/codcta="+numero);
				    writer.println("linea/codart=1");
				    writer.println("linea/cantid=5");
				    String precio = (String) factura.get("Total");
				    txt.setPrecio(precio);
				    writer.println("linea/precio="+precio);
				    writer.println("linea/grabar");

				    listaTxt.add(txt);
				    
				    writer.close();
				  }
				
				
				
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			
			
			
			
			
			
			
//			String jSonString = companyList.toJSONString();
//			String newString = jSonString.replace("Number", "factura/codcta=");
//			System.out.println(newString);
////			newString = newString.replace("\"Discount\":\"$ 0,00\",", "");
////			System.out.println(newString);
////			newString = newString.replace("\"Status\":\"Pago\"","factura/tipcom=Factura");
////			System.out.println(newString);
////			newString = newString.replace("\"Amount due\":\"$ 0,00\"","factura/codven=ADM");
////			newString = newString.replace("\"Organization name\":\"NUEVAS REDES S.A\"","");
//			
//
//			JSONArray jsonMod = (JSONArray) parser.parse(newString);
//			Iterator<JSONObject> iteratorMod = jsonMod.iterator();
//
//			while (iteratorMod.hasNext()) {
//				generadorArchivo(lineasTexto(iteratorMod.next()));
//			}

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

	}

	private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
		final File file = new File(multipartFile.getOriginalFilename());
		try (final FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(multipartFile.getBytes());
		} catch (final IOException ex) {
			LOGGER.error("Error converting the multi-part file to file= ", ex.getMessage());
		}
		return file;
	}


	private void generadorArchivo(String[] lineasTexto) {

		PrintWriter writer;
		try {
			writer = new PrintWriter(FileUtil.RUTA_ARCHIVOS + UUID.randomUUID() + ".txt", "UTF-8");
			for (String s : lineasTexto) {
				writer.println(s);
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	private String[] lineasTexto(JSONObject iteratorModificado) {

		String objetoString = iteratorModificado.toString();
		objetoString = objetoString.substring(1, objetoString.length() - 1);

		String separador = Pattern.quote(",");

		String[] parts = objetoString.split(separador);

		return parts;
	}
	
//	private void convertidor(JSONArray companyList) {
//		ArrayList<String> listdata = new ArrayList<String>();     
//		JSONArray jArray = companyList; 
//		if (jArray != null) { 
//		   for (int i=0;i<jArray.length();i++){ 
//		    listdata.add(jArray.getString(i));
//		   } 
//		}
//		for(String i : listdata) {
//			System.out.println(i);
//		}
//	}
	

}

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

import com.fabrizio.jsonFiles.util.FileUtil;

import java.util.Iterator;
import java.util.UUID;

@Service
public class DivideArchivoService {
	
	private Logger LOGGER = LoggerFactory.getLogger(DivideArchivoService.class);

	@SuppressWarnings("unchecked")
	public void readFile(MultipartFile archivo) {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(convertMultiPartFileToFile(archivo)));
			
			JSONArray companyList = (JSONArray) obj;
			
			String jSonString =  companyList.toJSONString();
			String newString = jSonString.replace("Number", "numero");
//			newString = jSonString.replace("\"Discount\":\"$ 0,00\",", "");
			
			JSONArray jsonMod = (JSONArray) parser.parse(newString);
			Iterator<JSONObject> iteratorMod = jsonMod.iterator();
			
			while(iteratorMod.hasNext()) {
				generadorArchivo(lineasTexto(iteratorMod.next()));
			}
			
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
			writer = new PrintWriter(FileUtil.RUTA_ARCHIVOS+UUID.randomUUID()+".txt", "UTF-8");
			for (String s : lineasTexto) {
				writer.println(s);
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	private String[] lineasTexto(JSONObject iteratorModificado){
		
		String objetoString = iteratorModificado.toString();
		objetoString = objetoString.substring(1,objetoString.length()-1);
		
		String separador = Pattern.quote(",");
		
		String[] parts = objetoString.split(separador);
		
		return parts;
	}
	
	
}

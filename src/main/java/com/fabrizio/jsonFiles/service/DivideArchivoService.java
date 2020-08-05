package com.fabrizio.jsonFiles.service;

import java.io.FileReader;
import java.io.StringReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.apache.commons.io.IOUtils;

import com.fabrizio.jsonFiles.entity.Factura;
import com.fabrizio.jsonFiles.util.FileUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;


public class DivideArchivoService {

	private static final Logger log = LoggerFactory.getLogger(DivideArchivoService.class);

	public List<Factura> readFacturas(File file) throws Exception {
		try {
			MultipartFile multipart = fileToMultipartFile(file);
			JSONArray listaFactura = CsvtoJSON(multipart);

			List<Factura> facturas = new ArrayList<Factura>();

			Integer contadorArchivos = 0;
			for (Object o : listaFactura) {
				contadorArchivos++;
				Factura f = new Factura();
				JSONObject factura = (JSONObject) o;

				String nombre = (String) factura.get("Client name");

				String id = (String) factura.get("Number");

				String direccion = (String) factura.get("Client address");

				String precio = (String) factura.get("Total");
				Integer coma = precio.indexOf(",");
				precio = precio.substring(0, coma);
				if (precio.contains(",00")) {
					precio = precio.replace(",00", "");
				}
				if (precio.contains("$")) {
					precio = precio.replace("$", "");
				}
				if (precio.contains(".")) {
					precio = precio.replace(".", "");
				}
				precio = precio.substring(1, precio.length());

				f.setMonto(precio);
				f.setNombre(nombre);
				f.setId(id);
				f.setDireccion(direccion);

				facturas.add(f);

			}

			log.info("Cantidad archivos: " + facturas.size());
			return facturas;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public void writeTxt(File file, List<Factura> facturas) throws Exception {
		try {
			modificaCsv(file);
			File modificado = new File(file.getAbsolutePath()+"-modificado.csv");
			MultipartFile multipart = fileToMultipartFile(modificado);
			JSONArray listaCliente = CsvtoJSON(multipart);

			Integer contadorArchivos;
			Integer espacioCliente;
			try {
				for (Factura f : facturas) {
					contadorArchivos = 0;
					espacioCliente = 0;
					for (int i = 0; i < listaCliente.length(); i++) {
						espacioCliente++;
						JSONObject factura = listaCliente.getJSONObject(i);
						if (((String) factura.get("Name")).equals(f.getNombre())) {
							contadorArchivos++;
							if (!(f.getMonto().equals("0"))) {

								if (contadorArchivos > 1) {

									armadoTxt(f, listaCliente.getJSONObject(i), listaCliente.getJSONObject(i + 1));

								}

								armadoTxt(f, listaCliente.getJSONObject(i), listaCliente.getJSONObject(i + 1));
							} else {
								log.info("PLAN FREE: " + (String) factura.get("Name"));
							}
						}
					}

				}

			} catch (FileNotFoundException | UnsupportedEncodingException e) {

				e.printStackTrace();
				throw e;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			throw e;
		}

	}

	private MultipartFile fileToMultipartFile(File file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain",
				IOUtils.toByteArray(input));
		return multipartFile;
	}

	public JSONArray CsvtoJSON(MultipartFile file) throws Exception {
		InputStream is;
		try {
			is = file.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String inputStr, csv = "";

			while ((inputStr = br.readLine()) != null) {

				csv += inputStr + "\n";
			}
			JSONArray array = CDL.toJSONArray(csv);
			return array;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void armadoTxt(Factura f, JSONObject factura, JSONObject facturaPlan)
			throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer;

		writer = new PrintWriter(FileUtil.RUTA_ARCHIVOS + "factura_" + f.getId() + ".txt", "UTF-8");

		String idCliente = (String) factura.get("Id");
		writer.println("factura/codcta=" + idCliente);

		writer.println("factura/nombre=" + f.getNombre());

		String dni = (String) factura.get("DNI (custom attribute)");
		writer.println("factura/nroiva=" + dni);
		if (dni.isEmpty() || dni == null) {
			log.info("FACTURA SIN DNI: factura_" + f.getId() + ".txt");
		}

		writer.println("factura/direccion=" + f.getDireccion());

		String mail = (String) factura.get("Emails");
		writer.println("factura/mail=" + mail);

		writer.println("factura/codiva=cf");

		String plan = (String) facturaPlan.get("Service invoice label");
		switch (plan) {
		case "PLAN HOGAR 3MB":
			plan = "1";
			break;
		case "PLAN HOGAR 6MB":
			plan = "2";
			break;
		case "PLAN HOGAR 10MB":
			plan = "3";
			break;
		case "PLAN HOGAR 15MB":
			plan = "4";
			break;
		case "PLAN HOGAR 20MB":
			plan = "5";
			break;
		}
		if (plan.contains("($ 3MB)")) {
			plan = "1";
		}
		if (plan.contains("($ 6MB)")) {
			plan = "2";
		}
		if (plan.contains("($ 10MB)")) {
			plan = "3";
		}
		if (plan.contains("($ 15MB)")) {
			plan = "4";
		}
		if (plan.contains("($ 20MB)")) {
			plan = "5";
		}
		writer.println("linea/codart=" + plan);

		writer.println("linea/cantid=1");

		writer.println("linea/precio=" + f.getMonto());

		writer.println("linea/grabar");

		writer.close();
	}

	private void modificaCsv(File file) {
		

		CSVReader csvReader;
		CSVWriter csvWriter;
		CSVWriter csvWriter2;
		CSVReader csvReader2;

		try {
			csvReader = new CSVReader(new FileReader(file));

			String[] fila = null;
			String[] fila2 = null;
			String[] fila3 = null;
			csvWriter = new CSVWriter(new FileWriter(file.getAbsolutePath()+"-modificado.csv"));
			while ((fila = csvReader.readNext()) != null) {
				fila2 = fila;
				fila3 = fila;
				for (int i = 0; i < 34; i++) {
					System.out.println(fila[i] + " | " + i); // NOTE: i=34
					fila2[i] = fila[i];
				}
				
				for (int i = 35; i < 63; i++) {
					System.out.println(fila[i] + " | " + i); // NOTE: i=34
					fila3[i - 1] = fila[i];
				}
				log.info("length = "+fila2.length);
				
				csvWriter.writeNext(fila2);
				
				
			}
//			File f = new File(file.getAbsolutePath()+"-modificado.csv");
//			f.
			
			
			csvWriter.close();
			csvReader.close();
		} catch (IOException | CsvValidationException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}

package com.fabrizio.jsonFiles.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.*;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

import com.fabrizio.jsonFiles.entity.Factura;
import com.fabrizio.jsonFiles.util.FileUtil;
import com.opencsv.exceptions.CsvValidationException;

public class DivideArchivoService {

	private static final Logger log = LoggerFactory.getLogger(DivideArchivoService.class);

	public List<Factura> readFacturas(File file) throws Exception {
		MultipartFile multipart = fileToMultipartFile(file);
		JSONArray listaFactura = csvToJSON(multipart);

		List<Factura> facturas = new ArrayList<>();

		int contadorArchivos = 0;
		for (Object o : listaFactura) {
			contadorArchivos++;
			Factura f = new Factura();
			JSONObject factura = (JSONObject) o;

			String nombre = (String) factura.get("Client name");

			String id = (String) factura.get("Number");

			String direccion = (String) factura.get("Client address");

			String precio = (String) factura.get("Total");
			int indexPunto = precio.indexOf(".");
			//Si existe el punto en el precio
			if(indexPunto != -1) {
				precio = precio.substring(0, indexPunto);
			}
			if (precio.contains(".00")) {
				precio = precio.replace(".00", "");
			}
			if (precio.contains("ARS")) {
				precio = precio.replace("ARS", "");
			}
			if (precio.contains(",")) {
				precio = precio.replace(",", "");
			}
			precio = precio.substring(1, precio.length());

			f.setMonto(precio);
			f.setNombre(nombre);
			f.setId(id);
			f.setDireccion(direccion);

			facturas.add(f);

		}

		log.info("Cantidad archivos: {}", facturas.size());
		return facturas;


	}

	public void writeAndConvertClientTxt(File file, List<Factura> facturas) throws Exception {
		try {
			modificaCsv(file);
			File modificado = new File(file.getAbsolutePath() + "-modificado.csv");
			MultipartFile multipart = fileToMultipartFile(modificado);
			JSONArray listaCliente = csvToJSON(multipart);

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
								log.info("PLAN FREE: {}", (String) factura.get("Name"));
							}
						}
					}

				}

			} catch (FileNotFoundException e) {

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
		return new MockMultipartFile("file", file.getName(), "text/plain",
				IOUtils.toByteArray(input));
	}

	public JSONArray csvToJSON(MultipartFile file) throws Exception {
		InputStream is;
		try {
			is = file.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String inputStr, csv = "";

			while ((inputStr = br.readLine()) != null) {
				csv += inputStr + "\n";
			}
			return CDL.toJSONArray(csv);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void armadoTxt(Factura f, JSONObject factura, JSONObject facturaPlan)
			throws FileNotFoundException {
		PrintWriter writer;

		writer = new PrintWriter(FileUtil.RUTA_ARCHIVOS + "factura_" + f.getId() + ".txt");

		String idCliente = (String) factura.get("Id");
		writer.println("factura/codcta=" + idCliente);

		writer.println("factura/nombre=" + f.getNombre());

		String dni = (String) factura.get("DNI (custom attribute)");
		writer.println("factura/nroiva=" + dni);
		if (dni == null || dni.isEmpty()) {
			log.info("FACTURA SIN DNI: factura_{}", f.getId() + ".txt");
		}

		writer.println("factura/direcc=" + f.getDireccion());

		String mail = (String) factura.get("Emails");
		writer.println("factura/mail=" + mail);

		writer.println("factura/codiva=cf");

		String plan = (String) facturaPlan.get("Service invoice label");
		switch (plan) {
			case "PLAN HOGAR 5MB":
				plan = "1";
				break;
			case "PLAN HOGAR 8MB":
				plan = "2";
				break;
			case "PLAN HOGAR 12MB":
				plan = "3";
				break;
			case "PLAN HOGAR 17MB":
				plan = "4";
				break;
			case "PLAN HOGAR 22MB":
				plan = "5";
				break;
			default:
				plan = "X";
				break;
		}
		if (plan.contains("($ 5MB)")) {
			plan = "1";
		}
		if (plan.contains("($ 8MB)")) {
			plan = "2";
		}
		if (plan.contains("($ 12MB)")) {
			plan = "3";
		}
		if (plan.contains("($ 17MB)")) {
			plan = "4";
		}
		if (plan.contains("($ 22MB)")) {
			plan = "5";
		}
		writer.println("linea/codart=" + plan);

		writer.println("linea/cantid=1");

		writer.println("linea/precio=" + f.getMonto());

		writer.println("linea/grabar");

		writer.close();
	}

	private void modificaCsv(File file) {

		try (CSVReader csvReader = new CSVReader(
				    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			try(CSVWriter csvWriter = new CSVWriter(new FileWriterWithEncoding(file.getAbsolutePath() + "-modificado.csv", "UTF-8")); ) {
				String[] fila = null;
				String[] fila2 = null;
				String[] fila3 = null;
				while ((fila = csvReader.readNext()) != null) {
					fila2 = fila;
					fila3 = fila;
					for (int i = 0; i < 34; i++) {
						fila2[i] = fila[i];
					}
	
					for (int i = 35; i < 63; i++) {
						fila3[i - 1] = fila[i];
					}
	
					csvWriter.writeNext(fila2);
	
				}
			}
		} catch (JSONException | IOException | CsvValidationException e) {
			e.printStackTrace();
		}

	}

}

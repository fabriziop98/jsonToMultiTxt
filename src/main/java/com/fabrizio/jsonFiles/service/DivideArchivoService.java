package com.fabrizio.jsonFiles.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fabrizio.jsonFiles.entity.ArchivoTxt;
import com.fabrizio.jsonFiles.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class DivideArchivoService {


	public void readFile(MultipartFile file) throws Exception{
		

		ArchivoTxt txt = new ArchivoTxt();
		try {
			JSONArray companyList = CsvtoJSON(file);
			List<ArchivoTxt> listaTxt = new ArrayList<>();

			PrintWriter writer;
			try {
				for (Object o : companyList) {
					System.out.println(o.toString());
					JSONObject factura = (JSONObject) o;
					writer = new PrintWriter(
							FileUtil.RUTA_ARCHIVOS + "factura_" + (String) factura.get("Number") + ".txt", "UTF-8");
					writer.println("factura/tipcom=Factura");
					writer.println("factura/codven=ADM");
					String numero = (String) factura.get("Number");
					txt.setFactura(numero);
					writer.println("factura/codcta=" + numero);
					writer.println("linea/codart=1");
					writer.println("linea/cantid=5");
					String precio = (String) factura.get("Total");
					txt.setPrecio(precio);
					writer.println("linea/precio=" + precio);
					writer.println("linea/grabar");

					listaTxt.add(txt);

					writer.close();
				}

			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public JSONArray CsvtoJSON(MultipartFile file) throws Exception{
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
	

}

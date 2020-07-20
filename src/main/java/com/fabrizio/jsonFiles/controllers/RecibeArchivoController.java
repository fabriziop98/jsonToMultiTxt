package com.fabrizio.jsonFiles.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.CDL;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fabrizio.jsonFiles.service.DivideArchivoService;
import com.fabrizio.jsonFiles.util.FileUtil;

@Controller
@RequestMapping(value="/")
public class RecibeArchivoController {
	
	@Autowired
	private DivideArchivoService archivoService;
	
//	@PostMapping("/enviar")
//	public String inicio(@RequestParam(required = false, value = "file") MultipartFile archivo){
//		archivoService.readFile(archivo);
//		return "success";
//	}

	@GetMapping("")
	public String inicio() {
		return "inicio";
	}
	
	public File multipartToFile(MultipartFile file){
		File x = null;
		File directorio = new File(FileUtil.RUTA_ARCHIVOS);
        if (!directorio.exists()) {
            directorio.mkdir();
        }
        String archivo = file.getOriginalFilename();
        x = new File(FileUtil.RUTA_ARCHIVOS + archivo);
        int fileNo = 0;
        while (x.exists()) {
            fileNo++;
            int punto = archivo.lastIndexOf('.');
            String extension = archivo.substring(punto);
            String nombre = archivo.substring(0, punto);
            archivo = nombre + "(" + fileNo + ")" + extension;
            x = new File(FileUtil.RUTA_ARCHIVOS + archivo);
        }
        try {
			file.transferTo(x);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return x;
	}
	
	@PostMapping("/cargar")
	public String cargar(@RequestParam(required = false, name = "excel") MultipartFile file,RedirectAttributes flash) {
		InputStream is;
		try {
			is = file.getInputStream();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String inputStr, csv = "";

		while ((inputStr = br.readLine()) != null){
		    csv += inputStr +"\n";
		}

		JSONArray array = CDL.toJSONArray(csv);
		archivoService.readFile(array);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		flash.addFlashAttribute("success", "Archivos generados con exito");
		return "redirect:/";

	}
	
//	@PostMapping("/cargar")
//    public String cargaMasiva(@RequestParam(required = false, name = "excel") MultipartFile file, RedirectAttributes flash) {
//
//		File x = multipartToFile(file);
//		String line = "";
//        String cvsSplitBy = ",";
//        JSONArray jsonArray = new JSONArray();
//        List<ArchivoJson>listaParaGuardar = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(x))) {
//        	
//            while ((line = br.readLine()) != null) {
//            	ArchivoJson archivoJson = new ArchivoJson();
//                String[] cadena = line.split(cvsSplitBy);
//                
//               	archivoJson.setNumber(cadena[0]);
//               	archivoJson.setStatus(cadena[1]);
//               	archivoJson.setCreatedDate(cadena[2]);
//               	archivoJson.setDueDate(cadena[3]);
//               	archivoJson.setTotal(cadena[4]);
//               	archivoJson.setTaxes(cadena[5]);
//               	archivoJson.setDiscount(cadena[6]);
//               	archivoJson.setAmountPaid(cadena[7]);
//               	archivoJson.setAmountDue(cadena[8]);
//               	archivoJson.setOrganizationName(cadena[9]);
//               	archivoJson.setClientName(cadena[10]);
//               	archivoJson.setOrganizationAddress(cadena[11]);
//               	archivoJson.setClientAddress(cadena[12]);				
//               	
//               	listaParaGuardar.add(archivoJson);
//            }
//            for (ArchivoJson archivoJson : listaParaGuardar) {            	
//            	Object number = (Object)archivoJson.getNumber();
//            	Object Status = (Object)archivoJson.getStatus();
//            	Object CreatedDate = (Object)archivoJson.getCreatedDate();
//            	Object Total = (Object)archivoJson.getTotal();
//            	Object Taxes = (Object)archivoJson.getTaxes();
//            	Object Discount = (Object)archivoJson.getDiscount();
//            	Object AmountPaid = (Object)archivoJson.getAmountPaid();
//            	Object ClientName = (Object)archivoJson.getClientName();
//            	Object OrganizationAddress = (Object)archivoJson.getOrganizationAddress();
//            	Object ClientAddress = (Object)archivoJson.getClientAddress();
//            	String propiedades [] = {"Number","Status","CreatedDate","Total","Taxes","Discount","AmountPaid","ClientName","OrganizationAddress","ClientAddress"};
//            	Object valores [] = {number,Status,CreatedDate,Total,Taxes,Discount,AmountPaid,ClientName,OrganizationAddress,ClientAddress};
//            	String prueba = getSimpleJSON(propiedades,valores);
//            	System.out.println(prueba);
//            	jsonArray.add(prueba);
//            }
//            jsonArray.remove(0);
//            archivoService.readFile(jsonArray);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "redirect:/";
//    }
	
	
	
	  public static String getSimpleJSON(String propiedad, Object valor) {
	        return "{\"" + propiedad + "\":\"" + valor + "\"}";
	    }
	  
	  public static String getSimpleJSON(String[] propiedades, Object[] valores) {

	        StringBuilder sb = new StringBuilder("{");
	        for (int n = 0; n < propiedades.length; n++) {
	            if (valores[n].getClass().isAssignableFrom(Number.class)
	                    || valores[n].getClass().isAssignableFrom(Double.class)
	                    || valores[n].getClass().isAssignableFrom(Integer.class)) {
	                sb.append("\"").append(propiedades[n]).append("\":").append(valores[n]);
	            } else {
	                sb.append("\"").append(propiedades[n]).append("\":\"").append(valores[n]).append("\"");
	            }

	            if (n + 1 < propiedades.length) {
	                sb.append(",");
	            }
	        }

	        return sb.append("}").toString();
	    }
	
}

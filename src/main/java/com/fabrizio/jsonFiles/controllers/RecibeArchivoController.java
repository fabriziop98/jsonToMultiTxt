package com.fabrizio.jsonFiles.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fabrizio.jsonFiles.service.DivideArchivoService;

@Controller
@RequestMapping(value="/")
public class RecibeArchivoController {
	
	@Autowired
	private DivideArchivoService archivoService;
	
	@GetMapping("")
	public String inicio() {
		return "inicio";
	}
	
	@PostMapping("/cargar")
	public String cargar(@RequestParam(required = false, name = "excel") MultipartFile file,RedirectAttributes flash) {
		try {
			archivoService.readFile(file);
		} catch (Exception e) {
			flash.addFlashAttribute("error", "Ocurrió un error en la conversión del archivo.");
			e.printStackTrace();
			return "redirect:/";
		}
		flash.addFlashAttribute("success", "Archivos generados con exito");
		return "redirect:/";

	}
	
}

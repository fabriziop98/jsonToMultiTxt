package com.fabrizio.jsonFiles.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
	
	@PostMapping("/enviar")
	public String inicio(@RequestParam(required = false, value = "file") MultipartFile archivo){
		archivoService.readFile(archivo);
		return "success";
	}
	
}

package com.fabrizio.jsonFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fabrizio.jsonFiles.entity.Factura;
import com.fabrizio.jsonFiles.service.DivideArchivoService;
import com.fabrizio.jsonFiles.util.FileUtil;

@SpringBootApplication
public class JsonFiles1Application implements CommandLineRunner {

	private final static Logger logger = LoggerFactory.getLogger(JsonFiles1Application.class);

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SpringApplication.run(JsonFiles1Application.class, args);
				DivideArchivoService service = new DivideArchivoService();

				System.setProperty("java.awt.headless", "false");

				List<Factura> facturas = new ArrayList<Factura>();

				JFileChooser chooser = new JFileChooser();

				FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo CSV", "csv");

				chooser.setFileFilter(filter);
				chooser.setApproveButtonText("Elegir este archivo de facturas");
				chooser.setDialogTitle("Selecciona primero el archivo que contiene las facturas");
				
				int returnVal = chooser.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					logger.info("Elegiste este archivo: " + chooser.getSelectedFile().getName());
					logger.info("PATH: " + chooser.getSelectedFile().getPath());
					File file = chooser.getSelectedFile();
					try {
						facturas = service.readFacturas(file);

						JFileChooser chooser2 = new JFileChooser();

						FileNameExtensionFilter filter2 = new FileNameExtensionFilter("Archivo CSV", "csv");

						chooser2.setFileFilter(filter2);
						chooser2.setApproveButtonText("Elegir este archivo de clientes");
						chooser2.setDialogTitle("Selecciona el archivo que contiene los clientes");
						
						int returnVal2 = chooser2.showOpenDialog(null);

						if (returnVal2 == JFileChooser.APPROVE_OPTION) {
							logger.info("You chose to open this file: " + chooser2.getSelectedFile().getName());
							logger.info("PATH: " + chooser2.getSelectedFile().getPath());
							File file2 = chooser2.getSelectedFile();
							try {
								service.writeAndConvertClientTxt(file2, facturas);
								JOptionPane.showMessageDialog(null,
										"Los archivos se generaron en la ruta: " + FileUtil.RUTA_ARCHIVOS, "¡Éxito!",
										JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e) {
								e.printStackTrace();
								JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar generar los archivos",
										"¡Ups!", JOptionPane.ERROR_MESSAGE);
							}
							

						}
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar generar los archivos. ¿Los elegiste en el orden correcto? (Archivo facturas - Archivo clientes).",
								"¡Ups!", JOptionPane.ERROR_MESSAGE);
					}
					
				}
			}
		});

	}

	@Override
	public void run(String... args) throws Exception {
	}

}

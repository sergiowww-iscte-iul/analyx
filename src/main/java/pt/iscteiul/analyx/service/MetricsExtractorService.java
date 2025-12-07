package pt.iscteiul.analyx.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class MetricsExtractorService {
	@SneakyThrows
	public void addProject(MultipartFile projectFiles) {
		Path zipFile = Files.createTempFile("project", "files");
		projectFiles.transferTo(zipFile);
		Files.createTempDirectory("project");

//		try (InputStream fis = Files.newInputStream(zipFile);
//			 ZipInputStream zis = new ZipInputStream(fis)) {
//
//			ZipEntry entry;
//			// Loop through all entries in the zip file
//			while ((entry = zis.getNextEntry()) != null) {
//				Path newPath = destDir.resolve(entry.getName());
//
//				if (entry.isDirectory()) {
//					// Create the directory if it's a directory entry
//					Files.createDirectories(newPath);
//				} else {
//					// Ensure parent directory structure exists for the file
//					Files.createDirectories(newPath.getParent());
//
//					// Write the file content
//					try (var outputStream = Files.newOutputStream(newPath)) {
//						byte[] buffer = new byte[BUFFER_SIZE];
//						int len;
//						while ((len = zis.read(buffer)) > 0) {
//							outputStream.write(buffer, 0, len);
//						}
//					}
//				}
//				zis.closeEntry();
//			}
//		}
//	}


	}

//	public static void main(String[] args) {
//		Path path = Paths.get("/Users/sergio/iscte-mei-workspace/Arquitetura e Desenho de Sistemas/analyx/src/main/java/pt/iscteiul/analyx/service/MyComplexClass.java");
//		new CK().calculate(path, result -> {
//			int loc = result.getLoc();
//			System.out.println("loc = " + loc);
//		});
//	}
}

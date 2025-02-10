package vn.hoidanit.jobhunter.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.domain.response.file.ResUploadFileDTO;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.StorageException;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class FileController {

	@Value("${hoanglong.upload-file.base-uri}")
	private String baseURI;

	private final FileService fileService;

	/**
	 * @param fileService
	 */
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping("/files")
	@ApiMessage("Upload single file")
	public ResponseEntity<ResUploadFileDTO> uploadFile(
			@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam(name = "folder", required = false) String folder)
			throws URISyntaxException, IOException, StorageException {

		// skip validation
		if (folder == null || file.isEmpty()) {
			throw new StorageException("File is empty. Please upload a file");
		}

		String fileName = file.getOriginalFilename();
		if (fileName == null) {
			throw new StorageException("Empty filename");
		}
		List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc");
		boolean isValid = allowedExtensions.stream().anyMatch(extension -> fileName.toLowerCase().endsWith(extension));
		if (!isValid) {
			throw new StorageException("Invalid file extension. Only allow " + allowedExtensions.toString());
		}
		this.fileService.createDirectory(baseURI + folder);
		String uploadFile = this.fileService.store(file, folder);
		ResUploadFileDTO dto = new ResUploadFileDTO(uploadFile, Instant.now());
		return ResponseEntity.ok(dto);
	}

	@GetMapping("/files")
	public ResponseEntity<Resource> downloadFile(
			@RequestParam(name = "fileName", required = false) String fileName,
			@RequestParam(name = "folder", required = false) String folder)
			throws StorageException, URISyntaxException, FileNotFoundException {
		if (fileName == null || folder == null) {
			throw new StorageException("Missing required params!");
		}

		long fileLength = this.fileService.getFileLength(fileName, folder);
		if (fileLength == 0) {
			throw new StorageException("File with name = " + fileName + " is not found!");
		}

		InputStreamResource resource = this.fileService.getResource(fileName, folder);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.contentLength(fileLength)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}

}

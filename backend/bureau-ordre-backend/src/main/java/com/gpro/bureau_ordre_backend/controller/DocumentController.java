package com.gpro.bureau_ordre_backend.controller;

import com.gpro.bureau_ordre_backend.model.Document;
import com.gpro.bureau_ordre_backend.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public Document upload(@RequestParam("file") MultipartFile file) throws Exception {
        new File(UPLOAD_DIR).mkdirs();
        String path = UPLOAD_DIR + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.write(Paths.get(path), file.getBytes());

        Document doc = new Document();
        doc.setNomFichier(file.getOriginalFilename());
        doc.setCheminFichier(path);
        doc.setStatut("RECU");
        return documentRepository.save(doc);
    }

    @GetMapping
    public List<Document> getAll() {
        return documentRepository.findAll();
    }
}
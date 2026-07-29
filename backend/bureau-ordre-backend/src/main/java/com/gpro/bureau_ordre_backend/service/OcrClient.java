package com.gpro.bureau_ordre_backend.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OcrClient {

    private final String OCR_SERVICE_URL = "http://localhost:8000/extract";
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> extract(String cheminFichier) {
        FileSystemResource fichier = new FileSystemResource(cheminFichier);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fichier);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        Map<String, Object> reponse = restTemplate.postForObject(OCR_SERVICE_URL, requestEntity, Map.class);

        return reponse;
    }
}
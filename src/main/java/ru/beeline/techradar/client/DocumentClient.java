package ru.beeline.techradar.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class DocumentClient {

    private final String documentServiceUrl;
    private final RestTemplate restTemplate;

    public DocumentClient(@Value("${integration.document-server-url}") String documentServiceUrl,
                          RestTemplate restTemplate) {
        this.documentServiceUrl = documentServiceUrl;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> updateDocument(Integer docId, MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Content-Disposition", file.getOriginalFilename());

            byte[] fileBytes = file.getBytes();

            Resource fileResource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            String url = documentServiceUrl + "/api/v1/export/" + docId;

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    requestEntity,
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return new ResponseEntity<>(response.getBody(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response.getBody(), HttpStatus.SERVICE_UNAVAILABLE);
            }
        } catch (Exception e) {
            log.error("Error occurred while trying to update document: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
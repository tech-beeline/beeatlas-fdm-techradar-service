package ru.beeline.techradar.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.beeline.techradar.dto.ProductDTO;
import ru.beeline.techradar.dto.ProductTechRelationDTO;
import ru.beeline.techradar.exception.NotFoundException;

import java.util.List;


@Slf4j
@Service
public class ProductClient {

    RestTemplate restTemplate;
    private final String productServerUrl;

    public ProductClient(@Value("${integration.products-server-url}") String productServerUrl, RestTemplate restTemplate) {
        this.productServerUrl = productServerUrl;
        this.restTemplate = restTemplate;
    }

    public List<ProductDTO> getProduct() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("SOURCE", "Sparx");

            return restTemplate.exchange(productServerUrl + "/api/v1/product-tech-relation",
                    HttpMethod.GET, new HttpEntity(headers), new ParameterizedTypeReference<List<ProductDTO>>() {
                    }).getBody();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public void postProduct(String cmdbCode, Integer techId) {
        HttpStatus resultStatus = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("SOURCE", "Sparx");

            HttpEntity<ProductTechRelationDTO> entity = new HttpEntity(ProductTechRelationDTO.builder()
                    .cmdbCode(cmdbCode)
                    .build(), headers);
            resultStatus = restTemplate.exchange(productServerUrl + "/api/v1/product-tech-relation/" + techId,
                    HttpMethod.POST, entity, Object.class).getStatusCode();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (resultStatus == HttpStatus.NOT_FOUND) {
            throw new NotFoundException("relation is not Found");
        }
    }
}

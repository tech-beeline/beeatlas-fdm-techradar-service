/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.beeline.techradar.dto.ProductDTO;
import ru.beeline.techradar.dto.ProductTechRelationDTO;
import ru.beeline.techradar.exception.ChapterNotFoundException;
import ru.beeline.techradar.exception.NotFoundException;
import ru.beeline.techradar.exception.ProductServiceUnavailableException;
import ru.beeline.techradar.dto.product.GetProductTechDto;

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
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("SOURCE", "Sparx");

            HttpEntity<ProductTechRelationDTO> entity = new HttpEntity(ProductTechRelationDTO.builder()
                    .cmdbCode(cmdbCode)
                    .build(), headers);

            restTemplate.exchange(productServerUrl + "/api/v1/product-tech-relation/" + techId,
                    HttpMethod.POST, entity, Object.class).getStatusCode();

        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("relation is not Found");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<GetProductTechDto> getTechProducts() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("SOURCE", "Sparx");

            HttpEntity<ProductTechRelationDTO> entity = new HttpEntity(ProductTechRelationDTO.builder()
                    .build(), headers);

            ResponseEntity<List<GetProductTechDto>> response = restTemplate.exchange(
                    productServerUrl + "/api/v1/products/relations/tech",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<GetProductTechDto>>() {
                    });
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("relation is not Found");
        }
    }

    public List<Integer> getChapterPatternIds(Integer chapterId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("SOURCE", "Sparx");

            ResponseEntity<List<Integer>> response = restTemplate.exchange(
                    productServerUrl + "/api/v1/chapter/" + chapterId + "/patterns",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Integer>>() {
                    }
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ChapterNotFoundException("Chapter not found in product", e);
        } catch (HttpServerErrorException e) {
            throw new ProductServiceUnavailableException("Product service unavailable", e);
        } catch (Exception e) {
            throw new ProductServiceUnavailableException("Product service unavailable", e);
        }
    }

    public boolean postPatternNfr(Integer patternId, List<Integer> nfrIds, boolean refreshRelation) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("SOURCE", "Sparx");

            String url = productServerUrl + "/api/v1/nfr/pattern/" + patternId;
            if (refreshRelation) {
                url += "?refresh-relation=true";
            }

            HttpEntity<List<Integer>> entity = new HttpEntity<>(nfrIds, headers);
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Product NFR link failed for pattern {}: {}", patternId, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Product NFR link error for pattern {}: {}", patternId, e.getMessage());
            return false;
        }
    }
}

/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.beeline.techradar.dto.product.GetProductsDTO;
import ru.beeline.techradar.domain.Tech;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExcelExporterService {

    public File exportTechnologies(Map<Tech, List<GetProductsDTO>> techProductsMap, String fileName) {
        List<String> headers = List.of("label", "description", "products", "sector", "status");
        String sheetName = "Technologies";
        Workbook workbook = createWorkbookWithHeaders(headers, sheetName);
        createTechCell(techProductsMap, workbook.getSheet(sheetName));
        return writeWorkbookToFile(workbook, fileName);
    }

    private Workbook createWorkbookWithHeaders(List<String> headers, String sheetName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
        }
        return workbook;
    }

    private void createTechCell(Map<Tech, List<GetProductsDTO>> techProductsMap, Sheet sheet) {
        int rowNum = 1;
        for (Map.Entry<Tech, List<GetProductsDTO>> entry : techProductsMap.entrySet()) {
            Tech tech = entry.getKey();
            List<GetProductsDTO> products = entry.getValue();
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(tech.getLabel());
            row.createCell(1).setCellValue(tech.getDescription());
            String productAliases = products.isEmpty() ? "" :
                    products.stream().map(GetProductsDTO::getAlias).collect(Collectors.joining(", "));
            row.createCell(2).setCellValue(productAliases);
            row.createCell(3).setCellValue(tech.getSector().getName());
            row.createCell(4).setCellValue(tech.getRing().getName());
        }
    }

    private File writeWorkbookToFile(Workbook workbook, String fileName) {
        File tempFile = null;
        try (FileOutputStream fileOut = new FileOutputStream(tempFile = File.createTempFile(fileName, ".xlsx"))) {
            workbook.write(fileOut);
            log.info("Excel file created successfully: {}", tempFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error while writing to Excel file: {}", e.getMessage(), e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                log.error("Error while closing the workbook: {}", e.getMessage(), e);
            }
        }
        return tempFile;
    }
}
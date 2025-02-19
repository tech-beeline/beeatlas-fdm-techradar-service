package ru.beeline.techradar.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import ru.beeline.fdmlib.dto.product.GetProductsDTO;
import ru.beeline.techradar.domain.Tech;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExcelExporterService {

    public MultipartFile createXlsx(Map<Tech, List<GetProductsDTO>> techProductsMap) {

        String fileName = "export_technology_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Technologies");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("label");
            headerRow.createCell(1).setCellValue("description");
            headerRow.createCell(2).setCellValue("products");
            headerRow.createCell(3).setCellValue("sector");
            headerRow.createCell(4).setCellValue("status");

            int rowNum = 1;
            for (Map.Entry<Tech, List<GetProductsDTO>> entry : techProductsMap.entrySet()) {
                Tech tech = entry.getKey();
                List<GetProductsDTO> products = entry.getValue();

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(tech.getLabel());
                row.createCell(1).setCellValue(tech.getDescription());

                StringBuilder productsBuilder = new StringBuilder();
                for (GetProductsDTO product : products) {
                    if (productsBuilder.length() > 0) {
                        productsBuilder.append(". ");
                    }
                    productsBuilder.append(product.getAlias());
                }
                row.createCell(2).setCellValue(productsBuilder.toString());

                row.createCell(3).setCellValue(tech.getSector().getName());
                row.createCell(4).setCellValue(tech.getRing().getName());
            }

            workbook.write(outputStream);

            byte[] excelBytes = outputStream.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(excelBytes);
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            if (!tempDir.exists() || !tempDir.isDirectory() || !tempDir.canWrite()) {
                log.error("Temporary directory is not accessible: {}", tempDir.getAbsolutePath());
                throw new IOException("Temporary directory is not accessible: " + tempDir.getAbsolutePath());
            }

            DiskFileItem diskFileItem = new DiskFileItem(
                    fileName,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    false,
                    fileName,
                    excelBytes.length,
                    tempDir
            );

            diskFileItem.getOutputStream();

            return new CommonsMultipartFile(diskFileItem);


        } catch (IOException e) {
            log.error("Error occurred while creating XLSX: ", e);
            throw new RuntimeException("Failed to create XLSX file", e);
        }
    }
}
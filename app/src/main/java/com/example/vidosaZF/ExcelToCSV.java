package com.example.vidosaZF;

import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class ExcelToCSV {
    public static void convertExcelToCSV(String excelFilePath, String csvFilePath) {
        try (FileInputStream fis = new FileInputStream(new File(excelFilePath));
             Workbook workbook = WorkbookFactory.create(fis);
             FileWriter csvWriter = new FileWriter(new File(csvFilePath))) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.getCellType()) {
                        case STRING:
                            csvWriter.append(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                csvWriter.append(cell.getDateCellValue().toString());
                            } else {
                                csvWriter.append(String.valueOf(cell.getNumericCellValue()));
                            }
                            break;
                        case BOOLEAN:
                            csvWriter.append(String.valueOf(cell.getBooleanCellValue()));
                            break;
                        case FORMULA:
                            csvWriter.append(cell.getCellFormula());
                            break;
                        default:
                            csvWriter.append("");
                    }
                    if (cellIterator.hasNext()) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\n");
            }

            csvWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

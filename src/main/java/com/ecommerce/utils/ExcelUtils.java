package com.ecommerce.utils;

import com.ecommerce.constants.AppConstants;
import com.ecommerce.exceptions.FrameworkException;
import io.qameta.allure.Step;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * ExcelUtils provides methods to read test data from Excel files.
 * - Path driven by config (AppConstants.KEY_EXCEL_PATH) with fallback.
 * - Returns each row as a Map<header, value>.
 * - Logs actions via LogUtils and reports via Allure @Step.
 */
public final class ExcelUtils {

    /** Excel file path (configurable) */
    private static final String EXCEL_FILE_PATH = ConfigReader.containsKey(AppConstants.KEY_EXCEL_PATH)
            ? ConfigReader.get(AppConstants.KEY_EXCEL_PATH)
            : "src/main/resources/testdata/testdata.xlsx";

    private ExcelUtils() {
        // prevent instantiation
    }

    /**
     * Read all rows from the given sheet, mapping header→cellValue.
     *
     * @param sheetName name of the sheet to read
     * @return list of rows, each row as a Map of columnHeader→cellValue
     */
    @Step("Reading Excel test data from sheet: {sheetName}")
    public static List<Map<String, String>> getTestData(String sheetName) {
        LogUtils.info("Attempting to read Excel file: " + EXCEL_FILE_PATH);
        List<Map<String, String>> data = new ArrayList<>();

        try (InputStream in = Files.newInputStream(Paths.get(EXCEL_FILE_PATH));
             Workbook workbook = WorkbookFactory.create(in)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                String msg = "Sheet '" + sheetName + "' not found in " + EXCEL_FILE_PATH;
                LogUtils.error(msg);
                throw new FrameworkException(msg);
            }

            Iterator<Row> rowIter = sheet.iterator();
            if (!rowIter.hasNext()) {
                LogUtils.warn("Sheet '" + sheetName + "' is empty");
                return data;
            }

            // Read header row
            Row headerRow = rowIter.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            DataFormatter formatter = new DataFormatter();

            // Read data rows
            while (rowIter.hasNext()) {
                Row row = rowIter.next();
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = formatter.formatCellValue(cell);
                    rowMap.put(headers.get(i), value);
                }
                data.add(rowMap);
            }

            LogUtils.info("Loaded " + data.size() + " rows from sheet: " + sheetName);
            return data;

        } catch (IOException e) {
            String msg = "Error reading Excel file: " + EXCEL_FILE_PATH;
            LogUtils.error(msg, e);
            throw new FrameworkException(msg, e);
        }
    }
}

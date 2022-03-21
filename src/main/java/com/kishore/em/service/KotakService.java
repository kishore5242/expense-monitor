package com.kishore.em.service;

import com.kishore.em.type.Record;
import com.kishore.em.util.EmUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KotakService {

    public List<Record> getRecords(String filePath) throws IOException, ParseException {
        FileInputStream fis = new FileInputStream(new File(filePath));
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        HSSFSheet sheet = wb.getSheetAt(0);
        FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
        boolean recording = false;
        List<Record> records = new ArrayList<>();

        for (Row row : sheet) {
            Cell firstCell = row.getCell(0);
            String firstCellValue = EmUtil.getCellValue(firstCell, formulaEvaluator);
            if (firstCellValue.startsWith("Sl. No.")) {
                // start recording
                recording = true;
                continue;
            } else if (firstCellValue.startsWith("Opening")) {
                // start recording
                recording = false;
                continue;
            } else if (!EmUtil.isNumber(firstCellValue)) {
                continue;
            }
            if (recording) {
//                System.out.println(firstCellValue);
                LocalDate valueDate = getValueDate(row, formulaEvaluator);
                Double balance = getBalance(row, formulaEvaluator);
                Double credit = getCredit(row, formulaEvaluator);
                Double debit = getDebit(row, formulaEvaluator);
                Record record = new Record("Kotak", valueDate, balance, credit, debit);
                records.add(record);
            }
        }
        return records.stream()
                .sorted(Comparator.comparing(Record::getValueDate))
                .collect(Collectors.toList());
    }

    private LocalDate getValueDate(Row row, FormulaEvaluator formulaEvaluator) throws ParseException {
        Cell cell = row.getCell(2);
        String cellValue = EmUtil.getCellValue(cell, formulaEvaluator);
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("yyyy/MMM/dd").toFormatter();
        return LocalDate.parse(cellValue, formatter);
    }

    private Double getBalance(Row row, FormulaEvaluator formulaEvaluator) {
        Cell cell = row.getCell(7);
        return EmUtil.amountAsDouble(EmUtil.getCellValue(cell, formulaEvaluator));
    }

    private Double getCredit(Row row, FormulaEvaluator formulaEvaluator) {
        Cell cell = row.getCell(6);
        return EmUtil.amountAsDouble(EmUtil.getCellValue(cell, formulaEvaluator));
    }

    private Double getDebit(Row row, FormulaEvaluator formulaEvaluator) {
        Cell cell = row.getCell(5);
        return EmUtil.amountAsDouble(EmUtil.getCellValue(cell, formulaEvaluator));
    }

}

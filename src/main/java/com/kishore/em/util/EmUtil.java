package com.kishore.em.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public final class EmUtil {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private EmUtil() {
    }

    public static Double amountAsDouble(String balance) {
        String balance1 = balance.replace(",", "");
        return getDouble(balance1);
    }

    public static String getCellValue(Cell cell, FormulaEvaluator formulaEvaluator) {
        Cell cellVal = formulaEvaluator.evaluateInCell(cell);
        if (cellVal == null) {
            return "";
        }
        CellType cellType = cellVal.getCellType();
        if (cellType == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            return "";
        }
    }

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static boolean isNumber(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
//            e.printStackTrace();
        }
        return false;
    }

    private static Double getDouble(String str) {
        if (StringUtils.isBlank(str)) {
            return 0.0;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
//            e.printStackTrace();
        }
        return 0.0;
    }
}

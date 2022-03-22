package com.kishore.em;

import com.kishore.em.service.IciciService;
import com.kishore.em.service.KotakService;
import com.kishore.em.type.AmountGroup;
import com.kishore.em.type.Record;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest")
public class EmRestController {

    @Autowired
    private IciciService iciciService;

    @Autowired
    private KotakService kotakService;

    @GetMapping("/records")
    public List<Record> getRecords() throws IOException, ParseException {

        List<Record> records = new ArrayList<>();
        records.addAll(iciciService.getRecords("D:\\finances\\temp\\icici.xls"));
        records.addAll(kotakService.getRecords("D:\\finances\\temp\\kotak.xls"));

        return records.stream()
                .sorted(Comparator.comparing(Record::getValueDate))
                .collect(Collectors.toList());
    }

    @GetMapping("/savings")
    public List<AmountGroup> getSavings() throws IOException, ParseException {

        List<AmountGroup> monthlySums = new ArrayList<>();

        List<Record> records = new ArrayList<>();
        records.addAll(iciciService.getRecords("D:\\finances\\temp\\icici.xls"));
        records.addAll(kotakService.getRecords("D:\\finances\\temp\\kotak.xls"));

        Map<LocalDate, List<Record>> monthlyRecords = records.stream()
                .collect(Collectors.groupingBy(record -> {
                    LocalDate valueDate = record.getValueDate();
                    return LocalDate.of(valueDate.getYear(), valueDate.getMonthValue(), 1);
                }));

        monthlyRecords.keySet().stream()
                .sorted()
                .forEach(group -> {
                    List<Record> mrecs = monthlyRecords.get(group);
                    String label = group.getMonth() + " " + group.getYear();
                    monthlySums.add(new AmountGroup(label, getSavings(mrecs)));
                });
        return monthlySums;
    }

    @GetMapping("/expenses")
    public List<AmountGroup> getExpenses() throws IOException, ParseException {

        List<AmountGroup> monthlySums = new ArrayList<>();

        List<Record> records = new ArrayList<>();
        records.addAll(iciciService.getRecords("D:\\finances\\temp\\icici.xls"));
        records.addAll(kotakService.getRecords("D:\\finances\\temp\\kotak.xls"));

        Map<LocalDate, List<Record>> monthlyRecords = records.stream()
                .collect(Collectors.groupingBy(record -> {
                    LocalDate valueDate = record.getValueDate();
                    return LocalDate.of(valueDate.getYear(), valueDate.getMonthValue(), 1);
                }));

        monthlyRecords.keySet().stream()
                .sorted()
                .forEach(group -> {
                    List<Record> mrecs = monthlyRecords.get(group);
                    String label = group.getMonth() + " " + group.getYear();
                    monthlySums.add(new AmountGroup(label, getExpenses(mrecs)));
                });
        return monthlySums;
    }

    @GetMapping("/salary")
    public List<AmountGroup> getSalary() throws IOException, ParseException {

        List<AmountGroup> monthlySums = new ArrayList<>();

        List<Record> records = new ArrayList<>(iciciService.getRecords("D:\\finances\\temp\\icici.xls"));

        Map<LocalDate, List<Record>> monthlyRecords = records.stream()
                .collect(Collectors.groupingBy(record -> {
                    LocalDate valueDate = record.getValueDate();
                    return LocalDate.of(valueDate.getYear(), valueDate.getMonthValue(), 1);
                }));

        monthlyRecords.keySet().stream()
                .sorted()
                .forEach(group -> {
                    List<Record> mrecs = monthlyRecords.get(group);
                    String label = group.getMonth() + " " + group.getYear();
                    monthlySums.add(new AmountGroup(label, getSalary(mrecs)));
                });
        return monthlySums;
    }

    private Double getSavings(List<Record> records) {
        Double savings = 0.0;
        for (Record record : records) {
            Double debit = record.getDebit();
            Double credit = record.getCredit();

            if (isInternal(record)) continue;
            if (isInvestment(record)) continue;
//            if (isLumpExact(debit, credit)) continue;

            savings -= debit;
            savings += credit;
        }
        return savings;
    }

    private Double getSalary(List<Record> records) {
        Double savings = 0.0;
        for (Record record : records) {
            Double credit = record.getCredit();

            if (isInternal(record)) continue;
            if (isInvestment(record)) continue;

            savings += credit;
        }
        return savings;
    }

    private Double getExpenses(List<Record> records) {
        Double expense = 0.0;
        for (Record record : records) {
            Double debit = record.getDebit();
            if (isInternal(record)) continue;
            if (isInvestment(record)) continue;
//            if (isLumpExact(debit, credit)) continue;
            expense += debit;
        }
        return expense;
    }

    private boolean isLumpExact(Double debit, Double credit) {
        if (debit == 100000.0 || credit == 100000.0) {
            return true;
        }
        if (debit == 50000.0 || credit == 50000.0) {
            return true;
        }
        if (debit == 150000.0 || credit == 150000.0) {
            return true;
        }
        if (debit == 200000.0 || credit == 200000.0) {
            return true;
        }
        return false;
    }

    private boolean isInvestment(Record record) {
        String remark = record.getRemark();
        // icici FD investment
        if (StringUtils.isNotBlank(remark) && remark.contains("TO FD")) {
            return true;
        }
        // icici ppf investment
        if (StringUtils.isNotBlank(remark) && remark.contains("/Self")) {
            return true;
        }
        // Kotak FD
        if (StringUtils.isNotBlank(remark) && remark.contains("FD ")) {
            return true;
        }
        return false;
    }

    private boolean isSalary(Record record) {
        String remark = record.getRemark();
        // icici salary
        if (StringUtils.isNotBlank(remark) && remark.contains("SALARY")) {
            return true;
        }
        return false;
    }

    private boolean isInternal(Record record) {
        String remark = record.getRemark();
        // transferred amount to Kotak
        if (StringUtils.isNotBlank(remark) && remark.contains("Kishore Ko")) {
            return true;
        }
        // received from icici
        if (StringUtils.isNotBlank(remark) && remark.contains("Received from KISH")) {
            return true;
        }
        return false;
    }


}

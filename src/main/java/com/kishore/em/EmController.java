package com.kishore.em;

import com.kishore.em.service.IciciService;
import com.kishore.em.service.KotakService;
import com.kishore.em.type.AmountGroup;
import com.kishore.em.type.Balance;
import com.kishore.em.type.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/em")
public class EmController {

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

        Map<String, List<Record>> monthlyRecords = records.stream()
                .sorted(Comparator.comparing(Record::getValueDate))
                .collect(Collectors.groupingBy(record -> {
                    LocalDate valueDate = record.getValueDate();
                    return valueDate.getMonth() + " " + valueDate.getYear();
                }));

        for (String group : monthlyRecords.keySet()) {
            List<Record> mrecs = monthlyRecords.get(group);
            monthlySums.add(new AmountGroup(group, getSavings(mrecs)));
        }
        return monthlySums;
    }

    @GetMapping("/balance")
    public List<Balance> getBalance() throws IOException, ParseException {

        List<Record> iciciRecords = iciciService.getRecords("D:\\finances\\temp\\icici.xls");
        List<Record> kotakRecords = kotakService.getRecords("D:\\finances\\temp\\kotak.xls");

        // both bank's records are needed
        if (iciciRecords.size() == 0 || kotakRecords.size() == 0) {
            return Collections.emptyList();
        }

        // decide start date
        LocalDate iciciStartDate = iciciRecords.get(0).getValueDate();
        LocalDate kotakStartDate = kotakRecords.get(0).getValueDate();
        LocalDate startDate;
        if (iciciStartDate.isAfter(kotakStartDate)) {
            startDate = iciciStartDate;
        } else {
            startDate = kotakStartDate;
        }

        // decide end date
        LocalDate endDate;
        LocalDate iciciEndDate = iciciRecords.get(iciciRecords.size() - 1).getValueDate();
        LocalDate kotakEndDate = kotakRecords.get(kotakRecords.size() - 1).getValueDate();
        if (iciciEndDate.isAfter(kotakEndDate)) {
            endDate = kotakEndDate;
        } else {
            endDate = iciciEndDate;
        }

        List<Balance> balances = new ArrayList<>();
        List<Record> records = getRecords();

        Map<LocalDate, List<Record>> iciciRecordsByDate = records.stream()
                .filter(record -> record.getBank().equalsIgnoreCase("ICICI"))
                .collect(Collectors.groupingBy(Record::getValueDate));

        Map<LocalDate, List<Record>> kotakRecordsByDate = records.stream()
                .filter(record -> record.getBank().equalsIgnoreCase("Kotak"))
                .collect(Collectors.groupingBy(Record::getValueDate));

        Double iciciBalance = 0.0;
        Double kotakBalance = 0.0;

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {

            List<Record> iciciRecordsForDay = iciciRecordsByDate.get(date);
            if (iciciRecordsForDay != null && iciciRecordsForDay.size() > 0) {
                Record record = iciciRecordsForDay.get(iciciRecordsForDay.size() - 1);
                iciciBalance = record.getBalance();
            }

            List<Record> kotakRecordsForDay = kotakRecordsByDate.get(date);
            if (kotakRecordsForDay != null && kotakRecordsForDay.size() > 0) {
                Record record = kotakRecordsForDay.get(kotakRecordsForDay.size() - 1);
                kotakBalance = record.getBalance();
            }

            Double balanceForDay = iciciBalance + kotakBalance;
            balances.add(new Balance(date, balanceForDay));
        }

        return balances;
    }

    private Double getSavings(List<Record> records) {
        Double savings = 0.0;
        for (Record record : records) {
            Double debit = record.getDebit();
            Double credit = record.getCredit();
            if (debit == 100000.0 || credit == 100000.0) {
                continue;
            }
            if (debit == 50000.0 || credit == 50000.0) {
                continue;
            }
            if (debit == 150000.0 || credit == 150000.0) {
                continue;
            }
            if (debit == 200000.0 || credit == 200000.0) {
                continue;
            }
            savings -= debit;
            savings += credit;
        }
        return savings;
    }

    private Double getExpense(List<Record> records) {
        Double expense = 0.0;
        for (Record record : records) {
            Double debit = record.getDebit();
            if (debit >= 100000.0) {
                continue;
            }
            expense += debit;
        }
        return expense;
    }
}

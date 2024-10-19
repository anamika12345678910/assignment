package com.csv.service;

import com.csv.model.CompanyData;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvProcessingService {

    private static Map<String, CompanyData> employees = new HashMap<>();
    private static Map<String, List<CompanyData>> managerMap = new HashMap<>();
    private static String ceoName;

    public static void main(String[] args) throws ParseException {
        String csvFile = "./src/main/resources/input.csv"; // Update the path as needed
        loadEmployees(csvFile);
        analyzeSalaries();
        identifyLongReportingLines();
    }
    public static void loadEmployees(String csvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String id = data[0];
                String firstName = data[1];
                String lastName = data[2];
               // String manager = data.length > 3 ? data[4] : null; // CEO has no manager
                String manager = data.length > 4 ? data[4] : null;
                // double salary = Double.parseDouble(data[3]);
               // double salary = new Double(data[3]).doubleValue();
                String salary=data[3];
               // double y=salary;

                CompanyData emp = new CompanyData(id, firstName, lastName,salary,manager);
                employees.put(id, emp);


                if (manager != null) {
                    managerMap.computeIfAbsent(manager, k -> new ArrayList<>()).add(emp);

                } else {
                    ceoName = id; // Store CEO's name
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void analyzeSalaries() throws ParseException {
        List<String> underpaidManagers = new ArrayList<>();
        List<String> overpaidManagers = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");

        for (Map.Entry<String, List<CompanyData>> entry : managerMap.entrySet()) {

            String managerName = entry.getKey();
            List<CompanyData> subordinates = entry.getValue();

            if (subordinates.isEmpty()) continue;

            double totalSalary = 0;
            for (CompanyData subordinate : subordinates) {

                if (!subordinate.getSalary().equals("salary")) {
                    totalSalary = DecimalFormat.getNumberInstance().parse(subordinate.getSalary()).doubleValue();

                }
            }
            double averageSalary = totalSalary / subordinates.size();

            if (!managerName.equals("managerId")) {
                CompanyData manager = employees.get(managerName);
                double minRequiredSalary = averageSalary * 1.2;
                double maxAllowedSalary = averageSalary * 1.5;
                if (!manager.getSalary().equals("salary")) {
                    if (Double.valueOf(manager.getSalary()) < minRequiredSalary) {
                        double deficit = minRequiredSalary - Double.valueOf(manager.getSalary());
                        underpaidManagers.add(managerName + " is underpaid by " + deficit);
                    } else if (Double.valueOf(manager.getSalary()) > maxAllowedSalary) {
                        double excess = Double.valueOf(manager.getSalary()) - maxAllowedSalary;
                        overpaidManagers.add(managerName + " is overpaid by " + excess);
                    }
                }
            }
        }

        // Output results
        System.out.println("Underpaid Managers:");
        underpaidManagers.forEach(System.out::println);
        System.out.println("\nOverpaid Managers:");
        overpaidManagers.forEach(System.out::println);
    }

    private static void identifyLongReportingLines() {
        List<String> longReportingLines = new ArrayList<>();
        for (CompanyData emp : employees.values()) {
            if (emp.getManagerId() != null) {
                int depth = getReportingDepth(emp.getManagerId());
                if (depth > 2) {
                    longReportingLines.add(emp.getId() + " has a reporting line too long: " + depth);
                }
            }
        }

        // Output results
        System.out.println("\nEmployees with Long Reporting Lines:");
        longReportingLines.forEach(System.out::println);
    }

    private static int getReportingDepth(String managerName) {
        int depth = 0;
        while (managerName != null) {
            CompanyData manager = employees.get(managerName);
            if (manager == null) break; // In case of invalid data
            managerName = manager.getManagerId();
            depth++;
        }
        return depth;
    }
}






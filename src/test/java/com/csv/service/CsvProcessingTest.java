package com.csv.service;

import com.opencsv.CSVReader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class CsvProcessingTest {
    @InjectMocks
    CsvProcessingService someManager;
    @Test
   public void testReadCsv() throws IOException, FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/input.csv"));
            String line;
            List<String> lines=new ArrayList<>();
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        assertNotNull(lines);
        assertFalse(lines.isEmpty());
            Assert.assertEquals("Id,firstName,lastName,salary,managerId",lines.get(0));
      //  assert
       // assertArrayEquals({"Id","firstName","lastName","salary","managerId"}, lines.get(0));
        CsvProcessingService.loadEmployees("./src/main/resources/input.csv");
        // Add more assertions as needed based on your CSV content
    }
}

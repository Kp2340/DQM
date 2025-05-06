package DQM_backend.Service;

import DQM_backend.Model.Log;
import DQM_backend.Repository.LogRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LogService {
    @Autowired
    private LogRepository repository;

    MultipartFile file;
    Workbook workbook;
    Sheet sheet;
    Row headerRow;
    List<String> columns;
    String filename, attributes, formatCheck;
    double omissionRate, comissionRate, errorRate;
    private static final List<String> test = new ArrayList<>(Arrays.asList("NOT PROVIDED", "NOT AVAILABLE", "NULL", "N/A", "N.A.","NA", "", null));

//    @Autowired
//    private DefaultErrorAttributes defaultErrorAttributes;

    //Upload File
    public List<String> extractColumns(MultipartFile uploadedfile) throws IOException {
        file = uploadedfile;
        filename = uploadedfile.getOriginalFilename();
        workbook = WorkbookFactory.create(file.getInputStream());
        sheet = workbook.getSheetAt(0);
        headerRow = sheet.getRow(0);
        columns = new ArrayList<>();
        for (Cell cell : headerRow) {
            if(cell.getCellType()!=CellType.BLANK) columns.add(cell.getStringCellValue());
        }
        workbook.close();
        return columns;
    }


    //Calculate Completeness
    public Map<String, Double> calculateCompleteness(String selectedColumns) throws IOException {
        this.attributes = selectedColumns;
        List<Integer> x = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (selectedColumns.contains(columns.get(i))) {
                x.add(i);
            }
        }

        // Calculate omission rates
        double totalRows = sheet.getPhysicalNumberOfRows() - 1, missingCount = 0;
        for (int i = 1; i <= totalRows; i++) {
            headerRow = sheet.getRow(i);
            for (int j : x) {
                if (headerRow.getCell(j).getCellType() == CellType.STRING) {
                    String cellValue = headerRow.getCell(j).getStringCellValue();
                    if (test.contains(cellValue.toUpperCase())) missingCount++;
                    else if (cellValue.length() == 1) {
                        if (!((cellValue.charAt(0) >= 97 && cellValue.charAt(0) <= 122) ||
                                (cellValue.charAt(0) >= 65 && cellValue.charAt(0) <= 90))) missingCount++;
                    }
                } else if (headerRow.getCell(j).getCellType() == CellType.NUMERIC) {
                    double cellValue = headerRow.getCell(j).getNumericCellValue();
                    if (cellValue == 0) missingCount++;
                } else if (headerRow.getCell(j).getCellType() == CellType.BLANK) missingCount++;
            }
        }
        omissionRate = (double) Math.round(10000 * missingCount / totalRows / x.size()) / 100;
        comissionRate = (double) Math.round(10000.0 * x.size() / columns.size()) / 100;
        workbook.close();
        Map<String, Double> rate = new HashMap<>();
        rate.put("omissionRate", omissionRate);
        rate.put("comissionRate", comissionRate);
        return rate;
    }
    //save Completeness
    public void saveCompleteness() {
        repository.save(new Log(filename, attributes, omissionRate, comissionRate, LocalDateTime.now()));
    }



    //Get All Logs
    public List<Log> getAllLogs() {
        return repository.findAll();
    }


    //Delete Logs By Id
    public void deleteLogById(Long id) {
        repository.deleteById(id);
    }


    //Delete All Logs
    public void deleteLogAll() {
        repository.deleteAll();
    }
}
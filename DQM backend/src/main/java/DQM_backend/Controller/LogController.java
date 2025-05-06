package DQM_backend.Controller;
import DQM_backend.Model.Log;
import DQM_backend.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class LogController {

    @Autowired
    private LogService service;
    //Upload File
    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadCompletenessFile(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        return ResponseEntity.ok(service.extractColumns(file));
    }
    //Calculate CompletenessCheck
    @PostMapping("/calculate/c")
    public Map<String, Double> calculateCompleteness(@RequestParam("columns") String columnsJson) throws IOException {
        return service.calculateCompleteness(columnsJson);
    }
    //Save Completeness Log
    @PostMapping("/save/c")
    public void saveCompleteness() {
        service.saveCompleteness();
    }
    //Get Logs
    @GetMapping("/all/c")
    public List<Log> getAllLogs() {
        return service.getAllLogs();
    }
    //Delete Log By Id
    @DeleteMapping("/delete/c/{id}")
    public ResponseEntity<String> deleteLogById(@PathVariable Long id) {
        try {
            service.deleteLogById(id);
            return new ResponseEntity<>("Log entry deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    //Delete All Logs
    @DeleteMapping("/delete/c/all")
    public ResponseEntity<String> deleteLogAll() {
        try {
            service.deleteLogAll();
            return new ResponseEntity<>("All Logs deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
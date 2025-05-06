package DQM_backend.Controller;

import DQM_backend.Model.Format;
import DQM_backend.Service.FormatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class FormatController {
    @Autowired
    private FormatService servicef;
    //Upload File
    @PostMapping("/upload/f")
    public ResponseEntity<List<String>> uploadFormatFile(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        return ResponseEntity.ok(servicef.extractColumnNames(file));
    }
    //Calculate FormatCheck
    @PostMapping("/calculate/date")
    public Map<String, Double> calculateDateFormat(@RequestParam("columns") String columnsJson) throws Exception {
        return servicef.calculateDateFormat(columnsJson);
    }
    @PostMapping("/calculate/file")
    public Map<String, Boolean> calculateFileFormat(@RequestParam("columns") String columnsJson) throws Exception {
        return servicef.calculateFileFormat(columnsJson);
    }
    @PostMapping("/calculate/stationcode")
    public Map<String, Double> calculateStationCode(@RequestParam("columns") String columnsJson) throws Exception {
        return servicef.calculateStationcodeFormat(columnsJson);
    }
    @PostMapping("/calculate/latlong")
    public Map<String, Double> calculateLatlong(@RequestParam("columns") String columnsJson) throws Exception {
        return servicef.calculateLatlongFormat(columnsJson);
    }
    @PostMapping("/calculate/railwaycode")
    public Map<String, Double> calculateRailwaycode(@RequestParam("columns") String columnsJson) throws Exception {
        return servicef.calculateRailwaycodeFormat(columnsJson);
    }
    @PostMapping("/calculate/pincode")
    public Map<String, Double> calculatePincode(@RequestParam("columns") String columnsJson) throws Exception {
        return servicef.calculatePincodeFormat(columnsJson);
    }
    @PostMapping("/calculate/state")
    public Map<String, Double> calculateState(@RequestParam("columns") String columnsJson) throws Exception {
        return servicef.calculateStateFormat(columnsJson);
    }
    @PostMapping("/calculate/district")
    public Map<String, Double> calculateDistrice(@RequestParam("columns") String columnsJson) throws Exception {
        return servicef.calculateDistrictFormat(columnsJson);
    }
    @PostMapping("/calculate/unionterritories")
    public Map<String, Double> calculateUnionterritories(@RequestParam("columns") String columnsJson) throws Exception {
        return servicef.calculateUnionterritoriesFormat(columnsJson);
    }
    @PostMapping("/calculate/phonenum")
    public Map<String, Double> calculatePhoneNo(@RequestParam("columns") String columnsJson) throws Exception {
        return servicef.calculatePhonenumFormat(columnsJson);
    }
    //Save Format
    @PostMapping("/save/{format}")
    public void saveFormat(@PathVariable String format) {servicef.saveFormat(format);}
    //Get Format Methods
    @GetMapping("/all/{format}")
    public List<Format> getByFormat(@PathVariable String format){ return servicef.getByFormat(format);}
    @GetMapping("/all/f/a")
    public List<Format> getAllFormats(){ return servicef.getAllFormats();}
    //Delete by Id  methods
    @DeleteMapping("/delete/f/{id}")
    public ResponseEntity<String> deleteFormatById(@PathVariable Long id) {
        try {
            servicef.deleteFormatById(id);
            return new ResponseEntity<>("Log entry deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    //Delete All Format By FormatCheck
    @DeleteMapping("/delete/f/all/{formatCheck}")
    public ResponseEntity<String> deleteFormatByFormatCheck(@PathVariable String formatCheck) {
        try {
            servicef.deleteFormatByFormatCheck(formatCheck);
            return new ResponseEntity<>("All Logs deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}

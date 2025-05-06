package DQM_backend.Controller;

import DQM_backend.Model.Domain;
import DQM_backend.Service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class DomainController {
    @Autowired
    private DomainService serviced;
    //Upload File
    @PostMapping("/upload/d")
    public ResponseEntity<List<String>> uploadDomainFile(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        return ResponseEntity.ok(serviced.extractColumns(file));
    }
    //Selected Rows for Domain Check
    @PostMapping("/findrange")
    public Object findRange(@RequestParam("col") String col, @RequestParam("type") String type) throws Exception {
        return serviced.getRangeForDomain(col, type);
    }
    //Calculate Domain Consistency
    @PostMapping("/calculate/d/all")
    public double[] calculateDomain(@RequestParam("min") double min, @RequestParam("max") double max, @RequestParam("index") int index) throws Exception {
        return serviced.calculateDomain(min, max, index);
    }
    @PostMapping("/calculate/d/list")
    public double[] calculateDomainList(@RequestParam("index") int index, @RequestParam("value") Object value) throws Exception {
        return serviced.calculateDomainForList(index, value.toString());
    }


    //Save Domain
    @PostMapping("save/d")
    public void saveDomain(){
        serviced.saveDomain();
    }
    //Get Domain
    @GetMapping("/all/d")
    public List<Domain> getAllDomains() {
        return serviced.getAllDomains();
    }

    //Delete Domain By Id
    @DeleteMapping("/delete/d/{id}")
    public ResponseEntity<String> deleteDomainById(@PathVariable Long id) {
        try {
            serviced.deleteDomainById(id);
            return new ResponseEntity<>("Log entry deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    //Delete All Domains
    @DeleteMapping("delete/d/all")
    public ResponseEntity<String> deleteDomainAll() {
        try {
            serviced.deleteDomainAll();
            return new ResponseEntity<>("All Logs deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}

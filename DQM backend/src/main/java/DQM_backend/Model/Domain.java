package DQM_backend.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName, attribute, type;
    private String min, max, inconsistency;
    private String average;
//    private String data;
//    private String formatCheck;
    private LocalDateTime uploadDateTime;
    public Domain() {}
    public Domain(String fileName, String attribute, String type, String min, String max, String inconsistency, String average, LocalDateTime uploadDateTime) {
        this.fileName = fileName;
        this.attribute = attribute;
        this.type = type;
        this.min=min;
        this.max=max;
        this.inconsistency = inconsistency;
        this.average=average;
//        this.formatCheck = formatCheck;
//        if(formatCheck.equals("list")) this.data = data;
        this.uploadDateTime = uploadDateTime;
    }
}

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
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private String attributes;
    private Double omissionRate;
    private Double comissionRate;
    private LocalDateTime uploadDateTime;
    public Log(){}
    public Log(String fileName, String attributes,Double omissionRate, Double comissionRate, LocalDateTime uploadDateTime) {
        this.filename = fileName;
        this.attributes = attributes;
        this.omissionRate = omissionRate;
        this.comissionRate = comissionRate;
        this.uploadDateTime = uploadDateTime;
//        this.uploadDateTime = LocalDateTime.now();
    }
}

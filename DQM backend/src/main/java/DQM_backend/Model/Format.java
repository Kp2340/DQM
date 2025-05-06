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
public class Format {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String attributes;
    private Double errorRate;
    private String data;
    private String formatCheck;
    private LocalDateTime uploadDateTime;
    public Format() {}
    public Format(String fileName, String attributes, Double errorRate, String data, String formatCheck, LocalDateTime uploadDateTime) {
        this.fileName = fileName;
        this.attributes = attributes;
        this.errorRate = errorRate;
        this.formatCheck = formatCheck;
        if(formatCheck.equals("file")) this.data = data;
        this.uploadDateTime = uploadDateTime;
    }
}

package DQM_backend.Repository;

import DQM_backend.Model.Format;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormatRepository extends JpaRepository<Format, Long> {
    List<Format> findByFormatCheck(String format);

    @Transactional
    void deleteByFormatCheck(String formatCheck);
}
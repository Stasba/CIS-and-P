// VotingApiApp.java
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.context.annotation.*;
import org.springframework.cache.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.http.*;
import javax.persistence.*;
import java.util.*;
import java.io.*;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class VotingApiApp {
    public static void main(String[] args) {
        SpringApplication.run(VotingApiApp.class, args);
    }

    // ======= Сущность =======
    @Entity
    class Result {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String votingTitle;
        private String candidate;
        private int votes;
        private boolean readOnly;

        public Long getId() { return id; }
        public String getVotingTitle() { return votingTitle; }
        public String getCandidate() { return candidate; }
        public int getVotes() { return votes; }
        public boolean isReadOnly() { return readOnly; }

        public void setId(Long id) { this.id = id; }
        public void setVotingTitle(String votingTitle) { this.votingTitle = votingTitle; }
        public void setCandidate(String candidate) { this.candidate = candidate; }
        public void setVotes(int votes) { this.votes = votes; }
        public void setReadOnly(boolean readOnly) { this.readOnly = readOnly; }
    }

    // ======= Репозиторий =======
    interface ResultRepository extends JpaRepository<Result, Long> {
        List<Result> findByReadOnlyTrue();
        List<Result> findByReadOnlyFalse();
    }

    // ======= Сервис =======
    @Service
    class ResultService {
        private final ResultRepository repo;

        public ResultService(ResultRepository repo) {
            this.repo = repo;
        }

        @Cacheable("readonly")
        public List<Result> getReadOnlyResults() {
            return repo.findByReadOnlyTrue();
        }

        public List<Result> getLiveResults() {
            return repo.findByReadOnlyFalse();
        }

        public Result save(Result r) {
            return repo.save(r);
        }

        public void exportResults(String filename) throws IOException {
            List<Result> all = repo.findAll();
            try (PrintWriter out = new PrintWriter(new File(filename))) {
                for (Result r : all) {
                    out.printf("Голосование: %s | Кандидат: %s | Голоса: %d | ReadOnly: %s%n",
                            r.getVotingTitle(), r.getCandidate(), r.getVotes(), r.isReadOnly());
                }
            }
        }
    }

    // ======= REST-Контроллер =======
    @RestController
    @RequestMapping("/api")
    class ResultController {
        private final ResultService service;

        public ResultController(ResultService service) {
            this.service = service;
        }

        @GetMapping("/readonly")
        public List<Result> getCached() {
            return service.getReadOnlyResults();
        }

        @GetMapping("/live")
        public List<Result> getLive() {
            return service.getLiveResults();
        }

        @PostMapping("/save")
        public Result save(@RequestBody Result result) {
            return service.save(result);
        }

        @GetMapping("/export")
        public ResponseEntity<String> export() {
            String file = "results_" + System.currentTimeMillis() + ".txt";
            try {
                service.exportResults(file);
                return ResponseEntity.ok("Результаты сохранены в файл: " + file);
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Ошибка сохранения: " + e.getMessage());
            }
        }
    }

    // ======= Обновление по таймеру =======
    @Component
    class TimerUpdater {
        private final ResultService service;

        public TimerUpdater(ResultService service) {
            this.service = service;
        }

        @Scheduled(fixedRate = 60000)
        public void refresh() {
            List<Result> dynamic = service.getLiveResults();
            System.out.println("Таймер: обновлены " + dynamic.size() + " динамических записей");
        }
    }

    // ======= Конфигурация H2 и кэша =======
    @Bean
    public CommandLineRunner init(ResultRepository repo) {
        return args -> {
            repo.save(create("Выборы 2024", "Иванов", 120, true));
            repo.save(create("Выборы 2024", "Петров", 95, false));
        };
    }

    private Result create(String t, String c, int v, boolean ro) {
        Result r = new Result();
        r.setVotingTitle(t); r.setCandidate(c); r.setVotes(v); r.setReadOnly(ro);
        return r;
    }
}

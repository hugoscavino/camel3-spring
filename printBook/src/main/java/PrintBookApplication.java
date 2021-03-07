import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@ComponentScan(basePackages="com.factory")
@EnableJpaRepositories(basePackages = "com.factory.*")
@EntityScan("com.factory.*")
public class PrintBookApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrintBookApplication.class, args);
    }

}
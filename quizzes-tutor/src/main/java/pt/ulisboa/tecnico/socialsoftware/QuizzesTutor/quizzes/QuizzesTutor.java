package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.EventService;

@PropertySource({"classpath:application.properties"})
@EnableJpaRepositories
@EnableTransactionManagement
@EnableJpaAuditing
@SpringBootApplication
@ComponentScan(basePackages = {"pt.ulisboa.tecnico.socialsoftware.ms"})
@EnableScheduling
public class QuizzesTutor implements InitializingBean {

	@Autowired
	private EventService eventService;

	public static void main(String[] args) {
		SpringApplication.run(QuizzesTutor.class, args);
	}

	@Override
	public void afterPropertiesSet() {
		// Run on startup
		eventService.clearEventsAtApplicationStartUp();
	}

}

package com.example.zzan;

import com.example.zzan.game.IdiomGameService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ZzanApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(ZzanApplication.class, args);
//    }
    public static void main(String[] args) {
        IdiomGameService idiomGameService = new IdiomGameService();
        idiomGameService.startGame();
    }
}

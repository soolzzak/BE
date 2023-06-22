package com.example.zzan.icebreaker.service;

import com.example.zzan.icebreaker.dto.IceBreakerDto;
import com.example.zzan.webRtc.rtc.SignalHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
@Slf4j
@Getter
public class IceBreakerService {
    private static final String ICE_BREAKER_QUESTION = "IceBreakerQuestion.txt";
    private final List<String> questions;
    private final ApplicationContext context;
    private String iceBreakerQuestion;

//    public void displayQuestion(Map<Long, WebSocketSession> iceBreaker) {
//        iceBreakerQuestion = getRandomQuestion();
//        SignalHandler signalHandler = context.getBean(SignalHandler.class);
//        IceBreakerDto iceBreakerDto = new IceBreakerDto(null, "iceBreaker", iceBreakerQuestion, null, null);
//        for (WebSocketSession session : iceBreaker.values()) {
//            signalHandler.iceBreakSendMessage(session, iceBreakerDto);
//        }
//    }

    public String getRandomQuestion() {
        Random random = new Random();
        int randomIndex = random.nextInt(questions.size());
        return questions.get(randomIndex);
    }

    public List<String> loadQuestionsFromFile() {
        List<String> questions = new ArrayList<>();
        try {
            InputStream inputStream = new ClassPathResource(ICE_BREAKER_QUESTION).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = StringUtils.trimWhitespace(line);
                if (!StringUtils.isEmpty(trimmedLine)) {
                    questions.add(trimmedLine);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public IceBreakerService(ApplicationContext context) {
        this.context = context;
        this.questions = loadQuestionsFromFile();
    }
}

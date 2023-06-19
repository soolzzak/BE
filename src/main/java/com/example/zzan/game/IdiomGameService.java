package com.example.zzan.game;

import com.example.zzan.game.dto.GameResponseDto;
import com.example.zzan.webRtc.rtc.SignalHandler;
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
public class IdiomGameService {
    private static final String WORDS_FILE_PATH = "4LetterIdiom.txt";
    private static final int INITIAL_DELAY_MS = 1000;       // 2000
    private static final int PARTIAL_WORD_DELAY_MS = 1000;      // 3000
    private static final int FULL_WORD_DELAY_MS = 1000;     // 6000
    private final List<String> idioms;
    private boolean gameRunning;
    private Timer gameTimer;
    private String currentIdiom;
    private final ApplicationContext context;

    //    @PostConstruct
    public IdiomGameService(ApplicationContext context) {
        this.context = context;
        this.idioms = loadIdiomsFromFile();
    }

    public void startGame(WebSocketSession session) {
        if (!gameRunning) {
            gameRunning = true;
            gameTimer = new Timer();

            schedulePartialWord(session);
            scheduleFullWordReveal(session);
            scheduleNextGame(session);
        }
    }

    public void stopGame() {
        if (gameRunning) {
            gameRunning = false;
            if (gameTimer != null) {
                gameTimer.cancel();
                gameTimer = null;
            }
        }
    }

    public void schedulePartialWord(WebSocketSession session) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameRunning) {
                    SignalHandler signalHandler = context.getBean(SignalHandler.class);
                    String partialWord = generatePartialWord();
                    log.info(partialWord);
                    GameResponseDto gameResponseDto = new GameResponseDto(null,"game", partialWord, null, null);
                    signalHandler.gameSendMessage(session, gameResponseDto);
                }
            }
        };
        gameTimer.schedule(task, INITIAL_DELAY_MS);
    }

    public void scheduleFullWordReveal(WebSocketSession session) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameRunning) {
                    SignalHandler signalHandler = context.getBean(SignalHandler.class);
                    String fullWord = currentIdiom;
                    log.info(fullWord);
                    GameResponseDto gameResponseDto = new GameResponseDto(null, null, fullWord, null, null);
                    signalHandler.gameSendMessage(session, gameResponseDto);
                }
            }
        };
        gameTimer.schedule(task, FULL_WORD_DELAY_MS);
    }

    public void scheduleNextGame(WebSocketSession session) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameRunning) {
                    stopGame();
                    log.info("게임 끝!");
                }
            }
        };
        gameTimer.schedule(task, PARTIAL_WORD_DELAY_MS);
    }

    public String generatePartialWord() {
        if (getRandomIdiom() != null && getRandomIdiom().length() >= 2) {
            return getRandomIdiom().substring(0, 2);
        }
        return getRandomIdiom();
    }

    public String getRandomIdiom() {
        Random random = new Random();
        int randomIndex = random.nextInt(idioms.size());
        currentIdiom = idioms.get(randomIndex);
        return currentIdiom;
    }

    public List<String> loadIdiomsFromFile() {
        List<String> idioms = new ArrayList<>();
        try {
            InputStream inputStream = new ClassPathResource(WORDS_FILE_PATH).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = StringUtils.trimWhitespace(line);
                if (!StringUtils.isEmpty(trimmedLine)) {
                    idioms.add(trimmedLine);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return idioms;
    }
}

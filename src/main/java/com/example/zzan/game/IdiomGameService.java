package com.example.zzan.game;

import com.example.zzan.game.dto.GameResponseDto;
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
public class IdiomGameService {
    private static final String WORDS_FILE_PATH = "4LetterIdiom.txt";
    private static final int INITIAL_DELAY_MS = 4000;
    private static final int PARTIAL_WORD_DELAY_MS = 8000;
    private static final int FULL_WORD_DELAY_MS = 10000;
    private final List<String> idioms;
    private boolean gameRunning;
    private boolean gamePaused;
    private Timer gameTimer;
    private String currentIdiom;
    private final ApplicationContext context;

    public IdiomGameService(ApplicationContext context) {
        this.context = context;
        this.idioms = loadIdiomsFromFile();
    }

    public void startGame(Map<Long, WebSocketSession> gamePlayers) {
        if (!gameRunning) {
            gameRunning = true;
            gamePaused = false;
            currentIdiom = getRandomIdiom();

            int num = 50;

            for (int i = 0; i < num; i++) {
                gameTimer = new Timer();
                countNumber6(gamePlayers);
                countNumber5(gamePlayers);
                countNumber4(gamePlayers);
                schedulePartialWord(gamePlayers);

                countNumber3(gamePlayers);
                countNumber2(gamePlayers);
                countNumber1(gamePlayers);
                scheduleFullWordReveal(gamePlayers);
            }
            scheduleNextGame(gamePlayers);
        }
    }

    public void resumeGame(Map<Long, WebSocketSession> gamePlayers) {
        if (gamePaused) {
            gameRunning = true;
            gamePaused = false;

            int num = 50 - idioms.size();

            for (int i = 0; i < num; i++) {
                gameTimer = new Timer();
                countNumber6(gamePlayers);
                countNumber5(gamePlayers);
                countNumber4(gamePlayers);
                schedulePartialWord(gamePlayers);

                countNumber3(gamePlayers);
                countNumber2(gamePlayers);
                countNumber1(gamePlayers);
                scheduleFullWordReveal(gamePlayers);
            }
            scheduleNextGame(gamePlayers);
        }
    }

    public void stopGame(Map<Long, WebSocketSession> gamePlayers) {
        if (gameRunning) {
            gameRunning = false;
            SignalHandler signalHandler = context.getBean(SignalHandler.class);
            GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", "게임 끝!", null, null);
            for (WebSocketSession session : gamePlayers.values()) {
                signalHandler.gameSendMessage(session, gameResponseDto);
            }
            idioms.clear();
        }
    }

    public void pauseGame(Map<Long, WebSocketSession> gamePlayers) {
        if (gameRunning && !gamePaused) {
            gamePaused = true;
            gameTimer.cancel();

            SignalHandler signalHandler = context.getBean(SignalHandler.class);
            GameResponseDto gameResponseDto = new GameResponseDto(null, "pauseGame", "게임 멈춤!", null, null);
            for (WebSocketSession session : gamePlayers.values()) {
                signalHandler.gameSendMessage(session, gameResponseDto);
            }
        } else if (gameRunning && gamePaused) {
            SignalHandler signalHandler = context.getBean(SignalHandler.class);
            GameResponseDto gameResponseDto = new GameResponseDto(null, "pauseGame", "게임 재시작!", null, null);
            for (WebSocketSession session : gamePlayers.values()) {
                signalHandler.gameSendMessage(session, gameResponseDto);
            }
            resumeGame(gamePlayers);
        }
    }

    public void schedulePartialWord(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameRunning) {
                    SignalHandler signalHandler = context.getBean(SignalHandler.class);
                    String partialWord = generatePartialWord();
                    GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", partialWord, null, null);
                    for (WebSocketSession session : gamePlayers.values()) {
                        signalHandler.gameSendMessage(session, gameResponseDto);
                    }
                }
            }
        };
        gameTimer.schedule(task, INITIAL_DELAY_MS);
    }

    public void scheduleFullWordReveal(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameRunning) {
                    SignalHandler signalHandler = context.getBean(SignalHandler.class);
                    String fullWord = currentIdiom;
                    GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", fullWord, null, null);
                    for (WebSocketSession session : gamePlayers.values()) {
                        signalHandler.gameSendMessage(session, gameResponseDto);
                    }
                }
            }
        };
        gameTimer.schedule(task, PARTIAL_WORD_DELAY_MS);
    }

    public void scheduleNextGame(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameRunning) {
                    stopGame(gamePlayers);
                }
            }
        };
        gameTimer.schedule(task, FULL_WORD_DELAY_MS);
    }

    public String generatePartialWord() {
        if (currentIdiom != null && currentIdiom.length() >= 2) {
            return currentIdiom.substring(0, 2);
        }
        return currentIdiom;
    }

    public String getRandomIdiom() {
        Random random = new Random();
        int randomIndex = random.nextInt(idioms.size());
        return idioms.get(randomIndex);
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

    public void countNumber6(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SignalHandler signalHandler = context.getBean(SignalHandler.class);
                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", 3, null, null);
                for (WebSocketSession session : gamePlayers.values()) {
                    signalHandler.gameSendMessage(session, gameResponseDto);
                }
            }
        };
        gameTimer.schedule(task, 1000);
    }

    public void countNumber5(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SignalHandler signalHandler = context.getBean(SignalHandler.class);
                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", 2, null, null);
                for (WebSocketSession session : gamePlayers.values()) {
                    signalHandler.gameSendMessage(session, gameResponseDto);
                }
            }
        };
        gameTimer.schedule(task, 2000);
    }

    public void countNumber4(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SignalHandler signalHandler = context.getBean(SignalHandler.class);
                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", 1, null, null);
                for (WebSocketSession session : gamePlayers.values()) {
                    signalHandler.gameSendMessage(session, gameResponseDto);
                }
            }
        };
        gameTimer.schedule(task, 3000);
    }

    public void countNumber3(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SignalHandler signalHandler = context.getBean(SignalHandler.class);
                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", 3, null, null);
                for (WebSocketSession session : gamePlayers.values()) {
                    signalHandler.gameSendMessage(session, gameResponseDto);
                }
            }
        };
        gameTimer.schedule(task, 5000);
    }

    public void countNumber2(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SignalHandler signalHandler = context.getBean(SignalHandler.class);
                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", 2, null, null);
                for (WebSocketSession session : gamePlayers.values()) {
                    signalHandler.gameSendMessage(session, gameResponseDto);
                }
            }
        };
        gameTimer.schedule(task, 6000);
    }

    public void countNumber1(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SignalHandler signalHandler = context.getBean(SignalHandler.class);
                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", 1, null, null);
                for (WebSocketSession session : gamePlayers.values()) {
                    signalHandler.gameSendMessage(session, gameResponseDto);
                }
            }
        };
        gameTimer.schedule(task, 7000);
    }
}

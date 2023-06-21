package com.example.zzan.game.service;

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
public class GameService {
    private static final String WORDS_FILE_PATH = "4LetterWords.txt";
    private static final int INITIAL_DELAY_MS = 2000;
    private static final int PARTIAL_WORD_DELAY_MS = 5000;
    private static final int FULL_WORD_DELAY_MS = 6000;
    private final List<String> words;
    private boolean gameRunning;
    private boolean gamePaused;
    private Timer gameTimer;
    private String currentWord;
    private final ApplicationContext context;

    public GameService(ApplicationContext context) {
        this.context = context;
        this.words = loadWordsFromFile();
    }

    public void startGame(Map<Long, WebSocketSession> gamePlayers) {
        if (!gameRunning) {
            gameRunning = true;
            gamePaused = false;

            if (gameTimer != null) {
                gameTimer.cancel();
            }

            TimerTask gameTask = new TimerTask() {
                int count = 1;

                @Override
                public void run() {
                    currentWord = getRandomWord();

//                    countNumberThree(gamePlayers);
//                    countNumberTwo(gamePlayers);
//                    countNumberOne(gamePlayers);

                    schedulePartialWord(gamePlayers);

                    countNumberTwoWithWord(gamePlayers);
                    countNumberOneWithWord(gamePlayers);

                    scheduleFullWordReveal(gamePlayers);

                    count++;

                    if (count >= 50) {
                        cancel();
                        scheduleNextGame(gamePlayers);
                    }
                }
            };
            gameTimer = new Timer();
            gameTimer.schedule(gameTask, 0, FULL_WORD_DELAY_MS);
        }
    }

    public void resumeGame(Map<Long, WebSocketSession> gamePlayers) {
        if (gamePaused) {
            gameRunning = true;
            gamePaused = false;

            int num = gameTimer.purge();

            TimerTask gameTask = new TimerTask() {
                int count = num;

                @Override
                public void run() {
                    currentWord = getRandomWord();

//                    countNumberThree(gamePlayers);
//                    countNumberTwo(gamePlayers);
//                    countNumberOne(gamePlayers);

                    schedulePartialWord(gamePlayers);

                    countNumberTwoWithWord(gamePlayers);
                    countNumberOneWithWord(gamePlayers);

                    scheduleFullWordReveal(gamePlayers);

                    count++;

                    if (count >= 50) {
                        cancel();
                        scheduleNextGame(gamePlayers);
                    }
                }
            };
            gameTimer = new Timer();
            gameTimer.schedule(gameTask, 0, FULL_WORD_DELAY_MS);
        }
    }

    public void stopGame(Map<Long, WebSocketSession> gamePlayers) {
        if (gameRunning) {
            gameRunning = false;
            gamePaused = false;
            gameTimer.cancel();
            SignalHandler signalHandler = context.getBean(SignalHandler.class);
            GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", "게임 끝!", null, null);
            for (WebSocketSession session : gamePlayers.values()) {
                signalHandler.gameSendMessage(session, gameResponseDto);
            }
        }
    }

    public void finishGame(Map<Long, WebSocketSession> gamePlayers) {
        if (gameRunning) {
            gameRunning = false;
            gamePaused = false;
            gameTimer.cancel();
            SignalHandler signalHandler = context.getBean(SignalHandler.class);
            GameResponseDto gameResponseDto = new GameResponseDto(null, "stopGame", "게임 끝!", null, null);
            for (WebSocketSession session : gamePlayers.values()) {
                signalHandler.gameSendMessage(session, gameResponseDto);
            }
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
                    GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", partialWord, 3, null, null);
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
                    String fullWord = currentWord;
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
        if (currentWord != null && currentWord.length() >= 2) {
            return currentWord.substring(0, 2);
        }
        return currentWord;
    }

    public String getRandomWord() {
        Random random = new Random();
        int randomIndex = random.nextInt(words.size());
        return words.get(randomIndex);
    }

    public List<String> loadWordsFromFile() {
        List<String> words = new ArrayList<>();
        try {
            InputStream inputStream = new ClassPathResource(WORDS_FILE_PATH).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = StringUtils.trimWhitespace(line);
                if (!StringUtils.isEmpty(trimmedLine)) {
                    words.add(trimmedLine);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

//    public void countNumberThree(Map<Long, WebSocketSession> gamePlayers) {
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                SignalHandler signalHandler = context.getBean(SignalHandler.class);
//                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", 3, null, null);
//                for (WebSocketSession session : gamePlayers.values()) {
//                    signalHandler.gameSendMessage(session, gameResponseDto);
//                }
//            }
//        };
//        gameTimer.schedule(task, 1000);
//    }
//
//    public void countNumberTwo(Map<Long, WebSocketSession> gamePlayers) {
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                SignalHandler signalHandler = context.getBean(SignalHandler.class);
//                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", 2, null, null);
//                for (WebSocketSession session : gamePlayers.values()) {
//                    signalHandler.gameSendMessage(session, gameResponseDto);
//                }
//            }
//        };
//        gameTimer.schedule(task, 2000);
//    }
//
//    public void countNumberOne(Map<Long, WebSocketSession> gamePlayers) {
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                SignalHandler signalHandler = context.getBean(SignalHandler.class);
//                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", 1, null, null);
//                for (WebSocketSession session : gamePlayers.values()) {
//                    signalHandler.gameSendMessage(session, gameResponseDto);
//                }
//            }
//        };
//        gameTimer.schedule(task, 3000);
//    }

    public void countNumberTwoWithWord(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SignalHandler signalHandler = context.getBean(SignalHandler.class);
                String partialWord = generatePartialWord();
                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", partialWord, 2, null, null);
                for (WebSocketSession session : gamePlayers.values()) {
                    signalHandler.gameSendMessage(session, gameResponseDto);
                }
            }
        };
        gameTimer.schedule(task, 3000);
    }

    public void countNumberOneWithWord(Map<Long, WebSocketSession> gamePlayers) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SignalHandler signalHandler = context.getBean(SignalHandler.class);
                String partialWord = generatePartialWord();
                GameResponseDto gameResponseDto = new GameResponseDto(null, "startGame", partialWord, 1, null, null);
                for (WebSocketSession session : gamePlayers.values()) {
                    signalHandler.gameSendMessage(session, gameResponseDto);
                }
            }
        };
        gameTimer.schedule(task, 4000);
    }
}

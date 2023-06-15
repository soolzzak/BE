package com.example.zzan.game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
@Slf4j
public class IdiomGameService {
    private static final String WORDS_FILE_PATH = "4LetterIdiom.txt";
    private static final int INITIAL_DELAY_MS = 2000;
    private static final int PARTIAL_WORD_DELAY_MS = 3000;
    private static final int FULL_WORD_DELAY_MS = 6000;

    private final List<String> idioms;
    private boolean gameRunning;
    private Timer gameTimer;

    private String currentIdiom;

    public IdiomGameService() {
        this.idioms = loadIdiomsFromFile();
    }

    public void startGame() {
        if (!gameRunning) {
            gameRunning = true;
            gameTimer = new Timer();

            schedulePartialWord();
            scheduleFullWordReveal();
            scheduleNextGame();
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

    private void schedulePartialWord() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameRunning) {
                    String partialWord = generatePartialWord();
                }
            }
        };
        gameTimer.schedule(task, INITIAL_DELAY_MS);
    }

    private void scheduleFullWordReveal() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameRunning) {
                    String fullWord = currentIdiom;
                }
            }
        };
        gameTimer.schedule(task, FULL_WORD_DELAY_MS);
    }

    private void scheduleNextGame() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameRunning) {
                    startGame();
                }
            }
        };
        gameTimer.schedule(task, PARTIAL_WORD_DELAY_MS);
    }

    private String generatePartialWord() {
        if (getRandomIdiom() != null && getRandomIdiom().length() >= 2) {
            return getRandomIdiom().substring(0, 2);
        }
        return getRandomIdiom();
    }

    private String getRandomIdiom() {
        Random random = new Random();
        int randomIndex = random.nextInt(idioms.size());
        currentIdiom = idioms.get(randomIndex);
        return currentIdiom;
    }

    private List<String> loadIdiomsFromFile() {
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

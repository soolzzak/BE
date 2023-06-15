//package com.example.zzan.game;
//
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//public class MusicGameService {
//
//    private final YouTubeApiService youTubeApiService;
//    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//
//    public MusicGameService(String filePath) throws IOException {
//        youTubeApiService = new YouTubeApiService(filePath);
//    }
//
//    public void startGame(WebSocketSession session) {
//        playRandomVideo(session);
//
//        // Schedule the game end
//        executorService.schedule(() -> endGame(session), GAME_DURATION_SECONDS, TimeUnit.SECONDS);
//    }
//
//    private void playRandomVideo(WebSocketSession session) {
//        String randomVideoUrl = youTubeApiService.getRandomMusicVideoUrl();
//
//        // Send the URL to the clients
//        try {
//            session.sendMessage(new TextMessage(randomVideoUrl));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void endGame(WebSocketSession session) {
//        // Send game end message to the clients
//        try {
//            session.sendMessage(new TextMessage("gameEnd"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static class YouTubeApiService {
//        private final List<String> youtubeUrls;
//
//        public YouTubeApiService(String filePath) throws IOException {
//            Path path = Paths.get(filePath);
//            youtubeUrls = Files.lines(path)
//                    .map(String::trim)
//                    .filter(url -> !url.isEmpty())
//                    .collect(Collectors.toList());
//        }
//
//        public String getRandomMusicVideoUrl() {
//            if (!youtubeUrls.isEmpty()) {
//                int randomIndex = ThreadLocalRandom.current().nextInt(youtubeUrls.size());
//                return youtubeUrls.get(randomIndex);
//            }
//
//            return ""; // Return an empty string if no URLs are available
//        }
//    }
//
//    private static final int GAME_DURATION_SECONDS = 10;
//}
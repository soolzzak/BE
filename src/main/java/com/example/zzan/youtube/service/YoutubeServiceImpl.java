// package com.example.zzan.youtube.service;
//
// package com.example.zzan.youtube.service;
//
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;
//
// import org.springframework.stereotype.Service;
//
// import com.gokchu.saye.repository.dto.Music;
// import com.google.api.client.googleapis.json.GoogleJsonResponseException;
// import com.google.api.client.http.HttpRequest;
// import com.google.api.client.http.HttpRequestInitializer;
// import com.google.api.client.http.HttpTransport;
// import com.google.api.client.http.javanet.NetHttpTransport;
// import com.google.api.client.json.JsonFactory;
// import com.google.api.client.json.jackson2.JacksonFactory;
// import com.google.api.services.youtube.YouTube;
// import com.google.api.services.youtube.model.ResourceId;
// import com.google.api.services.youtube.model.SearchListResponse;
// import com.google.api.services.youtube.model.SearchResult;
//
// @Service
// public class YoutubeServiceImpl implements YoutubeService {
//
// 	/** Global instance properties filename. */
// 	private static String PROPERTIES_FILENAME = "youtube.properties";
//
// 	/** Global instance of the HTTP transport. */
// 	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
//
// 	/** Global instance of the JSON factory. */
// 	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
//
// 	/**
// 	 * Global instance of the max number of videos we want returned (50 = upper
// 	 * limit per page).
// 	 */
// 	private static final long NUMBER_OF_VIDEOS_RETURNED = 1;
//
// 	/** Global instance of Youtube object to make all API requests. */
// 	private static YouTube youtube;
//
// 	/**
// 	 * Initializes YouTube object to search for videos on YouTube
// 	 * (Youtube.Search.List). The program then prints the names and thumbnails of
// 	 * each of the videos (only first 50 videos).
// 	 *
// 	 * @param args command line args.
// 	 * @throws IOException
// 	 */
//
// 	// youtube url 가져오기
// 	@Override
// 	public ArrayList<Music> selectUrlByTitle() {
// 		String musicurl = "";
// 		ArrayList<Music> result = new ArrayList<Music>();
//
// 		try {
//
// 			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
// 				public void initialize(HttpRequest request)
// 					throws IOException {
// 				}})
// 				.setApplicationName("youtube-cmdline-search-sample")
// 				.build();
// 			SearchListResponse searchResponse = null;
// 			YouTube.Search.List search = youtube.search().list("id,snippet");
// 			search.setKey("유튜브 API KEY");
// 			search.setType("video");
// 			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
// 			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
// 			search.setQ("키워드 입력");
// 			searchResponse = search.execute();
// 			System.out.println(temp.get(j).getmUrl());
// 			List<SearchResult> searchResultList = searchResponse.getItems();
//
// 			if (searchResultList != null) {
// 				SearchResult singleVideo = searchResultList.get(0);
// 				ResourceId rId = singleVideo.getId();
// 				musicurl = rId.getVideoId();
// 				temp.get(j).setmUrl("https://www.youtube.com/watch?v=" + musicurl);
// 				result.add(temp.get(j));
// 				System.out.println("\n-------------------------------------------------------------\n");
// 				//					musicurl = prettyPrint(searchResultList.iterator(), queryTerm);
//
// 			}
//
// 		} catch (GoogleJsonResponseException e) {
// 			j--;
// 			System.err.println(
// 				"There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
// 			continue;
// 		} catch (IOException e) {
// 			System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
// 			break;
// 		} catch (Throwable t) {
// 			// Use a logger to log this exception
// 			// t.printStackTrace();
// 			break;
// 		}
//
// 		return result;
// 	}
// }
//

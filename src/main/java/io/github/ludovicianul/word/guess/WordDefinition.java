package io.github.ludovicianul.word.guess;

import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

public final class WordDefinition {


  public static String getDefinition(String word, String language) {
    if (language.equalsIgnoreCase("en")) {
      return getEnglishDef(word);
    } else if (language.equalsIgnoreCase("ro")) {
      return getRomanianDef(word);
    }
    return "not available";
  }

  private static String getEnglishDef(String word) {
    String wordUrl = "https://www.dictionary.com/browse/" + word;
    try {
      Document document = Jsoup.parse(doGet(wordUrl));
      Elements elements = Xsoup.compile("//*[@id=\"base-pw\"]/main/section/section/div[1]/section[2]").evaluate(document).getElements();
      return elements.text() + " >> " + wordUrl;
    } catch (Exception e) {
      return "could not retrieve definition >> " + wordUrl;
    }
  }

  private static String doGet(String url) throws URISyntaxException, IOException, InterruptedException {
    HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).header("User-Agent", "Mozilla/5.0 (Mobile)").header("Accept", "*/*").GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    return response.body();
  }

  private static String getRomanianDef(String word) {
    try {
      List<String> results = new ArrayList<>();
      String definition = JsonPath.parse(doGet("https://dexonline.ro/definitie/" + word + "/json")).read("$['definitions'][0]['internalRep']");
      results.add(definition);
      results.add("https://dexonline.ro/definitie/" + word);
      return String.join(" >> ", results).replaceAll("[#$@\\n]+", "");
    } catch (Exception e) {
      return "could not retrieve definition";
    }
  }
}

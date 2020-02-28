package testelucene;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {
  private static final String LINK_REGEX = "<a\\s+href=\"([^\"]+)\"";

  private Set<String> paginasVisitadas;

  public WebCrawler() {
    paginasVisitadas = new HashSet<>();
  }

  private String readPage(String endereco) throws Exception {
    URL url = new URL(endereco);
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(url.openConnection().getInputStream()));
    StringBuilder strBuilder = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
      strBuilder.append(line + "\n");
    }
    reader.close();
    return strBuilder.toString();
  }

  private List<String> getLinks(String pagina) {
    List<String> links = new ArrayList<>();
    Matcher m = Pattern.compile(LINK_REGEX).matcher(pagina);
    while(m.find()) {
      links.add(m.group(1));
    }
    return links;
  }

  private void crawl(URL url, int nivel, int maxNiveis) throws Exception {
    if (nivel > maxNiveis) {
      return;
    }

    String urlPath = url.toString();

    if (paginasVisitadas.contains(urlPath)) {
      return;
    } else {
      paginasVisitadas.add(urlPath);
    }

    System.out.println("indexando a pagina " + urlPath);
    String pagina = readPage(urlPath);

    for (String link : getLinks(pagina)) {
      if (link.startsWith("/") && link.length() > 1) { // caminhos relativos
        crawl(new URL(url, link), nivel + 1, maxNiveis);
      } else if (link.startsWith("http")) { // caminhos absolutos
        crawl(new URL(link), nivel + 1, maxNiveis);
      }
    }
  }

  private void crawl(String url, int maxNiveis) {
    try {
      crawl(new URL(url), 0, maxNiveis);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static void main(String[] args) throws Exception {
    WebCrawler crawler = new WebCrawler();
    crawler.crawl("https://www.letras.mus.br/estilos/rock/", 1);
  }
}

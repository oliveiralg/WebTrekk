package rockpaperscissors;

import java.util.HashMap;
import java.util.Map;

public class MatchesRepository {

  private Map<String, Match> matches;

  public MatchesRepository() {
    Map<String, Match> matches = new HashMap<String, Match>();
    this.matches = matches;
  }

  public Map<String, Match> getMatches() {
    return matches;
  }

  public void setMatches(Map<String, Match> matches) {
    this.matches = matches;
  }
}

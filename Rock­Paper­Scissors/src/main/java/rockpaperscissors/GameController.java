package rockpaperscissors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/*
 * Game Controller listens for matches and responds with a message.
 *
 * @Author Leonardo Oliveira
 */
@Controller
public class GameController {

  private MatchesRepository matchesRepository = new MatchesRepository();

  private String getRandomHexString(Integer numchars) {
    Random r = new Random();
    StringBuffer sb = new StringBuffer();
    while (sb.length() < numchars) {
      sb.append(Integer.toHexString(r.nextInt()));
    }

    return sb.toString().substring(0, numchars);
  }

  private OutputMessage getWinner(Match matchObj, Message message, String matchId) {
    if (matchObj.getPlayer1Play().equals(matchObj.getPlayer2Play()))
      return new OutputMessage(message.getFrom(), matchId, "Finished",
          "Tie!!! " + matchObj.getPlayer1() + " played " + matchObj.getPlayer1Play().toString()
              + " and " + matchObj.getPlayer2() + " played " + matchObj.getPlayer2Play().toString(),
          "", new Date());
    else if (matchObj.getPlayer1Play().beats(matchObj.getPlayer2Play()))
      return new OutputMessage(message.getFrom(), matchId, "Finished",
          matchObj.getPlayer1() + " won. " + matchObj.getPlayer1() + " played "
              + matchObj.getPlayer1Play().toString() + " and " + matchObj.getPlayer2() + " played "
              + matchObj.getPlayer2Play().toString(),
          "", new Date());
    else
      return new OutputMessage(message.getFrom(), matchId, "Finished",
          matchObj.getPlayer2() + " won. " + matchObj.getPlayer1() + " played "
              + matchObj.getPlayer1Play().toString() + " and " + matchObj.getPlayer2() + " played "
              + matchObj.getPlayer2Play().toString(),
          "", new Date());
  }

  @MessageMapping("/game/{match}")
  @SendTo("/match/messages")
  public OutputMessage send(@DestinationVariable("match") String matchId, Message message)
      throws Exception {

    Map<String, Match> matches = matchesRepository.getMatches();
    Match matchObj = new Match();

    // If matchID is NEWMATCH or player is reconnecting or starting new match
    if (matchId.equals("NEWMATCH")) {
      // Computer playing... Get the first match missing player
      if (message.getFrom().equals("Computer")) {
        for (Map.Entry<String, Match> entry : matches.entrySet()) {
          Match match = entry.getValue();
          // Getting the first match waiting player
          if (match.getPlayer2() == null) {
            matchObj = match;
            matchObj.setPlayer2(message.getFrom());
            if (message.getFrom().equals("Computer"))
              matchObj.setPlayer2Play(Type.parseType(Integer.valueOf(new Random().nextInt(2) + 1)));
            // If not playing with 2 computer, wait Player choice
            if (!matchObj.getPlayer1().equals("Computer")
                || !matchObj.getPlayer2().equals("Computer"))
              return new OutputMessage(
                  message.getFrom(), match.getId(), "Start", "Players set. " + matchObj.getPlayer1()
                      + " and " + matchObj.getPlayer2() + ". Starting match!",
                  matchObj.getPlayer1(), new Date());
            else
              break;
          }
        }
      } else {
        // Try to find out any match started for the player
        for (Map.Entry<String, Match> entry : matches.entrySet()) {
          Match match = entry.getValue();
          if (Objects.equals(match.getPlayer1(), message.getFrom())
              || Objects.equals(match.getPlayer2(), message.getFrom())) {
            matchObj = match;
            break;
          }
        }
        // If Id is null the player has not a match already started
        if (matchObj.getId() == null) {
          for (Map.Entry<String, Match> entry : matches.entrySet()) {
            Match match = entry.getValue();
            // Getting the first match waiting player
            if (!match.getPlayer1().equals(message.getFrom()) && match.getPlayer2() == null) {
              matchObj = match;
              matchObj.setPlayer2(message.getFrom());
              if (message.getFrom().equals("Computer"))
                matchObj
                    .setPlayer2Play(Type.parseType(Integer.valueOf(new Random().nextInt(2) + 1)));
              return new OutputMessage(
                  message.getFrom(), match.getId(), "Start", "Players set. " + matchObj.getPlayer1()
                      + " and " + matchObj.getPlayer2() + ". Starting match!",
                  matchObj.getPlayer1(), new Date());
            }
          }
        }
      }
      // There is no match waiting player. Add to queue
      if (matchObj.getId() == null) {
        matchObj = new Match();
        matchObj.setId(getRandomHexString(10));
        matchObj.setPlayer1(message.getFrom());
        if (message.getFrom().equals("Computer"))
          matchObj.setPlayer1Play(Type.parseType(Integer.valueOf(new Random().nextInt(2) + 1)));
        matchObj.setPlayer2(null);
        List<Message> messages = new ArrayList<Message>();
        messages.add(message);
        matchObj.setMessages(messages);
      } else {
        // Match found for player and it is not finished. Reconnecting!
        if (!matchObj.getPlayer2().equals("Computer") && matchObj.getPlayer2() != null) {
          // Player reconnected
          return new OutputMessage(message.getFrom(), matchObj.getId(), "Reconnected",
              "Player " + message.getFrom() + " reconnected. Continuing match!",
              (Objects.equals(matchObj.getPlayer1(), message.getFrom()) ? matchObj.getPlayer2()
                  : matchObj.getPlayer1()),
              new Date());
        } else if (matchObj.getPlayer2().equals("Computer")
            && matchObj.getPlayer2().equals("Computer")) {
          return getWinner(matchObj, message, matchId);
        } else {
          return new OutputMessage(message.getFrom(), matchObj.getId(), "Waiting",
              "Waiting opponent to start match", "", new Date());
        }
      }
      matches.put(matchObj.getId(), matchObj);
      matchesRepository.setMatches(matches);
    } else

    {
      // New message for a match
      matchObj = matchesRepository.getMatches().get(matchId);
    }

    // Handle if match id was not found
    if (matchObj.getId() == null) {
      // Handle Match not found exception
      return new OutputMessage(message.getFrom(), matchId, "Error", "Match not found", "",
          new Date());
    } else if (message.getOption() != null && message.getOption() == 9) {
      // Player clicked disconnect button
      return new OutputMessage(message.getFrom(), matchObj.getId(), "Disconnect", "Player "
          + message.getFrom() + " was disconnected. Waiting opponent to continue the match", "",
          new Date());
    } else if (matchObj.getPlayer1() == null || matchObj.getPlayer2() == null) {
      // Still waiting opponent
      return new OutputMessage(message.getFrom(), matchObj.getId(), "Waiting",
          "Waiting opponent to start match", "", new Date());
    } else {
      matchObj.getMessages().add(message);

      if (message.getFrom().equals(matchObj.getPlayer1()))
        matchObj.setPlayer1Play(Type.parseType(message.getOption()));
      else if (message.getFrom().equals(matchObj.getPlayer2()))
        matchObj.setPlayer2Play(Type.parseType(message.getOption()));

      if (matchObj.getPlayer1Play() != null && matchObj.getPlayer2Play() != null) {
        // History
        matches.remove(matchId);
        matchesRepository.setMatches(matches);
        return getWinner(matchObj, message, matchId);
      } else {
        return new OutputMessage(message.getFrom(), matchObj.getId(), "Waiting",
            message.getFrom() + " already played!", "", new Date());
      }
    }
  }

}

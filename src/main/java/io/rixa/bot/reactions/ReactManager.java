package io.rixa.bot.reactions;

import io.rixa.bot.commands.exceptions.ReactNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class ReactManager {

  private Map<String, React> reactMap = new HashMap<>();


  private void registerReact(React react) {
    if (this.reactMap.containsKey(react.getName())) {
      return;
    }
    this.reactMap.put(react.getName(), react);
  }

  public void registerReact(React... reacts) {
    for (React react : reacts) {
      registerReact(react);
    }
  }

  public React getReaction(String react) throws ReactNotFoundException {
    if (reactMap.containsKey(react)) {
      return reactMap.get(react);
    }
    throw new ReactNotFoundException("Could not find reaction");
  }

  public Map<String, React> getAllReactions() {
    return this.reactMap;
  }
}

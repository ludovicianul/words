package io.github.ludovicianul.words.config;

import io.github.ludovicianul.words.definition.Language;
import io.github.ludovicianul.words.game.GameType;
import io.github.ludovicianul.words.game.stats.Stats;
import io.quarkus.runtime.annotations.RegisterForReflection;
import us.codecraft.xsoup.XElement;
import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.xevaluator.XEvaluators;
import us.codecraft.xsoup.xevaluator.XPathParser;

@RegisterForReflection(
    targets = {
      XElement.class,
      XElements.class,
      XPathParser.class,
      XEvaluators.class,
      Stats.class,
      GameType.class,
      Language.class
    })
public class ReflectionConfig {}

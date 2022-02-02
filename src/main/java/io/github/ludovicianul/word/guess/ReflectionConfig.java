package io.github.ludovicianul.word.guess;

import io.quarkus.runtime.annotations.RegisterForReflection;
import us.codecraft.xsoup.XElement;
import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.xevaluator.XEvaluators;
import us.codecraft.xsoup.xevaluator.XPathParser;

@RegisterForReflection(targets = {
    XElement.class, XElements.class, XPathParser.class, XEvaluators.class
})
public class ReflectionConfig {

}

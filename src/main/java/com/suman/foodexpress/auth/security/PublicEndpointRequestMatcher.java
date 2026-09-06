package com.suman.foodexpress.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class PublicEndpointRequestMatcher implements RequestMatcher {

  private final List<RequestMatcher> matchers = new ArrayList<>();

  public PublicEndpointRequestMatcher(RequestMappingHandlerMapping handlerMapping) {

    handlerMapping
        .getHandlerMethods()
        .forEach(
            (mapping, handlerMethod) -> {
              boolean isPublic =
                  handlerMethod.hasMethodAnnotation(Public.class)
                      || handlerMethod.getBeanType().isAnnotationPresent(Public.class);

              if (isPublic) {
                createMatchers(mapping);
              }
            });
  }

  @Override
  public boolean matches(HttpServletRequest request) {

    return matchers.stream().anyMatch(matcher -> matcher.matches(request));
  }

  private void createMatchers(RequestMappingInfo mapping) {

    for (String pattern : mapping.getPatternValues()) {

      var methods = mapping.getMethodsCondition().getMethods();

      if (methods.isEmpty()) {
        matchers.add(PathPatternRequestMatcher.withDefaults().matcher(pattern));

        continue;
      }

      for (RequestMethod method : methods) {

        matchers.add(
            PathPatternRequestMatcher.withDefaults()
                .matcher(HttpMethod.valueOf(method.name()), pattern));
      }
    }
  }
}

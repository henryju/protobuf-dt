/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.formatting;

import java.util.List;

import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.parser.IParseResult;
import org.junit.rules.MethodRule;
import org.junit.runners.model.*;

import com.google.eclipse.protobuf.junit.core.*;
import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CommentReaderRule implements MethodRule {
  private final Injector injector;

  private final CommentReader commentReader;
  private final ProtoParser protoParser;

  private ICompositeNode rootNode;
  private String expectedText;

  static CommentReaderRule overrideRuntimeModuleWith(Module...testModules) {
    ISetup setup = new OverrideRuntimeModuleSetup(testModules);
    Injector injector = setup.createInjectorAndDoEMFRegistration();
    return new CommentReaderRule(injector);
  }

  private CommentReaderRule(Injector injector) {
    this.injector = injector;
    commentReader = new CommentReader();
    protoParser = new ProtoParser(injector);
  }

  @Override public Statement apply(Statement base, FrameworkMethod method, Object target) {
    injector.injectMembers(target);
    rootNode = null;
    List<String> comments = commentReader.commentsIn(method);
    if (comments.size() == 2) {
      parseText(comments.get(0));
      expectedText = comments.get(1);
    }
    return base;
  }

  void parseText(String text) {
    IParseResult parseResult = protoParser.parseText(text);
    rootNode = parseResult.getRootNode();
  }

  ICompositeNode rootNode() {
    return rootNode;
  }

  String expectedText() {
    return expectedText;
  }
}

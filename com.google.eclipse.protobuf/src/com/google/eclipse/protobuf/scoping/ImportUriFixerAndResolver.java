/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.scoping.ImportUriFixer.PREFIX;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.inject.Inject;

/**
 * Resolves URIs. This implementation mimics how protoc understands imported file URIs. For example, the URI
 * "platform:/resource/proto1.proto" is understood by EMF but not by protoc. The URI in the proto file needs to be
 * simply "proto1.proto" for protoc to understand it.
 * <p>
 * This {@link ImportUriResolver} adds "platform:/resource" to any URI if is not specified, so EMF can find the
 * imported resource.
 * </p>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportUriFixerAndResolver extends ImportUriResolver {

  @Inject private ImportUriFixer uriFixer;
  
  /**
   * Prefix used by EMF for resource URIs: "platform:/resource/".
   */
  public static final String URI_PREFIX = PREFIX + "/";

  /**
   * If the given {@code EObject} is a <code>{@link Import}</code>, this method will add "platform:/resource" to the
   * URI of such import if not specified already.
   * @param from the given element to resolve.
   * @return the {@code String} representation of the given object's {@code URI}.
   */
  @Override public String apply(EObject from) {
    if (from instanceof Import) fixUri((Import) from);
    return super.apply(from);
  }

  private void fixUri(Import i) {
    String fixed = uriFixer.fixUri(i.getImportURI(), i.eResource().getURI());
    i.setImportURI(fixed);
  }
}

/*
 * Copyright (C) 2011.
 * All rights reserved.
 */
package ro.isdc.wro.examples.manager;

import ro.isdc.wro.extensions.processor.css.LessCssProcessor;
import ro.isdc.wro.manager.factory.standalone.DefaultStandaloneContextAwareManagerFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.SimpleProcessorsFactory;
import ro.isdc.wro.model.resource.processor.impl.css.CssImportPreProcessor;
import ro.isdc.wro.model.resource.processor.impl.css.CssUrlRewritingProcessor;

/**
 * @author Alex Objelean
 */
public class CustomStandaloneWroManagerFactory
    extends DefaultStandaloneContextAwareManagerFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  protected ProcessorsFactory newProcessorsFactory() {
    final SimpleProcessorsFactory factory = new SimpleProcessorsFactory();
    factory.addPreProcessor(new CssImportPreProcessor());
    factory.addPreProcessor(new CssUrlRewritingProcessor());
//    factory.addPreProcessor(new SemicolonAppenderPreProcessor());
//    factory.addPreProcessor(new JSMinProcessor());
//    factory.addPreProcessor(new YUICssCompressorProcessor());
//    factory.addPreProcessor(YUIJsCompressorProcessor.doMungeCompressor());

    factory.addPostProcessor(new LessCssProcessor());
//    factory.addPostProcessor(new CssVariablesProcessor());
    return factory;
  }
}

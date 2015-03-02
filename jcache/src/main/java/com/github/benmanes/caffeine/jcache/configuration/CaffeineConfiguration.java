/*
 * Copyright 2015 Ben Manes. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.benmanes.caffeine.jcache.configuration;

import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;

import com.github.benmanes.caffeine.jcache.copy.CopyStrategy;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

/**
 * A JCache configuration with Caffeine specific settings.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class CaffeineConfiguration<K, V> extends MutableConfiguration<K, V> {
  private static final long serialVersionUID = 1L;

  private Factory<CopyStrategy> copyStrategyFactory;

  public CaffeineConfiguration() {}

  public CaffeineConfiguration(CompleteConfiguration<K, V> configuration) {
    super(configuration);
    if (configuration instanceof CaffeineConfiguration<?, ?>) {
      copyStrategyFactory = ((CaffeineConfiguration<K, V>) configuration).getCopyStrategyFactory();
    }
  }

  /**
   * Retrieves the cache's settings from the configuration resource.
   *
   * @param config the configuration resource
   * @param cacheName the name of the cache configuration
   * @return the configuration for the cache with the specified name
   * @throws IllegalStateException if there is no configuration for the specified name
   */
  public static <K, V> CaffeineConfiguration<K, V> from(Config config, String cacheName) {
    CaffeineConfiguration<K, V> configuration = new CaffeineConfiguration<>();
    Config cacheConfig;
    try {
      cacheConfig = config.hasPath(cacheName)
          ? config.getConfig(cacheName).withFallback(config)
          : config;
    } catch (ConfigException.BadPath e) {
      cacheConfig = config;
    }

    configuration.setCopyStrategyFactory(FactoryBuilder.factoryOf(
        cacheConfig.getString("storeByValue.strategy")));
    return configuration;
  }

  public Factory<CopyStrategy> getCopyStrategyFactory() {
    return copyStrategyFactory;
  }

  public CaffeineConfiguration<K, V> setCopyStrategyFactory(Factory<CopyStrategy> factory) {
    copyStrategyFactory = factory;
    return this;
  }
}

package com.cloudq.cloudq.service;

import com.cloudq.cloudq.model.Configuration;
import com.cloudq.cloudq.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    /**
     * Get a configuration by key.
     *
     * @param key the configuration key
     * @return the configuration value, if found
     */
    @Transactional(readOnly = true)
    public Optional<Configuration> getConfiguration(String key) {
        return configurationRepository.findByKey(key);
    }

    /**
     * Save or update a configuration.
     *
     * @param configuration the configuration to save
     * @return the saved configuration
     */
    @Transactional
    public Configuration saveConfiguration(Configuration configuration) {
        return configurationRepository.save(configuration);
    }

    /**
     * Delete a configuration by key.
     *
     * @param key the configuration key
     */
    @Transactional
    public void deleteConfiguration(String key) {
        configurationRepository.deleteByKey(key);
    }

    /**
     * Update a configuration value by key.
     *
     * @param key   the configuration key
     * @param value the new configuration value
     * @return the updated configuration, if found
     */
    @Transactional
    public Optional<Configuration> updateConfiguration(String key, String value) {
        Optional<Configuration> optionalConfig = configurationRepository.findByKey(key);
        if (optionalConfig.isPresent()) {
            Configuration configuration = optionalConfig.get();
            configuration.setValue(value);
            configurationRepository.save(configuration);
            return Optional.of(configuration);
        }
        return Optional.empty();
    }
}
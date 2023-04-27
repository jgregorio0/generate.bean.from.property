package dev.jgregorio.test.generate.bean.from.property;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class Config {

    @Bean
    public PostProcessor getPostProcessor(ConfigurableEnvironment environment) {
        return new PostProcessor(environment);
    }

    private class PostProcessor implements BeanDefinitionRegistryPostProcessor {
        List<TestProp> testProps;

        public PostProcessor(ConfigurableEnvironment environment) {
            testProps = new ArrayList<>();
            parseProperties(environment);
        }

        private void parseProperties(ConfigurableEnvironment environment) {
            Map<String, TestProp> propMap = new HashMap<>();
            for (PropertySource<?> source : environment.getPropertySources()) {
                if (source instanceof EnumerablePropertySource) {
                    EnumerablePropertySource propertySource = (EnumerablePropertySource) source;
                    for (String property : propertySource.getPropertyNames()) {
                        if (property.startsWith("test")) {
                            int lastDot = property.lastIndexOf(".");
                            String objectId = property.substring(0, lastDot);
                            String attributeName = property.substring(lastDot + 1);
                            String attributeValue = propertySource.getProperty(property).toString();

                            TestProp testProp = propMap.get(objectId);
                            if (testProp == null) {
                                testProp = new TestProp();
                            }
                            if (attributeName.equals("id")) {
                                testProp.setId(attributeValue);
                            } else if (attributeName.equals("property")) {
                                testProp.setProperty(attributeValue);
                            }
                            propMap.put(objectId, testProp);
                        }
                    }
                }
            }
            testProps = propMap.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .map((e) -> e.getValue())
                    .collect(Collectors.toList());
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            testProps.stream().forEach(testProp -> {
                RootBeanDefinition beanDefinition = new RootBeanDefinition();
                beanDefinition.setBeanClass(TestProp.class);
                beanDefinition.setInstanceSupplier(() -> testProp);
                registry.registerBeanDefinition(testProp.getId(), beanDefinition);
            });
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            // No es necesario implementar este método
        }
    }
}

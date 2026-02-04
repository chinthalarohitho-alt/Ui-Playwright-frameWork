package config;

import org.aeonbits.owner.Config;

/**
 * Environment configuration interface using OWNER library.
 * Dynamically loads properties based on ${env} system property.
 * Properties must be prefixed with environment name (e.g., alpha.Url, beta.Url)
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "file:${user.dir}/src/main/resources/${env}.properties"
})
public interface EnvironmentConfig extends Config {

    @Key("${env}.Url")
    String Url();

    @Key("${env}.username")
    String username();

    @Key("${env}.password")
    String password();

    @Key("${env}.chatGptUrl")
    String chatGptUrl();

}

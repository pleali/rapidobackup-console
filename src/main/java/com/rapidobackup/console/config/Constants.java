package com.rapidobackup.console.config;

/**
 * Application constants.
 */
public interface Constants {

    // Regex for acceptable logins
    String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    String SYSTEM = "system";
    String DEFAULT_LANGUAGE = "en";


    // Spring profiles for development, test and production, 
    /** Constant <code>SPRING_PROFILE_DEVELOPMENT="dev"</code> */
    String SPRING_PROFILE_DEVELOPMENT = "dev";
    /** Constant <code>SPRING_PROFILE_TEST="test"</code> */
    String SPRING_PROFILE_TEST = "test";
    /** Constant <code>SPRING_PROFILE_E2E="e2e"</code> */
    String SPRING_PROFILE_E2E = "e2e";
    /** Constant <code>SPRING_PROFILE_PRODUCTION="prod"</code> */
    String SPRING_PROFILE_PRODUCTION = "prod";
    /** Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
        Constant <code>SPRING_PROFILE_CLOUD="cloud"</code> */
    String SPRING_PROFILE_CLOUD = "cloud";
    /** Spring profile used when deploying to Heroku
        Constant <code>SPRING_PROFILE_HEROKU="heroku"</code> */
    String SPRING_PROFILE_HEROKU = "heroku";
    /** Spring profile used to enable OpenAPI doc generation
        Constant <code>SPRING_PROFILE_API_DOCS="api-docs"</code> */
    String SPRING_PROFILE_API_DOCS = "api-docs";
    /** Spring profile used to disable running liquibase
        Constant <code>SPRING_PROFILE_NO_LIQUIBASE="no-liquibase"</code> */
    String SPRING_PROFILE_NO_LIQUIBASE = "no-liquibase";
    /** Spring profile used when deploying to Kubernetes and OpenShift
        Constant <code>SPRING_PROFILE_K8S="k8s"</code> */
    String SPRING_PROFILE_K8S = "k8s";

}

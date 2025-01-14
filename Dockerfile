# ------------------------------------------------------------------------------
# Dockerfile to build a GNS fred tomcat images
# ------------------------------------------------------------------------------
    ARG BUILD_ENV=staging

    ################################ STAGING-IMAGE-ONLY ADDITIONS ################################
    FROM artifactory.gns.cri.nz/gns/tomcat-base:1.13.staging AS build_staging
    
    ################################ PRODUCTION-IMAGE-ONLY ADDITIONS ################################
    FROM artifactory.gns.cri.nz/gns/tomcat-base:1.13.prod AS build_prod
    
    ################################ BASE IMAGE (PRODUCTION & STAGING) ################################
    FROM build_${BUILD_ENV}
    LABEL Name=tomcat-fred Version=0.0.2 maintainer="r.pringle@gns.cri.nz"
    
    # ------------------------------------------------------------------------------
    # Define (build time) arguments.
    ARG CONTEXT_FILE=context-dev.xml
    
    # ------------------------------------------------------------------------------
    # Define fixed (build time) environment variables.
    ENV CATALINA_HOME="/usr/local/tomcat"
    ENV CATALINA_OPTS="-Xms1G -Xmx1G"
    
    # ------------------------------------------------------------------------------
    # Get all the files for the build. Ned to make a switch to select dev or live
    
    COPY ./server.xml ${CATALINA_HOME}/conf/server.xml
    COPY ./context-dev.xml ${CATALINA_HOME}/conf/context.xml
    
    COPY fred.war ${CATALINA_HOME}/webapps/fred.war
    
    HEALTHCHECK --interval=1m --start-period=1m \
       CMD curl -f http://localhost:8080/fred/ || exit 1
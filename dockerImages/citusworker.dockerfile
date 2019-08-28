FROM postgres:11.4

ARG VERSION=8.3.2
ENV CITUS_VERSION ${VERSION}.citus-1
ENV POSTGIS_MAJOR 2.5
ENV POSTGIS_VERSION 2.5.2+dfsg-1~exp1.pgdg90+1
ENV POSTGRES_USER postgres
#Citus
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       ca-certificates \
       curl \
    && curl -s https://install.citusdata.com/community/deb.sh | bash \
    && apt-get install -y postgresql-$PG_MAJOR-citus-8.3=$CITUS_VERSION \
                          postgresql-$PG_MAJOR-hll=2.12.citus-1 \
                          postgresql-$PG_MAJOR-topn=2.2.0 \
    && apt-get purge -y --auto-remove curl \
    && rm -rf /var/lib/apt/lists/*

#Postgis
RUN apt-get update \
      && apt-cache showpkg postgresql-$PG_MAJOR-postgis-$POSTGIS_MAJOR \
      && apt-get install -y --no-install-recommends \
           postgresql-$PG_MAJOR-postgis-$POSTGIS_MAJOR=$POSTGIS_VERSION \
           postgresql-$PG_MAJOR-postgis-$POSTGIS_MAJOR-scripts=$POSTGIS_VERSION \
           postgis=$POSTGIS_VERSION \
      && rm -rf /var/lib/apt/lists/*

# install Citus

# add citus to default PostgreSQL config
RUN echo "shared_preload_libraries='citus, pg_stat_statements'" >> /usr/share/postgresql/postgresql.conf.sample

RUN mkdir -p /docker-entrypoint-initdb.d

# add scripts to run after initdb
COPY ./scripts/000-configure-stats.sh ./scripts/001-create-citus-extension.sql /docker-entrypoint-initdb.d/

# add health check script
COPY ./scripts/pg_healthcheck /
#COPY 003-wait.sh /

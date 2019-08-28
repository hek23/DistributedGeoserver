COMPOSE_PROJECT_NAME=$1 docker-compose -p $1 -f master.yml up -d &
sleep 120
COMPOSE_PROJECT_NAME=$1 docker-compose -p $1 -f manager.yml up -d &
sleep 10
COMPOSE_PROJECT_NAME=$1 docker-compose -p $1 -f worker.yml scale worker=$2 -d &

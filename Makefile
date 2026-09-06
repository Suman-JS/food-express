.PHONY: start stop restart logs build test clean

start:
	docker compose down
	docker compose up -d --wait
	./mvnw spring-boot:run


stop:
	docker compose down

restart:
	$(MAKE) stop
	$(MAKE) start

logs:
	docker compose logs -f

build:
	./mvnw clean package

test:
	./mvnw test

clean:
	./mvnw clean
	docker compose down -v
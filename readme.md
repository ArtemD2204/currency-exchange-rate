# Currency Exchange Rate

Приложение обращается к сервису курсов валют и отдает gif в ответ.
если курс по отношению к рублю за сегодня стал выше вчерашнего, то возвращается gif c тегом rich.
Если ниже, то возвращается gif c тегом broke.

Для запуска в корневой папке необходимо выполнить команду
 ./gradlew bootRun

Для запуска через Docker в корневой папке необходимо выполнить команды
1) Создание образа:  docker build -t exchange-docker .
2) Создание и запуск контейнера: docker run --name exchange -d -p 8080:8080 exchange-docker

Запрос: GET  http://localhost:8080/exchangerate/{currency}

currency - это код валюты, курс которой необходимо посмотреть. Например: USD, EUR и т.д.

Пример запроса:
http://localhost:8080/exchangerate/USD





# Дипломный проект по профессии «Тестировщик»

### Описание приложения

#### Бизнес-часть

Приложение — это веб-сервис, который предлагает купить тур по определённой цене двумя способами:

* Обычная оплата по дебетовой карте
* Уникальная технология: выдача кредита по данным банковской карты

Само приложение не обрабатывает данные по картам, а пересылает их банковским сервисам:


* сервису платежей, далее Payment Gate
* кредитному сервису, далее Credit Gate

Приложение в собственной СУБД должно сохранять информацию о том, успешно ли был совершён платёж и каким способом. Данные карт при этом сохранять не допускается.

#### Техническая часть

Само приложение расположено в файле [aqa-shop.jar](https://github.com/SereZzZ/QADiploma/tree/main/artifacts) и запускается на порту 8080.

В файле [application.properties](https://github.com/SereZzZ/QADiploma/blob/main/application.properties) приведены настройки:

* учётные данные для подключения к СУБД
* URL-адреса банковских сервисов.

#### СУБД

Заявлена поддержка двух СУБД:

* MySQL
* PostgreSQL

#### Банковские сервисы

Разработчики подготовили симулятор банковских сервисов, который может принимать запросы в нужном формате и генерировать ответы.

Симулятор написан на Node.js, расположен в каталоге [gate-simulator](https://github.com/SereZzZ/QADiploma/tree/main/gate-simulator) и запускается на порту 9999. Он позволяет генерировать предопределённые ответы для заданного набора карт. Набор карт представлен в формате JSON в файле [data.json](https://github.com/Akir800/QA_Diploma/blob/master/gate-simulator/data.json).

Данный сервис симулирует Payment Gate и Credit Gate.

### Работа с проектом

#### Перед работой необходимо установить:

На Машину установить Браузер Google Chrome, Git, Docker, IntelliJ IDEA.

#### Процедура запуска автотестов

1. Скачать код репозитория командой **git clone https://github.com/SereZzZ/QADiploma**
2. Запустить Docker Desktop
3. Открыть проект в IDEA
4. Выполнить команду в терминале **docker-compose up**
5. Выполнить команду во второй вкладке терминала:
* для запуска работы с БД MySQL:
* **java -jar ./artifacts/aqa-shop.jar --spring.datasource.url=jdbc:mysql://localhost:3306/app**
* для запуска работы с БД PostgreSQL:
* **java -jar ./artifacts/aqa-shop.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/app**
6. Открыть консоль двойным нажатие Ctrl и запустить тесты командой:
* при работе с MySQL:
* **./gradlew clean test -Dapp=jdbc:mysql://localhost:3306/app**
* при работе с PostgreSQL:
* **./gradlew clean test -Dapp=jdbc:postgresql://localhost:5432/app**
7. Для формирования отчета Allure выполнить в консоли команду **./gradlew allureServe**
8. Завершить работу SUT:
* в открытом терминале сочетанием клавиш **Ctrl+C**
9. Завершить работу контейнеров:
* сначала клавишами **Ctrl+C**, затем командой **docker-compose down**




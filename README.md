java-explore-with-me
Описание
Приложение предоставляет сервис для планирования интересных событий (афиша) и поиска компании для участия в них.

Состоит из четырех микросервисов - основной сервис, основная БД, сервис статистики просмотров, БД для статистики. Каждый микросервис поднимается в отдельном docker-контейнере.


Ссылка на pull - request: https://github.com/VasilisaZautinskaya/java-explore-with-me/pull/9/

Спецификации основного сервиса - https://github.com/VasilisaZautinskaya/java-explore-with-me/blob/main/ewm-main-service-spec.json
Спецификация сервиса статистики - https://github.com/VasilisaZautinskaya/java-explore-with-me/blob/main/ewm-stats-service-spec.json
Реализованные эндпоинты:

- POST/hit - сохранение информации о том, что к эндпоинту был запрос;
- GET/stats - получение статистики по посещениям;
- POST/admin/categories - добавление новой категории;
- DELETE//admin/categories/{catId} - удаление категории;
- PATCH/admin/categories/{catId} - изменения категории;
- 




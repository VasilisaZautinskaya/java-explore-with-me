java-explore-with-me  
Описание  
Приложение предоставляет сервис для планирования интересных событий (афиша) и поиска компании для участия в них.

Состоит из четырех микросервисов - основной сервис, основная БД, сервис статистики просмотров, БД для статистики. Каждый микросервис поднимается в отдельном docker-контейнере.


Ссылка на pull - request: https://github.com/VasilisaZautinskaya/java-explore-with-me/pull/10

Спецификации основного сервиса - https://github.com/VasilisaZautinskaya/java-explore-with-me/blob/main/ewm-main-service-spec.json  
Спецификация сервиса статистики - https://github.com/VasilisaZautinskaya/java-explore-with-me/blob/main/ewm-stats-service-spec.json  
Реализованные эндпоинты:

- POST/hit - сохранение информации о том, что к эндпоинту был запрос;
- GET/stats - получение статистики по посещениям;

Категории

- POST/admin/categories - добавление новой категории;
- DELETE//admin/categories/{catId} - удаление категории;
- PATCH/admin/categories/{catId} - изменения категории;

Подборка событий
- GET/compilations - получение подборки событий;
- GET/compilations/{compId} - получений подборки событий по id;
- POST/admin/compilations - добавление новой подборки;
- DELETE/admin/compilations/{compId}  - удаление подборки
- PATCH/admin/compilations/{compId} - обновление информации;

События и запросы на участие в них
- GET/users/{userId}/events - получение событий, добавленных текущим пользователем;
- POST/users/{userId}/events - добавление нового события;
- GET/users/{userId}/events/{eventId} - получение полной информации о событии, добавленном текущим пользователем;
- PATCH/users/{userId}/events/{eventId} - изменение события;
- GET/users/{userId}/events/{eventId}/requests - получение информации о запросах на участие в событии текущего пользователя;
- PATCH/users/{userId}/events/{eventId}/requests - изменение статуса;
- GET/admin/events - поиск событий;
- PATCH/admin/events/{eventId} - редактирование данных события;
- GET/events - получение событий с возможностью фильтрации;
- GET/events/{id} - получение подробной информации о событии по его id;
- GET/users/{userId}/requests - получение информации о заявках текущего пользователя на участие в событиях;
- POST/users/{userId}/requests - добавление запроса от текущего пользователя на участие в событии;
- PATCH/users/{userId}/requests/{requestId}/cancel - отмена своего запроса на участие в событии;

Пользователи:
- GET/admin/users - получение информации о пользователях;
- POST/admin/users - добавление пользователя;
- DELETE/admin/users/{userId} - удаление пользователя;

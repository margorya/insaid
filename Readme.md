### Краткое описание:
Приложение принимает, хранит и отдает историю сообщений для пользователей.
### Алгоритм:
1. Бд postgresql две таблицы **user_name** и **user_password**. Соединены fk user_name.id -> user_password.id_user_name.
2. Таблица **user_name** содержит колонки id и name. \
Колонка **id** тип int первичный ключ\
Колонка **name** тип varchar(255)
3. Таблица **user_password** содержит три колонки: id, id_user_name и passwd\
   Колонка **id** тип int первичный ключ\
   Колонка **id_user_name** тип int 
   Колонка **passwd** тип varchar(255)
4. Таблица **message** для хранения сообщений от пользователей. Содержит 4 колонки: id, id_user_name, message, date_time. 
Соединена с таблицей **user_name** fk user_name.id -> message.id_user_name.\
   Колонка **id** тип int первичный ключ\
   Колонка **id_user_name** тип int
   Колонка **message тип** message(255)
   Колонка **date_time** тип timestamp, время сохранения в бд сообщения.
5. Запросом post: **\login** принимается логин и пароль пользователя:
   ```
   {
   "name": "имя отправителя"
   "password": "пароль"
   }
   ```
6. Приятные пользователь и пароль проверяются в бд, и после успешной проверки генерируется в ответ токен:\
```
{
   "token": "тут сгенерированный токен"
 }
```
Иначе приходит ответ с кодом **401**.
8. После успешной генерации токена, возможна отправка сообщения от пользователя:\
   ```
   {
   "name": "имя отправителя",
   "message": "текст сообщение"
   }
   ```
В заголовке необходимо добавить поле *Authorization* и значение Bearer_<генерированный токен>
9. Токен валидируется. После успешной валидации происходит проверка поля name из тела сообщения, что name = user_name отправителя.\
Если произошла ошибка валидации токена, в ответ приходит ответ с кодом **401**.\
Если произошла ошибка валидации поля name и имени отправителя, в ответ приходит ответ с кодом 400
10. Если валидация на шаге 8 прошла успешно, парсится поле message из заголовка. \
Если оно вида **history 10**, где на месте 10 может быть любое число, то в ответ приходит список сообщений из бд, которые отправлял данный пользователь ранее, отфильтрованный по дате в формате:
```
  [
    {
    "name": "имя отправителя",
    "message": "текст сообщение",
    "dateTime": "время сохранения сообщения в бд"
    }
  ]
```
Количество сообщений будет меньше либо равно переданному числу, в зависимости от количества уже сохраненных в бд.
Если сообщение не равно виду **history 10**, пользователю в ответ будет отправлено его сообщение в формате:\
```

   [
    {
    "name": "имя отправителя",
    "message": "текст сообщение",
    "dateTime": "время сохранения сообщения в бд"
    }
    ]
```
### Примеры Curl:
1. Логин:\
   curl -d "{"name":"Vera","password":"pass"}" -H "Content-Type: application/json" -X POST http://localhost:8090/login
2. Отправка любого сообщения:\
   curl -d "{\"name\":\"Vera\",\"message\":\"Hi! I'm Vera!\"}" -H "Content-Type: application/json" -H "Authorization:Bearer_token" -X POST http://localhost:8090/message
3. Запрос истории сообщений:
   curl -d "{\"name\":\"Vera\",\"message\":\"history 2\"}" -H "Content-Type: application/json" -H "Authorization:Bearer_token" -X POST http://localhost:8090/message
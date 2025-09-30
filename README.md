# KT1-2


Запуск через IntelliJ IDEA

1. Откройте проект в IntelliJ IDEA
2. Найдите файл src/main/kotlin/com/example/Application.kt
3. Нажмите на зеленую стрелку ▶️ рядом с функцией main()
4. Выберите "Run 'ApplicationKt'"
5. Если пункт не появляется, убедитесь, что проект собран (`Build` → `Rebuild Project`).

Запуск тестов

Способ 1: Через Gradle (все тесты)
# Запуск всех тестов
./gradlew test

# Запуск тестов с подробным выводом
./gradlew test --info

# Очистка и запуск тестов
./gradlew clean test

Способ 2: Через IntelliJ IDEA (отдельные тесты)

1. Откройте файл src/test/kotlin/Test/example/ApplicationTest.kt
2. Для запуска всех тестов в классе:
   · Нажмите на зеленую стрелку ▶️ рядом с названием класса ApplicationTest
   · Выберите "Run 'ApplicationTest'"
3. Для запуска отдельного теста:
   · Нажмите на зеленую стрелку ▶️ рядом с аннотацией @Test
   · Выберите "Run 'testMethodName'"
4. Для запуска через контекстное меню:
   · Правой кнопкой на папке test → "Run Tests in 'com.example'"
   · Правой кнопкой на классе → "Run ApplicationTest"

Доступные эндпоинты

GET /products

Получить все продукты
curl http://localhost:8080/products

GET /products/{id}

Получить продукт по ID
curl http://localhost:8080/products/1

POST /products

Создать новый продукт
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"productName":"New Product"}'

DELETE /products/{id}

Удалить продукт по ID
curl -X DELETE http://localhost:8080/products/1



# Приложение «Личные Финансы» (Personal Finance CLI)

Итоговый проект (сессионное задание). Принципы объектно-ориентированного программирования

---

## Архитектура приложения

Проект спроектирован с упором на принципы **SOLID**, **слабую связанность** и явное разделение ответственности. 
В основе лежит **многослойная архитектура**

### Компоненты архитектуры:
*   **Слой Модели:** Инкапсулирует бизнес-правила системы (`service`) и логику персистентности данных (`repository`). Сервисы изолированы от консольного вывода и оперируют чистыми данными.
*   **Слой Представления:** Разделен на два класса:
    *   `Controller` — управляет логикой выполнения команд, координирует потоки данных между сервисами и выводом.
    *   `View` — «глупый» компонент, отвечающий исключительно за форматирование текста, построение таблиц и вывод информации на экран.
*   **Инфраструктурное Ядро (`core`):** Содержит IoC-контейнер, маршрутизатор и менеджер сессий.

---

## Диаграммы взаимодействия

### 1. Инициализация приложения
Схема показывает, как приложение запускается, изолированно собирает все слои через `DIContainer` и настраивает маршруты без использования жестких связей:

```mermaid
graph TD
    %% Шаг запуска
    Start([Main.java]) -->|Вызов bootstrap| AB[core.AppBootstrap]
    
    %% Создание инфраструктуры ядра
    AB -->|new| DI[core.DIContainer]
    AB -->|new| Router[core.Router]

%% Этапы предоставления зависимостей
    subgraph Инициализация и сборка слоев в DIContainer
        DI -->|1. provide| Repos[repository]
        DI -->|2. provide| CoreUtils[coreUtils]
        DI -->|3. provide| Services[service]
        DI -->|4. provide| Views[view]
        DI -->|5. provide| Controllers[controller]
    end

%% Регистрация маршрутов
  subgraph Конфигурация маршрутизатора Router.register
        Router -->|routeName| controller.Controller
  end
 %% Финальный шаг возврата
    AB -.->|7. Возврат настроенного Router| Start
```

### 2. Поток данных при выполнении команд
Схема демонстрирует совместную работу всех слоев системы при обработке пользовательского ввода:

```mermaid
graph TD
    %% Определение слоев
    subgraph UI [Слой Представления / CLI]
        Console([Ввод пользователя]) --> Router[Router]
        Router --> Controller[Controller]
        View[View] --> Screen([Консоль / Экран])
    end

    subgraph CoreLayer [Ядро Системы]
        Session[SessionManager]
    end

    subgraph ModelLayer [Слой Бизнес-логики и Данных]
        Service[Service]
        Repository[Repository]
        DB[(JSON Файлы)]
    end

    %% Потоки управления и данных (Обновленные)
    Controller <-->|1. Запрос операции / Возврат результата| Service
    Service -.->|Проверка UUID пользователя| Session
    Service <-->|2. Запрос данных / Возврат сущностей| Repository
    Repository <-->|Чтение и Запись| DB
    
    Controller -->|3. Передача чистых данных для рендеринга| View

    %% Стилизация для наглядности
    style Controller fill:#f9f,stroke:#333,stroke-width:2px
    style Session fill:#ff9,stroke:#333,stroke-width:2px
    style Service fill:#bbf,stroke:#333,stroke-width:2px
    style View fill:#bfb,stroke:#333,stroke-width:2px

```

---

## Структура пакетов проекта

Архитектурные слои физически разложены по следующим пакетам в директории `src/main/java/mephi/`:

```text
└── mephi
    ├── controller
    ├── core
    ├── dto
    │   ├── request
    │   └── response
    ├── exception
    ├── Main.java
    ├── model
    │   ├── enums
    ├── repository
    ├── service
    ├── util
    └── view
```

---

## 🛠️ Технологический стек
*   **Язык:** Java 17+
*   **Сериализация:** Google Gson (работа с файловой БД в формате JSON)
*   **Форматированные таблицы** Ascii-table
*   **Сборка проекта:** Maven
*   **Визуализация архитектуры:** Mermaid.js (интегрировано в GitHub Markdown)

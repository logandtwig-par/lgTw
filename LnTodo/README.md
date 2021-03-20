# L&T(odo)
L&T(odo) är en backend-applikation skriven i Java 8 med Spring Boot och byggs med Maven. 

## Intro
Applikationens databas (MySQL 5.7) rullar på en virtuell server. Inloggningsuppgifter till databasen finns längre ner. 
Viss databaslogik är redan implementerad - däribland databasuppkopplingen. Se `TodoRepository`, `UserRepository`, `TodoEntity` samt `UserEntity`. Det är fritt fram att implementera ytterligare databaslogik.
IDn skapas automatiskt av databasen, så när du sparar en Entity ska ID inte anges. 
 
Du får lov att ändra den existerande koden men det krävs inte för att slutföra uppgiften (med undantag av klassen `TodoApi`).

För att förenkla tillvaron för de stackars utvecklarna av applikationen så finns det ingen inloggning till L&T(odo),
utan vi förlitar oss på att användare använder sina egna användarnamn och är ärliga med det. Användarnamnet anges som en query parameter i requesten,


Ditt mål är att implementera resten av applikationen i enlighet med kravspecifikationen nedan. Var observant på detaljerna. Lycka till!

## Krav
Applikationen ska innehålla:
1. En POST-endpoint som sparar en ny TODO till databasen och svarar med den skapade TODOn, inkl. IDt.
2. En GET-endpoint som svarar med en lista som innehåller en given användares samtliga TODOs i databasen.
3. En GET-endpoint som svarar med en specifik TODO i databasen givet ett ID, förutsatt att den tillhör användaren.
    3.1 Om det givna IDt för TODOn inte matchar den givna användarens username ska detta hanteras på lämpligt sätt.
4. En DELETE-endpoint som tar bort en specifik TODO från databasen, förutsatt att den tillhör användaren.
    4.1 Om det givna IDt för TODOn inte matchar den givna användarens username ska detta hanteras på lämpligt sätt.
5. Responsen från de tre första resurserna (POST, GET, GET) ska även innehålla vilken användare det var som gjorde anropet. Utgå från den befintliga koden i `TodoDto`. 


6. Applikationen har stöd för flera användare - användare får alltså bara se/ändra/ta bort sina egna TODOs. 

7. Applikationen ska returnera lämpliga HTTP-statuskoder.

8. När du anser dig färdig med uppgiften ska du pusha lösningen som en Pull Request på Github.

### Bonus:
- applikationen ska stödja att uppdatera en befintlig TODO
- gör det möjligt att markera tasks som slutförda
- JUnit-test som testar att TodoValidator#validate kastar IllegalStateException i de möjliga fallen
- sätt upp en /health-endpoint

## Databasdetaljer
Connection string: jdbc:mysql://165.22.81.196:44244/todo

Username: todo-user

Password: -skickat till dig via mail-
 

Uppdatera filen `src/main/resources/application.properties` med lösenordet.

Tabellerna i databasen (dessa är redan skapade åt dig, men så här ser de ut):
```sql
CREATE TABLE `users`
(
    `id`       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(30) NOT NULL,
    `created`  TIMESTAMP DEFAULT now(),
    `updated`  TIMESTAMP DEFAULT now() ON UPDATE now()
) ENGINE = INNODB;

CREATE TABLE `todos`
(
    `id`      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `task`    VARCHAR(160)  NOT NULL,
    `user_id` INT UNSIGNED NOT NULL,
    `created` TIMESTAMP DEFAULT now(),
    `updated` TIMESTAMP DEFAULT now() ON UPDATE now(),
    FOREIGN KEY (`user_id`)
        REFERENCES users(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
```

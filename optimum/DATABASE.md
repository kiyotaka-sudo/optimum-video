# Base de donnees MySQL

Optimum Video utilise MySQL, pas H2.

## Configuration par defaut

L'application tente de se connecter a :

```properties
jdbc:mysql://localhost:3306/optimum_video?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
user=root
password=
```

## Configuration actuelle du projet

Le fichier `src/main/resources/application.properties` est configure pour MySQL en utilisateur `root` sans mot de passe :

```properties
spring.datasource.username=root
spring.datasource.password=
```

## Modifier sans changer le code si besoin

Sous PowerShell :

```powershell
$env:DDL_AUTO="update"
.\mvnw.cmd spring-boot:run
```

Pour une premiere execution de demo, `DDL_AUTO=create` recree les tables et recharge les donnees d'exemple.
Pour conserver les donnees, utilisez `DDL_AUTO=update`.

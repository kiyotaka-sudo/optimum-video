# Optimum Video

Application Spring Boot + Thymeleaf inspiree d'une plateforme de streaming type Prime Video, avec une identite visuelle rouge/noir proche de Netflix.

## Concepts utilises du cours Java / Spring

- POO : encapsulation dans les entites, enums metier, constructeurs, getters et validations simples.
- Heritage : `BaseEntity` factorise `id`, `createdAt`, `updatedAt`.
- Collections Java/JPA : genres, casting, profils, episodes, progressions.
- Architecture en couches : `model`, `repository`, `service`, `controller`, `dto`, `config`.
- Spring Boot : injection de dependances par constructeur, JPA repositories, services transactionnels, MVC, REST.
- Thymeleaf : pages serveur principales, fragments de navigation/footer, expressions `th:*`.
- AJAX : suggestions de recherche, creation de profil, creation de session playback.

## Prerequis

- Java 17 ou plus
- MySQL lance localement
- Maven wrapper inclus (`mvnw.cmd`)

## Base MySQL

Par defaut :

```properties
DB_URL=jdbc:mysql://localhost:3306/optimum_video?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=
DDL_AUTO=create
```

Voir aussi `DATABASE.md`.

## Lancer

```powershell
.\mvnw.cmd spring-boot:run
```

La configuration MySQL incluse utilise `root` sans mot de passe.

Puis ouvrir :

- Interface : http://localhost:9092
- API catalogue : http://localhost:9092/api/titles
- Plans : http://localhost:9092/api/subscriptions/plans

Si le port `9092` est aussi occupe :

```powershell
$env:SERVER_PORT="9093"
.\mvnw.cmd spring-boot:run
```

Compte admin insere au demarrage :

- Username : `MBARGA ERNEST`
- Email : `mbargaernest80@gmail.com`
- Mot de passe : celui fourni pour l'administrateur

Compte utilisateur demo :

- Email : `demo@optimum.video`
- Mot de passe : `Password@123`

## Fonctionnalites ajoutees

- Connexion Spring Security avec mots de passe BCrypt.
- Role `ADMIN` pour `/admin`.
- Validation mot de passe cote serveur et cote navigateur avec cases cochees automatiquement.
- Traduction FR/EN avec `?lang=fr` et `?lang=en`.
- Switch theme Netflix rouge/noir et Prime Video bleu/noir.
- Logs dans `Logs/optimum-video.log`.
- Categories africaines et fantastiques : cinema camerounais, chaines africaines, sorcellerie, necromancie, mythes africains.
- Abonnement partage `Famille partagee`.
- API chaines TV : `/api/channels`.
- Facture PDF : `/api/subscriptions/invoices/demo/pdf`.

## Signature PDF

L'application genere de vrais fichiers PDF. Une signature PDF reconnue par Adobe Reader, lecteurs PDF et clients mail exige un certificat numerique PKCS#12/PFX valide.
Renseigner ensuite :

```properties
optimum.signature.pkcs12.path=C:/chemin/certificat.p12
optimum.signature.pkcs12.password=mot_de_passe_du_certificat
```

Sans certificat, aucun logiciel PDF ne peut detecter une signature cryptographique valide.

## Compiler

```powershell
.\mvnw.cmd -DskipTests package
```

Le test Spring Boot standard demarre tout le contexte et a donc besoin d'un serveur MySQL accessible.

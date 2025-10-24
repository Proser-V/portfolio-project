package com.atelierlocal.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.AskingStatus;
import com.atelierlocal.model.Avatar;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.Recommendation;
import com.atelierlocal.model.MessageStatus;
import com.atelierlocal.model.UploadedPhoto;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.AskingRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.repository.EventCategoryRepo;
import com.atelierlocal.repository.MessageRepo;
import com.atelierlocal.repository.RecommendationRepo;
import com.atelierlocal.repository.AvatarRepo;
import com.atelierlocal.service.PasswordService;

@Configuration
@Profile("demo")
public class DemoDataConfig {

    @Bean
    CommandLineRunner initDemoData(
        ClientRepo clientRepo,
        ArtisanRepo artisanRepo,
        EventCategoryRepo eventCategoryRepo,
        ArtisanCategoryRepo categoryRepo,
        AskingRepo askingRepo,
        PasswordService passwordService,
        MessageRepo messageRepo,
        AvatarRepo  avatarRepo,
        RecommendationRepo recoRepo
    ) {
        return args -> {
            if (clientRepo.count() > 0 || artisanRepo.count() > 0 || categoryRepo.count() > 0) {
                System.out.println("Données déjà présentes, seed ignoré.");
                return;
            }

            /* -----------------------------
             * Catégories d'artisans
             * ----------------------------- */

            ArtisanCategory plombier = new ArtisanCategory();
            plombier.setName("Plombier");
            plombier.setDescription("Travaux d'installation et de dépannage en plomberie.");

            ArtisanCategory forgeron = new ArtisanCategory();
            forgeron.setName("Forgeron");
            forgeron.setDescription("Artisanat lié au travail du métal.");

            ArtisanCategory electricien = new ArtisanCategory();
            electricien.setName("Électricien");
            electricien.setDescription("Installation et rénovation électrique.");

            ArtisanCategory menuisier = new ArtisanCategory();
            menuisier.setName("Menuisier");
            menuisier.setDescription("Travail du bois et fabrication sur mesure.");

            ArtisanCategory photographe = new ArtisanCategory();
            photographe.setName("Photographe");
            photographe.setDescription("Prend des photos.");

            ArtisanCategory traiteur = new ArtisanCategory();
            traiteur.setName("Traiteur");
            traiteur.setDescription("Propose des repas de groupe.");

            ArtisanCategory fleuriste = new ArtisanCategory();
            fleuriste.setName("Fleuriste");
            fleuriste.setDescription("Créer des compositions florales.");

            ArtisanCategory patissier = new ArtisanCategory();
            patissier.setName("Patissier");
            patissier.setDescription("Créer des gateaux.");

            ArtisanCategory brasseur = new ArtisanCategory();
            brasseur.setName("Brasseur");
            brasseur.setDescription("Créer des bières.");

            categoryRepo.saveAll(Arrays.asList(plombier, forgeron, electricien, menuisier, photographe, traiteur, fleuriste, patissier, brasseur));


            /* -----------------------------
             * Catégories d'événements
             * ----------------------------- */

            EventCategory mariage = new EventCategory();
            mariage.setName("Mariage");
            mariage.setArtisanCategoryList(List.of(photographe, traiteur, fleuriste, patissier));

            EventCategory baptême = new EventCategory();
            baptême.setName("Mariage");
            baptême.setArtisanCategoryList(List.of(photographe, traiteur, fleuriste));

            EventCategory kermesse = new EventCategory();
            kermesse.setName("Kermesse");
            kermesse.setArtisanCategoryList(List.of(brasseur, photographe));

            eventCategoryRepo.saveAll(List.of(mariage, baptême, kermesse));

            /* -----------------------------
             * Clients (dont admin)
             * ----------------------------- */

            Client admin = new Client();
            admin.setEmail("admin@mail.com");
            admin.setHashedPassword(passwordService.hashPassword("password"));
            admin.setActive(true);
            admin.setUserRole(UserRole.ADMIN);
            admin.setFirstName("Alice");
            admin.setLastName("Ladmine");
            admin.setLatitude(47.338598);
            admin.setLongitude(5.052522);
            admin.setPhoneNumber("0600000001");

            Client client1 = new Client();
            client1.setEmail("client1@mail.com");
            client1.setHashedPassword(passwordService.hashPassword("password"));
            client1.setActive(true);
            client1.setUserRole(UserRole.CLIENT);
            client1.setFirstName("Jean");
            client1.setLastName("Dupont");
            client1.setLatitude(47.316307);
            client1.setLongitude(5.018013);
            client1.setPhoneNumber("0600000002");

            Client client2 = new Client();
            client2.setEmail("client2@mail.com");
            client2.setHashedPassword(passwordService.hashPassword("password"));
            client2.setActive(true);
            client2.setUserRole(UserRole.CLIENT);
            client2.setFirstName("Sophie");
            client2.setLastName("Martin");
            client2.setLatitude(47.310555);
            client2.setLongitude(5.068165);
            client2.setPhoneNumber("0600000003");

            Client client3 = new Client();
            client3.setEmail("client3@mail.com");
            client3.setHashedPassword(passwordService.hashPassword("password"));
            client3.setActive(true);
            client3.setUserRole(UserRole.CLIENT);
            client3.setFirstName("Jo");
            client3.setLastName("Muller");
            client3.setLatitude(47.333630);
            client3.setLongitude(5.030626);
            client3.setPhoneNumber("0600000004");

            clientRepo.saveAll(Arrays.asList(admin, client1, client2, client3));

            /* -----------------------------
             * Artisans
             * ----------------------------- */

            Artisan artisan1 = new Artisan();
            artisan1.setEmail("artisan1@mail.com");
            artisan1.setHashedPassword(passwordService.hashPassword("password"));
            artisan1.setActive(true);
            artisan1.setUserRole(UserRole.ARTISAN);
            artisan1.setName("FERPLAY");
            artisan1.setBio("J'ai choisi de me forger un avenir à Dijon en 2020, en me reconvertissant vers ma passion brûlante de toujours. Depuis, je m'efforce de marquer le marché local au fer rouge. Mon fer de lance ? Battre le fer tant qu'il est chaud, tout en évitant de me retrouver entre le marteau et l'enclume et garder la tête froide pour ne pas devenir marteau !");
            artisan1.setCategory(forgeron);
            artisan1.setSiret("12345678901234");
            artisan1.setLatitude(47.322200);
            artisan1.setLongitude(5.039335);
            artisan1.setPhoneNumber("0600000004");
            artisan1.setActivityStartDate(LocalDate.of(2010, 5, 12));

            /* -----------------------------
             * Gallerie photo d'artisan 1
             * ----------------------------- */

            UploadedPhoto artisan1Photo1 = new UploadedPhoto();
            artisan1Photo1.setArtisan(artisan1);
            artisan1Photo1.setExtension(".jpg");
            artisan1Photo1.setUploadedPhotoUrl("https://d1gmao6ee1284v.cloudfront.net/shadowmourne2.jpg");
            artisan1.getPhotoGallery().add(artisan1Photo1);

            UploadedPhoto artisan1Photo2 = new UploadedPhoto();
            artisan1Photo2.setArtisan(artisan1);
            artisan1Photo2.setExtension(".jpg");
            artisan1Photo2.setUploadedPhotoUrl("https://d1gmao6ee1284v.cloudfront.net/Epee-inspiree-de-la-Mastersword-8.jpg");
            artisan1.getPhotoGallery().add(artisan1Photo2);

            UploadedPhoto artisan1Photo3 = new UploadedPhoto();
            artisan1Photo3.setArtisan(artisan1);
            artisan1Photo3.setExtension(".png");
            artisan1Photo3.setUploadedPhotoUrl("https://d1gmao6ee1284v.cloudfront.net/epee-the-witcher-geralt-de-riv-9.png");
            artisan1.getPhotoGallery().add(artisan1Photo3);

            UploadedPhoto artisan1Photo4 = new UploadedPhoto();
            artisan1Photo4.setArtisan(artisan1);
            artisan1Photo4.setExtension(".jpg");
            artisan1Photo4.setUploadedPhotoUrl("https://d1gmao6ee1284v.cloudfront.net/Votre-texte-zadazzade-paragraphe-1-copie.jpg");
            artisan1.getPhotoGallery().add(artisan1Photo4);

            UploadedPhoto artisan1Photo5 = new UploadedPhoto();
            artisan1Photo5.setArtisan(artisan1);
            artisan1Photo5.setExtension(".jpg");
            artisan1Photo5.setUploadedPhotoUrl("https://d1gmao6ee1284v.cloudfront.net/mastersword-et-fourreau.jpg");
            artisan1.getPhotoGallery().add(artisan1Photo5);

            artisanRepo.save(artisan1);

            Artisan artisan2 = new Artisan();
            artisan2.setEmail("artisan2@mail.com");
            artisan2.setHashedPassword(passwordService.hashPassword("password"));
            artisan2.setActive(true);
            artisan2.setUserRole(UserRole.ARTISAN);
            artisan2.setName("Élec Services Dijon");
            artisan2.setBio("Installation et mise en conformité de votre réseau électrique.");
            artisan2.setCategory(electricien);
            artisan2.setSiret("23456789012345");
            artisan2.setLatitude(47.327880);
            artisan2.setLongitude(5.059904);
            artisan2.setPhoneNumber("0633476589");
            artisan2.setActivityStartDate(LocalDate.of(2015, 3, 8));

            Artisan artisan3 = new Artisan();
            artisan3.setEmail("artisan3@mail.com");
            artisan3.setHashedPassword(passwordService.hashPassword("password"));
            artisan3.setActive(true);
            artisan3.setUserRole(UserRole.ARTISAN);
            artisan3.setName("Le Bois du Sud Dijonnais");
            artisan3.setBio("Fabrication sur mesure et rénovation bois.");
            artisan3.setCategory(menuisier);
            artisan3.setSiret("34567890123456");
            artisan3.setLatitude(47.313124);
            artisan3.setLongitude(5.036413);
            artisan3.setPhoneNumber("0634867511");
            artisan3.setActivityStartDate(LocalDate.of(2012, 9, 20));

            Artisan artisan4 = new Artisan();
            artisan4.setEmail("artisan4@mail.com");
            artisan4.setHashedPassword(passwordService.hashPassword("password"));
            artisan4.setActive(true);
            artisan4.setUserRole(UserRole.ARTISAN);
            artisan4.setName("Le Bon Traiteur");
            artisan4.setBio("Création et fourniture de repas de père en fils aux alentours de Dijon depuis 1990.");
            artisan4.setCategory(traiteur);
            artisan4.setSiret("34567890197624");
            artisan4.setLatitude(47.316489);
            artisan4.setLongitude(5.093911);
            artisan4.setPhoneNumber("0626184975");
            artisan4.setActivityStartDate(LocalDate.of(1990, 4, 21));

            Artisan artisan5 = new Artisan();
            artisan5.setEmail("artisan5@mail.com");
            artisan5.setHashedPassword(passwordService.hashPassword("password"));
            artisan5.setActive(true);
            artisan5.setUserRole(UserRole.ARTISAN);
            artisan5.setName("LE BOUQUET");
            artisan5.setBio("Les fleurs ça nous connait et on aime ça. N'hésitez pas à nous contacter pour tous vos évènements.");
            artisan5.setCategory(fleuriste);
            artisan5.setSiret("34567890167945");
            artisan5.setLatitude(47.350117);
            artisan5.setLongitude(5.037830);
            artisan5.setPhoneNumber("0637988425");
            artisan5.setActivityStartDate(LocalDate.of(2001, 4, 21));

            Artisan artisan6 = new Artisan();
            artisan6.setEmail("artisan6@mail.com");
            artisan6.setHashedPassword(passwordService.hashPassword("password"));
            artisan6.setActive(true);
            artisan6.setUserRole(UserRole.ARTISAN);
            artisan6.setName("Palais de la Bière");
            artisan6.setBio("Brasseur dijonnais fier de sa région, je crée des bières artisanales inspirées du terroir bourguignon, à partager entre amis autour d'un bon moment.");
            artisan6.setCategory(brasseur);
            artisan6.setSiret("34567890167945");
            artisan6.setLatitude(47.284530);
            artisan6.setLongitude(5.057619);
            artisan6.setPhoneNumber("0637988425");
            artisan6.setActivityStartDate(LocalDate.of(2001, 4, 21));

            Artisan artisan7 = new Artisan();
            artisan7.setEmail("artisan7@mail.com");
            artisan7.setHashedPassword(passwordService.hashPassword("password"));
            artisan7.setActive(true);
            artisan7.setUserRole(UserRole.ARTISAN);
            artisan7.setName("Effet Miroir");
            artisan7.setBio("Photographe à Dijon, je capture les émotions et les instants vrais, qu'il s'agisse d'un mariage, d'un portrait ou d'un projet professionnel. Chaque image raconte une histoire unique.");
            artisan7.setCategory(photographe);
            artisan7.setSiret("34567890176813");
            artisan7.setLatitude(47.347558);
            artisan7.setLongitude(5.003767);
            artisan7.setPhoneNumber("0666744395");
            artisan7.setActivityStartDate(LocalDate.of(2023, 7, 14));

            /* -----------------------------
             * Gallerie photo d'artisan 7
             * ----------------------------- */

            UploadedPhoto artisan7Photo1 = new UploadedPhoto();
            artisan7Photo1.setArtisan(artisan7);
            artisan7Photo1.setExtension(".jpg");
            artisan7Photo1.setUploadedPhotoUrl("https://d1gmao6ee1284v.cloudfront.net/paysage1.jpg");
            artisan7.getPhotoGallery().add(artisan7Photo1);

            UploadedPhoto artisan7Photo2 = new UploadedPhoto();
            artisan7Photo2.setArtisan(artisan7);
            artisan7Photo2.setExtension(".jpg");
            artisan7Photo2.setUploadedPhotoUrl("https://d1gmao6ee1284v.cloudfront.net/animal1.jpg");
            artisan7.getPhotoGallery().add(artisan7Photo2);

            UploadedPhoto artisan7Photo3 = new UploadedPhoto();
            artisan7Photo3.setArtisan(artisan7);
            artisan7Photo3.setExtension(".jpeg");
            artisan7Photo3.setUploadedPhotoUrl("https://d1gmao6ee1284v.cloudfront.net/famille1.jpeg");
            artisan7.getPhotoGallery().add(artisan7Photo3);

            artisanRepo.save(artisan7);

            artisanRepo.saveAll(Arrays.asList(artisan1, artisan2, artisan3, artisan4, artisan5, artisan6, artisan7));

            /* -----------------------------
            * Demandes (Askings)
            * ----------------------------- */

            Asking asking1 = new Asking();
            asking1.setTitle("Fuite sous évier");
            asking1.setContent("Bonjour, je suis en train de préparer le baptême de mon petit dernier, et j'ai besoin d'un traiteur. Concernant le menu je veux un buffet chaud, pas de spécification particulière nous sommes tous gourmands... nous avons déjà prévu les boissons.");
            asking1.setEventCategory(baptême);
            asking1.setEventLocalisation("Dijon");
            asking1.setEventDate(LocalDateTime.now().plusDays(30));
            asking1.setClient(client1);
            asking1.setArtisanCategory(traiteur);

            Asking asking2 = new Asking();
            asking2.setTitle("Installation de chauffe-eau");
            asking2.setContent("Je souhaite remplacer mon ancien chauffe-eau par un modèle plus récent. Besoin d'un devis.");
            asking2.setClient(client2);
            asking2.setArtisanCategory(plombier);

            Asking asking3 = new Asking();
            asking3.setTitle("Une nouvelle épée");
            asking3.setContent("J'ai besoin dans le cadre d'un évènement médiéval, d'une nouvelle épée de type Claymore pour des combats simulés.");
            asking3.setClient(client2);
            asking3.setArtisanCategory(forgeron);

            Asking asking4 = new Asking();
            asking4.setTitle("Trophé d'escalade");
            asking4.setContent("Je souhaite réaliser un trophée véritablement unique pour notre compétition d'escalade, qui soit à la fois élégant, artistique et symbolique. L'idée est de créer une pièce en métal forgé, finement travaillée, représentant une montagne stylisée avec ses reliefs, ses crêtes et sa texture rugueuse évoquant le défi et l'aventure. Au sommet de cette montagne, une silhouette de grimpeur en plein effort serait intégrée, capturant l'instant précis où il atteint le sommet, dans une posture dynamique qui illustre la détermination, la persévérance et la passion pour l'escalade. Le socle du trophée serait massif et stable, conçu pour mettre en valeur la sculpture tout en offrant un espace personnalisable, où pourraient être gravés le nom du gagnant, la date de l'événement et éventuellement le nom de la compétition. L'ensemble doit refléter le caractère exceptionnel du défi, un trophée digne de trôner fièrement chez ceux qui l'auront remporté.");
            asking4.setClient(client1);
            asking4.setArtisanCategory(forgeron);

            Asking asking5 = new Asking();
            asking5.setTitle("Kermesse Messigny");
            asking5.setContent("J'ai besoin d'un brasseur pour notre kermesse de Messigny-et-Vantoux.");
            asking5.setClient(client2);
            asking5.setEventCategory(kermesse);
            asking5.setEventLocalisation("Messigny-et-Vantoux");
            asking5.setEventDate(LocalDateTime.now().plusDays(10));
            asking5.setArtisanCategory(brasseur);

            Asking asking6 = new Asking();
            asking6.setTitle("Kermesse Messigny");
            asking6.setContent("J'ai besoin d'un photographe pour notre kermesse de Messigny-et-Vantoux. Uniquement le matin.");
            asking6.setClient(client2);
            asking6.setEventCategory(kermesse);
            asking6.setEventLocalisation("Messigny-et-Vantoux");
            asking6.setEventDate(LocalDateTime.now().plusDays(10));
            asking6.setArtisanCategory(photographe);

            asking1.setStatus(AskingStatus.PENDING);
            asking2.setStatus(AskingStatus.PENDING);
            asking3.setStatus(AskingStatus.PENDING);
            asking4.setStatus(AskingStatus.PENDING);
            asking5.setStatus(AskingStatus.PENDING);
            asking6.setStatus(AskingStatus.PENDING);

            // Sauvegarde
            askingRepo.saveAll(List.of(asking1, asking2, asking3, asking4, asking5,asking6));

            /* -----------------------------
             * Avatars
             * ----------------------------- */
            Avatar avatar1 = new Avatar();
            avatar1.setExtension("jpeg");
            avatar1.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/devenir-forgeron.jpeg");
            avatar1.setUser(artisan1);
            artisan1.setAvatar(avatar1);
            artisanRepo.save(artisan1);

            Avatar avatar2 = new Avatar();
            avatar2.setExtension("jpg");
            avatar2.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/avatars/4dd499e3-1452-44ec-823c-4b10f2c08090/%C3%A9lec.jpg");
            avatar2.setUser(artisan2);
            artisan2.setAvatar(avatar2);
            artisanRepo.save(artisan2);

            Avatar avatar3 = new Avatar();
            avatar3.setExtension("jpg");
            avatar3.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/la-matriarche-de-veruschka-zarate-couture-sur-papier-foundation-paper-piecing.jpg");
            avatar3.setUser(artisan3);
            artisan3.setAvatar(avatar3);
            artisanRepo.save(artisan3);

            Avatar avatar4 = new Avatar();
            avatar4.setExtension("jpg");
            avatar4.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/random2.jpg");
            avatar4.setUser(client1);
            client1.setAvatar(avatar4);
            clientRepo.save(client1);

            Avatar avatar5 = new Avatar();
            avatar5.setExtension("jpg");
            avatar5.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/random3.jpg");
            avatar5.setUser(client2);
            client2.setAvatar(avatar5);
            clientRepo.save(client2);

            Avatar avatar6 = new Avatar();
            avatar6.setExtension("jpg");
            avatar6.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/random5.jpg");
            avatar6.setUser(admin);
            admin.setAvatar(avatar6);
            clientRepo.save(admin);

            Avatar avatar7 = new Avatar();
            avatar7.setExtension("jpg");
            avatar7.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/random4.jpg");
            avatar7.setUser(client3);
            client3.setAvatar(avatar7);
            clientRepo.save(client3);

            Avatar avatar8 = new Avatar();
            avatar8.setExtension("jpg");
            avatar8.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/traiteur.jpg");
            avatar8.setUser(artisan4);
            artisan4.setAvatar(avatar8);
            artisanRepo.save(artisan4);

            Avatar avatar9 = new Avatar();
            avatar9.setExtension("jpg");
            avatar9.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/fleuriste.jpg");
            avatar9.setUser(artisan5);
            artisan5.setAvatar(avatar9);
            artisanRepo.save(artisan5);

            Avatar avatar10 = new Avatar();
            avatar10.setExtension("jpg");
            avatar10.setAvatarUrl("https://d1gmao6ee1284v.cloudfront.net/brasseur.jpg");
            avatar10.setUser(artisan6);
            artisan6.setAvatar(avatar10);
            artisanRepo.save(artisan6);

            Avatar avatar11 = new Avatar();
            avatar11.setExtension("jpeg");
            avatar11.setAvatarUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/photographe.jpeg");
            avatar11.setUser(artisan7);
            artisan7.setAvatar(avatar11);
            artisanRepo.save(artisan7);

            /* -----------------------------
            * Recommendation
            * ----------------------------- */

            Recommendation reco1 = new Recommendation();
            reco1.setClient(client1);
            reco1.setArtisan(artisan1);
            
            Recommendation reco2 = new Recommendation();
            reco2.setClient(client2);
            reco2.setArtisan(artisan1);
            
            Recommendation reco3 = new Recommendation();
            reco3.setClient(client3);
            reco3.setArtisan(artisan1);
            
            Recommendation reco4 = new Recommendation();
            reco4.setClient(client1);
            reco4.setArtisan(artisan2);
            
            Recommendation reco5 = new Recommendation();
            reco5.setClient(client1);
            reco5.setArtisan(artisan3);
            
            Recommendation reco6 = new Recommendation();
            reco6.setClient(client2);
            reco6.setArtisan(artisan6);
            
            Recommendation reco7 = new Recommendation();
            reco7.setClient(client3);
            reco7.setArtisan(artisan5);
            
            Recommendation reco8 = new Recommendation();
            reco8.setClient(client2);
            reco8.setArtisan(artisan5);
            
            Recommendation reco9 = new Recommendation();
            reco9.setClient(client1);
            reco9.setArtisan(artisan1);
            
            Recommendation reco10 = new Recommendation();
            reco10.setClient(client1);
            reco10.setArtisan(artisan7);

            Recommendation reco11 = new Recommendation();
            reco11.setClient(client3);
            reco11.setArtisan(artisan7);

            recoRepo.saveAll(List.of(reco1, reco2, reco3, reco4, reco5, reco6, reco7, reco8, reco9, reco10, reco11));

            /* -----------------------------
            * Messages de démo
            * ----------------------------- */
            Message msg1 = new Message();
            msg1.setSender(artisan7);
            msg1.setReceiver(client1);
            msg1.setContent("Bonjour, je peux vour fournir un menu et un devis pour votre évènement, combien de personnes seront présentes?");
            msg1.setMessageStatus(MessageStatus.DELIVERED);
            msg1.setRead(true);

            Message msg2 = new Message();
            msg2.setSender(client3);
            msg2.setReceiver(artisan1);
            msg2.setContent("Bonjour, j'ai un besoin spécifique en fer forgé, il faudrait passer à mon domicile pour en discuter, seriez-vous disponible prochainement?.");
            msg2.setMessageStatus(MessageStatus.DELIVERED);
            msg2.setRead(true);

            Message msg3 = new Message();
            msg3.setSender(artisan1);
            msg3.setReceiver(client3);
            msg3.setContent("Bonjour, je suis dispo pour intervenir demain à 8h, l'adresse de votre profil est bien correcte?");
            msg3.setMessageStatus(MessageStatus.DELIVERED);
            msg3.setRead(false);

            Message msg4 = new Message();
            msg4.setSender(client2);
            msg4.setReceiver(artisan2);
            msg4.setContent("Bonjour, j'aurais besoin d'un devis pour remettre aux normes mon installation électrique.");
            msg4.setMessageStatus(MessageStatus.DELIVERED);
            msg4.setRead(false);

            Message msg5 = new Message();
            msg5.setSender(artisan3);
            msg5.setReceiver(client2);
            msg5.setContent("Bonjour Sophie, je peux passer jeudi pour voir votre projet de menuisier si vous voulez.");
            msg5.setMessageStatus(MessageStatus.DELIVERED);
            msg5.setRead(false);

            Message msg6 = new Message();
            msg6.setSender(artisan6);
            msg6.setReceiver(client2);
            msg6.setContent("Bonjour Sophie, je peux vous proposer un panel de bière très varié et 100% local pour votre demande. N'hésitez pas à me recontacter si vous êtes interessée.");
            msg6.setMessageStatus(MessageStatus.DELIVERED);
            msg6.setRead(false);

            Message msg7 = new Message();
            msg7.setSender(client3);
            msg7.setReceiver(artisan1);
            msg7.setContent("Merci pour votre rapidité, oui l'adresse est correcte, je vous attend demain à 8h.");
            msg7.setMessageStatus(MessageStatus.DELIVERED);
            msg7.setRead(true);
            messageRepo.saveAll(List.of(msg1, msg2, msg3, msg4, msg5, msg6, msg7));

            System.out.println("Messages de démo insérés : 7 messages d'exemple entre clients et artisans.");
        };
    }
}

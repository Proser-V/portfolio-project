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
import com.atelierlocal.model.MessageStatus;
import com.atelierlocal.model.UploadedPhoto;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.AskingRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.repository.EventCategoryRepo;
import com.atelierlocal.repository.MessageRepo;
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
        AvatarRepo  avatarRepo
    ) {
        return args -> {
            if (clientRepo.count() > 0 || artisanRepo.count() > 0 || categoryRepo.count() > 0) {
                System.out.println("Données déjà présentes, seed ignoré.");
                return;
            }

            /* -----------------------------
             * Catégories d'artisans
             * ----------------------------- */

            ArtisanCategory artCat1 = new ArtisanCategory();
            artCat1.setName("Plombier");
            artCat1.setDescription("Travaux d'installation et de dépannage en plomberie.");

            ArtisanCategory artCat2 = new ArtisanCategory();
            artCat2.setName("Forgeron");
            artCat2.setDescription("Artisanat lié au travail du métal.");

            ArtisanCategory artCat3 = new ArtisanCategory();
            artCat3.setName("Électricien");
            artCat3.setDescription("Installation et rénovation électrique.");

            ArtisanCategory artCat4 = new ArtisanCategory();
            artCat4.setName("Manuisier");
            artCat4.setDescription("Travail du bois et fabrication sur mesure.");

            categoryRepo.saveAll(Arrays.asList(artCat1, artCat2, artCat3, artCat4));


            /* -----------------------------
             * Catégories d'événements
             * ----------------------------- */

            EventCategory depannage = new EventCategory();
            depannage.setName("Dépannage");
            depannage.setArtisanCategoryList(List.of(artCat1, artCat3));

            EventCategory renovation = new EventCategory();
            renovation.setName("Rénovation");
            renovation.setArtisanCategoryList(List.of(artCat4, artCat3));

            eventCategoryRepo.saveAll(List.of(depannage, renovation));

            /* -----------------------------
             * Clients (dont admin)
             * ----------------------------- */

            Client admin = new Client();
            admin.setEmail("admin@mail.com");
            admin.setHashedPassword(passwordService.hashPassword("password"));
            admin.setActive(true);
            admin.setUserRole(UserRole.ADMIN);
            admin.setFirstName("Alice");
            admin.setLastName("Admin");
            admin.setLatitude(48.8566);
            admin.setLongitude(2.3522);
            admin.setPhoneNumber("0600000001");

            Client client1 = new Client();
            client1.setEmail("client1@mail.com");
            client1.setHashedPassword(passwordService.hashPassword("password"));
            client1.setActive(true);
            client1.setUserRole(UserRole.CLIENT);
            client1.setFirstName("Jean");
            client1.setLastName("Dupont");
            client1.setLatitude(45.7640);
            client1.setLongitude(4.8357);
            client1.setPhoneNumber("0600000002");

            Client client2 = new Client();
            client2.setEmail("client2@mail.com");
            client2.setHashedPassword(passwordService.hashPassword("password"));
            client2.setActive(true);
            client2.setUserRole(UserRole.CLIENT);
            client2.setFirstName("Sophie");
            client2.setLastName("Martin");
            client2.setLatitude(43.6047);
            client2.setLongitude(1.4442);
            client2.setPhoneNumber("0600000003");

            clientRepo.saveAll(Arrays.asList(admin, client1, client2));

            /* -----------------------------
             * Artisan 1
             * ----------------------------- */

            Artisan artisan1 = new Artisan();
            artisan1.setEmail("artisan1@mail.com");
            artisan1.setHashedPassword(passwordService.hashPassword("password"));
            artisan1.setActive(true);
            artisan1.setUserRole(UserRole.ARTISAN);
            artisan1.setName("FERPLAY");
            artisan1.setBio("J'ai choisi de me forger un avenir à Dijon en 2020, en me reconvertissant vers ma passion brûlante de toujours. Depuis, je m'efforce de marquer le marché local au fer rouge. Mon fer de lance ? Battre le fer tant qu'il est chaud, tout en évitant de me retrouver entre le marteau et l'enclume et garder la tête froide pour ne pas devenir marteau !");
            artisan1.setCategory(artCat2);
            artisan1.setSiret("12345678901234");
            artisan1.setLatitude(47.322200);
            artisan1.setLongitude(5.039335);
            artisan1.setPhoneNumber("0600000004");
            artisan1.setActivityStartDate(LocalDate.of(2010, 5, 12));

            /* -----------------------------
             * Gallerie photo d'artisan 1
             * ----------------------------- */

            UploadedPhoto photo1 = new UploadedPhoto();
            photo1.setArtisan(artisan1);
            photo1.setExtension(".jpg");
            photo1.setUploadedPhotoUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/shadowmourne2.jpg");
            artisan1.getPhotoGallery().add(photo1);

            UploadedPhoto photo2 = new UploadedPhoto();
            photo2.setArtisan(artisan1);
            photo2.setExtension(".jpg");
            photo2.setUploadedPhotoUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/Epee-inspiree-de-la-Mastersword-8.jpg");
            artisan1.getPhotoGallery().add(photo2);

            UploadedPhoto photo3 = new UploadedPhoto();
            photo3.setArtisan(artisan1);
            photo3.setExtension(".png");
            photo3.setUploadedPhotoUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/epee-the-witcher-geralt-de-riv-9.png");
            artisan1.getPhotoGallery().add(photo3);

            UploadedPhoto photo4 = new UploadedPhoto();
            photo4.setArtisan(artisan1);
            photo4.setExtension(".jpg");
            photo4.setUploadedPhotoUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/Votre-texte-zadazzade-paragraphe-1-copie.jpg");
            artisan1.getPhotoGallery().add(photo4);

            UploadedPhoto photo5 = new UploadedPhoto();
            photo5.setArtisan(artisan1);
            photo5.setExtension(".jpg");
            photo5.setUploadedPhotoUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/mastersword-et-fourreau.jpg");
            artisan1.getPhotoGallery().add(photo5);

            artisanRepo.save(artisan1);

            /* -----------------------------
             * Autres artisans
             * ----------------------------- */

            Artisan artisan2 = new Artisan();
            artisan2.setEmail("artisan2@mail.com");
            artisan2.setHashedPassword(passwordService.hashPassword("password"));
            artisan2.setActive(true);
            artisan2.setUserRole(UserRole.ARTISAN);
            artisan2.setName("Élec Services Dijon");
            artisan2.setBio("Installation et mise en conformité de votre réseau électrique.");
            artisan2.setCategory(artCat3);
            artisan2.setSiret("23456789012345");
            artisan2.setLatitude(45.7640);
            artisan2.setLongitude(4.8357);
            artisan2.setPhoneNumber("0600000005");
            artisan2.setActivityStartDate(LocalDate.of(2015, 3, 8));

            Artisan artisan3 = new Artisan();
            artisan3.setEmail("artisan3@mail.com");
            artisan3.setHashedPassword(passwordService.hashPassword("password"));
            artisan3.setActive(true);
            artisan3.setUserRole(UserRole.ARTISAN);
            artisan3.setName("artCat4 Sud Dijonnais");
            artisan3.setBio("Fabrication sur mesure et rénovation bois.");
            artisan3.setCategory(artCat4);
            artisan3.setSiret("34567890123456");
            artisan3.setLatitude(43.2965);
            artisan3.setLongitude(5.3698);
            artisan3.setPhoneNumber("0600000006");
            artisan3.setActivityStartDate(LocalDate.of(2012, 9, 20));

            artisanRepo.saveAll(Arrays.asList(artisan1, artisan2, artisan3));

            /* -----------------------------
            * Demandes (Askings)
            * ----------------------------- */

            Asking asking1 = new Asking();
            asking1.setTitle("Fuite sous évier");
            asking1.setContent("Bonjour, je rencontre actuellement un problème assez important avec ma artCat1 dans ma cuisine. Une fuite d'eau s'est déclarée sous l'évier, provoquant un débordement dans le meuble inférieur et des traces d'humidité sur le sol. Je souhaite qu'un artisan qualifié intervienne rapidement pour identifier précisément la source de la fuite, remplacer ou réparer les éléments défectueux tels que les tuyaux, joints, robinets ou siphons, et vérifier l'ensemble du réseau sous l'évier afin d'éviter toute fuite supplémentaire. Je souhaiterais également recevoir un devis détaillé avant l'intervention, connaître vos disponibilités dans les prochains jours et vos conditions de travail. Je peux fournir des photos ou d'autres informations si nécessaire. Merci d'avance pour votre retour rapide et votre professionnalisme. Il est très important que l'intervention soit effectuée dans les plus brefs délais, car l'eau pourrait endommager davantage mes meubles et le sol si rien n'est fait rapidement. Merci.");
            asking1.setEventCategory(depannage);
            asking1.setEventLocalisation("Dijon");
            asking1.setEventDate(LocalDateTime.now().plusDays(2));
            asking1.setClient(client1);
            asking1.setArtisanCategory(artCat1);

            Asking asking2 = new Asking();
            asking2.setTitle("Installation de chauffe-eau");
            asking2.setContent("Je souhaite remplacer mon ancien chauffe-eau par un modèle plus récent. Besoin d'un devis.");
            asking2.setClient(client2);
            asking2.setArtisanCategory(artCat1);

            // On peut ajouter un statut si ton enum AskingStatus le permet :
            asking1.setStatus(AskingStatus.PENDING);
            asking2.setStatus(AskingStatus.PENDING);

            // Sauvegarde
            askingRepo.saveAll(List.of(asking1, asking2));

            /* -----------------------------
             * Avatars
             * ----------------------------- */
            Avatar avatar1 = new Avatar();
            avatar1.setExtension("jpeg");
            avatar1.setAvatarUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/devenir-forgeron.jpeg");
            avatar1.setUser(artisan1);
            artisan1.setAvatar(avatar1);
            artisanRepo.save(artisan1);

            Avatar avatar2 = new Avatar();
            avatar2.setExtension("jpg");
            avatar2.setAvatarUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/horaires-patissier.jpg");
            avatar2.setUser(artisan2);
            artisan2.setAvatar(avatar2);
            artisanRepo.save(artisan2);

            Avatar avatar3 = new Avatar();
            avatar3.setExtension("jpg");
            avatar3.setAvatarUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/la-matriarche-de-veruschka-zarate-couture-sur-papier-foundation-paper-piecing.jpg");
            avatar3.setUser(artisan3);
            artisan3.setAvatar(avatar3);
            artisanRepo.save(artisan3);

            Avatar avatar4 = new Avatar();
            avatar4.setExtension("jpg");
            avatar4.setAvatarUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/random2.jpg");
            avatar4.setUser(client1);
            client1.setAvatar(avatar4);
            clientRepo.save(client1);

            Avatar avatar5 = new Avatar();
            avatar5.setExtension("jpg");
            avatar5.setAvatarUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/random3.jpg");
            avatar5.setUser(client2);
            client2.setAvatar(avatar5);
            clientRepo.save(client2);

            Avatar avatar6 = new Avatar();
            avatar6.setExtension("jpg");
            avatar6.setAvatarUrl("https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/random4.jpg");
            avatar6.setUser(admin);
            admin.setAvatar(avatar6);
            clientRepo.save(admin);
            
            /* -----------------------------
            * Messages de démo
            * ----------------------------- */
            Message msg1 = new Message();
            msg1.setSender(client1);
            msg1.setReceiver(artisan1);
            msg1.setContent("Bonjour, pourriez-vous intervenir pour une fuite dans ma salle de bain ?");
            msg1.setTimestamp(LocalDateTime.now().minusDays(2));
            msg1.setMessageStatus(MessageStatus.DELIVERED);
            msg1.setRead(true);

            Message msg2 = new Message();
            msg2.setSender(artisan1);
            msg2.setReceiver(client1);
            msg2.setContent("Bonjour Jean, oui bien sûr. Pouvez-vous m'envoyer votre adresse exacte ?");
            msg2.setTimestamp(LocalDateTime.now().minusDays(1).minusHours(3));
            msg2.setMessageStatus(MessageStatus.DELIVERED);
            msg2.setRead(true);

            Message msg3 = new Message();
            msg3.setSender(client1);
            msg3.setReceiver(artisan1);
            msg3.setContent("C'est au 10 rue du Bourg, 21000 Dijon. Merci !");
            msg3.setTimestamp(LocalDateTime.now().minusHours(6));
            msg3.setMessageStatus(MessageStatus.DELIVERED);
            msg3.setRead(false);

            Message msg4 = new Message();
            msg4.setSender(client2);
            msg4.setReceiver(artisan2);
            msg4.setContent("Bonjour, j'aurais besoin d'un devis pour remettre aux normes mon installation électrique.");
            msg4.setTimestamp(LocalDateTime.now().minusDays(1));
            msg4.setMessageStatus(MessageStatus.DELIVERED);
            msg4.setRead(false);

            Message msg5 = new Message();
            msg5.setSender(artisan3);
            msg5.setReceiver(client2);
            msg5.setContent("Bonjour Sophie, je peux passer jeudi pour voir votre projet de artCat4 si vous voulez.");
            msg5.setTimestamp(LocalDateTime.now().minusHours(12));
            msg5.setMessageStatus(MessageStatus.DELIVERED);
            msg5.setRead(false);

            messageRepo.saveAll(List.of(msg1, msg2, msg3, msg4, msg5));

            System.out.println("Messages de démo insérés : 5 messages d'exemple entre clients et artisans.");
        };
    }
}

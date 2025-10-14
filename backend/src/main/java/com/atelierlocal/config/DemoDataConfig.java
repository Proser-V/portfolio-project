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
import com.atelierlocal.model.Client;
import com.atelierlocal.model.EventCategory;
import com.atelierlocal.model.Message;
import com.atelierlocal.model.MessageStatus;
import com.atelierlocal.model.UploadedPhoto;
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.repository.EventCategoryRepo;
import com.atelierlocal.repository.MessageRepo;
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
        PasswordService passwordService,
        MessageRepo messageRepo
    ) {
        return args -> {
            if (clientRepo.count() > 0 || artisanRepo.count() > 0 || categoryRepo.count() > 0) {
                System.out.println("Données déjà présentes, seed ignoré.");
                return;
            }

            /* -----------------------------
             * Catégories d'artisans
             * ----------------------------- */
            ArtisanCategory plomberie = new ArtisanCategory();
            plomberie.setName("Plomberie");
            plomberie.setDescription("Travaux d'installation et de dépannage en plomberie.");

            ArtisanCategory electricite = new ArtisanCategory();
            electricite.setName("Électricité");
            electricite.setDescription("Installation et rénovation électrique.");

            ArtisanCategory menuiserie = new ArtisanCategory();
            menuiserie.setName("Menuiserie");
            menuiserie.setDescription("Travaux de menuiserie et fabrication sur mesure.");

            categoryRepo.saveAll(Arrays.asList(plomberie, electricite, menuiserie));


            /* -----------------------------
             * Catégories d'événements
             * ----------------------------- */

            EventCategory depannage = new EventCategory();
            depannage.setName("Dépannage");
            depannage.setArtisanCategoryList(List.of(plomberie, electricite));

            EventCategory renovation = new EventCategory();
            renovation.setName("Rénovation");
            renovation.setArtisanCategoryList(List.of(menuiserie, electricite));

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
            artisan1.setName("Plomberie Dijon Service");
            artisan1.setBio("Spécialiste du dépannage rapide et installations sanitaires.");
            artisan1.setCategory(plomberie);
            artisan1.setSiret("12345678901234");
            artisan1.setLatitude(48.8700);
            artisan1.setLongitude(2.3500);
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
            artisan2.setCategory(electricite);
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
            artisan3.setName("Menuiserie Sud Dijonnais");
            artisan3.setBio("Fabrication sur mesure et rénovation bois.");
            artisan3.setCategory(menuiserie);
            artisan3.setSiret("34567890123456");
            artisan3.setLatitude(43.2965);
            artisan3.setLongitude(5.3698);
            artisan3.setPhoneNumber("0600000006");
            artisan3.setActivityStartDate(LocalDate.of(2012, 9, 20));

            artisanRepo.saveAll(Arrays.asList(artisan1, artisan2, artisan3));

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
            msg3.setContent("C’est au 10 rue du Bourg, 21000 Dijon. Merci !");
            msg3.setTimestamp(LocalDateTime.now().minusHours(6));
            msg3.setMessageStatus(MessageStatus.DELIVERED);
            msg3.setRead(false);

            Message msg4 = new Message();
            msg4.setSender(client2);
            msg4.setReceiver(artisan2);
            msg4.setContent("Bonjour, j’aurais besoin d’un devis pour remettre aux normes mon installation électrique.");
            msg4.setTimestamp(LocalDateTime.now().minusDays(1));
            msg4.setMessageStatus(MessageStatus.DELIVERED);
            msg4.setRead(false);

            Message msg5 = new Message();
            msg5.setSender(artisan3);
            msg5.setReceiver(client2);
            msg5.setContent("Bonjour Sophie, je peux passer jeudi pour voir votre projet de menuiserie si vous voulez.");
            msg5.setTimestamp(LocalDateTime.now().minusHours(12));
            msg5.setMessageStatus(MessageStatus.DELIVERED);
            msg5.setRead(false);

            messageRepo.saveAll(List.of(msg1, msg2, msg3, msg4, msg5));

            System.out.println("Messages de démo insérés : 5 messages d'exemple entre clients et artisans.");
        };
    }
}

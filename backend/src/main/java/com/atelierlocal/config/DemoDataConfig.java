package com.atelierlocal.config;

import java.time.LocalDate;
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
import com.atelierlocal.model.UserRole;
import com.atelierlocal.repository.ArtisanCategoryRepo;
import com.atelierlocal.repository.ArtisanRepo;
import com.atelierlocal.repository.ClientRepo;
import com.atelierlocal.repository.EventCategoryRepo;
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
        PasswordService passwordService
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
             * Artisans
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

            System.out.println("Données de démo insérées : 1 admin, 2 clients, 3 artisans, 3 catégories d'artisans, 2 catégories d'événements.");
            System.out.println("Identifiants de test :");
            System.out.println("  Admin: admin@mail.com / password");
            System.out.println("  Clients: client1@mail.com, client2@mail.com / password");
            System.out.println("  Artisans: artisan1@mail.com, artisan2@mail.com, artisan3@mail.com / password");
        };
    }
}

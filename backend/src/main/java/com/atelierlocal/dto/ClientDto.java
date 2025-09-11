package com.atelierlocal.dto;

import java.util.UUID;

public class ClientDto {
        private UUID id;
        private String email;
        private String avatarUrl;
        private String phoneNumber;
        private String firstName;
        private String lastName;

        public ClientDto(UUID id, String email, String avatarUrl, String phoneNumber, String firstName, String lastName) {
            this.id = id;
            this.email = email;
            this.avatarUrl = avatarUrl;
            this.phoneNumber = phoneNumber;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    
        // Getters et setters
    
        public UUID getId() { return id; }
    
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    
    
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatar(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; } 

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    
}

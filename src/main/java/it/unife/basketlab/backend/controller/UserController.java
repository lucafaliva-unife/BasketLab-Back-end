package it.unife.basketlab.backend.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unife.basketlab.backend.model.User;
import it.unife.basketlab.backend.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /*
    Crea un nuovo utente nel DB dopo:
        -> aver validato i campi dell'oggetto ricevuto;
        -> essersi assicurato che il nome utente non esista già;
        -> essersi assicurato che il tipo sia "Allenatore" oppure "Amministratore";
    Il codice di ritorno è:
        -> "201 Created" se i controlli vengono passati e l'utente viene creato correttamente;
        -> "400 Bad Request" se l'oggetto passato non supera i controlli di validazione dei campi o il tipo non è valido;
        -> "409 Conflict" se il nome utente passato è già usato da un altro utente;
    */
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid User user) {
        if(userService.userExistsByUsername(user.getUsername())) {
            return ResponseEntity.status(409).build();
        }
        if(!user.getType().equals("Allenatore") && !user.getType().equals("Amministratore")) {
            return ResponseEntity.badRequest().build();
        }
        user.setId_user(null);
        userService.saveUser(user);
        return ResponseEntity.status(201).build();
    }

    /*
    Elimina un utente esistente dal DB dopo:
        -> essersi assicurato che l'ID passato esista;
    Il codice di ritorno è:
        -> "204 No Content" se il controllo viene passato ed l'utente viene eliminato correttamente;
        -> "404 Not Found" se l'ID passato non esiste;
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        if(!userService.userExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build();
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    Modifica un utente esistente dopo:
        -> aver validato i campi dell'oggetto ricevuto;
        -> essersi assicurato che l'ID dell'utente da modificare esista;
        -> essersi assicurato che il nome utente non sia già usato da un altro utente;
    Il codice di ritorno è:
        -> "204 No Content" se i controlli vengono passati e l'utente viene modificato correttamente;
        -> "404 Not Found" se l'ID passato non esiste;
        -> "409 Conflict" se il nome utente passato appartiene già a un altro utente;
    */
    @PutMapping("/{id}")
    public ResponseEntity<Void> editUserById(@PathVariable UUID id, @RequestBody @Valid User user) {
        if(!userService.userExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if(userService.userExistsByUsernameExcludeId(user.getUsername(), user.getId_user())) {
            return ResponseEntity.status(409).build();
        }
        user.setId_user(id);
        userService.saveUser(user);
        return ResponseEntity.noContent().build();
    }

    /*
    Effettua il login di un utente dopo:
        -> essersi assicurato che l'username esista;
        -> aver verificato che la password sia corretta;
        -> aver verificato che il tipo utente corrisponda;
    Il codice di ritorno è:
        -> "200 OK" se l'accesso è valido;
        -> "404 Not Found" se l'username non esiste;
        -> "401 Unauthorized" se la password o il tipo utente sono errati;
    */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid User user) {
        if(!userService.userExistsByUsername(user.getUsername())) {
            return ResponseEntity.notFound().build();
        }
        if(!userService.verifyCredentials(user.getUsername(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }
        if(!userService.verifyType(user.getUsername(), user.getType())) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok().build();
    }

}
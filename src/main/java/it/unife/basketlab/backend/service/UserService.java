package it.unife.basketlab.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unife.basketlab.backend.model.User;
import it.unife.basketlab.backend.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    /*
    Ritorna l'utente dato il suo username.
    -> Se lo username esiste allora ritorna l'utente;
    -> Se lo username non esiste allora ritorna null.
    */
    private User getUserByUsername(String username) {
        List<User> allUsers= repository.findAll();
        for(User user : allUsers) {
            if(user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /*
    Ritorna la password dell'utente dato il suo username.
    -> Se lo username esiste allora ritorna la sua password;
    -> Se lo username non esiste allora ritorna null.
    */
    private String getPasswordByUsername(String username) {
        User user= getUserByUsername(username);
        if(user != null) {
            return user.getPassword();
        }
        return null;
    }

    /*
    Ritorna il tipo di permessi dell'utente dato il suo username.
    -> Se lo username esiste allora ritorna i suoi permessi;
    -> Se lo username non esiste allora ritorna null.
    */
    private String getTypeByUsername(String username) {
        User user= getUserByUsername(username);
        if(user != null) {
            return user.getType();
        }
        return null;
    }

    /*
    Ritorna un booleano che indica se lo username esiste o no.
    */
    public boolean userExistsByUsername(String username) {
        User user= getUserByUsername(username);
        if(user != null) {
            return true;
        }
        return false;
    }

    /*
    Ritorna un booleano che indica se lo username esiste o no.
    */
    public boolean userExistsById(UUID id) {
        return repository.existsById(id);
    }

    /*
    Ritorna un booleano che indica se la password è associata allo username o no.
    Se lo username non esiste allora ritorna false.
    */
    public boolean verifyCredentials(String username, String password) {
        String psw= getPasswordByUsername(username);
        if(psw != null) {
            return psw.equals(password);
        } else {
            return false;
        }
    }

    /*
    Ritorna un booleano che indica se i permessi corrispondono allo username o no.
    Se lo username non esiste allora ritorna false.
    */
    public boolean verifyType(String username, String type) {
        String permission= getTypeByUsername(username);
        if(permission != null) {
            return permission.equals(type);
        } else {
            return false;
        }
    }

    /*
    Salva un nuovo user se l'ID dello user passato non è già presente, altrimenti modifica quello già presente.
    Infine ritorna la risposta HTTP.
    */
    public User saveUser(User user) {
        return repository.save(user);
    }

    /*
    Elimina lo user che ha come ID l'ID passato e ritorna la risposta HTTP.
    */
    public void deleteUserById(UUID id) {
        repository.deleteById(id);
    }

    /*
    Funziona come userExistsByUsername() ma permette di escludere un ID dalla ricerca.
    */
    public boolean userExistsByUsernameExcludeId(String username, UUID id) {
        List<User> allUsers= repository.findAll();
        for(User user : allUsers) {
            if(user.getUsername().equals(username) && !user.getId_user().equals(id)) {
                return true;
            }
        }
        return false;
    }

}

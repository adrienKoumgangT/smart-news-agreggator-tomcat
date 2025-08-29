package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user;



import java.util.List;
import java.util.Optional;

public class Authentication {

    /*
    public static UserToken checkLogin(UserLoginView UserLoginView) throws Exception {



        Optional<UserLogin> optionalUserLogin = UserLogin.get(userLoginModel.getEmail());


        if(optionalUserLogin.isEmpty()) {
            throw new UnregisteredUserException("Unregistered user");
        }

        UserLogin userLogin = optionalUserLogin.get();
        if(!PasswordChecker.checkPassword(userLogin.getPassword(), UserLoginView.getPassword())) {
            throw new InvalidAuthentificationException("Incorrect Password");
        }

        Optional<User> optionalUser = UserRepository.getById(userLogin.getIdUser());
        if(optionalUser.isEmpty()) {
            throw new UnregisteredUserException("Unregistered user");
        }

        UserRepository user = optionalUser.get();
        return new UserTokenBuilder(user).build();
    }*/

}

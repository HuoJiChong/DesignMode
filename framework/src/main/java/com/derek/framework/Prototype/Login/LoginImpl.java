package com.derek.framework.Prototype.Login;

public class LoginImpl implements Login {

    @Override
    public void Login() {
        User loginedUser = new User();
        loginedUser.name = "derek";
        loginedUser.age = 12;
        loginedUser.phoneNum = "18888888888";
        loginedUser.address = new Address("beijing","changping","anningzhuang");

        LoginSession.getInstance().setLoginedUser(loginedUser);

        System.out.println( " loginUser " + loginedUser.toString() );
        User user = loginedUser.clone();
        user.name = "helld";
        user.age = 45;
        user.phoneNum = "12344567";
        user.address.city = "shanghai";

        System.out.println( " loginUser " + loginedUser.toString() );
        System.out.println( " user : " + user.toString() );
    }
}

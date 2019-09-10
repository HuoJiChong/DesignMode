package com.derek.framework.Prototype.Login;

public class LoginSession {
    private static class LoginSessionHolder{
        private static LoginSession instance = new LoginSession();
    }

    public static LoginSession getInstance(){
        return LoginSessionHolder.instance;
    }

    private User loginedUser;

    public User getLoginedUser() {
        return loginedUser;
    }

    void setLoginedUser(User loginedUser) {
        this.loginedUser = loginedUser;
    }
}

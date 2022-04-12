package models;

public class UserModel
{
    private String Login;
    private String Password;

    public void SetLogin(String login) {
        this.Login = login;
    }

    public void SetPassword(String password) {
        this.Password = password;
    }

    public String GetLogin() {
        return this.Login;
    }

    public String GetPassword() {
        return this.Password;
    }
}

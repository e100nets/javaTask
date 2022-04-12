package models;

public final class DataBaseModel
{
    private String url = "jdbc:mysql://localhost:3307/database1?autoReconnect=true&useSSL=false";
    private String login = "root";
    private String password = "cat02";

    public void SetUrl(String URL) {
        this.url = URL;
    }

    public void SetLogin(String LOGIN) {
        this.login = LOGIN;
    }

    public void SetPassword(String PASSWORD) {
        this.password = PASSWORD;
    }

    public String GetUrl() {
        return this.url;
    }

    public String GetLogin() {
        return this.login;
    }

    public String GetPassword() {
        return this.password;
    }
}

package services;

import models.LogStatus;
import models.UserModel;

public class UserService
{
    private LogStatus adminLogStatus = LogStatus.LoggedOut;
    private final UserModel UModel;

    public UserService(UserModel UModel){
        this.UModel = UModel;
    }

    public void SignIn(String login)
    {
        this.adminLogStatus = LogStatus.LoggedIn;
        UModel.SetLogin(login);
    }

    public String GetCurrentUser()
    {
        return UModel.GetLogin();
    }

    public void LogOut()
    {
        this.adminLogStatus = LogStatus.LoggedOut;
    }
}

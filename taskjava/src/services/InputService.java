package services;

import models.Exceptions;
import models.UserModel;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.swing.*;


public class InputService {

    private final DataBaseService DBService;
    private final UserModel UModel;
    private final UserService UService;
    private boolean deleteStatus;

    public InputService(DataBaseService DBService, UserModel UModel, UserService UService) {
        this.DBService = DBService;
        this.UModel = UModel;
        this.UService = UService;
    }

    // проверяем поля для регистрации
    public void Try_SignUp(String login, String password, String passwordRepeat) throws Exceptions, SQLException, ClassNotFoundException {

            Check_NoEmptyFieldsExist(login, password, passwordRepeat);

            Check_PasswordMatch(password, passwordRepeat);

            DBService.SignUp(login,password);
    }

    public void DisplayErrorMessage(String error)
    {
        JOptionPane.showMessageDialog(null, error, "Error!", JOptionPane.ERROR_MESSAGE);
    }

    public void Check_NoEmptyFieldsExist(String @NotNull ... inputFields) throws Exceptions {
        for (String field : inputFields) {
            if (field.isEmpty()) throw new Exceptions("Fill all the fields");
            if (field.length() <= 3)
                throw new Exceptions("Login and Password should be at least 3 characters");
        }
    }

    public void Check_PasswordMatch(@NotNull String password, String passwordRepeat) throws Exceptions {
        if (!password.equals(passwordRepeat))
            throw new Exceptions("Password doesnt match");
    }

    public void Try_SignIn(String login, String password) throws Exceptions, SQLException, ClassNotFoundException {

            Check_NoEmptyFieldsExist(login, password);
            DBService.SignIn(login, password);

    }

    public void Try_RunMedia(String mediaName) {
        try {
            DBService.RunMedia(mediaName);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            System.out.println(e);
        }
    }

    public String[] Try_AddMedia(DefaultListModel<String> listModel) {
        String[] mediaData = new String[]{};
        try {
            mediaData = GetDataFromFile(listModel);
            DBService.AddMedia(mediaData);
        } catch (SQLException | ClassNotFoundException | NullPointerException | Exceptions | IOException e) {
            DisplayErrorMessage("Cant add a file");
        }
        return mediaData;
    }

    @Contract("_ -> new")
    public String @NotNull [] GetDataFromFile(DefaultListModel<String> listModel) throws NullPointerException, Exceptions {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);

        if (chooser.getSelectedFile() == null)
            throw new NullPointerException("No file selected");

        if (listModel.contains(chooser.getSelectedFile().getName()))
            throw new Exceptions("This file name already exist in the list");

        FileInputStream fis;
        byte[] readedFile = new byte[0];

        try {
            fis = new FileInputStream(chooser.getSelectedFile().getPath());
            readedFile = fis.readAllBytes();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String byteString = new String(readedFile, StandardCharsets.UTF_8);

        return new String[]{
                chooser.getSelectedFile().getName(),
//                byteString,
                UModel.GetLogin(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                chooser.getSelectedFile().getAbsolutePath().replaceAll("\\\\", "\\\\\\\\")
        };
    }

    public void checkChangeStatus(String login, String mediaName){
        deleteStatus = Objects.equals(login, mediaName);
        if (!deleteStatus)
            DisplayErrorMessage("You dont have permissions");
    }

    public void Try_DeleteMedia(String mediaName, String login) {

        try {
            DBService.DeleteMedia(mediaName, login);
        } catch (SQLException | ClassNotFoundException | Exceptions e) {
            DisplayErrorMessage("You dont have permissions ");
        }
    }
}

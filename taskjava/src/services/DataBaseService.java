package services;

import models.Exceptions;
import models.DataBaseModel;

import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataBaseService {

    private Connection Session = null;
    private final DataBaseModel DBModel;
    private final UserService UService;

    private final String url;
    private final String login;
    private final String password;


    public DataBaseService(DataBaseModel DBModel, UserService UService) {
        this.DBModel = DBModel;
        this.UService = UService;
        this.url = DBModel.GetUrl();
        this.login = DBModel.GetLogin();
        this.password = DBModel.GetPassword();
    }


    // подключение к бд
    public void SetupDataBaseConnection() {
        try {
            Connect();
            SQL_TestConnectivity();
            Disconnect();
        } catch (SQLException | ClassNotFoundException e) {
            System.exit(1);
        }
    }

    // подключение к бд
    public void Connect() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Session = DriverManager.getConnection(this.url, this.login, this.password);
    }


    private void SQL_TestConnectivity() throws SQLException {
        String query = "SELECT * FROM user;";
        Session.createStatement().executeQuery(query);
    }

    // отключение от бд
    public void Disconnect() throws SQLException {
        if (Session != null) {
            Session.close();
            Session = null;
        }
    }

    // регистрация
    public void SignUp(String login, String password) throws SQLException, ClassNotFoundException, Exceptions {
        Connect();
        SQL_Check_LoginAvailable(login);
        SQL_SignUp(login, password);
        SignInLocally(login);
        Disconnect();
    }

    // проверка логина при регистрации
    private void SQL_Check_LoginAvailable(String login) throws SQLException, Exceptions {
        String query = "SELECT * FROM user WHERE UserName='" + login + "';";
        ResultSet rt = Session.createStatement().executeQuery(query);
        if (rt.next()) throw new Exceptions("Login is occupied");
    }

    // добавление логина в бд
    private void SQL_SignUp(String login, String password) throws SQLException {
        String query = "INSERT INTO user(UserName, Password) VALUES ('" + login + "', '" + password + "');";
        Session.createStatement().executeUpdate(query);
    }

    // авторизация
    public void SignIn(String login, String password) throws SQLException, ClassNotFoundException, Exceptions {
        Connect();
        SQL_Check_UserExist(login, password);
        SignInLocally(login);
        Disconnect();
    }

    // проверка логина при входе в систему
    private void SQL_Check_UserExist(String login, String password) throws SQLException, Exceptions {
        String query = "SELECT * FROM user WHERE UserName='" + login + "' AND Password='" + password + "';";
        ResultSet rt = Session.createStatement().executeQuery(query);
        if (!rt.isBeforeFirst()) {
            throw new Exceptions("Invalid login or password");
        }
    }

    // изменение статуса
    private void SignInLocally(String login) {
        UService.SignIn(login);
    }

    // возвращает список добавленных файлов
    public String @NotNull [] GetMediaList() throws SQLException, ClassNotFoundException {
        String[] mediaList;
        Connect();
        mediaList = SQL_GetMediaList();
        Disconnect();
        return mediaList;
    }


    // возвращает список файлов через sql запрос
    private String @NotNull [] SQL_GetMediaList() throws SQLException {
        String query = "SELECT FileName FROM media;";
        ResultSet dataSet = Session.createStatement().executeQuery(query);
        return ConvertMediaListToStringArray(dataSet);
    }

    // преобразование списка файлов в массив String
    private String @NotNull [] ConvertMediaListToStringArray(@NotNull ResultSet DataSet) throws SQLException {
        List<String> dataList = new ArrayList<>();
        while (DataSet.next()) {
            dataList.add(DataSet.getString(1));
        }

        String[] dataStringArray = new String[dataList.size()];
        dataList.toArray(dataStringArray);
        return dataStringArray;
    }

    // список параметров файла
    public String @NotNull [] GetMediaDescription(String itemName) throws SQLException, ClassNotFoundException {
        Connect();
        String[] ItemDescription = SQL_GetItemDescription(itemName);
        Disconnect();
        return ItemDescription;
    }

    // список параметров через sql запрос
    private String @NotNull [] SQL_GetItemDescription(String itemName) throws SQLException {
        String query = "SELECT AddedBy, UploadDate, Location FROM media WHERE FileName='" + itemName + "';";
        ResultSet dataSet = Session.createStatement().executeQuery(query);
        return ConvertItemDescriptionToStringArray(dataSet);
    }

    public String getAddedBy(String itemName) throws SQLException, ClassNotFoundException {
        String user = null;
        Connect();
        String query = "SELECT AddedBy FROM media WHERE FileName='" + itemName + "';";
        ResultSet dataSet = Session.createStatement().executeQuery(query);
        if (dataSet.next())
            user = dataSet.getString(1);
        Disconnect();
        return user;
    }

    // преобразование списка параметров в массив String
    private String @NotNull [] ConvertItemDescriptionToStringArray(@NotNull ResultSet dataSet) throws SQLException {
        List<String> dataList = new ArrayList<>();
        int i = 1;
        if (dataSet.next()) {
            int dataSetSize = dataSet.getMetaData().getColumnCount();
            while (i <= dataSetSize) {
                dataList.add(dataSet.getString(i++));
            }

            String[] dataStringArray = new String[dataList.size()];
            dataList.toArray(dataStringArray);
            return dataStringArray;
        }
        return new String[4];
    }

    // открытие файла
    public void RunMedia(String mediaName) throws SQLException, ClassNotFoundException, IOException {
        Connect();
        Desktop myDesktop = Desktop.getDesktop();
        myDesktop.open(new File(SQL_GetMediaPath(mediaName)));
        Disconnect();
    }

    // путь к файлу
    private String SQL_GetMediaPath(String mediaName) throws SQLException {
        String query = "SELECT Location FROM media where FileName = '" + mediaName + "';";
        ResultSet dataset = Session.createStatement().executeQuery(query);
        dataset.next();
        return dataset.getString(1);
    }

    // добавление файла
    public void AddMedia(String[] mediaData) throws SQLException, ClassNotFoundException, IOException {
        Connect();
        SQL_AddMedia(mediaData);
        Disconnect();
    }

    // добавление файла через sql запрос
    private void SQL_AddMedia(String @NotNull [] mediaData) throws SQLException, IOException {
        File file = new File(mediaData[3]);
        try (FileInputStream fis = new FileInputStream(file); PreparedStatement ps = Session.prepareStatement
                ("INSERT INTO media(FileName, Data, AddedBy, UploadDate, Location) VALUES ('" + mediaData[0] + "',?, '" +
                         mediaData[1] + "'," +
                         " '" + mediaData[2] + "', '" + mediaData[3] + "');")) {
            ps.setBinaryStream(1, fis, (int) file.length());
            ps.executeUpdate();
        }
    }

    // удаление файла
    public void DeleteMedia(String mediaName, String login) throws SQLException, ClassNotFoundException, Exceptions {
        Connect();
        SQL_DeleteMedia(mediaName, login);
        Disconnect();
    }

    // удаление файла через sql запрос
    private void SQL_DeleteMedia(String mediaName, String login) throws SQLException, Exceptions {
        String queryUser = "SELECT * FROM media WHERE FileName='" + mediaName + "';";
        ResultSet rt = Session.createStatement().executeQuery(queryUser);
        if (rt.next()) {
            if (!Objects.equals(rt.getString("AddedBy"), login)) {
                throw new Exceptions("You dont have permission to delete this file");
            } else {
                String query = "DELETE FROM media WHERE FileName='" + mediaName + "';";
                Session.createStatement().executeUpdate(query);
            }
        }
    }
}

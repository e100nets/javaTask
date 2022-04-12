package servlets;

import models.Exceptions;
import models.DataBaseModel;
import models.UserModel;
import services.DataBaseService;
import services.InputService;
import services.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;


public class StartupServlet extends HttpServlet {

    private static DataBaseModel dataBaseModel;
    protected static DataBaseService dataBaseService;
    protected static UserModel userModel;
    protected static UserService userService;
    protected static InputService inputService;

    // инициилизируем логику
    public void init(){
        dataBaseModel = new DataBaseModel();
        userModel = new UserModel();
        userService = new UserService(userModel);
        dataBaseService = new DataBaseService(dataBaseModel,userService);
        inputService = new InputService(dataBaseService,userModel,userService);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.getWriter().append("Served at: ").append(request.getContextPath());

        RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp"); // привязываем к файлу
        dispatcher.forward(request,response); // перенаправление
    }

    //геттеры для последующей инициализации в других сервлетах, чтобы ничего не потерять
    public static DataBaseModel getDataBaseModel() {
        return dataBaseModel;
    }

    public static DataBaseService getDataBaseService() {
        return dataBaseService;
    }

    public static UserModel getUserModel() {
        return userModel;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static InputService getInputService() {
        return inputService;
    }

    // записываем из вёрстки в переменные
    // проверям кнопки и выполняем условие
    // если вход - проверка бд + перенаправление
    // регистрация - перенаправление на другой сервлет
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        try {
            if(request.getParameter("signupbutton") != null) {
                response.sendRedirect("/registration");
            }
            if(request.getParameter("signinbutton") != null) {
                inputService.Try_SignIn(login, password);
                response.sendRedirect("/home");
            }
        } catch (Exceptions e) {
            response.sendRedirect("/");
            e.printStackTrace();
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }
}

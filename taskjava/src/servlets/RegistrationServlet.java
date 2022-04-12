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

public class RegistrationServlet extends HttpServlet {

    DataBaseModel dataBaseModel;
    DataBaseService dataBaseService;
    UserModel userModel;
    UserService userService;
    InputService inputService;

    public void init(){ // Инициализируем логику (продолжаем ее - берем статик)
        dataBaseModel = StartupServlet.getDataBaseModel();
        userModel = StartupServlet.getUserModel();
        userService = StartupServlet.getUserService();
        dataBaseService = StartupServlet.getDataBaseService();
        inputService = StartupServlet.getInputService();
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.getWriter().append("Served at: ").append(request.getContextPath());

        RequestDispatcher dispatcher = request.getRequestDispatcher("regis.jsp"); // привязываем сервлет к файлу
        dispatcher.forward(request,response); // перенаправляем
    }


    // тут получаем поля из вёрстки и записываем их
    // затем проверяем нажатия кнопки и исходя из того,
    // какая кнопка нажата - делаем действия - либо
    // перенаправляем пользователя сюда же (в случае неудачи или воли пользователя вернуться на страницу логина)
    // либо пробуем зарегистрироваться + перенаправление
    // на ссылку с домашней страницей
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String repeat = request.getParameter("repeatPassword");

        try {
            if(request.getParameter("signupbutton") != null) {
                inputService.Try_SignUp(login,password,repeat);
                response.sendRedirect("/home");
            }
            if(request.getParameter("signinbutton") != null) {
                response.sendRedirect("/");
            }
        } catch (Exceptions e) {
            response.sendRedirect("/registration");
            e.printStackTrace();
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }
}

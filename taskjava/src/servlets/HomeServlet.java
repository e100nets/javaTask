package servlets;

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
import java.util.List;

public class HomeServlet extends HttpServlet {

    DataBaseModel dataBaseModel;
    DataBaseService dataBaseService;
    UserModel userModel;
    UserService userService;
    InputService inputService;

    // иницилизируем логику
    public void init() {
        dataBaseModel = StartupServlet.getDataBaseModel();
        userModel = StartupServlet.getUserModel();
        userService = StartupServlet.getUserService();
        dataBaseService = StartupServlet.getDataBaseService();
        inputService = StartupServlet.getInputService();
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());

        RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp"); // ривязываем сервлет к файлу
        request.setAttribute("currentUser", userModel.GetLogin()); // создаем переменную с логином текущего пользователя

        // получаем список файлов, записываем его в атрибут для передачи в вёрстку и оборачиваем все в исключения
        try {
            List<String> mediaList = List.of(dataBaseService.GetMediaList());
            request.setAttribute("filelist", mediaList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        dispatcher.forward(request, response); // перенаправляем запрос
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    }
}

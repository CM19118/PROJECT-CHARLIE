package com.example.projectgui;

public class SessionManager {
    private static UserSession userSession;

    public static void setUsuarioSesion(UserSession sesion) {
        userSession = sesion;
    }

    public static UserSession getUsuarioSesion() {
        return userSession;
    }

}

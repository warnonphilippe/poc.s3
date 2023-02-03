package be.civadis.poc.s3.utils;

public class ApplicationInfosUtils {

    private static ThreadLocal<String> currentApp = new ThreadLocal<>();
    private static ThreadLocal<String> currentAppUser = new ThreadLocal<>();


    private ApplicationInfosUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Retourne l'application courante au nom de laquelle l'appel à GPDOC est effectué
     * Recherche d'abord dans le SecurityContext puis dans la threadLocal si rien dans le SecurityContext
     *
     * @return
     */
    public static String getCurrentApp() {
        return getCurrentAppFromThreadLocal();
    }

    /**
     * Initialise l'application courante dans la threadLocal
     * Cette valeur sera retournée par le getCurrentApp s'il n'y a pas d'application courante dans le SecurityContext
     * Par exemple, utile dans les tests, listeners ne partageant pas le SecurityContext,...
     *
     * @param defaultApp
     */
    public static void initDefaultCurrentApp(String defaultApp) {
        currentApp.set(defaultApp);
    }

    /**
     * Set le user de l'application courante dans la threadLocal
     *
     * @param appUser
     */
    public static void setAppUser(String appUser) {
        currentAppUser.set(appUser);
    }

    /**
     * Retourn le user de l'application courante dans la threadLocal
     * @return appUser
     */
    public static String getAppUser() {
        return currentAppUser.get();
    }

    /**
     * Supprime l'application et le user de la threadLocal
     */
    public static void clear() {
        currentApp.remove();
        currentAppUser.remove();
    }

    /**
     * Supprime l'application de la threadLocal
     */
    public static void clearApplication() {
        currentApp.remove();
    }

    /**
     * Recherche le code de l'application appelant dans le threadLocal
     */
    private static String getCurrentAppFromThreadLocal() {
        return currentApp.get();
    }


}


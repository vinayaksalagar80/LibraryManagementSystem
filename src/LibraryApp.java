import auth.Login;
import menu.AdminMenu;
import menu.StudentMenu;

public class LibraryApp {

    public static void main(String[] args) {
        String role = Login.login();

        if(role != null) {
            if(role.equals("admin")) {
                AdminMenu.showAdminMenu();
            } else if(role.equals("student")) {
                StudentMenu.showStudentMenu();
            }
        } else {
            System.out.println("Exiting program...");
        }
    }
}

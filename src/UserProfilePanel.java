import javax.swing.*;

public abstract class UserProfilePanel extends JFrame {

    protected int userID;

    public UserProfilePanel(int userID) {
        this.userID = userID;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Abstract methods (force child to implement)
    protected abstract void showMainMenu();
    protected abstract void openEditProfile();
    protected abstract void openViewProfile();
}

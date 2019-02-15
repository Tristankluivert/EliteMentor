package ng.com.hybrid.elitementor.Model;

public class User {

    private String id;
    private String username;
    private String profileimage;

    public User(String id, String username, String profileimage) {
        this.id = id;
        this.username = username;
        this.profileimage = profileimage;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}

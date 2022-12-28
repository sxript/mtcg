package game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Profile {
    private String bio;
    private String image;

    public Profile () {}
    public Profile(String bio, String image) {
        setBio(bio);
        setImage(image);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "bio='" + bio + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

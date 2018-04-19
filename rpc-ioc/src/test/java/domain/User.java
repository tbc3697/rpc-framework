package domain;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Created by tbc on 2018/4/16.
 */
@Data
public class User {
    private String name;
    private String email;
}

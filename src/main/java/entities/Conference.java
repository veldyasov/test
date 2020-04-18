package entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Conference {
    private String country;
    private String startingDate;
    private List<String> emails;
}

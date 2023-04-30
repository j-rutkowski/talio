package server.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class RandomService {

    /**
     * Returns a random string of length 10
     *
     * @return a random string of length 10
     */
    public String getRandomString() {
        return RandomStringUtils.random(10, true, true);
    }

}

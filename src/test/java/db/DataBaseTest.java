package db;

import model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class DataBaseTest {

    @Test
    void 새로운_유저를_저장할_수_있다() {
        // given
        final User dinoUser = new User("dino", "1234", "디노", "dino.shin@kakaocorp.com");

        // when
        DataBase.addUser(dinoUser);

        // then
        assertThat(DataBase.findUserById("dino")).isPresent();
    }

    @Test
    void 이미_존재하는_아이디인지_확인할_수_있다() {
        // given
        final User user = new User("gooddino", "1234", "디노", "dino.shin@kakaocorp.com");
        DataBase.addUser(user);

        // when & then
        assertThat(DataBase.isAlreadyExistId("gooddino")).isTrue();
    }

    @Test
    void 아이디와_비밀번호를_통해_해당_유저가_존재하는지_확인할_수_있다() {
        // given
        final User user = new User("user", "4567", "또다른디노", "dino2.shin@kakaocorp.com");
        DataBase.addUser(user);

        // when & then
        assertThat(DataBase.isUserExist("user", "4567")).isTrue();
    }
}

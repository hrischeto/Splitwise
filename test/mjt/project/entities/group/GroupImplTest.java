package mjt.project.entities.group;

import mjt.project.entities.users.RegisteredUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GroupImplTest {
    @Mock
    private Set<RegisteredUser> membersMock;

    private GroupImpl group;

    @BeforeEach
    void setUp() {
        group = new GroupImpl("name", membersMock);
    }

    @Test
    void testUnmodifiableSetOfMembers() {
        assertThrows(UnsupportedOperationException.class, ()-> group.getMembers().add("newMember"));
    }
}

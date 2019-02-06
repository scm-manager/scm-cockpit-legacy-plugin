package sonia.scm.legacy;

import com.github.sdorra.shiro.SubjectAware;
import de.otto.edison.hal.Embedded;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.activity.api.ActivitiesDto;
import sonia.scm.activity.api.ActivityDto;
import sonia.scm.activity.api.ActivityResource;
import sonia.scm.api.v2.resources.ChangesetDto;
import sonia.scm.api.v2.resources.PersonDto;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.time.Instant;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SubjectAware(username = "admin",
        password = "secret",
        configuration = "classpath:sonia/scm/legacy/shiro.ini")
public class ActivityLegacyResourceTest {

    @Mock
    private ActivityResource activityResource;

    @InjectMocks
    private ActivityLegacyResource resource;

    private Dispatcher dispatcher;
    private final MockHttpResponse response = new MockHttpResponse();

    @Before
    public void setUp() {
        dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addSingletonResource(resource);
    }

    @Test
    public void shouldGetActivities() throws UnsupportedEncodingException, URISyntaxException {
        ActivitiesDto activities = new ActivitiesDto();
        ChangesetDto changeset = new ChangesetDto();
        PersonDto author = new PersonDto();
        author.setName("Arthur Dent");
        author.setMail("dent@hitchhikers.com");
        changeset.setAuthor(author);
        changeset.setDate(Instant.now());
        changeset.setDescription("spaceship");
        ActivityDto activity = new ActivityDto(null, Embedded.embedded("changeset", asList(changeset)));
        activity.setRepositoryNamespace("space");
        activity.setRepositoryName("X");
        activity.setRepositoryType("git");
        activities.setActivities(asList(activity));
        when(activityResource.getLatestActivity(any())).thenReturn(activities);

        MockHttpRequest request = MockHttpRequest.get("/rest/activity.json");
        dispatcher.invoke(request, response);

        assertThat(response.getContentAsString())
                .contains("author")
                .contains("dent")
                .contains("gravatar-hash")
                .contains("spaceship")
                .contains("space/X")
                .contains("git");
    }
}
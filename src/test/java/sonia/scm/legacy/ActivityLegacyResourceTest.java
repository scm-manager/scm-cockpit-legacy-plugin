/**
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
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
import com.google.common.collect.ImmutableMultiset;
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
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.statistic.dto.CommitsPerAuthor;
import sonia.scm.statistic.dto.CommitsPerMonth;
import sonia.scm.statistic.resources.StatisticResource;
import sonia.scm.statistic.resources.StatisticSubResource;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SubjectAware(username = "admin",
        password = "secret",
        configuration = "classpath:sonia/scm/legacy/shiro.ini")
public class StatisticsLegacyResourceTest {

    @Mock
    private StatisticResource statisticResource;
    @Mock
    private StatisticSubResource statisticSubResource;
    @Mock
    private RepositoryServiceFactory repositoryServiceFactory;
    @Mock
    private RepositoryService repositoryService;

    @InjectMocks
    private StatisticsLegacyResource resource;

    private Dispatcher dispatcher;
    private final MockHttpResponse response = new MockHttpResponse();

    @Before
    public void setUp() {
        dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addSingletonResource(resource);

        when(repositoryServiceFactory.create(anyString())).thenReturn(repositoryService);

        when(repositoryService.getRepository()).thenReturn(new Repository("123", "git", "space", "X"));

        when(statisticResource.getSubResource("space", "X")).thenReturn(statisticSubResource);
    }

    @Test
    public void shouldGetCommitsPerAuthor() throws UnsupportedEncodingException, URISyntaxException {
        when(statisticSubResource.getCommitPerAuthor(anyInt())).thenReturn(new CommitsPerAuthor(ImmutableMultiset.of("dent", "dent")));

        MockHttpRequest request = MockHttpRequest.get("/rest/plugins/statistic/123/commits-per-author.json");
        dispatcher.invoke(request, response);

        assertThat(response.getContentAsString())
                .contains("author")
                .contains("dent")
                .contains("2");
    }

    @Test
    public void shouldGetCommitsPerMonth() throws UnsupportedEncodingException, URISyntaxException {
        when(statisticSubResource.getCommitPerMonth()).thenReturn(new CommitsPerMonth(ImmutableMultiset.of("1974-10", "1974-10")));

        MockHttpRequest request = MockHttpRequest.get("/rest/plugins/statistic/123/commits-per-month.json");
        dispatcher.invoke(request, response);

        assertThat(response.getContentAsString())
                .contains("month")
                .contains("1974-10")
                .contains("2");
    }
}
/*
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

import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.statistic.resources.StatisticResource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("rest/plugins/statistic")
public class StatisticsLegacyResource {

    private final StatisticResource statisticResource;
    private final RepositoryServiceFactory serviceFactory;

    @Inject
    public StatisticsLegacyResource(StatisticResource statisticResource, RepositoryServiceFactory serviceFactory) {
        this.statisticResource = statisticResource;
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Path("{id}/commits-per-author.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response commitsPerAuthor(@PathParam("id") String id) {
        try (RepositoryService service = serviceFactory.create(id)) {
            Repository repository = service.getRepository();
            return Response.ok(statisticResource.getSubResource(repository.getNamespace(), repository.getName()).getCommitPerAuthor(Integer.MAX_VALUE)).build();
        }
    }

    @GET
    @Path("{id}/commits-per-month.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response commitsPerMonth(@PathParam("id") String id) {
        try (RepositoryService service = serviceFactory.create(id)) {
            Repository repository = service.getRepository();
            return Response.ok(statisticResource.getSubResource(repository.getNamespace(), repository.getName()).getCommitPerMonth()).build();
        }
    }
}

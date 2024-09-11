/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.legacy;

import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.statistic.resources.StatisticResource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

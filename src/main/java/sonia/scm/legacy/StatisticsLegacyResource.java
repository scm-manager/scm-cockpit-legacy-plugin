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

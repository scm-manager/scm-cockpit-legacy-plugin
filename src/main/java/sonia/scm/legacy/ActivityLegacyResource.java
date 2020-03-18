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

import lombok.Getter;
import sonia.scm.activity.api.ActivityDto;
import sonia.scm.activity.api.ActivityResource;
import sonia.scm.api.v2.resources.ChangesetDto;
import sonia.scm.api.v2.resources.PersonDto;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Path("rest/activity.json")
public class ActivityLegacyResource {

    private final ActivityResource activityResource;

    @Inject
    public ActivityLegacyResource(ActivityResource activityResource) {
        this.activityResource = activityResource;
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response latestActivities(@Context UriInfo uriInfo) {
        List<ActivityDto> activities = activityResource.getLatestActivity(uriInfo).getActivities();

        return Response.ok(new LegacyActivityCollectionDto(activities)).build();
    }

    @Getter
    private static class LegacyActivityCollectionDto {
        private final List<LegacyActivityDto> activities;

        public LegacyActivityCollectionDto(List<ActivityDto> activities) {
            this.activities = activities.stream().map(LegacyActivityDto::new).collect(Collectors.toList());
        }
    }

    @Getter
    private static class LegacyActivityDto {

        public LegacyActivityDto(ActivityDto activityDto) {
            this.repositoryName = String.format("%s/%s", activityDto.getRepositoryNamespace(), activityDto.getRepositoryName());
            this.repositoryType = activityDto.getRepositoryType();
            this.changeset = new LegacyChangesetDto(activityDto.extractChangeset());
        }

        @XmlElement(name = "repository-name")
        private String repositoryName;

        @XmlElement(name = "repository-type")
        private String repositoryType;
        private LegacyChangesetDto changeset;

    }

    @Getter
    private static class LegacyChangesetDto {
        private final LegacyAuthorDto author;
        private final List<LegacyPropertyDto> properties;
        private final String description;
        private final Long date;

        LegacyChangesetDto(ChangesetDto changeset) {
            this.author = changeset.getAuthor() == null ? null : new LegacyAuthorDto(changeset.getAuthor());
            this.properties = this.author == null ? emptyList() : singletonList(new LegacyPropertyDto("gravatar-hash", GravatarMD5Util.md5Hex(this.author.getMail())));
            this.description = changeset.getDescription();
            this.date = changeset.getDate().toEpochMilli();
        }
    }

    @Getter
    private static class LegacyAuthorDto {
        private final String mail;
        private final String name;

        LegacyAuthorDto(PersonDto author) {
            this.mail = author.getMail();
            this.name = author.getName();
        }
    }

    @Getter
    private static class LegacyPropertyDto {
        private final String key;
        private final String value;

        LegacyPropertyDto(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}

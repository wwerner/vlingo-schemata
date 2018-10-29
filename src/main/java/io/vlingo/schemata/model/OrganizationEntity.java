// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.function.BiConsumer;

import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.model.Id.OrganizationId;

public class OrganizationEntity extends EventSourced implements Organization {
  static {
    BiConsumer<OrganizationEntity, OrganizationDefined> applyOrganizationDefinedFn = OrganizationEntity::applyDefined;
    EventSourced.registerConsumer(OrganizationEntity.class, OrganizationDefined.class, applyOrganizationDefinedFn);
    BiConsumer<OrganizationEntity, OrganizationDescribed> applyOrganizationDescribedFn = OrganizationEntity::applyDescribed;
    EventSourced.registerConsumer(OrganizationEntity.class, OrganizationDescribed.class, applyOrganizationDescribedFn);
    BiConsumer<OrganizationEntity, OrganizationRenamed> applyOrganizationRenamedFn = OrganizationEntity::applyRenamed;
    EventSourced.registerConsumer(OrganizationEntity.class, OrganizationRenamed.class, applyOrganizationRenamedFn);
  }

  private OrganizationEntity.State state;

  public OrganizationEntity(final String name, final String description) {
    apply(new OrganizationDefined(OrganizationId.unique(), name, description));
  }

  @Override
  public void describeAs(final String description) {
    apply(new OrganizationDescribed(state.id, description));
  }

  @Override
  public void renameTo(final String name) {
    apply(new OrganizationRenamed(state.id, name));
  }

  public void applyDefined(OrganizationDefined event) {
    state = new State(OrganizationId.existing(event.organizationId), event.name, event.description);
  }

  public final void applyDescribed(OrganizationDescribed event) {
    state = state.withDescription(event.description);
  }

  public final void applyRenamed(OrganizationRenamed event) {
    state = state.withName(event.name);
  }

  public class State {
    public final OrganizationId id;
    public final String name;
    public final String description;

    public OrganizationEntity.State withDescription(final String description) {
      return new State(this.id, this.name, description);
    }

    public State withName(final String name) {
      return new State(this.id, name, this.description);
    }

    public State(final OrganizationId id, final String name, final String description) {
      this.id = id;
      this.name = name;
      this.description = description;
    }
  }
}
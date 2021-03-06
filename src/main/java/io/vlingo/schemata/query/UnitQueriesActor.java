// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vlingo.common.Completes;
import io.vlingo.lattice.query.StateObjectQueryActor;
import io.vlingo.schemata.model.UnitState;
import io.vlingo.schemata.resource.data.UnitData;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.QueryExpression;

public class UnitQueriesActor extends StateObjectQueryActor implements UnitQueries {
  private static final String QueryAll =
          "SELECT * FROM TBL_UNITS " +
          "WHERE organizationId = :organizationId";

  private static final String ById =
          "SELECT * FROM TBL_UNITS " +
          "WHERE organizationId = :organizationId " +
          "AND unitId = :unitId";

  private static final String ByName =
          "SELECT * FROM TBL_UNITS " +
          "WHERE organizationId = :organizationId " +
          "AND name = :name";

  private final Map<String,String> parameters;

  public UnitQueriesActor(final ObjectStore objectStore) {
    super(objectStore);
    this.parameters = new HashMap<>(2);
  }

  @Override
  public Completes<List<UnitData>> units(final String organizationId) {
    parameters.clear();
    parameters.put("organizationId", organizationId);

    final QueryExpression query = MapQueryExpression.using(UnitState.class, QueryAll, parameters);

    return queryAll(UnitState.class, query, (List<UnitState> states) -> UnitData.from(states));
  }

  @Override
  public Completes<UnitData> unit(final String organizationId, final String unitId) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("unitId", unitId);

    return queryOne(ById, parameters);
  }

  @Override
  public Completes<UnitData> unitNamed(final String organizationId, final String name) {
    parameters.clear();
    parameters.put("organizationId", organizationId);
    parameters.put("name", name);

    return queryOne(ByName, parameters);
  }

  private Completes<UnitData> queryOne(final String query, final Map<String,String> parameters) {
    final QueryExpression expression = MapQueryExpression.using(UnitState.class, query, parameters);

    return queryObject(UnitState.class, expression, (UnitState state) -> UnitData.from(state));
  }
}

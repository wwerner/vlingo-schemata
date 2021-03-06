// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.processor.types;

import java.util.Optional;

import io.vlingo.schemata.codegen.ast.types.TypeDefinition;

public interface TypeResolver {
    Optional<TypeDefinition> resolve(final String fullQualifiedTypeName, final String simpleTypeName);
}

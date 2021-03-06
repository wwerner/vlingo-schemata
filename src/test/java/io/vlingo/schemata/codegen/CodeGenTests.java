// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen;

import java.io.InputStream;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.backend.Backend;
import io.vlingo.schemata.codegen.backend.java.JavaBackend;
import io.vlingo.schemata.codegen.parser.AntlrTypeParser;
import io.vlingo.schemata.codegen.parser.TypeParser;
import io.vlingo.schemata.codegen.processor.Processor;
import io.vlingo.schemata.codegen.processor.types.ComputableTypeProcessor;
import io.vlingo.schemata.codegen.processor.types.CacheTypeResolver;
import io.vlingo.schemata.codegen.processor.types.TypeResolverProcessor;

public abstract class CodeGenTests {
    protected static final long TIMEOUT = 500L;

    private World world;
    private CacheTypeResolver typeResolver;
    private TypeParser typeParser;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.startWithDefaults(getClass().getSimpleName()).world();
        typeResolver = new CacheTypeResolver();
        typeParser = world.actorFor(TypeParser.class, AntlrTypeParser.class);
    }

    @After
    public void tearDown() throws Exception {
        world.terminate();
    }

    protected final TypeDefinitionCompiler compilerWithJavaBackend() {
        return world.actorFor(TypeDefinitionCompiler.class, TypeDefinitionCompilerActor.class,
                typeParser,
                Arrays.asList(
                        world.actorFor(Processor.class, ComputableTypeProcessor.class),
                        world.actorFor(Processor.class, TypeResolverProcessor.class, typeResolver)
                ),
                world.actorFor(Backend.class, JavaBackend.class, true)
        );
    }

    protected final void registerType(final String filePath, final String fullyQualifiedTypeName, final String version) {
      InputStream typeDefinition = typeDefinition(filePath);
      TypeDefinition parsed = (TypeDefinition) typeParser.parseTypeDefinition(typeDefinition, fullyQualifiedTypeName).await(TIMEOUT);

      typeResolver.produce(parsed, version);
    }

    protected final InputStream typeDefinition(final String name) {
        try {
            return getClass().getResourceAsStream("/io/vlingo/schemata/codegen/vss/" + name + ".vss");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

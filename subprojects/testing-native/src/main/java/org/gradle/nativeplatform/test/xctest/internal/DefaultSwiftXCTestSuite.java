/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.nativeplatform.test.xctest.internal;

import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.language.swift.SwiftComponent;
import org.gradle.language.swift.SwiftPlatform;
import org.gradle.language.swift.internal.DefaultSwiftComponent;
import org.gradle.nativeplatform.test.xctest.SwiftXCTestBinary;
import org.gradle.nativeplatform.test.xctest.SwiftXCTestSuite;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainInternal;
import org.gradle.nativeplatform.toolchain.internal.PlatformToolProvider;

import javax.inject.Inject;

/**
 * Abstract software component representing an XCTest suite.
 */
public class DefaultSwiftXCTestSuite extends DefaultSwiftComponent implements SwiftXCTestSuite {
    private final ObjectFactory objectFactory;
    private final Property<SwiftXCTestBinary> developmentBinary;
    private final Property<SwiftComponent> testedComponent;

    @Inject
    public DefaultSwiftXCTestSuite(String name, FileOperations fileOperations, ObjectFactory objectFactory, ConfigurationContainer configurations) {
        super(name, fileOperations, objectFactory, configurations);
        this.testedComponent = objectFactory.property(SwiftComponent.class);
        this.objectFactory = objectFactory;
        this.developmentBinary = objectFactory.property(SwiftXCTestBinary.class);
    }

    public SwiftXCTestBinary addExecutable(SwiftPlatform targetPlatform, NativeToolChainInternal toolChain, PlatformToolProvider platformToolProvider) {
        SwiftXCTestBinary result = objectFactory.newInstance(DefaultSwiftXCTestBinary.class, getName() + "Executable", getModule(), true, false, true, getSwiftSource(), getImplementationDependencies(), targetPlatform, toolChain, platformToolProvider);
        getBinaries().add(result);
        return result;
    }

    public Property<SwiftComponent> getTestedComponent() {
        return testedComponent;
    }

    @Override
    public Property<SwiftXCTestBinary> getDevelopmentBinary() {
        return developmentBinary;
    }

    @Override
    public Provider<SwiftXCTestBinary> getTestExecutable() {
        return getDevelopmentBinary();
    }
}

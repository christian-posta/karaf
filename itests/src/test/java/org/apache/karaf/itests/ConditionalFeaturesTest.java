/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.itests;


import java.util.EnumSet;

import org.apache.karaf.features.FeaturesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ConditionalFeaturesTest extends KarafTestSupport {
    /*
    @Inject
    private FeaturesService featuresService;

    @Inject
    private BundleContext bundleContext;
    
    @Inject
    BootFinished bootFinished;


    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }


    @Configuration
    public Option[] config() {
        
        MavenArtifactUrlReference karafUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("zip").versionAsInProject();
        return new Option[]{
            karafDistributionConfiguration().frameworkUrl(karafUrl),
            KarafDistributionOption.editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", KarafTestSupport.HTTP_PORT)
        };
    }
    */

    @Test
    public void testScr() throws Exception {
        //Remove management and install scr
        featureService.uninstallFeature("management", EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
        featureService.installFeature("scr", EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
        assertBundleNotInstalled("org.apache.karaf.scr.management");

        //Add management back
        featureService.installFeature("management", EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
        assertBundleInstalled("org.apache.karaf.scr.management");
    }

    @Test
    public void testWebconsole() throws Exception {
        try {
            featureService.uninstallFeature("scr", EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
        } catch (Exception e) {
        }
        featureService.installFeature("webconsole", EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));

        assertBundleInstalled("org.apache.karaf.webconsole.features");
        assertBundleInstalled("org.apache.karaf.webconsole.instance");
        assertBundleInstalled("org.apache.karaf.webconsole.gogo");
        assertBundleInstalled("org.apache.karaf.webconsole.http");
        assertBundleInstalled("org.apache.felix.webconsole.plugins.event");

        // remove eventadmin
        featureService.uninstallFeature("eventadmin", EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
        assertBundleNotInstalled("org.apache.felix.webconsole.plugins.event");
    }
}

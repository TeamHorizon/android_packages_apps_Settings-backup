/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.settings.enterprise;

import android.content.Context;
import android.content.res.Resources;
import com.android.settings.R;
import android.support.v7.preference.Preference;

import com.android.settings.SettingsRobolectricTestRunner;
import com.android.settings.TestConfig;
import com.android.settings.core.PreferenceAvailabilityObserver;
import com.android.settings.testutils.FakeFeatureFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link CaCertsPreferenceController}.
 */
@RunWith(SettingsRobolectricTestRunner.class)
@Config(manifest = TestConfig.MANIFEST_PATH, sdk = TestConfig.SDK_VERSION)
public final class CaCertsPreferenceControllerTest {

    private static final String KEY_CA_CERTS = "ca_certs";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Context mContext;
    private FakeFeatureFactory mFeatureFactory;
    @Mock private PreferenceAvailabilityObserver mObserver;

    private CaCertsPreferenceController mController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FakeFeatureFactory.setupForTest(mContext);
        mFeatureFactory = (FakeFeatureFactory) FakeFeatureFactory.getFactory(mContext);
        mController = new CaCertsPreferenceController(mContext, null /* lifecycle */);
        mController.setAvailabilityObserver(mObserver);
    }

    @Test
    public void testGetAvailabilityObserver() {
        assertThat(mController.getAvailabilityObserver()).isEqualTo(mObserver);
    }

    @Test
    public void testUpdateState() {
        final Preference preference = new Preference(mContext, null, 0, 0);

        when(mContext.getResources().getQuantityString(R.plurals.enterprise_privacy_number_ca_certs,
                10, 10)).thenReturn("10 certs");
        when(mFeatureFactory.enterprisePrivacyFeatureProvider
                .getNumberOfOwnerInstalledCaCertsForCurrentUserAndManagedProfile()).thenReturn(10);
        mController.updateState(preference);
        assertThat(preference.getSummary()).isEqualTo("10 certs");
    }

    @Test
    public void testIsAvailable() {
        when(mFeatureFactory.enterprisePrivacyFeatureProvider
                .getNumberOfOwnerInstalledCaCertsForCurrentUserAndManagedProfile()).thenReturn(0);
        assertThat(mController.isAvailable()).isFalse();
        verify(mObserver).onPreferenceAvailabilityUpdated(KEY_CA_CERTS, false);

        when(mFeatureFactory.enterprisePrivacyFeatureProvider
                .getNumberOfOwnerInstalledCaCertsForCurrentUserAndManagedProfile()).thenReturn(10);
        assertThat(mController.isAvailable()).isTrue();
        verify(mObserver).onPreferenceAvailabilityUpdated(KEY_CA_CERTS, true);
    }

    @Test
    public void testHandlePreferenceTreeClick() {
        assertThat(mController.handlePreferenceTreeClick(new Preference(mContext, null, 0, 0)))
                .isFalse();
    }

    @Test
    public void testGetPreferenceKey() {
        assertThat(mController.getPreferenceKey()).isEqualTo(KEY_CA_CERTS);
    }
}

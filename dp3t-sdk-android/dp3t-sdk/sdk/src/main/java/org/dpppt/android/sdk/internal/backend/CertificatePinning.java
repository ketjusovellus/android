/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package org.dpppt.android.sdk.internal.backend;

import androidx.annotation.NonNull;

import okhttp3.CertificatePinner;

public class CertificatePinning {

	private static CertificatePinner certificatePinner = CertificatePinner.DEFAULT;

	public static void setCertificatePinner(@NonNull CertificatePinner certificatePinner) {
		CertificatePinning.certificatePinner = certificatePinner;
	}

	public static CertificatePinner getCertificatePinner() {
		return certificatePinner;
	}

}

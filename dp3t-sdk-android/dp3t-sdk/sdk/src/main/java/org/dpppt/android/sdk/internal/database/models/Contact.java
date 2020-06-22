/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package org.dpppt.android.sdk.internal.database.models;

import org.dpppt.android.sdk.internal.crypto.EphId;

public class Contact {

	private int id;
	private long date;
	private EphId ephId;
	private int windowCount;
	private int associatedKnownCase;

	private String ketjuUserPrefix;
	private long ketjuStartDate;
	private long ketjuEndDate;
	private int ketjuMinutes;
	private double ketjuMeanAttenuation;
	private double ketjuMeanDistance;

	public Contact(int id, long date, EphId ephId, int windowCount, int associatedKnownCase,
				   String ketjuUserPrefix, long ketjuStartDate, long ketjuEndDate, int ketjuMinutes, double ketjuMeanAttenuation, double ketjuMeanDistance) {
		this.id = id;
		this.date = date;
		this.ephId = ephId;
		this.windowCount = windowCount;
		this.associatedKnownCase = associatedKnownCase;

		this.ketjuUserPrefix = ketjuUserPrefix;
		this.ketjuStartDate = ketjuStartDate;
		this.ketjuEndDate = ketjuEndDate;
		this.ketjuMinutes = ketjuMinutes;
		this.ketjuMeanAttenuation = ketjuMeanAttenuation;
		this.ketjuMeanDistance = ketjuMeanDistance;
	}

	public EphId getEphId() {
		return ephId;
	}

	public long getDate() {
		return date;
	}

	public double getWindowCount() {
		return windowCount;
	}

	public int getAssociatedKnownCase() {
		return associatedKnownCase;
	}

	public int getId() {
		return id;
	}

	public String getKetjuUserPrefix() {
		return ketjuUserPrefix;
	}

	public long getKetjuStartDate() {
		return ketjuStartDate;
	}

	public long getKetjuEndDate() {
		return ketjuEndDate;
	}

	public int getKetjuMinutes() {
		return ketjuMinutes;
	}

	public double getKetjuMeanAttenuation() {
		return ketjuMeanAttenuation;
	}

	public double getKetjuMeanDistance() {
		return ketjuMeanDistance;
	}
}

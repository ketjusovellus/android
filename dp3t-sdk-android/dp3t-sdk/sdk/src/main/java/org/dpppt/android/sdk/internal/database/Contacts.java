/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package org.dpppt.android.sdk.internal.database;

interface Contacts {

	String TABLE_NAME = "contacts";

	String ID = "id";
	String DATE = "date";
	String EPHID = "ephID";
	String WINDOW_COUNT = "windowCount";
	String ASSOCIATED_KNOWN_CASE = "associated_known_case";

	String KETJU_USER_PREFIX = "ketjuUserPrefix";
	String KETJU_START_DATE = "ketjuStartDate";
	String KETJU_END_DATE = "ketjuEndDate";
	String KETJU_MINUTES = "ketjuMinutes";
	String KETJU_MEAN_ATTENUATION = "ketjuMeanAttenuation";
	String KETJU_MEAN_DISTANCE = "ketjuMeanDistance";

	String[] PROJECTION = {
			ID,
			DATE,
			EPHID,
			WINDOW_COUNT,
			ASSOCIATED_KNOWN_CASE,
			KETJU_USER_PREFIX,
			KETJU_START_DATE,
			KETJU_END_DATE,
			KETJU_MINUTES,
			KETJU_MEAN_ATTENUATION,
			KETJU_MEAN_DISTANCE
	};

	static String create() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				DATE + " INTEGER NOT NULL, " + EPHID + " BLOB NOT NULL, " +
				WINDOW_COUNT + " INTEGER NOT NULL, " + ASSOCIATED_KNOWN_CASE + " INTEGER, " +
				KETJU_USER_PREFIX + " TEXT," + KETJU_START_DATE + " INTEGER NOT NULL, " + KETJU_END_DATE + " INTEGER NOT NULL, " + KETJU_MINUTES + " INTEGER NOT NULL, " +
				KETJU_MEAN_ATTENUATION + " REAL NOT NULL, " + KETJU_MEAN_DISTANCE + " REAL NOT NULL, " +
				"CONSTRAINT no_duplicates UNIQUE (" + DATE + ", " + EPHID + "), " +
				"FOREIGN KEY (" + ASSOCIATED_KNOWN_CASE + ") REFERENCES " +
				KnownCases.TABLE_NAME + " (" + KnownCases.ID + ") ON DELETE SET NULL)";
	}

	static String drop() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}

}

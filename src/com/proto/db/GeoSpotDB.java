package com.proto.db;

import com.antlersoft.android.db.FieldAccessor;
import com.antlersoft.android.db.TableInterface;

@TableInterface(ImplementingClassName = "GeoBean", TableName = "places")
interface GeoSpotDB {
	@FieldAccessor
	long get_Id();

	@FieldAccessor
	String getName();

	@FieldAccessor
	int getX();

	@FieldAccessor
	int getY();
}
